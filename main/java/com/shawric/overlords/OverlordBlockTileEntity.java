package com.shawric.overlords;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class OverlordBlockTileEntity extends TileEntity {


	public static final String publicName = "overlordBlockTileEntity";
	
    private String name = "overlordBlockTileEntity";
    private Chunk placedChunk;

    private ItemStack[] itemStackArray = new ItemStack[1];
    private TileEntityChest[] chestArray = new TileEntityChest[6];
    
	//the Overlord's domain
	private ArrayList<EntityPlayer> playerList;
	
	public String chunkListString = "z";
	public String overlordName = "unamed";
	
	private int hungerLevel;
	private int hungerTimeCheck=0;
	
	private int hungerSatedLevel=10;
	private boolean overlordHungerSated;
	
	private int overlordLevel=1;
	private int angerLevel;
	
	private boolean sentToEventList;
	private int timeCheck=0;
	
	
	
	public OverlordBlockTileEntity() {

		System.out.println("---!!!this.sentToEventList!!!---"+this.sentToEventList);
		System.out.println("---!!!Overlord BLOCK CONSTRUCTOR TRIGGERED!!!---");
		this.sentToEventList = false;
		this.generateOverlordName();
		this.timeCheck = 0;
		

	}

	
	
	@Override
	public void updateEntity(){
		   
		   
			//Initial update tick
		//REMOVED THE ONCE ONLY BOOLEAN FOR TEST PURPOSES
		// && this.sentToEventList==false)
		  if(!this.worldObj.isRemote){   
		   if (this.timeCheck < 40){
			   ++this.timeCheck;	   
		   }else{  
					   System.out.println("---!!!SENDING UPDATE TO EH!!!---");
					   ++this.timeCheck;
					   this.setDomain();
					   this.getInventories();
					   this.markDirty();
					   this.sentToEventList = true;
		        }  
		   }
		   
		   //update Hunger time check
		  if(!this.worldObj.isRemote){
			  // System.out.println("---!!!TIMECHECK!!!---"+this.timeCheck);
			   
			   if (this.hungerTimeCheck < 1200){
				   ++this.hungerTimeCheck;
				   
			   }else{  
						   System.out.println("---IM HUNGRY FEED ME!---");
						   ++this.hungerTimeCheck;
						   this.checkHunger();
						   this.markDirty();
						 
			        }  
		  }
		   //timer resets
		   if (this.timeCheck > 40){this.timeCheck=0;}
		   if (this.hungerTimeCheck > 1200){this.hungerTimeCheck=0;}
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
	
		
	private void checkHunger(){
		
		//Look to see if it can feed on an item in nearby chest
		//if so, eat the item, and increase sated level to max
		//if not, increase hunger level
		
		this.overlordHungerSated=this.eatItem();
		
		if(!this.overlordHungerSated){
			
			System.out.println("---MY HUNGER GROWS---");
			this.hungerLevel++;
		}
		
	}
	
	
	private boolean eatItem(){
		
		
		for( int i = 0; i<this.chestArray.length; i++ ){
			
			if(!(this.chestArray[i]==null)){
			
				for(int i2 = 0; i2<=this.chestArray[i].getSizeInventory(); i++){
					
					ItemStack tstStack = this.chestArray[i].getStackInSlot(i2);
					
					//tstStack;
					
				}
				
			}
			
			
		}
		
		
		return false;
		
	}
	
	private void generateOverlordName(){
		
		if(this.overlordName.contains("unamed")){
			
			this.overlordName = "";
			
			Random ranIndex = new Random();

		    String alphabet = "abcdefghijklmnopqrstuvwxyz";
		    for (int i = 0; i < 10; i++) {
		    	this.overlordName += alphabet.charAt(ranIndex.nextInt(alphabet.length()));
		    }	
		}
		
    	System.out.println("COWER MORTAL FOR MY NAME IS: "+this.overlordName);
		
	}
	
	
	private void getInventories(){
		
		int x=this.xCoord;
		int y=this.yCoord;
		int z=this.zCoord;
		
		
		
		Block blockToCheck1 = this.worldObj.getBlock((x+1), y, z);
		Block blockToCheck2 = this.worldObj.getBlock((x-1), y, z);
		Block blockToCheck3 = this.worldObj.getBlock(x, y, (z+1));
		Block blockToCheck4 = this.worldObj.getBlock(x, y, (z-1));
		Block blockToCheck5 = this.worldObj.getBlock(x, y+1, z);
		Block blockToCheck6 = this.worldObj.getBlock(x, y-1, z);
		
		if(blockToCheck1.hasTileEntity(0)){
			TileEntity checkTileEntity1 = this.worldObj.getTileEntity((x+1), y, z);
			if(checkTileEntity1 instanceof TileEntityChest){
				
				//do chest stuff here
				this.chestArray[2]=(TileEntityChest) checkTileEntity1;
				
				System.out.println("---THERE IS A CHEST HERE---");
				
				
				}
		}
			
		if(blockToCheck2.hasTileEntity(0)){
			TileEntity checkTileEntity2 = this.worldObj.getTileEntity((x-1), y, z);
				if(checkTileEntity2 instanceof TileEntityChest){
					//do chest stuff here
					
					this.chestArray[3]=(TileEntityChest) checkTileEntity2;
					
					System.out.println("---THERE IS A CHEST HERE---");
					}
		}
		if(blockToCheck3.hasTileEntity(0)){
			TileEntity checkTileEntity3 = this.worldObj.getTileEntity(x, y, (z+1));
			if(checkTileEntity3 instanceof TileEntityChest){
				//do chest stuff here
				
				this.chestArray[4]=(TileEntityChest) checkTileEntity3;
				System.out.println("---THERE IS A CHEST HERE---");
				}
		}
		if(blockToCheck4.hasTileEntity(0)){
			TileEntity checkTileEntity4 = this.worldObj.getTileEntity(x, y, (z-1));
			if(checkTileEntity4 instanceof TileEntityChest){
				//do chest stuff here
				
				this.chestArray[5]=(TileEntityChest) checkTileEntity4;
				System.out.println("---THERE IS A CHEST HERE---");
				}
		}
		if(blockToCheck5.hasTileEntity(0)){
			TileEntity checkTileEntity5 = this.worldObj.getTileEntity(x, y+1, z);
			if(checkTileEntity5 instanceof TileEntityChest){
				//do chest stuff here
				
				this.chestArray[0]=(TileEntityChest) checkTileEntity5;
				System.out.println("---THERE IS A CHEST HERE---");
				}
		}
		if(blockToCheck6.hasTileEntity(0)){
			TileEntity checkTileEntity6 = this.worldObj.getTileEntity(x, y-1, z);
			if(checkTileEntity6 instanceof TileEntityChest){
				//do chest stuff here
				
				this.chestArray[1]=(TileEntityChest) checkTileEntity6;
				
				System.out.println("---THERE IS A CHEST HERE---");
				}
		}
		
		
	}
	
	private void checkDomainForPlayers(){
		

		String[] chunkCoordsList = this.chunkListString.split(";");
		
		for(int i = 0; i<chunkCoordsList.length; i++){
			
			String[] indChunkCoords = chunkCoordsList[i].split(",");
			
			int chunkX = new Integer(indChunkCoords[0]);
			int chunkZ = new Integer(indChunkCoords[1]);
			
			Chunk chunkToCheckForPlayers = this.worldObj.getChunkFromChunkCoords(chunkX, chunkZ);
			
			//chunkToCheckForPlayers.
		}
		
		
	}
	
	public void addPlayerToDomain(EntityPlayer player){
		
		playerList.add(player);
		
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound par1)
	{
		   super.writeToNBT(par1);
	      par1.setString("ChunkList", this.chunkListString );
	      par1.setString("OverlordName", this.overlordName );
	      par1.setInteger("OverlordLevel", this.overlordLevel);
	      

	   }

	   @Override
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      
		   super.readFromNBT(par1);
		   this.chunkListString = par1.getString("ChunkList");
		   this.overlordName = par1.getString("OverlordName");
		   this.overlordLevel = par1.getInteger("OverlordLevel");
		  
	    
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
