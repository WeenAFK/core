package com.stabilise.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.stabilise.core.Constants;
import com.stabilise.entity.Entity;
import com.stabilise.entity.EntityMob;
import com.stabilise.entity.GameObject;
import com.stabilise.entity.collision.Hitbox;
import com.stabilise.entity.particle.Particle;
import com.stabilise.util.Log;
import com.stabilise.util.Profiler;
import com.stabilise.util.annotation.UserThread;
import com.stabilise.util.collect.ClearingLinkedList;
import com.stabilise.util.collect.LightLinkedList;
import com.stabilise.util.concurrent.ClearingQueue;
import com.stabilise.util.concurrent.SynchronizedClearingQueue;
import com.stabilise.util.maths.Maths;
import com.stabilise.world.dimension.Dimension;
import com.stabilise.world.provider.WorldProvider;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * An implementation of World which contains features common to all world
 * types.
 */
public abstract class AbstractWorld implements World {
	
	public final WorldProvider<? extends AbstractWorld> provider;
	/** This world's dimension.
	 * <p>Though doing so is permitted for convenience purposes, it is
	 * ill-advised to interact with this object directly. */
	public final Dimension dimension;
	
	/** All players in the world. Maps IDs -> players' EntityMobs. */
	protected final Map<Integer, EntityMob> players = new HashMap<>(1);
	/** The map of loaded entities in the world. Maps IDs -> Entities.
	 * This is a LinkedHashMap as to allow for consistent iteration. */
	protected final Map<Integer, Entity> entities = new LinkedHashMap<>(64);
	/** The total number of entities which have existed during the lifetime of
	 * the world. When a new entity is created this is incremented and set as
	 * its ID. */
	protected int entityCount = 0;
	/** Entities queued to be added to the world at the end of the tick.
	 * <p>This is a ClearingQueue as entities may be added to a world from
	 * from another dimension, which can be hosted on another thread. */
	private final ClearingQueue<Entity> entitiesToAdd =
			new SynchronizedClearingQueue<>();
	/** Entities queued to be removed from the world at the end of the tick.
	 * <p>Implementation detail: Though it would be cleaner to invoke {@code
	 * destroy()} on entities and let them self-remove while being iterated
	 * over, this does not work when moving an entity to another dimension: if
	 * {@code destroy} is invoked, this would also invalidate its position in
	 * the dimension it is being moved to. */
	private final ClearingLinkedList<Integer> entitiesToRemove =
			new ClearingLinkedList<>();
	/** The number of hostile mobs in the world. */
	protected int hostileMobCount = 0;
	
	/** Stores tile entities for iteration and updating. A loaded tile entity
	 * need not exist in this list if it does not require updates. */
	protected final LightLinkedList<TileEntity> tileEntities =
			new LightLinkedList<>();
	
	/** The list of hitboxes in the world. */
	protected final LightLinkedList<Hitbox> hitboxes =
			new LightLinkedList<>();
	/** The total number of hitboxes which have existed during the lifetime of
	 * the world. */
	protected int hitboxCount = 0;
	
	/** Stores all particles in the world. This should remain empty if this is
	 * a server world. */
	protected final LightLinkedList<Particle> particles =
			new LightLinkedList<Particle>();
	/** The total number of particles which have existed during the lifetime of
	 * the world. */
	protected int particleCount = 0;
	
	/** The x-coordinate the slice in which players initially spawn, in
	 * slice-lengths. */
	protected int spawnSliceX;
	/** The x-coordinate the slice in which players initially spawn, in
	 * slice-lengths. */
	protected int spawnSliceY;
	
	private float timeDelta = 1f;
	private float timeIncrement = timeDelta / Constants.TICKS_PER_SECOND;
	private final float gravity = -3 * 9.8f; // arbitrary, but 9.8 feels too sluggish
	private float gravityIncrement = gravity * timeIncrement;
	private float gravity2ndOrder = gravity * timeIncrement * timeIncrement / 2;
	
	/** An easy-access utility RNG which should be used by any GameObject with
	 * a reference to this world in preference to constructing a new one. */
	public final Random rng = new Random();
	
	/** Use this to profile the world's operation. */
	protected final Profiler profiler;
	protected final Log log;
	
	
	/**
	 * Creates a new AbstractWorld.
	 * 
	 * @param provider This world's provider.
	 * @param dimension The dimension of this world.
	 * 
	 * @throws NullPointerException if either argument is {@code null}.
	 */
	public AbstractWorld(WorldProvider<? extends AbstractWorld> provider, Dimension dimension) {
		this.provider = Objects.requireNonNull(provider);
		this.dimension = Objects.requireNonNull(dimension);
		
		profiler = provider.profiler;
		log = Log.getAgent("World_" + dimension.info.name);
	}
	
