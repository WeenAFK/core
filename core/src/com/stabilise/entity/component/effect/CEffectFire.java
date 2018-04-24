package com.stabilise.entity.component.effect;

import com.stabilise.core.Constants;
import com.stabilise.core.Settings;
import com.stabilise.entity.Entity;
import com.stabilise.entity.component.Component;
import com.stabilise.entity.damage.GeneralSource;
import com.stabilise.entity.event.EntityEvent;
import com.stabilise.entity.particle.ParticleFlame;
import com.stabilise.util.maths.Maths;
import com.stabilise.world.World;

/**
 * The effect a Mob has when it is on fire.
 * 
 * <p>A mob which is on fire will take 2 damage a second and constantly produce
 * flame particles.
 */
public class CEffectFire extends CParticleEffect<ParticleFlame> {
    
    private int damage;
    private int extra = 1;
    
    
    /**
     * Creates a new fire effect.
     * 
     * @param duration The duration of the effect, in ticks.
     */
    public CEffectFire(int duration, int damage) {
        super(duration);
        this.damage = damage;
    }
    
    @Override
    public void init(Entity e) {}
    
    @Override
    protected Class<ParticleFlame> particleClass() {
        return ParticleFlame.class;
    }
    
    @Override
    public void update(World w, Entity e) {
        super.update(w, e);
        
        if(Settings.settingParticlesAll())
            createFireParticle(w, e);
        else if(Settings.settingParticlesReduced() && age % 3 == 0)
            createFireParticle(w, e);
        
        if(age % Constants.TICKS_PER_SECOND == 0)
            e.damage(w, GeneralSource.fire(damage + w.rnd().nextInt(extra)));
    }
    
    /**
     * Creates a fire particle.
     * 
     * @param target The target of the effect.
     */
    private void createFireParticle(World world, Entity target) {
        particleSrc.createBurst(1, 0.2f, 2.0f, Maths.PIf / 6.0f,
                Maths.PIf * 5.0f / 6.0f, target);
    }
    
    @Override
    public boolean handle(World w, Entity e, EntityEvent ev) {
        return false;
    }
    
    @Override
    public Action resolve(Component c) {
        CEffectFire e = (CEffectFire)c; // c is guaranteed to be equal()
        duration += e.duration;
        damage += e.damage;
        extra++;
        return Action.REJECT;
    }
    
}