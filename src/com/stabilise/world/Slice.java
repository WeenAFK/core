package com.stabilise.world;

import com.stabilise.world.tile.Tile;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * A slice represents a 16x16-tile chunk of the world.
 */
public class Slice {
	
	//--------------------==========--------------------
	//-----=====Static Constants and Variables=====-----
	//--------------------==========--------------------
	
	/** The length of an edge of the square of tiles in a slice. */
	public static final int SLICE_SIZE = 16;
	/** {@link SLICE_SIZE} - 1; minor optimisation purposes. */
	public static final int SLICE_SIZE_MINUS_ONE = 15;
	/** The power of 2 of {@link SLICE_SIZE}; minor optimisation purposes. */
	public static final int SLICE_SIZE_SHIFT = 4;
	
	//--------------------==========--------------------
	//-------------=====Member Variables=====-----------
	//--------------------==========--------------------
	
	/** A reference to the slice's parent region. */
	public final Region region;
	
	/** The slice's x-coordinate, in slice-lengths. */
	public final int x;
	/** The slice's y-coordinate, in slice-lengths. */
	public final int y;
	
	/** The tiles within the slice.
	 * <p>Note that tiles are indexed in the form [y][x]. */
	public int[][] tiles;
	
	/** The number of tile entities in the slice. */
	public int numTileEntities = 0;
	/** The tile entities within the slice.
	 * <p>Note that they are indexed in the form [y][x].*/
	public TileEntity[][] tileEntities;
	
	
	/**
	 * Creates a new slice.
	 * 
	 * @param x The x-coordinate of the slice, in slice-lengths.
	 * @param y The y-coordinate of the slice, in slice-lengths.
	 * @param region The slice's parent region.
	 */
	public Slice(int x, int y, Region region) {
		this(x, y, region, null);
	}
	
	/**
	 * Creates a new slice.
	 * 
	 * @param x The x-coordinate of the slice, in slice-lengths.
	 * @param y The y-coordinate of the slice, in slice-lengths.
	 * @param region The slice's parent region.
	 * @param tiles The slice's tiles.
	 */
	public Slice(int x, int y, Region region, int[][] tiles) {
		this.x = x;
		this.y = y;
		this.region = region;
		this.tiles = tiles;
		
		tileEntities = new TileEntity[SLICE_SIZE][SLICE_SIZE];
	}
	
	/**
	 * Gets a tile from the specified coordinates.
	 * 
	 * @param x The x-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param y The y-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * 
	 * @return The tile at the specified coordinates.
	 * @throws ArrayIndexOutOfBoundsException Thrown if either x or y is
	 * negative or greater than 15.
	 */
	public Tile getTileAt(int x, int y) {
		//System.out.println("Getting tile: " + x + "," + y + " from slice " + this.x + "," + this.y);
		return Tile.getTile(tiles[y][x]);
	}
	
	/**
	 * Sets a tile in the slice.
	 * 
	 * @param x The x-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param y The y-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param tileID The ID of the tile.
	 * 
	 * @throws IllegalArgumentException Thrown if x or y are < 0 or > 15.
	 */
	public void setTileAt(int x, int y, int tileID) {
		tiles[y][x] = tileID;
		region.unsavedChanges = true;
	}
	
	/**
	 * Gets a tile entity at the specified coordinates.
	 * 
	 * @param x The x-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param y The y-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * 
	 * @return The tile entity at the specified coordinates, or {@code null}
	 * if no such tile entity exists.
	 * @throws ArrayIndexOutOfBoundsException Thrown if either x or y is
	 * negative or greater than 15.
	 */
	public TileEntity getTileEntityAt(int x, int y) {
		return tileEntities[y][x];
	}
	
	/**
	 * Sets a tile entity at the specified coordinates. A value of
	 * {@code null} for the {@code tileEntity} parameter indicates that the
	 * tile entity at the specified coordinates is to be removed.
	 * 
	 * @param x The x-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param y The y-coordinate of the tile relative to the slice, in
	 * tile-lengths.
	 * @param tileEntity The tile entity.
	 * 
	 * @throws ArrayIndexOutOfBoundsException Thrown if either x or y is
	 * negative or greater than 15.
	 */
	public void setTileEntityAt(int x, int y, TileEntity tileEntity) {
		if(tileEntities[y][x] != null) {
			if(tileEntity == null)
				numTileEntities--;
			tileEntities[y][x] = null;
			region.unsavedChanges = true;
		} else if(tileEntity != null) {
			numTileEntities++;
			tileEntities[y][x] = tileEntity;
			region.unsavedChanges = true;
		}
	}
	
	/**
	 * Gets the slice's tiles in the form of a 1D integer array.
	 * 
	 * @return The slice's tiles in the form of a 1D integer array;
	 */
	public int[] getTilesAsIntArray() {
		int[] tileArray = new int[SLICE_SIZE * SLICE_SIZE];
		for(int r = 0; r < SLICE_SIZE; r++) {
			for(int c = 0; c < SLICE_SIZE; c++) {
				tileArray[r * SLICE_SIZE + c] = tiles[r][c];
			}
		}
		return tileArray;
	}
	
	/**
	 * Sets the slice's tiles.
	 * 
	 * @param tileArray The tiles, in the form of a 1D integer array. The array
	 * will be converted to a suitable 2D array for tile storage.
	 */
	public void setTilesAsIntArray(int[] tileArray) {
		tiles = new int[SLICE_SIZE][SLICE_SIZE];
		
		for(int r = 0; r < SLICE_SIZE; r++) {
			for(int c = 0; c < SLICE_SIZE; c++) {
				tiles[r][c] = tileArray[r * SLICE_SIZE + c];
			}
		}
	}
	
	/**
	 * Adds any entities and tile entities contained by the slice to the world.
	 * 
	 * @param world The world.
	 */
	public void addContainedEntitiesToWorld(World world) {
		// TODO: A more efficient method of finding tile entities may be ideal
		if(numTileEntities == 0) return;
		TileEntity t;
		for(int r = 0; r < SLICE_SIZE; r++) {
			for(int c = 0; c < SLICE_SIZE; c++) {
				if((t = tileEntities[r][c]) != null) {
					t.world = world;
					world.addTileEntity(t);
				}
			}
		}
	}
	
}