	/**
	 * Prepares the world by performing any necessary preemptive loading
	 * operations, such as preparing the spawn regions, etc. Polling {@link
	 * #isLoaded()} allows one to check the status of this operation.
	 * 
	 * @throws IllegalStateException if the world has already been prepared.
	 */
	public abstract void prepare();
	
	/**
	 * Polls the loaded status of the world.
	 * 
	 * @return {@code true} if the world is loaded; {@code false} otherwise.
	 */
	public abstract boolean isLoaded();
	
	/**
	 * Updates the world by executing a single tick of game logic. In general,
	 * all GameObjects in the world will be updated (i.e. entities, hitboxes,
	 * tile entities, etc).
	 */
	@UserThread("MainThread")
	public void update() {
		profiler.start("entity"); // root.update.game.world.entity
		updateObjects(getEntities());
		profiler.next("hitbox"); // root.update.game.world.hitbox
		updateObjects(getHitboxes());
		profiler.next("tileEntity"); // root.update.game.world.tileEntity
		updateObjects(getTileEntities());
		profiler.next("particle"); // root.update.game.world.particle
		updateObjects(getParticles());
		
		// Now, add all queued entities
		profiler.next("entity"); // root.update.game.world.entity
		profiler.start("add"); // root.update.game.world.entity.add
		
		if(!entitiesToAdd.isEmpty()) {
			for(Entity e : entitiesToAdd) {
				e.id = ++entityCount;
				e.onAdd(); 
				entities.put(e.id, e);
			}
		}
		
		profiler.next("remove"); // root.update.game.world.entity.remove
		
		if(!entitiesToRemove.isEmpty())
			for(Integer id : entitiesToRemove)
				entities.remove(id);
		
		profiler.end(); // root.update.game.world.entity
		profiler.end(); // root.update.game.world
	}
	
	/**
	 * Iterates over the specified collection of GameObjects as per {@link
	 * GameObject#updateAndCheck(World)}. GameObjects are removed from the
	 * collection by the iterator if {@code updateAndCheck()} returns {@code
	 * true}.
	 */
	protected <E extends GameObject> void updateObjects(Collection<E> objects) {
		Iterator<E> i = objects.iterator();
		while(i.hasNext())
			if(i.next().updateAndCheck(this))
				i.remove();
	}
	
	/**
	 * Sets a mob as a player. The mob will be treated as if the player is
	 * controlling it thereafter.
	 */
	public void setPlayer(EntityMob m) {
		players.put(m.id, m);
	}
	
	/**
	 * Removes the status of player from a mob. The mob will no longer be
	 * treated as if controlled by a player thereafter.
	 */
	public void unsetPlayer(EntityMob m) {
		players.remove(m.id);
	}
	
	@Override
	@UserThread("AnyDimensionThread")
	public void addEntity(Entity e) {
		entitiesToAdd.add(e);
	}
	
	@Override
	public Entity getEntity(int id) {
		return entities.get(id);
	}
	
	@Override
	public void removeEntity(Entity e) {
		removeEntity(e.id);
	}
	
	@Override
	public void removeEntity(int id) {
		entitiesToRemove.add(id);
	}
	
	@Override
	public void addHitbox(Hitbox h) {
		hitboxCount++;
		hitboxes.add(Objects.requireNonNull(h));
	}
	
	@Override
	public void addParticle(Particle p) {
		particleCount++;
		particles.add(Objects.requireNonNull(p));
	}
	
	/**
	 * Adds a tile entity to the list of tile entities, so that it may be
	 * updated. Note that it will only be added if {@link
	 * TileEntity#requiresUpdates() t.requiresUpdates()} returns {@code true}.
	 * 
	 * @param t The tile entity.
	 * 
	 * @throws NullPointerException if {@code t} is {@code null}.
	 */
	protected void addTileEntity(TileEntity t) {
		if(t.requiresUpdates())
			tileEntities.add(t);
	}
	
	/**
	 * Removes a tile entity from the list of tile entities. It will no longer
	 * be updated.
	 * 
	 * <p>If the tile entity is not present in the list of tile entities, this
	 * method does nothing asides from invoking {@link TileEntity#destroy()}.
	 * 
	 * @throws NullPointerException if {@code t} is {@code null}.
	 */
	protected void removeTileEntity(TileEntity t) {
		// Since it is expensive to directly remove an object from a
		// LinkedList, simply set its destroyed flag to true and have it remove
		// itself upon the next iteration.
		t.destroy();
	}
	
