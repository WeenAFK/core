package com.stabilise.world.dimension;

import com.stabilise.world.WorldInfo;
import com.stabilise.world.gen.WorldGenerator;
import com.stabilise.world.gen.misc.ChestGen;
import com.stabilise.world.gen.misc.OreGen;
import com.stabilise.world.gen.terrain.CaveGen;
import com.stabilise.world.gen.terrain.OverworldTerrainGen;
import com.stabilise.world.loader.WorldLoader;

/**
 * The Overworld is the default world dimension.
 */
public class DimOverworld extends Dimension {
    
    public DimOverworld(Info info) {
        super(info);
    }
    
    @Override
    public void addGenerators(WorldGenerator g) {
        g.addGenerator(OverworldTerrainGen::new);
        g.addGenerator(new CaveGen());
        g.addGenerator(new OreGen(2));
        g.addGenerator(new ChestGen());
    }
    
    @Override
    public void addLoaders(WorldLoader wl, WorldInfo info) {
        // no extra stuff to load
    }
    
}
