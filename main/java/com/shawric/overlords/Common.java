package com.shawric.overlords;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class Common
{

public void registerTileEntities() {
	 
    GameRegistry.registerTileEntity(OverlordBlockTileEntity.class, OverlordBlockTileEntity.publicName);
}


}