	/**
	 * Removes a tile entity from the map of tile entities. This does not
	 * remove it from its owner slice.
	 * 
	 * @param x The x-coordinate of the tile entity, in tile-lengths.
	 * @param y The y-coordinate of the tile entity, in tile-lengths.
	 */
	protected void removeTileEntity(int x, int y) {
		TileEntity t = getTileEntityAt(x, y);
		if(t != null)
			removeTileEntity(t);
	}
	
	// ==========Collection getters==========
	
	@Override
	public Collection<EntityMob> getPlayers() {
		return players.values();
	}
	
	@Override
	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	@Override
	public Collection<Hitbox> getHitboxes() {
		return hitboxes;
	}
	
	@Override
	public Collection<TileEntity> getTileEntities() {
		return tileEntities;
	}
	
	@Override
	public Collection<Particle> getParticles() {
		return particles;
	}
	
	// ========== Stuff ==========
	
	@Override
	public float getGravity() {
		return gravity;
	}
	
	@Override
	public float getGravityIncrement() {
		return gravityIncrement;
	}
	
	@Override
	public float getGravity2ndOrder() {
		return gravity2ndOrder;
	}
	
	@Override
	public void setTimeDelta(float delta) {
		if(delta <= 0f)
			throw new IllegalArgumentException("delta <= 0: " + delta);
		
		timeDelta = delta;
		
		timeIncrement = delta / Constants.TICKS_PER_SECOND;
		gravityIncrement = gravity * timeIncrement;
		gravity2ndOrder = gravity * timeIncrement * timeIncrement / 2;
	}
	
	@Override
	public float getTimeDelta() {
		return timeDelta;
	}
	
	@Override
	public float getTimeIncrement() {
		return timeIncrement;
	}
	
	// ========== Utils ==========
	
	@Override
	public long getAge() {
		return dimension.info.age;
	}
	
	@Override
	public Random getRnd() {
		return rng;
	}
	
	/**
	 * The name of this world's dimension.
	 */
	public String getDimensionName() {
		return dimension.info.name;
	}
	
	// ========== Lifecycle Methods ==========
	
	/**
	 * Saves the world.
	 */
	public abstract void save();
	
	/**
	 * Closes the world. This method may block for a prolonged period while the
	 * the world is closed if this is a HostWorld.
	 */
	public abstract void close();
	
	// ========== Misc ==========
	
	/**
	 * Attempts to spawn a mob at a given location.
	 * 
	 * @param mob The mob.
	 * @param x The x-coordinate at which to attempt to spawn the mob, in
	 * tile-lengths.
	 * @param y The y-coordinate at which to attempt to spawn the mob, in
	 * tile-lengths.
	 */
	public void spawnMob(EntityMob mob, double x, double y) {
		if(hostileMobCount >= HOSTILE_MOB_CAP)
			return;
		
		// For now, mobs must spawn in a radius from a player in the
		// bounds of 32 <= r <= 128
		boolean inRange = false;		// In range of at least 1 player
		double dx, dy, dist2;
		for(EntityMob p : players.values()) {
			dx = p.x - x;
			dy = p.y - y;
			dist2 = dx*dx + dy*dy;
			
			if(dist2 <= 1024D)//32*32
				return;
			else if(dist2 <= 16384D)//128*128
				inRange = true;
		}
		if(!inRange)
			return;
		
		int minX = Maths.floor(x + mob.boundingBox.v00.x);
		int maxX = Maths.ceil(x + mob.boundingBox.v11.x);
		int minY = Maths.floor(y + mob.boundingBox.v00.y);
		int maxY = Maths.ceil(y + mob.boundingBox.v11.y);
		
		// Check to see if the mob would be spawning in any tiles
		for(int tileX = minX; tileX < maxX; tileX++)
			for(int tileY = minY; tileY < maxY; tileY++)
				if(getTileAt(tileX, tileY).isSolid())
					return;
		addEntity(mob, x, y);
	}
	
	/**
	 * Destroys all non-player-controlled mobs in the world.
	 */
	public void exterminateMobs() {
		for(Entity e : entities.values()) {
			if(e instanceof EntityMob && !((EntityMob)e).isPlayerControlled())
				e.destroy();
		}
	}
	
}
