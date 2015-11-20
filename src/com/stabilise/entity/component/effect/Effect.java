package com.stabilise.entity.component.effect;

import com.stabilise.entity.component.Component;

/**
 * An Effect is a lingering condition which can affect a mob.
 */
public abstract class Effect implements Component {
    
    /** The duration of the effect, in ticks. */
    public final int duration;
    /** The age of the effect, in ticks. */
    public int age = 0;
    
    
    /**
     * Creates a new Effect.
     * 
     * @param duration The duration of the effect.
     */
    protected Effect(int duration) {
        if(duration < 1)
            throw new IllegalArgumentException("The duration of an effect must be a positive number!");
        
        this.duration = duration;
    }
    
    @Override
    public boolean remove() {
        return ++age >= duration;
    }
    
    /**
     * Clones the Effect.
     * 
     * @return The clone.
     */
    public abstract Effect clone();
    
}