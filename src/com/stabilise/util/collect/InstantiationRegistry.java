package com.stabilise.util.collect;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import com.stabilise.util.collect.Registry.DuplicatePolicy;

/**
 * A registry which provides instantiation facilities for registered classes.
 * 
 * <h3>Usage Example</h3>
 * 
 * <pre>
 * public static class MyClass {
 *     // generic superclass
 * }
 * public static class MyOtherClass extends MyClass {
 *     public MyOtherClass(int x, int y) {
 *         super();
 *     }
 * }
 * public static class YetAnotherClass extends MyClass {
 *     {@code // Note that this constructor is private (this means reflection would}
 *     {@code // fail at instantiating it).}
 *     private YetAnotherClass(String name) {
 *         super();
 *     }
 * }
 * 
 * public InstantiationRegistry{@code <MyClass>} registry =
 *     new InstantiationRegistry{@code <MyClass>}("objects", 2, Registry.DuplicatePolicy.THROW_EXCEPTION);
 * 
 * registry.register(0, MyOtherClass.class, Integer.TYPE, Integer.TYPE);
 * registry.register(1, YetAnotherClass.class,
 *     new InstantiationRegistry.Factory{@code <YetAnotherClass>}() {
 *         &#64;Override
 *         public YetAnotherClass create(Object... args) {
 *             return new YetAnotherClass((String)args[0]);
 *         }
 *     });
 * 
 * {@code // Henceforth there following blocks of code are equivalent:}
 * 
 * MyClass obj1 = new MyOtherClass(0, 1);
 * MyClass obj2 = new YetAnotherClass("Penguin");
 * 
 * MyClass obj1 = registry.instantiate(0, 0, 1);
 * MyClass obj2 = registry.instantiate(1, "Penguin");
 * </pre>
 * 
 * @param <T> The type of object to instantiate.
 */
public class InstantiationRegistry<T> {
	
	/** The factory registry. */
	private final RegistryNamespaced<Factory<? extends T>> factories;
	/** Maps classes to IDs. */
	private final Map<Class<? extends T>, Integer> idMap;
	
	/** The default constructor arguments. */
	private final Class<?>[] defaultArgs;
	
	
	/**
	 * Creates a new instantiation registry.
	 * 
	 * @param name The name of the registry.
	 * @param capacity The initial registry capacity.
	 * @param dupePolicy The duplicate entry policy.
	 * @param defaultArgs The default constructor arguments.
	 * 
	 * @throws NullPointerException if either {@code name} or {@code
	 * dupePolicy} are {@code null}.
	 * @throws IllegalArgumentException if {@code capacity < 0}.
	 * @see DuplicatePolicy
	 */
	public InstantiationRegistry(String name, int capacity,
			DuplicatePolicy dupePolicy, Class<?>... defaultArgs) {
		factories = new RegistryNamespaced<Factory<? extends T>>(name, "", capacity, dupePolicy);
		idMap = new HashMap<Class<? extends T>, Integer>();
		this.defaultArgs = defaultArgs != null ? defaultArgs : new Class<?>[0];
	}
	
	/**
	 * Registers a reflective object factory.
	 * 
	 * <p>Invoking this method is equivalent to invoking {@link
	 * #register(int, String, Class, Class...)
	 * register(id, name, objClass, defaultArgs)}, where {@code defaultArgs}
	 * is specified in the {@link
	 * #InstantiationRegistry(String, String, int, DuplicatePolicy, Class...)
	 * constructor}.
	 * 
	 * @param id The ID of the object type.
	 * @param objClass The objects' class.
	 * 
	 * @throws RuntimeException if the specified class does not have a
	 * constructor accepting the default arguments.
	 * @throws IndexOutOfBoundsException if {@code id < 0}.
	 * @throws NullPointerException if any argument is {@code null}.
	 * @throws IllegalArgumentException if either {@code id} is already mapped
	 * to a factory or a class with the same name has already been registered,
	 * and this registry uses the {@link DuplicatePolicy#THROW_EXCEPTION
	 * THROW_EXCEPTION} duplicate policy.
	 */
	public void registerDefaultArgs(int id, Class<? extends T> objClass) {
		register(id, objClass, defaultArgs);
	}
	
