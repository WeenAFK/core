package com.stabilise.entity;

import com.stabilise.world.World;

/**
 * A FreeGameObject is a game object whose coordinates are not constrained to
 * the coordinate grid of the world, and generally move freely.
 */
public abstract class FreeGameObject extends GameObject {
	
	/** The GameObject's coordinates, in tile-lengths */
	public double x, y;
	
	
	/**
	 * Creates a new FixedGameObject.
	 */
	public FreeGameObject() {
		// nothing to see here, move along
	}
	
	/**
	 * Creates a new GameObject.
	 * 
	 * @param world The world.
	 */
	public FreeGameObject(World world) {
		super(world);
	}
	
	@Override
	public final int getSliceX() {
		return World.sliceCoordFromTileCoord(x);
	}
	
	@Override
	public final int getSliceY() {
		return World.sliceCoordFromTileCoord(y);
	}

}
