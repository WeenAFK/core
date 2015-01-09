package com.stabilise.world.gen;

import static com.stabilise.world.Region.REGION_SIZE;
import static com.stabilise.world.Slice.SLICE_SIZE;

import java.util.Random;

import com.stabilise.world.Region;
import com.stabilise.world.Slice;
import com.stabilise.world.WorldData;

/**
 * The flatland generator generates a flat land.
 */
public class FlatlandGeneratorII extends WorldGenerator {
	
	/**
	 * Creates a flatland generator.
	 * 
	 * @param data The world's data object.
	 */
	FlatlandGeneratorII(WorldData data) {
		super(data);
	}
	
	@Override
	protected void generateRegion(Region r) {
		/*
		Slice s = r.getSliceAt(0, 0);
		for(int x = 0; x < REGION_SIZE_IN_TILES; x++) {
			for(int y = 0; y < REGION_SIZE_IN_TILES; y++) {
				if(y % SLICE_SIZE == 0) s = r.getSliceAt(x, y);
				if(r.y * REGION_SIZE_IN_TILES + y > 128)
					s.setTileAt(x % SLICE_SIZE, y % SLICE_SIZE, 0);
					
				genSlice(r, r.getSliceAt(x, y));// = genSlice(x, y);
			}
		}
		*/
		
		for(int x = 0; x < REGION_SIZE; x++) {
			for(int y = 0; y < REGION_SIZE; y++) {
				genSlice(r, r.getSliceAt(x, y));// = genSlice(x, y);
			}
		}
		
		Random rnd = new Random((seed + r.loc.x) ^ r.loc.y);
		
		if(rnd.nextFloat() < 0.75f)
			addSchematicAt(r, "testhouse", rnd.nextInt(15), 15, rnd.nextInt(15), 15, SchematicParams.defaultParams());
		//addSchematicAt(r, "testhouse", 15, 15, 8, 15, SchematicParams.defaultParams());
		//addSchematicAt(r, "testStructure", 7, 0, 1, 0, SchematicParams.defaultParams());
	}
	
	/**
	 * Gens a slice.
	 * 
	 * @param s The slice to gen.
	 */
	private void genSlice(Region re, Slice s) {
		//Slice slice = new Slice(x, y, new int[SLICE_SIZE][SLICE_SIZE]);
		Random rng = new Random(seed + s.x + s.y);
		
		for(int c = 0; c < SLICE_SIZE; c++) {
			for(int r = 0; r < SLICE_SIZE; r++) {
				if(s.y >= 0)
					s.setTileAt(c, r, 0);		// Air
				//else if(s.y <= -8)
				//	s.setTileAt(c, r, 8);		// Lava
				else {
					int rnd = rng.nextInt(5);
					if(rnd < 2)
						s.setTileAt(c, r, 1);		// Stone
					else if(rnd < 4)
						s.setTileAt(c, r, 12);		// Stone Brick
					else
						s.setTileAt(c, r, 11);		// Ice
				}
			}
		}
	}
	
}