	/**
	 * Registers a reflective object factory.
	 * 
	 * @param id The ID of the object type.
	 * @param objClass The objects' class.
	 * @param args The desired constructor's arguments.
	 * 
	 * @throws RuntimeException if the specified class does not have a
	 * constructor accepting arguments of the specified type.
	 * @throws IndexOutOfBoundsException if {@code id < 0}.
	 * @throws NullPointerException if any argument is {@code null}.
	 * @throws IllegalArgumentException if either {@code id} is already mapped
	 * to a factory or a class with the same name has already been registered,
	 * and this registry uses the {@link DuplicatePolicy#THROW_EXCEPTION
	 * THROW_EXCEPTION} duplicate policy.
	 */
	public <S extends T> void register(int id, Class<S> objClass, Class<?>... args) {
		register(id, objClass, new ReflectiveFactory<S>(objClass, args));
	}
	
	/**
	 * Registers an object factory.
	 * 
	 * @param id The ID of the object type.
	 * @param name The name of the object type.
	 * @param objClass The objects' class.
	 * @param factory The factory object with which to create instances of the
	 * object.
	 * 
	 * @throws IndexOutOfBoundsException if {@code id < 0}.
	 * @throws NullPointerException if any argument is {@code null}.
	 * @throws IllegalArgumentException if either {@code id} is already mapped
	 * to a factory or a class with the same name has already been registered,
	 * and this registry uses the {@link DuplicatePolicy#THROW_EXCEPTION
	 * THROW_EXCEPTION} duplicate policy.
	 */
	public <S extends T> void register(int id, Class<S> objClass, Factory<S> factory) {
		if(factories.register(id, objClass.getSimpleName(), factory))
			idMap.put(objClass, Integer.valueOf(id));
	}
	
	/**
	 * Instantiates an object which has been registered with the specified ID.
	 * 
	 * @param id The ID of the object type.
	 * @param args The constructor arguments.
	 * 
	 * @return The newly-created object, or {@code null} if the specified ID
	 * lacks a mapping.
	 * @throws RuntimeException if object creation failed.
	 */
	public T instantiate(int id, Object... args) {
		Factory<? extends T> factory = factories.get(id);
		if(factory != null)
			return factory.create(args);
		return null;
	}
	
	/**
	 * Instantiates an object of the specified class name.
	 * 
	 * @param className The name the object's class.
	 * @param args The constructor arguments.
	 * 
	 * @return The newly-created object, or {@code null} if there is no class
	 * of the specified name mapped.
	 * @throws RuntimeException if object creation failed.
	 */
	public T instantiate(String className, Object... args) {
		Factory<? extends T> factory = factories.get(className);
		if(factory != null)
			return factory.create(args);
		return null;
	}
	
	/**
	 * Gets the ID of the specified object class.
	 * 
	 * @return The ID, or {@code -1} if the object class has not been
	 * registered.
	 */
	public int getID(Class<? extends T> objClass) {
		//return factories.getObjectID(factories.get(objClass.getSimpleName()));
		Integer i = idMap.get(objClass);
		return i == null ? -1 : i.intValue();
	}
	
	//--------------------==========--------------------
	//-------------=====Nested Classes=====-------------
	//--------------------==========--------------------
	
	/**
	 * A Factory instantiates objects of the specified type on request.
	 */
	public static interface Factory<T> {
		
		/**
		 * Creates an object.
		 * 
		 * @param args The object arguments.
		 * 
		 * @return The newly-created object.
		 * @throws RuntimeException if object creation failed.
		 */
		public abstract T create(Object... args);
		
	}
	
	/**
	 * A ReflectiveFactory utilises reflection to instantiate its objects.
	 */
	private static class ReflectiveFactory<T> implements Factory<T> {
		
		/** The object constructor. */
		private final Constructor<? extends T> constructor;
		
		
		/**
		 * Creates a new ReflectiveFactory for objects of the specified class.
		 * 
		 * @param objClass The objects' class.
		 * @param args The desired constructor's arguments.
		 * 
		 * @throws NullPointerException if {@code objClass} is {@code null}.
		 * @throws RuntimeException if the specified class does not have a
		 * constructor accepting only two integer parameters.
		 */
		private ReflectiveFactory(Class<? extends T> objClass, Class<?>... args) {
			try {
				constructor = objClass.getConstructor(args);
			} catch(Exception e) {
				throw new RuntimeException("Constructor for " + objClass.getCanonicalName() +
						" with requested arguments does not exist! (" + e.getMessage() + ")");
			}
		}
		
		@Override
		public T create(Object... args) {
			try {
				return constructor.newInstance(args);
			} catch(Exception e) {
				throw new RuntimeException("Could not reflectively instantiate object of class \""
						+ constructor.getDeclaringClass().getSimpleName() + "\"! (" + e.getMessage() + ")",
						e);
			}
		}
		
	}
	
}
