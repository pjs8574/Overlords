package com.shawric.overlords;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "shawric_overlords", name = "Overlords Mod", version = "0.0.01 MC_1.7.10")

public class Overlords {

	
	@SidedProxy(clientSide = "com.shawric.overlords.Client", serverSide = "com.shawric.overlords.Common")
	
	public static Common proxy;
	
	int entIDCount = 50;
	
	public static final String modid = "shawric_overlords";
	public static CreativeTabs tabMyMod = new OverlordsCreativeTab("tabOverlords");
	
	public static Block overlordBlock;
    
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		
		 overlordBlock = new OverlordBlock("overlordBlock", 1, 200);
		 GameRegistry.registerTileEntity(OverlordBlockTileEntity.class, "OverlordBlockID"+this.getUniqeEntID());
			
		
	}
	
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		
    	GameRegistry.registerBlock(overlordBlock, modid + (overlordBlock.getUnlocalizedName().substring(5)));
       
        
        
    }
    
  //used to generate a unique ID for all of my mod entities.
  	public int getUniqeEntID()
  	{
  		this.entIDCount++;
  		
  	return this.entIDCount;
  	}
}
