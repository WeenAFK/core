package com.stabilise.world;

import java.util.Collection;

import com.stabilise.character.CharacterData;
import com.stabilise.entity.Entity;
import com.stabilise.entity.EntityMob;
import com.stabilise.entity.particle.Particle;
import com.stabilise.util.annotation.Incomplete;
import com.stabilise.world.dimension.Dimension;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * The foundation of a world as viewed by a client connected to a server. An
 * instance of this class should be wrapped in a {@link ClientWorld}.
 * 
 * <p>TODO: Interactions with server
 * <p>TODO: This entire class in general
 */
@Incomplete
public class MultiplayerClientWorld extends BaseWorld implements IClientWorld {
	
	/** Holds slices provided by the server. */
	public final SliceMapClient slices = new SliceMapClient();
	
	
	public MultiplayerClientWorld(WorldProvider provider, Dimension dimension) {
		super(provider, dimension);
	}
	
	@Override
	public void prepare() {
		// TODO
	}
	
	public void addClientPlayer(CharacterData data, EntityMob mob) {
		// TODO
	}
	
	public void saveClientPlayer(CharacterData data, EntityMob mob) {
		// don't really do anything if we're the client...
	}
	
	@Override
	public boolean isLoaded() {
		return slices.isLoaded();
	}
	
	@Override
	public void update() {
		super.update();
		
		profiler.start("sliceMap");
		slices.update();
		profiler.end();
	}
	
	@Override
	public void addParticle(Particle p) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<Particle> getParticles() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Slice getSliceAt(int x, int y) {
		return slices.getSlice(x, y);
	}
	
	@Override
	public void setTileAt(int x, int y, int id) {
		
	}
	
	@Override
	public void breakTileAt(int x, int y) {
		
	}
	
	@Override
	public void setTileEntityAt(int x, int y, TileEntity t) {
		Slice slice = getSliceAtTile(x, y);
			
		if(slice != null) {
			int tileX = tileCoordRelativeToSliceFromTileCoord(x);
			int tileY = tileCoordRelativeToSliceFromTileCoord(y);
			
			TileEntity t2 = slice.getTileEntityAt(tileX, tileY);
			if(t2 != null) {
				t2.handleRemove(this, x, y);
				removeTileEntity(t2);
			}
			
			slice.setTileEntityAt(tileX, tileY, t);
			
			addTileEntity(t);
		}
	}
	
	@Override
	public void removeTileEntityAt(int x, int y) {
		
	}
	
	@Override
	public void blowUpTile(int x, int y, float explosionPower) {
		
	}
	
	@Override
	public void save() {
		// multiplayer clients don't save
	}
	
	@Override
	public void close() {
		// just let the GC do stuff
	}

	@Override
	public void sendToDimension(String dimension, Entity e, double x, double y) {
		// TODO Auto-generated method stub
		
	}
	
}
