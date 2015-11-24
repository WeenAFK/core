package com.stabilise.entity.particle;

import com.stabilise.util.annotation.NotThreadSafe;
import com.stabilise.util.collect.Array;
import com.stabilise.world.World;


/**
 * Provides a pool of particles of the same type to avoid unnecessary
 * object instantiation and to reduce the strain on the GC.
 */
@NotThreadSafe
public class ParticlePool<T extends Particle> {
    
    /** Functions as the initial and the minimum capacity. */
    private static final int CAPACITY_INITIAL = 16;
    /** Maximum pool capacity. */
    private static final int CAPACITY_MAX = 1024; // 6 expansions
    /** Number of active particles must be this many times the size of
     * the pool to force a resize. */
    private static final int LOAD_FACTOR = 3;
    /** The amount by which the pool's length is multiplied when it is
     * resized. */
    private static final int EXPANSION = 2;
    /** Maximum number of pooled particles retained when the pool is
     * flushed. This must be less than {@link #CAPACITY_INITIAL}. */
    private static final int RETENTION_ON_FLUSH = 8;
    
    
    private final int id;
    
    private final Array<T> pool = new Array<>(CAPACITY_INITIAL);
    /** Number of particles in the pool. Always < pool.length(). */
    private int poolSize = 0;
    /** Number of particles currently in the world which are linked to this
     * pool. */
    private int activeParticles = 0;
    /** If activeParticles exceeds the expansion load, we increase the size
     * of the pool. */
    private int expansionLoad = CAPACITY_INITIAL * LOAD_FACTOR;
    
    
    /**
     * Creates a new pool for particles of the specified class.
     * 
     * @throws IllegalStateException if the given class has not been
     * registered.
     */
    ParticlePool(Class<T> clazz) {
        id = Particle.REGISTRY.getID(clazz);
        if(id == -1)
            throw new IllegalStateException("Particle class " + clazz
                    + " not registered!");
    }
    
    /**
     * Gets a particle from this pool, instantiating a new one if
     * necessary.
     */
    T get() {
        activeParticles++;
        if(poolSize == 0) {
            @SuppressWarnings("unchecked")
            T p = (T)Particle.REGISTRY.create(id);
            p.reset();
            return p;
        }
        return pool.get(--poolSize);
    }
    
    /**
     * Puts a particle in this pool. If this pool is full, this method does
     * nothing. If it is added to this pool, {@code p} is {@link
     * Particle#reset() reset}.
     */
    private void put(T p) {
        // If the pool is full, let the particle get GC'd
        if(poolSize == pool.length())
            return;
        
        p.reset();
        pool.set(poolSize++, p);
    }
    
    /**
     * Reclaims a particle into this pool. This should only be invoked by
     * a particle when it is registered as destroyed (i.e. from within
     * {@link Particle#updateAndCheck(World)}).
     */
    public void reclaim(T p) {
        if(activeParticles-- > expansionLoad && pool.length() < CAPACITY_MAX) {
            pool.resize(EXPANSION * pool.length());
            expansionLoad = pool.length() * LOAD_FACTOR;
        }
        
        put(p);
    }
    
    /**
     * Flushes this pool by garbage-collecting all but a few pooled
     * particles, and shrinking the internal size if necessary. This
     * shouldn't be invoked too frequently as this can be an expensive
     * operation.
     */
    void flush() {
        // Dump all but RETENTION_ON_FLUSH-many pooled particles.
        // TODO: We might want to retain a larger amount if the pool has
        // sufficiently expanded.
        pool.setBetween(null, RETENTION_ON_FLUSH, poolSize);
        // If the pool has been expanded over its initial capacity and the
        // most recent expansion's worth of space is unused, we'll shrink
        // the pool.
        if(pool.length() > CAPACITY_INITIAL &&
                activeParticles < pool.length() / LOAD_FACTOR) {
            //System.out.println("Shrinking the pool from " + pool.length()
            //        + " to " + (pool.length() / EXPANSION));
            pool.resize(pool.length() / EXPANSION);
            expansionLoad = pool.length() * LOAD_FACTOR;
        }
        if(poolSize > RETENTION_ON_FLUSH)
            poolSize = RETENTION_ON_FLUSH;
    }
    
}