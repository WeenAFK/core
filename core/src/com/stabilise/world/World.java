package com.stabilise.world;

import com.stabilise.entity.Entity;
import com.stabilise.entity.Position;
import com.stabilise.entity.hitbox.Hitbox;
import com.stabilise.entity.particle.Particle;
import com.stabilise.entity.particle.ParticleManager;
import com.stabilise.entity.particle.ParticleSource;
import com.stabilise.util.Profiler;
import com.stabilise.util.collect.FunctionalIterable;
import com.stabilise.world.multiverse.Multiverse;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * Defines methods which summarise a world implementation.
 */
public interface World extends WorldProvider {
    
    /** The file name of the world info file. */
    public static final String FILE_INFO = "info";
    /** The name of the directory relative to the world dir in which dimension
     * data is stored. */
    public static final String DIR_DIMENSIONS = "dimensions/";
    /** The name of the directory in which data about individual players is to
     * be stored. */
    public static final String DIR_PLAYERS = "players/";
    /** The file extension for player data files. */
    public static final String EXT_PLAYERS = ".player";
    
    /** The maximum number of hostile mobs which may spawn.
     * <p>TODO: Arbitrary, and probably temporary. */
    public static final int HOSTILE_MOB_CAP = 100;
    
    
    /**
     * Gets the Multiverse to which this World belongs.
     */
    Multiverse<?> multiverse();
    
    /**
     * Gets the entity with the specified ID.
     * 
     * @return The entity with the specified ID, or {@code null} if there is no
     * such entity in the world.
     */
    Entity getEntity(long id);
    
    /**
     * Removes an entity from the world.
     * 
     * <p>The entity is not removed from the world immediately; rather, it is
     * removed at the end of the current tick.
     * 
     * <p>Note that it is normally preferable to invoke {@link Entity#destroy()
     * destroy()} on an entity to remove it from the world.
     * 
     * @param e The entity.
     * 
     * @throws NullPointerException if {@code e} is {@code null}.
     */
    default void removeEntity(Entity e) {
        removeEntity(e.id());
    }
    
    /**
     * Removes an entity from the world.
     * 
     * <p>The entity is not removed from the world immediately; rather, it is
     * removed at the end of the current tick.
     * 
     * <p>Note that it is normally preferable to invoke {@link Entity#destroy()
     * destroy()} on an entity to remove it from the world.
     * 
     * @param id The ID of the entity.
     */
    void removeEntity(long id);
    
    /**
     * Adds a hitbox to the world. The hitbox's ID is assigned automatically.
     * 
     * @param h The hitbox.
     * @param pos The position at which to place the hitbox.
     * 
     * @throws NullPointerException if either argument is {@code null}.
     */
    default void addHitbox(Hitbox h, Position pos) {
        h.pos.set(pos);
        addHitbox(h);
    }
    
    /**
     * Adds a hitbox to the world. The hitbox's ID is assigned automatically.
     * 
     * @param h The hitbox.
     * 
     * @throws NullPointerException if {@code h} is {@code null}.
     */
    void addHitbox(Hitbox h);
    
    // ==========Collection getters==========
    
    /**
     * @return The collection of all players in the world. Note that as a
     * player is an entity, every element in the returned collection is also
     * a member of the one returned by {@link #getEntities()}.
     */
    FunctionalIterable<Entity> getPlayers();
    
    /**
     * @return The collection of entities in the world.
     */
    FunctionalIterable<Entity> getEntities();
    
    /**
     * @return The collection of hitboxes in the world.
     */
    FunctionalIterable<Hitbox> getHitboxes();
    
    /**
     * @return The collection of particles in the world.
     */
    FunctionalIterable<Particle> getParticles();
    
    /**
     * @return The collection of {@link TileEntity.Updated updated} tile
     * entities in the world.
     */
    FunctionalIterable<TileEntity> getTileEntities();
    
    /**
     * Gets this world's particle manager.
     */
    ParticleManager getParticleManager();
    
    /**
     * Gets a {@code ParticleSource} for particles of the specified type. This
     * method is equivalent to - and provided as a convenient alternative for -
     * {@link #getParticleManager()}{@code .getSource(particleClass)}.
     */
    default <T extends Particle> ParticleSource<T> particleSource(Class<T> particleClass) {
        return getParticleManager().getSource(particleClass);
    }
    
    /**
     * Gets this world's camera.
     */
    WorldCamera getCamera();
    
    // ==========World component getters and setters==========
    
    /**
     * Breaks a tile.
     * 
     * @param pos The position of the tile to break.
     */
    void breakTileAt(Position pos);
    
