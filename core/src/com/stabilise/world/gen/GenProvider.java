package com.stabilise.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.RandomXS128;
import com.stabilise.entity.Entity;
import com.stabilise.entity.Position;
import com.stabilise.world.HostWorld;
import com.stabilise.world.Region;
import com.stabilise.world.Slice;
import com.stabilise.world.WorldProvider;
import com.stabilise.world.gen.action.*;
import com.stabilise.world.tile.Tile;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * WorldProvider implementation which is passed to generators as to give them
 * a WorldProvider-y view on the world.
 */
class GenProvider implements WorldProvider {
    
    @SuppressWarnings("unused")
    private final HostWorld w;
    private final Region r;
    
    private int lastX, lastY;
    private Slice lastSlice;
    
    private final Random rnd;
    
    
    GenProvider(HostWorld w, Region r, long seed) {
        this.w = w;
        this.r = r;
        
        lastX = lastY = 0;
        lastSlice = r.getSliceAt(0, 0);
        
        long mix = 0x59c5180355b14d9bL;
        long n = 13*r.x() + 57*r.y();
        n = (n<<13) ^ n;
        n = n * (n*n*15731 + 789221) + 1376312589;
        rnd = new RandomXS128(seed ^ n ^ mix);
    }
    
    @Override
    public void addEntity(Entity e) {
        ActionAddEntity a = new ActionAddEntity();
        a.e = e;
        actions().add(a);
    }
    
    @Override
    public void addEntityDontSetID(Entity e) {
        throw new UnsupportedOperationException("idk how a generator would pull"
                + " this off, so for now I'm just forbidding it");
    }
    
    @Override
    public Slice getSliceAt(int x, int y) {
        if(x == lastX && y == lastY) return lastSlice;
        if(!checkXBound(x) || !checkYBound(y))
            throw new IllegalArgumentException("Accessing outside region (" + x + "," + y + ")!");
        lastX = x;
        lastY = y;
        return lastSlice = r.getSliceAt(
                Position.sliceCoordRelativeToRegionFromSliceCoord(x),
                Position.sliceCoordRelativeToRegionFromSliceCoord(y));
    }
    
    /*
    @Override
    public void setTileAt(int x, int y, int id) {
        getTileAt(x, y).handleRemove(this, x, y);
        getSliceAtTile(x,y).setTileIDAt(
                Position.tileCoordRelativeToSliceFromTileCoord(x),
                Position.tileCoordRelativeToSliceFromTileCoord(y),
                id);
        Tile.getTile(id).handlePlace(this, x, y);
    }
    */
    
    /*
    @Override
    public void setTileEntityAt(int x, int y, TileEntity t) {
        getSliceAtTile(x, y).setTileEntityAt(
                Position.tileCoordRelativeToSliceFromTileCoord(x),
                Position.tileCoordRelativeToSliceFromTileCoord(y),
                t);
        ActionAddTileEntity a = new ActionAddTileEntity();
        a.t = t;
        actions().add(a);
    }
    */
    
    @Override
    public void setTileAt(Position pos, int id) {
        getTileAt(pos).handleRemove(this, pos);
        getSliceAt(pos).setTileIDAt(pos.ltx(), pos.lty(), id);
        Tile.getTile(id).handlePlace(this, pos);
    }
    
	@Override
	public void setTileEntityAt(Position pos, TileEntity t) {
	    t.pos.set(pos);
		doSetTileEntity(pos, t);
	}
	
	@Override
	public void removeTileEntityAt(Position pos) {
		doSetTileEntity(pos, null);
	}
	
    /**
     * @param pos never null
     * @param t may be null -- null means remove whatever TE is there
     */
    private void doSetTileEntity(Position pos, TileEntity t) {
        Slice s = getSliceAt(pos);
        
        if(!s.isDummy()) {
            int tx = pos.ltx();
            int ty = pos.lty();
            
            TileEntity t2 = s.getTileEntityAt(tx, ty);
            if(t2 != null) {
            	// TODO
            	//t2.handleRemove(this, pos);
                //removeTileEntity(t2);
            }
            
            s.setTileEntityAt(tx, ty, t);
            
            if(t != null) {
            	ActionAddTileEntity a = new ActionAddTileEntity();
            	a.t = t;
            	actions().add(a);
                //addTileEntity(t);
            }
        }
    }
    
    @Override
    public Random rnd() {
        return rnd;
    }
    
    private List<Action> actions() {
        if(r.queuedActions == null)
            r.queuedActions = new ArrayList<>();
        return r.queuedActions;
    }
    
    //private boolean checkBounds(int c) {
    //    return c >= 0 && c < Region.REGION_SIZE_IN_TILES;
    //}
    
    private boolean checkXBound(int x) {
        return x >= r.offsetX && x < r.offsetX + Region.REGION_SIZE;
    }
    
    private boolean checkYBound(int y) {
        return y >= r.offsetY && y < r.offsetY + Region.REGION_SIZE;
    }
    
}
