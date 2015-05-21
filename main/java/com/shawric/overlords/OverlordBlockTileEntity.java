package com.shawric.overlords;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class OverlordBlockTileEntity extends TileEntity {


	public static final String publicName = "overlordBlockTileEntity";
	
    private String name = "overlordBlockTileEntity";
    private Chunk placedChunk;

	
	//the Overlord's domain
	private ArrayList<String> chunkList;
	
	public String chunkListString = "z";
	
	private boolean sentToEventList;
	private int timeCheck=0;
	
	public OverlordBlockTileEntity() {

		System.out.println("---!!!this.sentToEventList!!!---"+this.sentToEventList);
		System.out.println("---!!!Overlord BLOCK CONSTRUCTOR TRIGGERED!!!---");
		this.sentToEventList = false;
		this.timeCheck = 0;
		

	}

	
	
	@Override
	public void updateEntity(){
		   
		   if(!this.worldObj.isRemote && this.sentToEventList==false){
		  // System.out.println("---!!!TIMECHECK!!!---"+this.timeCheck);
		   
		   if (this.timeCheck < 40){
			   ++this.timeCheck;
			   
		   }else{  
					   System.out.println("---!!!SENDING UPDATE TO EH!!!---");
					   ++this.timeCheck;
					   
					   
					   this.setDomain();
					   
					   
					   this.markDirty();
					   this.sentToEventList = true;
		        }  
		   }
		   
		   if (this.timeCheck > 40){this.timeCheck=0;}
	   }
	
	
	
	public void setDomain(){
		
		this.placedChunk = this.worldObj.getChunkFromBlockCoords(xCoord, zCoord);
		
		int chunkX = placedChunk.xPosition;
		int chunkZ = placedChunk.zPosition;
			//list of chunks z+1, z-1,  X+1, x-1,  x+1&z+1,  X+1&Z-1, X-1&Z+1, X-1&Z-1, 
			// need to create a single strong to store in NBT that contains coords for all Domain chunks
		// comma separates the X and Z
		//Semicolon separates the chunk
		this.chunkListString = (chunkX)+","+(chunkZ+1)+";"+ (chunkX+1)+","+(chunkZ-1)+";"+ (chunkX+1)+","+(chunkZ)+";"
				+(chunkX-1)+","+(chunkZ)+";"+(chunkX+1)+","+(chunkZ+1)+";"+(chunkX+1)+","+(chunkZ-1)+";"+(chunkX-1)+","
				+(chunkZ+1)+";"+(chunkX-1)+","+(chunkZ-1)+";";
		
		
	}
	
	public String getDomain(){
		return this.chunkListString ;
		
	}
	
		
	
	
	@Override
	public void writeToNBT(NBTTagCompound par1)
	{
		   super.writeToNBT(par1);
	      par1.setString("ChunkList", this.chunkListString );
	      

	   }

	   @Override
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      
		   super.readFromNBT(par1);
		   this.chunkListString = par1.getString("ChunkList");
		  
	    
	   }
	   
	   
	   @Override
	   public Packet getDescriptionPacket()
	   {
	       NBTTagCompound syncData = new NBTTagCompound();
	       writeToNBT(syncData);
	       return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	   }
	   
	   @Override
	   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	   {
		   readFromNBT(pkt.func_148857_g());
	   }
	
}
