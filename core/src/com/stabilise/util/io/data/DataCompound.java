package com.stabilise.util.io.data;

import javaslang.control.Option;

import com.stabilise.util.Checks;

/**
 * A DataCompound is the basic unifying building block for this package. A
 * DataCompound is essentially equivalent to any old object - it encapsulates
 * data, and may be saved in a variety of {@link Format}{@code s}.
 * 
 * <p>There are three primary data-interaction methods for a DataCompound:
 *  
 * <ul>
 * <li>{@code put()} methods. Each of these methods inserts data into this tag;
 *     if data with the specified name already exists, it will be overwritten.
 * <li>{@code get()} methods. Each of these methods get data from this tag. For
 *     each of the {@code get} methods, if data is not present or is present in
 *     a different format (e.g. invoking {@code getBool("foo")} when the data
 *     type of {@code "foo"} is {@code int}), a suitable default will be
 *     returned.
 * <li>{@code opt()} methods. These methods behave like the {@code get()}
 *     methods, but return an {@code Option} instead of a default value.
 * </ul>
 */
public interface DataCompound extends ITag, IContainerTag<DataCompound> {
    
    /**
     * Creates a DataCompound of the format determined the current thread's
     * default value.
     * 
     * @see Format#getDefaultFormat()
     * @see Format#setDefaultFormat(Format)
     */
    public static DataCompound create() {
        return Format.getDefaultFormat().newCompound();
    }
    
    
    
    /**
     * Checks for whether or not a tag with the specified name is contained
     * within this compound.
     * 
     * <p>This method is meaningless for {@link Format#BYTE_STREAM}.
     */
    boolean contains(String name);
    
    /**
     * Gets a compound which is a child of this one. If a compound by the
     * specified name already exists, it is returned, otherwise one is
     * created and added to this compound. If another data type under the
     * specified name is present, it will be overwritten.
     * 
     * <p>The returned compound will be of the same format as this one.
     */
    DataCompound createCompound(String name);
    
    /**
     * Gets a list which is a child of this compound. If a list by the
     * specified name already exists, it is returned, otherwise one is created
     * and added to this compound. If another data type under the specified
     * name is present, it will be overwritten.
     * 
     * <p>The returned list will be of the same format as this compound.
     */
    DataList createList(String name);
    
    // <----- PUT METHODS ----->
    // Insert the data into this tag; if data with the specified name already
    // exists, it'll be overwritten.
    
    /**
     * If the given compound is of a different format to this one, it will be
     * converted before being added.
     */
    void put(String name, DataCompound data);
    
    /**
     * If the given list is of a different format to this compound, it will be
     * converted before being added.
     */
    void put(String name, DataList     data);
    
    void put(String name, boolean  data);
    void put(String name, byte     data);
    void put(String name, double   data);
    void put(String name, float    data);
    void put(String name, int      data);
    void put(String name, long     data);
    void put(String name, short    data);
    void put(String name, String   data);
    void put(String name, byte[]   data);
    void put(String name, int[]    data);
    void put(String name, long[]   data);
    void put(String name, float[]  data);
    void put(String name, double[] data);
    
    /**
     * Convenient shorthand for {@code o.exportToCompound(this)}.
     * 
     * @see #getInto(Exportable)
     */
    default void put(Exportable o) {
        o.exportToCompound(this);
    }
    
    /**
     * Convenient shorthand for
     * <pre>
     * DataCompound c = this.createCompound(name);
     * o.exportToCompound(c);
     * </pre>
     * 
     * @see #getInto(String, Exportable)
     */
    default void put(String name, Exportable o) {
        DataCompound c = this.createCompound(name);
        o.exportToCompound(c);
    }
    
    // <----- GET METHODS ----->
    // Gets data from this compound. If data with the specified name is not
    // present or is of another type, suitable defaults are returned.
    
    /**
     * If a compound with the specified name is not present, an empty one is
     * created and returned, but not added to this compound.
     */
    DataCompound getCompound(String name);
    
    /**
     * If a list with the specified name is not present, an empty one is
     * created and returned, but not added to this compound.
     */
    DataList getList(String name);
    
    boolean  getBool  (String name);
    byte     getI8    (String name);
    short    getI16   (String name);
    int      getI32   (String name);
    long     getI64   (String name);
    float    getF32   (String name);
    double   getF64   (String name);
    byte[]   getI8Arr (String name);
    int[]    getI32Arr(String name);
    long[]   getI64Arr(String name);
    float[]  getF32Arr(String name);
    double[] getF64Arr(String name);
    String   getString(String name);
    
    /**
     * Convenient shorthand for {@code o.importFromCompound(this)}.
     * 
     * @see #put(Exportable)
     */
    default void getInto(Exportable o) {
        o.importFromCompound(this);
    }
    
    /**
     * Convenient shorthand for
     * <pre>
     * DataCompound c = this.getCompound(name);
     * o.importFromCompound(c);
     * </pre>
     * 
     * @see #put(String, Exportable)
     */
    default void getInto(String name, Exportable o) {
        DataCompound c = this.getCompound(name);
        o.importFromCompound(c);
    }
    
    // <----- OPTION GETTERS ----->
    // Gets data from this compound. If data with the specified name is
    // present, a Some<T> is returned.
    
    Option<DataCompound> optCompound (String name);
    Option<DataList>     optList     (String name);
    Option<Boolean>      optBool     (String name);
    Option<Byte>         optI8       (String name);
    Option<Short>        optI16      (String name);
    Option<Integer>      optI32      (String name);
    Option<Long>         optI64      (String name);
    Option<Float>        optF32      (String name);
    Option<Double>       optF64      (String name);
    Option<byte[]>       optI8Arr    (String name);
    Option<int[]>        optI32Arr   (String name);
    Option<long[]>       optI64Arr   (String name);
    Option<float[]>      optF32Arr   (String name);
    Option<double[]>     optF64Arr   (String name);
    Option<String>       optString   (String name);
    
    /**
     * Clones this DataCompound.
     */
    default DataCompound copy() {
        return copy(format());
    }
    
    /**
     * Clones this DataCompound.
     * 
     * @param format The desired format of the clone.
     */
    DataCompound copy(Format format);
    
    /**
     * Wraps this {@code DataCompound} in an {@code ImmutableCompound}, or
     * returns this compound if it is already immutable.
     */
    default ImmutableCompound immutable() {
        return ImmutableCompound.wrap(this);
    }
    
    /**
     * Casts this DataCompound to a MapCompound if able, and throws a
     * RuntimeException if not. This may be done to expose a number of
     * additional utility methods that MapCompound provides.
     */
    default MapCompound asMapCompound() {
    	if(this instanceof MapCompound)
    		return (MapCompound)this;
    	throw new RuntimeException("This DataCompound is not a map compound!");
    }
    
    @Override default boolean isBoolean() { return false; }
    @Override default boolean isLong()    { return false; }
    @Override default boolean isDouble()  { return false; }
    @Override default boolean isString()  { return false; }
    
    @Override default boolean getAsBoolean() { throw Checks.ISE("Can't convert compound to boolean"); }
    @Override default long    getAsLong()    { throw Checks.ISE("Can't convert compound to long");    }
    @Override default double  getAsDouble()  { throw Checks.ISE("Can't convert compound to double");  }
    @Override default String  getAsString()  { throw Checks.ISE("Can't convert compound to string");  }
    
    @Override
    default ITag convertToSameType(ITag other) {
        if(isSameType(other))
            return other;
        throw Checks.ISE("Can't convert " + other.getClass().getSimpleName() + " to compound type.");
    }
    
}
