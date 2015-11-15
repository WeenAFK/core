package com.stabilise.entity.old;
//package com.stabilise.entity;
//
//import com.badlogic.gdx.math.MathUtils;
//import com.stabilise.entity.collision.Hitbox;
//import com.stabilise.entity.old.EntityOld;
//import com.stabilise.util.shape.Shape;
//import com.stabilise.world.World;
//
///**
// * A projectile is an entity with an associative hitbox.
// */
//public abstract class EntityProjectile extends EntityOld {
//    
//    /** TODO: Temporary means of referencing a world so we can add particles
//     * to the world in destroy(). FIND A BETTER WAY TO DO THIS */
//    protected World world;
//    
//    /** The projectile's owner. */
//    public EntityOld owner;
//    /** The projectile's hitbox. */
//    public Hitbox hitbox;
//    
//    private final Shape baseShape;
//    /** The rotation of the projectile. */
//    public float rotation = 0;            // TODO: Is this necessary for /all/ projectile types?
//    
//    
//    /**
//     * Creates a new projectile entity.
//     * 
//     * @param world The world in which the projectile will be placed.
//     * @param owner The projectile's owner.
//     * @param hitbox The projectile's hitbox.
//     */
//    public EntityProjectile(World world, EntityOld owner, Hitbox hitbox) {
//        super();
//        
//        this.owner = owner;
//        this.hitbox = hitbox;
//        
//        //hitbox.owner = owner;
//        //hitbox.x = x;
//        //hitbox.y = y;
//        
//        // Since projectiles typically rotate...
//        this.baseShape = hitbox.boundingBox;
//        hitbox.persistent = true;
//        
//        world.addHitbox(hitbox, x, y);
//        
//        this.world = world;
//    }
//    
//    @Override
//    public void update(World world) {
//        if(destroyed) {
//            hitbox.destroy();
//            return;
//        }
//        
//        super.update(world);
//        
//        rotate();
//    }
//    
//    /**
//     * Updates the projectile's rotation.
//     */
//    protected void rotate() {
//        rotation = MathUtils.atan2(dy, dx); //(float)Math.atan2(dy, dx);
//        
//        if(hitbox != null)
//            hitbox.boundingBox = baseShape.rotate(rotation);
//        
//        setFacingRight(dx > 0);
//    }
//    
//    /**
//     * {@inheritDoc}
//     * 
//     * <p>This method also invokes {@code destroy()} on this projectile's
//     * linked hitbox, if it is non-null.
//     */
//    @Override
//    public void destroy() {
//        super.destroy();
//        if(hitbox != null)
//            hitbox.destroy();
//    }
//    
//}