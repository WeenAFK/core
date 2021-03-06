package com.stabilise.entity.component.physics;

import com.stabilise.entity.Entity;
import com.stabilise.entity.component.Component;
import com.stabilise.entity.event.EntityEvent;
import com.stabilise.util.io.data.DataCompound;
import com.stabilise.world.World;


/**
 * An empty physics component implementation that does not attempt to simulate
 * any physics.
 */
public class CNoPhysics extends CPhysics {
    
    public static final CNoPhysics INSTANCE = new CNoPhysics();
    
    private CNoPhysics() {}
    
    @Override public void init(Entity e) {}
    @Override public void update(World w, Entity e, float dt) {}
    @Override public boolean handle(World w, Entity e, EntityEvent ev) { return false; }
    @Override public int getWeight() { return 0; }
    @Override public Action resolve(Component other) { return Action.REJECT; }
    
    @Override public boolean onGround() { return false; }
    
    @Override public void importFromCompound(DataCompound c) {}
    @Override public void exportToCompound(DataCompound c) {}
    
}
