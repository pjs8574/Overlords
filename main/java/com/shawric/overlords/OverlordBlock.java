package com.shawric.overlords;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class OverlordBlock extends Block implements ITileEntityProvider{

	private IIcon blockTexture;

	public OverlordBlock(String name, int i, int j) {
		super(Material.rock);
		this.setHardness(5);
		this.setResistance(10);
		this.setCreativeTab(Overlords.tabMyMod);
		
		
		this.setBlockName(name);
	
	}

	//sets the block texture stored in \src\main\resources\assets\shawric_overlords\textures\blocks
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icReg){	
			
		blockTexture = icReg.registerIcon(Overlords.modid + ":" + this.getUnlocalizedName().substring(5));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta){
		
	return blockTexture;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		//Minecraft.getMinecraft().thePlayer.sendChatMessage("Tile Ent created.");
		return new OverlordBlockTileEntity();
	
	}
	
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itmStk)
		{
			
			if(!world.isRemote){
				
				//((EntityPlayer) placer).addChatMessage(new ChatComponentText("The chunk "+ placedChunk.toString() + " has been claimed by " + this.owner));
					OverlordBlockTileEntity tile = (OverlordBlockTileEntity) world.getTileEntity(x, y, z);
						tile.setDomain();
			}
	    
		}
	
	
	
	@Override
	 public boolean onBlockActivated(World wrld, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_){
	        
			OverlordBlockTileEntity tileentityOverlordBlock = (OverlordBlockTileEntity)wrld.getTileEntity(x, y, z);
	
			if(!wrld.isRemote){
				player.addChatMessage(new ChatComponentText("I am Overlord "+tileentityOverlordBlock.overlordName ));
				player.addChatMessage(new ChatComponentText("I Currently Desire "+tileentityOverlordBlock.getItemDesired() ));
			}
			return true;
	    }
}
