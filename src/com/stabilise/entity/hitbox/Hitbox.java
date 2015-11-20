package com.stabilise.entity.hitbox;

import java.util.function.Consumer;

import com.stabilise.entity.Entity;
import com.stabilise.entity.FreeGameObject;
import com.stabilise.entity.damage.DamageSource;
import com.stabilise.opengl.render.WorldRenderer;
import com.stabilise.util.shape.Shape;
import com.stabilise.world.World;

/**
 * A Hitbox is an object which, when overlapping with an entity, may damage or
 * otherwise influence it.
 */
public class Hitbox extends FreeGameObject {
    
    /** The entity which owns the hitbox. */
    public final long ownerID;
    /** Whether or not the hitbox is to persist for longer than a tick. */
    public boolean persistent = true;
    /** The number of ticks the hitbox should persist for, if it is persistent.
     * A value of -1 indicates an indefinite length of time. */
    public int persistenceTimer = 2;
    /** The hitbox's bounding volume. */
    public Shape boundingBox;
    /** The number of entities the hitbox may hit. A value less than 0 means
     * the hitbox may hit an unlimited number of entities.<br>
     * <b>Note:</b> a value of 0 can result in the hitbox not colliding with
     * anything, and as such, do <i>NOT</i> set it to 0. */
    public int hits = 1;
    
    /** The damage the hitbox deals. */
    public int damage;
    /** How much force the hitbox applies upon impact. */
    public float force = 0.0f;
    /** The percentage of impact force distributed horizontally/vertically,
     * from -1.0 to 1.0. */
    public float fx = 0.0f, fy = 0.0f;
    /** The number of frames for which a mob hit by the hitbox will be trapped
     * in hitstun. */
    public int freezeFrames = 0;
    
    /** The effect to be applied to the mob the hitbox hits. */
    public Consumer<Entity> effects = null;
    
    
    /**
     * Creates a new Hitbox.
     * 
     * @param owner The Hitbox's owner.
     * @param boundingBox The Hitbox's bounding box.
     * @param damage The damage the hitbox deals.
     */
    public Hitbox(long ownerID, Shape boundingBox, int damage) {
        super();
        this.ownerID = ownerID;
        this.boundingBox = boundingBox;
        this.damage = damage;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>On each update tick, a Hitbox will move to the location of its owner,
     * then iterate through all entities in the world, colliding with any if
     * applicable and resolving the collision appropriately.
     */
    @Override
    public void update(World world) {
        if(isDestroyed())
            return;
        
        moveToOwner(world);
        
        for(Entity e : world.getEntities()) {
            if(e.id() == ownerID) continue;
            // TODO: broadphase
            if(e.aabb.intersects(boundingBox, (float)(e.x-x), (float)(e.y-y))) {
                hit(world, e);
                if(hits == 0)
                    break;
            }
        }
        
        if(!persistent || --persistenceTimer == 0)
            destroy();
    }
    
    /**
     * Moves the hitbox to the location of its owner.
     */
    protected void moveToOwner(World w) {
        Entity e = w.getEntity(ownerID);
        x = e.x;
        y = e.y;
    }
    
    /**
     * Calculates the result of the hitbox colliding with an entity.
     * 
     * @param e The entity.
     * 
     * @return {@code true} if a collision was made; {@code false} if not.
     */
    protected boolean hit(World w, Entity e) {
        // TODO: current implementation of collision resolution is crude and temporary
        if(e.damage(w, createSrc())) {
            onHit();
            return true;
        }
        return false;
    }
    
    private DamageSource createSrc() {
        return new DamageSource(damage, ownerID, fx*force, fy*force) {
            @Override
            public void applyEffects(Entity e) {
                if(effects != null)
                    effects.accept(e);
            }
        };
    }
    
    /**
     * Called when the hitbox successfully collides with something.
     */
    protected void onHit() {
        if(--hits == 0)
            destroy();
    }
    
    @Override
    public void render(WorldRenderer renderer) {
        // hitboxes are not rendered
    }
    
}
