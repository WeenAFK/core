package com.stabilise.entity.component.physics;

import com.stabilise.entity.Entity;
import com.stabilise.entity.Position;
import com.stabilise.entity.event.ETileCollision;
import com.stabilise.entity.event.EntityEvent;
import com.stabilise.util.Checks;
import com.stabilise.util.Direction;
import com.stabilise.util.io.data.DataCompound;
import com.stabilise.util.maths.Maths;
import com.stabilise.world.World;
import com.stabilise.world.tile.Tile;

/**
 * Extremely crappy physics implementation
 */
public class CPhysicsImpl2 extends CPhysics {
    
    protected static final float AIR_FRICTION = 0.001f;
    
    
    public float dxi, dyi; // dx, dy integrals
    public boolean dxp, dyp; // dx/dy positive
    public boolean onGround;
    public int floorTile;
    private final Position projPos = Position.create(); // for update()
    private final Position tmp1 = Position.createFixed(); // for horizontalCollisions and verticalCollisions
    private final Position tmp2 = Position.create(); // for update() to give to horizontalCollisions and verticalCollisions
    private final Position tmp3 = Position.createFixed(); // for rowValid and columnValid
    private final Position tmp4 = Position.createFixed(); // for collideVertical
    
    
    @Override
    public void init(Entity e) {}
    
    @Override
    public void update(World w, Entity e) {
        //if(dx != 0)
        //    dx *= (1-friction);
        
        dxi = e.dx * w.getTimeIncrement();
        dyi = e.dy * w.getTimeIncrement() + w.getGravity2ndOrder();
        
        dxp = dxi > 0;
        dyp = dyi > 0;
        
        onGround = false;
        
        if(dxi > 1.0f || dxi < -1.0f || dyi > 1.0f || dyi < -1.0f) {
            int divisor = Maths.ceil(Maths.max(Math.abs(dxi), Math.abs(dyi)));
            float xInc = dxi / divisor;   // x increments
            float yInc = dyi / divisor;   // y increments
            projPos.set(e.pos, xInc, yInc);
            boolean xCollided = false;
            boolean yCollided = false;
            
            for(int i = 0; i < divisor; i++) {
                if(!yCollided)
                    yCollided = verticalCollisions(w, e, tmp2.set(projPos));
                if(!xCollided)
                    xCollided = horizontalCollisions(w, e, tmp2.set(projPos));
                projPos.add(xInc, yInc);
            }
        } else {
            projPos.set(e.pos, dxi, dyi);
            
            verticalCollisions(w, e, projPos);
            
            // The following is necessary because otherwise gravity will offset the vertical
            // wall being checked for sideways collisions slightly when on the ground.
            horizontalCollisions(w, e, projPos.set(e.pos, dxi, dyi));
        }
        
        e.pos.add(dxi, dyi).align();
        
        e.dy += w.getGravityIncrement(); // apply after updating y
        
        e.dx *= getXFriction(w, e);
        e.dy *= getYFriction(w, e);
    }
    
    /**
     * Gets the frictive force acting on the entity.
     */
    protected float getXFriction(World w, Entity e) {
        Tile groundTile = w.getTileAt(tmp1.set(e.pos).addY(-1).alignY());
        return 1 - groundTile.getFriction();
    }
    
    /**
     * Gets the vertical frictive force acting on the entity.
     * 
     * @return The frictive force.
     */
    protected float getYFriction(World w, Entity e) {
        return 1f;
    }
    
    /**
     * Gets the air friction.
     * 
     * @return The air friction acting on the entity.
     */
    protected final float getAirFriction() {
        // TODO: Possibly a temporary method
        return AIR_FRICTION;
    }
    
    /**
     * Gets the friction of the tile the entity is standing on.
     */
    protected float getTileFriction() {
        if(onGround)
            return Tile.getTile(floorTile).getFriction();
        else
            return 0;
    }
    
