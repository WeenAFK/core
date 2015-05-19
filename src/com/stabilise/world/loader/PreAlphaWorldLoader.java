package com.stabilise.world.loader;

import static com.stabilise.world.Region.REGION_SIZE;
import static com.stabilise.world.World.*;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.stabilise.util.nbt.NBTIO;
import com.stabilise.util.nbt.NBTTag;
import com.stabilise.util.nbt.NBTTagCompound;
import com.stabilise.util.nbt.NBTTagList;
import com.stabilise.world.Region;
import com.stabilise.world.Slice;
import com.stabilise.world.Region.QueuedStructure;
import com.stabilise.world.multiverse.Multiverse;
import com.stabilise.world.tile.tileentity.TileEntity;

/**
 * Pre-alpha world loading!
 * 
 * <p>TODO: Too many things which are crucial to the game mechanisms are
 * currently implemented as a part of PreAlphaWorldLoader. We need to find a
 * way to abstract the crucial parts away and leave only the nuances of world
 * loading up to particular implementations!
 */
public class PreAlphaWorldLoader extends WorldLoader {
	
	/**
	 * Creates a new PreAlphaWorldLoader.
	 * 
	 * @param provider The world provider.
	 */
	public PreAlphaWorldLoader(Multiverse<?> provider) {
		super(provider);
	}
	
	@Override
	protected void load(Region r, FileHandle file) {
		NBTTagCompound regionTag;
		try {
			regionTag = NBTIO.readCompressed(file);
		} catch(IOException e) {
			log.postSevere("Could not load the NBT data for region " + r.x()
					+ "," + r.y() + "!", e);
			return;
		}
		
		boolean generated = regionTag.getBoolean("generated");
		
		//System.out.println("Loaded NBT of " + r);
		//if(!r.generated)
		//	System.out.println(r + ": " + regionTag.toString());
		
		if(generated) {
			for(int y = 0; y < REGION_SIZE; y++) {			// Row (y)
				for(int x = 0; x < REGION_SIZE; x++) {		// Col (x)
					NBTTagCompound sliceTag = regionTag.getCompound("slice" + x + "_" + y);
					Slice s = new Slice(r.offsetX + x, r.offsetY + y, sliceTag.getIntArray("tiles"));
					
					NBTTagList tileEntities = sliceTag.getList("tileEntities");
					if(tileEntities.size() > 0)
						s.initTileEntities();
					for(NBTTag t : tileEntities) {
						NBTTagCompound tc = (NBTTagCompound)t;
						TileEntity te = TileEntity.createTileEntityFromNBT(tc);
						s.tileEntities		// I just love really long method names!
							[tileCoordRelativeToSliceFromTileCoord(te.y)]
							[tileCoordRelativeToSliceFromTileCoord(te.x)] = te; 
					}
					
					r.slices[y][x] = s;
				}
			}
		}
		
		NBTTagList schematics = regionTag.getList("queuedSchematics");
		
		if(schematics.size() != 0) {
			for(int i = 0; i < schematics.size(); i++) {
				NBTTagCompound schematic = (NBTTagCompound)schematics.getTagAt(i);
				QueuedStructure s = new Region.QueuedStructure();
				s.schematicName = schematic.getString("schematicName");
				s.sliceX = schematic.getInt("sliceX");
				s.sliceY = schematic.getInt("sliceY");
				s.tileX = schematic.getInt("tileX");
				s.tileY = schematic.getInt("tileY");
				s.offsetX = schematic.getInt("offsetX");
				s.offsetY = schematic.getInt("offsetY");
				r.addStructure(s);
			}
			
			//log.postDebug("Loaded " + schematics.size() + " schematics into " + r);
		}
		
		if(generated)
			r.setGenerated();
	}
	
	@Override
	protected void save(Region r, FileHandle file) {
		NBTTagCompound regionTag = new NBTTagCompound();
		
		regionTag.addBoolean("generated", r.isGenerated());
		
		if(r.isGenerated()) {
			for(int y = 0; y < REGION_SIZE; y++) {			// Row (y)
				for(int x = 0; x < REGION_SIZE; x++) {		// Col (x)
					NBTTagCompound sliceTag = new NBTTagCompound();
					Slice s = r.slices[y][x];
					sliceTag.addIntArray("tiles", s.getTilesAsIntArray());
					regionTag.addCompound("slice" + x + "_" + y, sliceTag);
					
					if(s.tileEntities != null) {
						NBTTagList tileEntities = new NBTTagList();
						
						TileEntity t;
						for(int tileX = 0; tileX < Slice.SLICE_SIZE; tileX++) {
							for(int tileY = 0; tileY < Slice.SLICE_SIZE; tileY++) {
								if((t = s.tileEntities[tileY][tileX]) != null) {
									NBTTagCompound tileEntity = t.toNBT();
									tileEntity.addInt("id", t.getID());
									tileEntity.addInt("x", t.x);
									tileEntity.addInt("y", t.y);
									tileEntities.appendTag(tileEntity);
								}
							}
						}
						
						sliceTag.addList("tileEntities", tileEntities);
					}
				}
			}
		}
		
		if(r.hasQueuedStructures()) {
			NBTTagList schematics = new NBTTagList();
			for(QueuedStructure s : r.getStructures()) {
				NBTTagCompound schematic = new NBTTagCompound();
				schematic.addString("schematicName", s.schematicName);
				schematic.addInt("sliceX", s.sliceX);
				schematic.addInt("sliceY", s.sliceY);
				schematic.addInt("tileX", s.tileX);
				schematic.addInt("tileY", s.tileY);
				schematic.addInt("offsetX", s.offsetX);
				schematic.addInt("offsetY", s.offsetY);
				schematics.appendTag(schematic);
			}
			
			regionTag.addList("queuedSchematics", schematics);
			
			//log.postDebug("Saved " + schematics.size() + " schematics in " + r);
		}
		
		try {
			NBTIO.safeWriteCompressed(file, regionTag);
		} catch(IOException e) {
			log.postSevere("Could not save " + r + "!", e);
		}
	}

}
