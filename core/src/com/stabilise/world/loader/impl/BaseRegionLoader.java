package com.stabilise.world.loader.impl;

import static com.stabilise.world.Region.REGION_SIZE;

import com.stabilise.util.io.data.DataCompound;
import com.stabilise.util.io.data.DataList;
import com.stabilise.world.Region;
import com.stabilise.world.Slice;
import com.stabilise.world.loader.IRegionLoader;
import com.stabilise.world.tile.tileentity.TileEntity;

public class BaseRegionLoader implements IRegionLoader {
	
	@Override
	public void load(Region r, DataCompound c, boolean generated) {
		if(!generated)
		    return;
		
        for(int y = 0; y < REGION_SIZE; y++) {
            for(int x = 0; x < REGION_SIZE; x++) {
                DataCompound sliceTag = c.getCompound("slice" + x + "_" + y);
                Slice s = new Slice(r.offsetX + x, r.offsetY + y,
                        sliceTag.getI32Arr("tiles"),
                        sliceTag.getI32Arr("walls"),
                        sliceTag.getI8Arr("light"));
                
                DataList tileEntities = sliceTag.childList("tileEntities");
                if(tileEntities.size() > 0)
                    s.initTileEntities();
                for(int i = 0; i < tileEntities.size(); i++) {
                    DataCompound tc = tileEntities.getCompound();
                    TileEntity te = TileEntity.createFromCompound(tc);
                    s.tileEntities[te.pos.lty()][te.pos.ltx()] = te; 
                }
                
                r.slices[y][x] = s;
            }
        }
	}
	
	@Override
	public void save(Region r, DataCompound c, boolean generated) {
		if(!generated)
		    return;
		
		for(int y = 0; y < REGION_SIZE; y++) {
            for(int x = 0; x < REGION_SIZE; x++) {
                DataCompound sliceTag = c.childCompound("slice" + x + "_" + y);
                Slice s = r.slices[y][x];
                sliceTag.put("tiles", Slice.to1DArray(s.tiles));
                sliceTag.put("walls", Slice.to1DArray(s.walls));
                sliceTag.put("light", Slice.to1DArray(s.light));
                
                if(s.tileEntities != null) {
                    DataList tileEntities = sliceTag.childList("tileEntities");
                    
                    TileEntity t;
                    for(int tileX = 0; tileX < Slice.SLICE_SIZE; tileX++) {
                        for(int tileY = 0; tileY < Slice.SLICE_SIZE; tileY++) {
                            if((t = s.tileEntities[tileY][tileX]) != null) {
                                t.exportToCompound(tileEntities.childCompound());
                            }
                        }
                    }
                }
            }
		}
	}
	
}