    /**
     * Tests for all horizontal collisions.
     * 
     * @param proj Projected position.
     * 
     * @return {@code true} if a collision is detected.
     */
    private boolean horizontalCollisions(World w, Entity e, Position proj) {
        if(dxi == 0) return false;
        
        float leadingEdge = dxp ? e.aabb.maxX() : e.aabb.minX();
        proj.addX(leadingEdge);
        
        // If the vertical wall is the same wall as the one the entity is
        // currently occupying, don't bother checking
        if(dxp ? Math.ceil(proj.lx()) == Math.ceil(e.pos.lx() + leadingEdge)
               : Math.floor(proj.lx()) == Math.floor(e.pos.lx() + leadingEdge))
        //if(Math.floor(proj.lx) == Math.floor(e.pos.lx + leadingEdge))
            return false;
        
        // Check the vertical wall of tiles to the left/right of the entity
        int max = Maths.floor(proj.ly() + e.aabb.maxY());
        
        for(int v = Maths.floor(proj.ly() + e.aabb.minY()); v <= max; v++) {
        	tmp1.set(proj.sx, proj.sy, proj.lx(), v).align();
            if(w.getTileAt(tmp1).isSolid() && rowValid(w, e, tmp1)) {
                collideHorizontal(w, e, tmp1, dxp ? Direction.RIGHT : Direction.LEFT);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Tests for all vertical collisions.
     * 
     * @param proj The entity's projected position.
     * 
     * @return {@code true} if a collision is detected.
     */
    private boolean verticalCollisions(World w, Entity e, Position proj) {
        if(dyi == 0.0f) return false;
        
        float leadingEdge = dyp ? e.aabb.maxY() : e.aabb.minY();
        proj.addY(leadingEdge);
        
        // If the horizontal wall is the same as the one the entity is
        // currently occupying, don't bother checking.
        if(dyp ? Maths.ceil(proj.ly()) == Maths.ceil(e.pos.ly() + leadingEdge)
               : Maths.floor(proj.ly()) == Maths.floor(e.pos.ly() + leadingEdge))
        //if(Maths.floor(proj.ly) == Maths.floor(e.pos.ly + leadingEdge))
            return false;
        
        // Check the horizontal wall of tiles at the top/bottom of the entity
        int max = Maths.ceil(proj.lx() + e.aabb.maxX());
        
        for(int h = Maths.floor(proj.lx() + e.aabb.minX()); h < max; h++) {
        	tmp1.set(proj.sx, proj.sy, h, proj.ly()).align();
            if(w.getTileAt(tmp1).isSolid() && columnValid(w, e, tmp1)) {
                collideVertical(w, e, tmp1, dyp ? Direction.UP : Direction.DOWN);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if a column of tiles above or below (depending on the
     * entity's vertical velocity) a given tile are valid tiles for the entity
     * to move into (that is, are non-solid).
     * 
     * @param pos The position of the tile to check about
     * 
     * @return {@code true} if and only if the entity is able to move into the
     * column.
     */
    private boolean columnValid(World w, Entity e, Position pos) {
        // Only check as many tiles above or below the tile in question that
        // the height of the entity's bounding box would require.
        int max = Maths.ceil(e.aabb.height());
        for(int i = 1; i <= max; i++) {
            if(w.getTileAt(tmp3.set(pos).add(0f, dyp ? -i : i).align()).isSolid())
                return false;
        }
        return true;
    }
    
    /**
     * Returns true if a row of tiles to the left or right of (depending on the
     * entity's horizontal velocity) a given tile are valid tiles for the
     * entity to move into (that is, are non-solid).
     * 
     * @param pos The position of the tile to check about.
     * 
     * @return {@code true} if and only if the entity is able to move into the
     * row.
     */
    private boolean rowValid(World w, Entity e, Position pos) {
        // Only check as many tiles to the left or right of the tile in
        // question that the width of the entity's bounding box would require.
        int max = Maths.ceil(e.aabb.width());
        for(int i = 1; i <= max; i++) {
            if(w.getTileAt(tmp3.set(pos).add(dxp ? -i : i, 0f).align()).isSolid())
                return false;
        }
        return true;
    }
    
    /**
     * Causes the entity to horizontally collide with a tile.
     * 
     * @param collisionPos The position at which the collision is to be made.
     * Only the x-coord matters here.
     * @param direction The direction relative to the entity that the tile the
     * entity has collided with is located.
     */
    private void collideHorizontal(World w, Entity e, Position collisionPos, Direction direction) {
        //e.post(w, ETileCollision.collision(e.dx));
        e.post(w, ETileCollision.collisionH(e.dx));
        
        e.dx = dxi = 0;
        
        e.pos.sx = collisionPos.sx;
        if(direction == Direction.RIGHT)
        	e.pos.setLx(Maths.floor(collisionPos.lx()) - e.aabb.maxX());
        else
        	e.pos.setLx(Maths.ceil(collisionPos.lx()) + 1 - e.aabb.minX());
    }
    
    /**
     * Causes the entity to vertically collide with a tile.
     * 
     * @param collisionPos The position at which the collision is to be made.
     * Only the y-coord matters here.
     * @param direction The direction relative to the entity that the tile the
     * entity has collided with is located.
     */
    private void collideVertical(World w, Entity e, Position collisionPos, Direction direction) {
        //e.post(w, ETileCollision.collision(e.dy));
        e.post(w, ETileCollision.collisionV(e.dy));
        
        e.dy = dyi = 0;
        
        e.pos.sy = collisionPos.sy;
        if(direction == Direction.UP) {
        	e.pos.setLy(Maths.floor(collisionPos.ly()) - e.aabb.maxY());
        } else {
        	e.pos.setLy(Maths.ceil(collisionPos.ly()) + 1 - e.aabb.minY());
            
        	tmp4.set(e.pos).addY(-1).alignY();
        	Tile t = w.getTileAt(tmp4);
        	t.handleStep(w, tmp4, e);
        	floorTile = t.getID();
        	onGround = true;
        }
    }
    
    @Override public boolean onGround() { return onGround; }
    
    @Override
    public boolean handle(World w, Entity e, EntityEvent ev) {
        return false;
    }
    
    @Override
    public void importFromCompound(DataCompound c) {
        Checks.TODO(); // TODO
    }
    
    @Override
    public void exportToCompound(DataCompound c) {
        Checks.TODO(); // TODO
    }
    
}
