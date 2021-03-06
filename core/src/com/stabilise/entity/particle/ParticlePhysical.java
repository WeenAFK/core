package com.stabilise.entity.particle;

import com.stabilise.world.World;

/**
 * A physical particle (for want of a better name) is a particle with a dx and
 * dy.
 */
public abstract class ParticlePhysical extends Particle {
    
    /** The particle's velocity along the x/y-axes. */
    public float dx, dy;
    
    
    @Override
    protected void update(World world, float dt) {
        super.update(world, dt);
        
        // Crude euler integration.
        pos.addX(dx * world.getTimeIncrement());
        pos.addY(dy * world.getTimeIncrement());
        
        // Not such a big deal if we're not slice-aligned, so don't bother.
        // pos.realign();
    }
    
    @Override
    public void reset() {
        super.reset();
        dx = dy = 0f;
    }
    
}