    /**
     * Adds a tile entity to the "update list" of tile entities, so that it may
     * be updated as per {@link TileEntity#updateAndCheck(World)} every tick.
     * The supplied tile entity will only be added if {@link
     * TileEntity#requiresUpdates()} returns {@code true}. To remove a tile
     * entity from the update list, either {@link TileEntity#destroy() destroy}
     * it, or invoke {@link #removeTileEntity(TileEntity)}.
     * 
     * <p>Note that if the supplied tile entity is already on the update list,
     * it will be added again, and hence updated multiple times per tick!
     * 
     * @param t The tile entity.
     * 
     * @throws NullPointerException if {@code t} is {@code null}.
     */
    void addTileEntity(TileEntity t);
    
    /**
     * Removes a tile entity from the "update list" of tile entities. It will
     * no longer be updated.
     * 
     * <p>If the tile entity is not present in the list of tile entities, this
     * method does nothing asides from invoking {@link TileEntity#destroy()}.
     * 
     * <p>A technical point: {@code t} is not removed from the update list
     * immediately; rather, it is removed by {@link Iterator#shouldRemove()} while
     * iterating over the update list iff {@link
     * TileEntity#updateAndCheck(World)} returns {@code true} (which it should,
     * as this method invokes {@code t.destroy()}).
     * 
     * @throws NullPointerException if {@code t} is {@code null}.
     */
    default void removeTileEntity(TileEntity t) {
        // Since it is expensive to find and remove an object from a list,
        // simply set its destroyed flag and have it remove itself upon the 
        // next iteration.
        if(t.requiresUpdates()) // not actually a necessary check
            t.destroy();
    }
    
    /**
     * Attempts to blow up a tile.
     * 
     * @param pos The position of the tile.
     * @param explosionPower The power of the explosion.
     */
    void blowUpTile(Position pos, float explosionPower);
    
    // ========== Dimensional stuff ==========
    
    /**
     * Sends an entity to the specified dimension.
     * 
     * @param dimension The name of the dimension to which to send the entity.
     * @param e The entity.
     * @param x The x-coordinate at which to place the entity, in tile-lengths.
     * @param y The y-coordinate at which to place the entity, in tile-lengths.
     * 
     * @throws NullPointerException if either argument is {@code null}.
     */
    void sendToDimension(String dimension, Entity e, double x, double y);
    
    // ========== Time delta stuff ==========
    
    /**
     * Returns the gravity of the world, in ts<sup><font size=-1>-2</font>
     * </sup> (tiles per second squared).
     */
    float getGravity();
    
    /**
     * Returns the gravity increment per update tick.
     * 
     * @return {@code gt}, where {@code g == }{@link #getGravity()} and {@code
     * t == }{@link #getTimeIncrement()}.
     */
    float getGravityIncrement();
    
    /**
     * Returns the 2<sup><font size=-1>nd</font></sup>-order value for gravity
     * with respect to time. This should be added to every non-grounded
     * entity's y-coordinate each tick.
     * 
     * @return <tt>gt<sup><font size=-1>2</font></sup>/2</tt>, where {@code g
     * == }{@link #getGravity()} and {@code t == }{@link #getTimeIncrement()}.
     */
    float getGravity2ndOrder();
    
    /**
     * Sets the world's time delta, where a value of {@code 1} is considered
     * normal.
     * 
     * <p>For example, passing {@code 2} to this method will in general cause
     * the world to update twice as quickly, and passing {@code 0.5} will cause
     * everything to slow down to half as quickly.
     * 
     * @throws IllegalArgumentException if {@code delta <= 0}.
     */
    void setTimeDelta(float delta);
    
    /**
     * @return The world's time delta.
     */
    float getTimeDelta();
    
    /**
     * @return The time increment of each update tick, in seconds.
     */
    float getTimeIncrement();
    
    /**
     * @return The age of this world, in ticks.
     */
    long getAge();
    
    // ========== Utility Methods ==========
    
    /**
     * @return {@code true} if this is a {@code HostWorld}; {@code false}
     * otherwise.
     */
    default boolean isHost() {
        return this instanceof HostWorld;
    }
    
    /**
     * Returns {@code true} if this {@code World} holds a client view. This is
     * {@code true} for all cases but for a server world without an integrated
     * client.
     */
    boolean isClient();
    
    /**
     * Returns {@code true} if this world has particles; that is, if this is a
     * client world.
     */
    //boolean hasParticles();
    
    /**
     * Returns the world profiler.
     */
    Profiler profiler();
    
}
