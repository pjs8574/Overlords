package com.shawric.overlords;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class OverlordBlockTileEntity extends TileEntity {


	public static final String publicName = "overlordBlockTileEntity";

	private static String[] itemToEatList = {"cobblestone","stone","dirt","gravel","sand"};
	private String itemDesired;
	
    private String name = "overlordBlockTileEntity";
    private Chunk placedChunk;

    private ArrayList<EntityPlayer> domainPlayerList = new ArrayList<EntityPlayer>();
    private ArrayList<EntityPlayer> boundPlayerList = new ArrayList<EntityPlayer>();
    
    private TileEntityChest[] chestArray = new TileEntityChest[6];
    

	
	
	public String chunkListString = "z";
	public String overlordName = "unamed";
	
	private int hungerLevel;
	private int hungerTimeCheck=0;
	
	private int hungerSatedLevel=0;
	private boolean overlordHungerSatedBooleon = false;
	
	private int overlordLevel=1;
	private int angerLevel;
	
	private boolean sentToEventList;
	private int timeCheck=0;

	private double overlordExpeirence;

	private int totalAmountOfDesiredItem;


	
	
	public OverlordBlockTileEntity() {

		
		System.out.println("---!!!Overlord BLOCK CONSTRUCTOR TRIGGERED!!!---");
		this.sentToEventList = false;
		this.generateOverlordName();
		this.timeCheck = 0;
		this.randomSetItemDesired();
	}

	
	
	private void randomSetItemDesired(){
		
		Random ranEat= new Random();
		this.setItemDesired(itemToEatList[ranEat.nextInt(itemToEatList.length)]);
		
	}
	
	
	@Override
	public void updateEntity(){
		   
		   
			//Initial update tick
		//REMOVED THE ONCE ONLY BOOLEAN FOR TEST PURPOSES
		// && this.sentToEventList==false)
		  if(!this.worldObj.isRemote){   
		   if (this.timeCheck < 120){
			   ++this.timeCheck;	   
		   }else{  
					   System.out.println("---!!!SENDING UPDATE TO EH!!!---");
					   ++this.timeCheck;
					   this.setDomain();
					   this.getInventories();
					   this.checkDomainForPlayers();
					   this.markDirty();
					   this.sentToEventList = true;
		        }  
		   }
		   
		   //update Hunger time check
		  if(!this.worldObj.isRemote){
			  // System.out.println("---!!!TIMECHECK!!!---"+this.timeCheck);
			   
			   if (this.hungerTimeCheck < 120){
				   ++this.hungerTimeCheck;
				   
			   }else{  
						   
						   ++this.hungerTimeCheck;
						   
						   this.checkHunger();
						   this.markDirty();
						 
			        }  
		  }
		   //timer resets
		   if (this.timeCheck > 120){this.timeCheck=0;}
		   if (this.hungerTimeCheck > 120){this.hungerTimeCheck=0;}
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
				+(chunkZ+1)+";"+(chunkX-1)+","+(chunkZ-1)+";"+(chunkX)+","+(chunkZ)+";";
		
		
	}
	
	public String getDomain(){
		return this.chunkListString ;
		
	}
	
		
	private void checkHunger(){
		
		//Look to see if it can feed on an item in nearby chest
		//if so, eat the item, and increase sated level to max
		//if not, increase hunger level
		
		//System.out.println("---I CRAVE "+ this.itemDesired + "---");
		//System.out.println("---SatedLevel -  "+ hungerSatedLevel + "---");
		this.sendMessageToDomain("I CRAVE "+ this.getItemDesired()+".");
		
		
		if(this.hungerSatedLevel < 1){
			
			this.eatItem();
			
			if(this.overlordHungerSatedBooleon==false)
			{
				this.sendMessageToDomain("MY HUNGER GROWS.");
			this.hungerLevel++;
			}
			
		}else{
			this.hungerSatedLevel--;
		}
		
	}
	
	
	private void eatItem(){
		
		this.totalAmountOfDesiredItem =  (this.hungerLevel+this.overlordLevel)*this.overlordLevel;
		
		int totalDesiredItemInChests=0;
		
		for( int i = 0; i<this.chestArray.length; i++ ){
			
			if(!(this.chestArray[i]==null)){
				
				for(int i2 = 0; i2<this.chestArray[i].getSizeInventory(); i2++){
					
					if(!(this.chestArray[i].getStackInSlot(i2)==null)){
					ItemStack tstStack = this.chestArray[i].getStackInSlot(i2);
					
					//System.out.println("---I FOUND "+tstStack.getDisplayName()+" in chest "+i+"---");
					
						if(tstStack.getDisplayName().equalsIgnoreCase(this.getItemDesired())){
							//running tally of number of desired item in the chests
							//this is needed when desire goes over stack limit of 64
							totalDesiredItemInChests+=tstStack.stackSize;
						}
						
					}
				}
			}		
		}			
		
		this.sendMessageToDomain("Total in chests:"+totalDesiredItemInChests);
		this.sendMessageToDomain("Total desired:"+this.totalAmountOfDesiredItem);
		
		if(totalDesiredItemInChests>=this.totalAmountOfDesiredItem ){

			for( int i = 0; i<this.chestArray.length; i++ ){
				if(!(this.chestArray[i]==null)){
					for(int i2 = 0; i2<this.chestArray[i].getSizeInventory(); i2++){
						if(!(this.chestArray[i].getStackInSlot(i2)==null)){
						
							ItemStack stackToEat = this.chestArray[i].getStackInSlot(i2);

							if(stackToEat.getDisplayName().equalsIgnoreCase(this.getItemDesired())){
								
								if(this.totalAmountOfDesiredItem>stackToEat.stackSize){
									totalDesiredItemInChests -= stackToEat.stackSize;
									this.totalAmountOfDesiredItem -=stackToEat.stackSize;
									ItemStack eatenStack = this.chestArray[i].decrStackSize(i2,(stackToEat.stackSize));
								}else{
									if(this.totalAmountOfDesiredItem>0){
										ItemStack eatenStack = this.chestArray[i].decrStackSize(i2,(this.totalAmountOfDesiredItem));
										this.totalAmountOfDesiredItem=0;
										this.overlordExpeirence += 1;
										this.checkOverlordLevel();
										this.hungerSatedLevel=5;
										this.hungerLevel=0;
										this.overlordHungerSatedBooleon = true;
										this.sendMessageToDomain("MY HUNGER IS SATED....FOR NOW.");
										this.sendMessageToDomain("I GAIN IN POWER:"+this.overlordExpeirence+"XP");
										this.randomSetItemDesired();
									}
								}
				
		}}}}}}else{
			this.sendMessageToDomain("I REQURE "+ ((this.totalAmountOfDesiredItem-totalDesiredItemInChests)) + " MORE "+this.getItemDesired()+"");
			this.overlordHungerSatedBooleon = false;
		}
			

		
	}
	
	private void checkOverlordLevel() {
		
		double checkXP = (this.overlordExpeirence/10);
		
		if((this.overlordExpeirence/10)>=this.overlordLevel){
			
			this.overlordLevel++;
			
			this.sendMessageToDomain("MY POWER GROWS!");
			
		}
		
	}


	
	private void sendMessageToDomain(String message){
		
		
		if(!(this.domainPlayerList.isEmpty())){
				
			for (int i = 0; i <this.domainPlayerList.size(); ++i){
				    	EntityPlayer targetPlayer = (EntityPlayer)this.domainPlayerList.get(i);
				    	targetPlayer.addChatMessage(new ChatComponentText("Overlord "+this.overlordName+": "+message));
			}
		}
		
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
		
    	//System.out.println("COWER MORTAL FOR MY NAME IS: "+this.overlordName);
		
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
				
				//EAST CHEST
				this.chestArray[2]=(TileEntityChest) checkTileEntity1;
				
				}else{this.chestArray[2]=null;}
		}else{this.chestArray[2]=null;}
		
		if(blockToCheck2.hasTileEntity(0)){
			TileEntity checkTileEntity2 = this.worldObj.getTileEntity((x-1), y, z);
				if(checkTileEntity2 instanceof TileEntityChest){
					
					//WEST CHEST
					this.chestArray[3]=(TileEntityChest) checkTileEntity2;
					
					}else{this.chestArray[3]=null;}
		}else{this.chestArray[3]=null;}
		
		if(blockToCheck3.hasTileEntity(0)){
			TileEntity checkTileEntity3 = this.worldObj.getTileEntity(x, y, (z+1));
			if(checkTileEntity3 instanceof TileEntityChest){
				
				//SOUTH CHEST
				this.chestArray[4]=(TileEntityChest) checkTileEntity3;
				
				}else{this.chestArray[4]=null;}
		}else{this.chestArray[4]=null;}
		
		if(blockToCheck4.hasTileEntity(0)){
			TileEntity checkTileEntity4 = this.worldObj.getTileEntity(x, y, (z-1));
			if(checkTileEntity4 instanceof TileEntityChest){
				
				//NORTH CHEST
				this.chestArray[5]=(TileEntityChest) checkTileEntity4;
				
				}else{this.chestArray[5]=null;}
		}else{this.chestArray[5]=null;}
		
		if(blockToCheck5.hasTileEntity(0)){
			TileEntity checkTileEntity5 = this.worldObj.getTileEntity(x, y+1, z);
			if(checkTileEntity5 instanceof TileEntityChest){
				
				//TOP CHEST
				this.chestArray[0]=(TileEntityChest) checkTileEntity5;
				
				}else{this.chestArray[0]=null;}
		}else{this.chestArray[0]=null;}
		
		if(blockToCheck6.hasTileEntity(0)){
			TileEntity checkTileEntity6 = this.worldObj.getTileEntity(x, y-1, z);
			if(checkTileEntity6 instanceof TileEntityChest){
				
				//BOTTOM CHEST
				this.chestArray[1]=(TileEntityChest) checkTileEntity6;
				
				}else{this.chestArray[1]=null;}
		}else{this.chestArray[1]=null;}
		
		
	}
	
	private void checkDomainForPlayers(){
		
		for (int i2 = 0; i2 <this.worldObj.playerEntities.size(); ++i2){
      		
	    	EntityPlayer targetPlayer = (EntityPlayer)this.worldObj.playerEntities.get(i2);

			String[] chunkCoordsList = this.chunkListString.split(";");
			
			for(int i = 0; i<chunkCoordsList.length; i++){
				
				String[] indChunkCoords = chunkCoordsList[i].split(",");
				
				int chunkX = new Integer(indChunkCoords[0]);
				int chunkZ = new Integer(indChunkCoords[1]);
				
				System.out.println("Player chunk X"+targetPlayer.chunkCoordX +"Player chunk Z"+targetPlayer.chunkCoordZ+"    ChunkX: "+chunkX+ " ChunkZ "+chunkZ);
				
				if((targetPlayer.chunkCoordX == chunkX) && (targetPlayer.chunkCoordZ == chunkZ)){
					
					if(!(domainPlayerList.contains(targetPlayer))){
						domainPlayerList.add(targetPlayer);
					}

				}
				}
	
			}
		
		
	}
	
	public void addBoundPlayerToDomain(EntityPlayer player){
		
		boundPlayerList.add(player);
		
	}
	
	
	@Override
	public void writeToNBT(NBTTagCompound par1)
	{
		   super.writeToNBT(par1);
	      par1.setString("ChunkList", this.chunkListString );
	      par1.setString("OverlordName", this.overlordName );
	      par1.setInteger("OverlordLevel", this.overlordLevel);
	      par1.setDouble("OverlordXP", this.overlordExpeirence);
	      

	   }

	   @Override
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      
		   super.readFromNBT(par1);
		   this.chunkListString = par1.getString("ChunkList");
		   this.overlordName = par1.getString("OverlordName");
		   this.overlordLevel = par1.getInteger("OverlordLevel");
		   this.overlordExpeirence =par1.getDouble("OverlordXP");
	    
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



	public String getItemDesired() {
		return itemDesired;
	}



	public void setItemDesired(String nameOfItemDesired) {
		this.itemDesired = nameOfItemDesired;
	}
	
}
