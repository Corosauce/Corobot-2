package corobot;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.c_CoroAIUtil;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import CoroAI.PFQueue;
import CoroAI.PathEntityEx;
import CoroAI.entity.EnumActState;
import CoroAI.entity.EnumJob;
import CoroAI.entity.EnumJobState;
import CoroAI.entity.JobManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class c_AIP {
	
	public static c_AIP i;
	
	public static Minecraft mc = ModLoader.getMinecraftInstance();
	public EntityPlayer player;
	public static World worldObj;
	
	public EntityPlayer ent;
	
	public Random rand = new Random(System.currentTimeMillis());
	public int dangerLevel;
	public PathEntityEx pathToEntity;
	public EnumActState currentAction;
	public boolean fleeing;
	public Entity jobFleeFrom;

    public JobManager job;
	public Entity entityToAttack;
	
	public c_EnumAIPOrders orders = c_EnumAIPOrders.STAY_CLOSE;
	public int lastItemCount;
	
	//walking target
	public int targX;
	public int targY;
	public int targZ;
	
	//follow target
	public Entity followTarget;
	
	//Village fields
	public int homeX;
	public int homeY;
	public int homeZ;
	public double maxDistanceFromHome = 96;
	
	public long checkAreaDelay;
	public long checkRange = 16;
	public int openedChest = 0;
	
	public int maxPFRange = 512;
	
	//Customizable item use vars
	public int cooldown_Melee;
	public int cooldown_Ranged;
	public int slot_Melee;
	public int slot_Ranged;
	public int slot_Food;
	public float maxReach_Melee;
	public float maxReach_Ranged;
	public int itemSearchRange;
	public List wantedItems;
	
	public boolean forcejump;
	public boolean mining;
	public int mineDelay;
	public int noMoveTicks;
	public int curCooldown_Melee;
	public int curCooldown_Ranged;
	public int pfTimeout;
	
	public int cooldown_Pathfind = 20;
	public int curCooldown_Pathfind;

	public static boolean enableAI = false;

	public boolean wallOfPainToggle = true;
	
	public boolean isUsing = false;
	public int isUsingTicks = 0;
	
	//For blocks
	public boolean dangerZone = false;
	
	//For entities
	public boolean enemyClose = false;
	
	public int walkingTimeout;
	
	public boolean attackPlayers = false;
	public boolean attackAnimals = false;
	
	
	public static List<String> owners = new ArrayList();
	
	public boolean zcMode = false;
	
	public c_AIP() {
		i = this;
		player = mc.thePlayer;
		worldObj = mc.theWorld;

    	job = new JobManager(null);
    	
    	job.jobTypes.put(EnumJob.PLAYER_SURVIVE, new JobPlayerInventory(job));
    	job.jobTypes.put(EnumJob.PLAYER_HUNT, new c_Job_Player_Hunt(job));
    	job.jobTypes.put(EnumJob.PLAYER_FOLLOW, new c_Job_Player_Follow(job));
    	
    	job.addJob(EnumJob.PLAYER_HUNT);
    	job.addJob(EnumJob.PLAYER_SURVIVE);
    	job.addJob(EnumJob.PLAYER_FOLLOW);
    	
    	job.setPrimaryJob(EnumJob.PLAYER_SURVIVE);
    	//job.setPrimaryJob(EnumJob.PLAYER_HUNT);
    	
    	wantedItems = new LinkedList();
        slot_Melee = 0;
        slot_Ranged = 1;
        slot_Food = 2;
        cooldown_Melee = 1;
        cooldown_Ranged = 40;
        maxReach_Melee = 5F;
        maxReach_Ranged = 22F;
        itemSearchRange = 5;
        
        owners.add("Corosus");
        owners.add("drfrox");
		//owners.add("medsouz");
		owners.add("Cojomax99");
		//owners.add("solonox");
		//owners.add("Malqua");
		
	}
	
	public static boolean okToGo() {
		if (worldObj == null) worldObj = mc.theWorld;
		if (i.player == null) i.player = mc.thePlayer;
		
		if (worldObj == null || i.player == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isEnemy(Entity entity1) {
		if ((entity1 instanceof EntityMob || entity1 instanceof EntitySlime || entity1 instanceof EntityWither || entity1 instanceof EntityWitherSkull) && !(entity1 instanceof EntityWolf || entity1 instanceof EntityEnderman || entity1 instanceof EntityPigZombie || entity1 instanceof EntityVillager/* || entity1 instanceof EntityCreeper*/)) {
			return true;
		} else {
			
			if (entity1 instanceof EntityEnderman) {
				if (((EntityEnderman)entity1).func_70823_r()) { //if attacking, client side datawatcher style check
					return true;
				}
			}
			
			//animal adjuster (give own job later?)
			if (attackAnimals && entity1 instanceof EntityCow) return true;
			
			if (entity1 instanceof EntityPlayer && attackPlayers) {
				if (!owners.contains(((EntityPlayer) entity1).username)) {
					return true;
				}
			}
			
			return false;
			
		}
	}
	
	public boolean isThreat(Entity entity1) { return isFeared(entity1); }
	
	public boolean isFeared(Entity entity1) {
		if (entity1 instanceof EntityCreeper) {
			return true;
		} else {
			return false;
			
		}
	}
	
	public boolean isMeleeUser(Entity ent) {
		if (ent instanceof EntityMob && !(ent instanceof EntitySkeleton)) {
			return true;
		}
		return false;
	}
	
	public boolean isRangedUser(Entity ent) {
		if (ent instanceof EntitySkeleton || ent instanceof EntityBlaze || ent instanceof EntityGhast) {
			return true;
		}
		return false;
	}
	
	public void setState(EnumActState eka) {
		currentAction = eka;
	}
	
	//returns: -1 = not checking, timeout in progress, 0 = no danger, 1 = found danger
	public int checkSurroundings() {
		
		//Hostile block checking
		
		dangerZone = false;
		int range = 3;
		for (int xx = -range; xx < range; xx++) {
			for (int zz = -range; zz < range; zz++) {
				int id = player.worldObj.getBlockId((int)player.posX+xx, (int)player.posY, (int)player.posZ+zz);
				if (id == Block.cactus.blockID) {
					//System.out.println("DANGER ZONE!");
					dangerZone = true;					
				}
			}
		}
		
		
		
		//if player or some hostile gets close, if not hunter perhaps run back to the village, find a hunter, update his job and have him get it
		if (checkAreaDelay < System.currentTimeMillis()) {
			checkAreaDelay = System.currentTimeMillis() + 1500 + rand.nextInt(1000);
			
			float closest = 9999F;
			Entity clEnt = null;
			
			List list = worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(checkRange, checkRange/2, checkRange));
	        for(int j = 0; j < list.size(); j++)
	        {
	            Entity entity1 = (Entity)list.get(j);
	            if(isEnemy(entity1))
	            {
	            	if (((EntityLiving) entity1).canEntityBeSeen(player)) {
	            		float dist = player.getDistanceToEntity(entity1);
            			if (dist < closest) {
            				closest = dist;
            				clEnt = entity1;
            			}
	            		
	            		//break;
	            	}
	            }
	        }
	        
	        if (clEnt != null) { 
	        	//alertHunters(clEnt);
	        	return 1;
	        } else {
	        	return 0;
	        }
	        
	        
		} else {
			return -1;
		}
		
		
		
		//if threat - setstate moving -> village
	}
	
	public boolean checkHealth() {
		//dont flee if attacking player, go relentless
		if (entityToAttack instanceof EntityPlayer) {
			return false;
		}
		if (player.getHealth() < player.getMaxHealth() * 0.65) {
			return true;
		}
		return false;
	}
	
	public void updateJob() {
		//setEntityDead();
		
		//NEW SYSTEM!
		//if (job.curJobs.size() > 0) {
		job.tick();
	}
	
	public boolean eat() {
		
		if(!this.isUsing && player.inventory.mainInventory[this.slot_Food] != null && isFood(player.inventory.mainInventory[this.slot_Food])) {
			this.setCurrentSlot(this.slot_Food);
			rightClickItem();
			return true;
		}
		/*for(int j = 0; j < player.inventory.mainInventory.length; j++)
        {
            if(player.inventory.mainInventory[j] != null && isFood(player.inventory.mainInventory[j]))
            {
            	//inventory.consumeInventoryItem(j);
            	//setCurrentItem(mod_tropicraft.fishingRodTropical.shiftedIndex);
            	this.setCurrentSlot(j);
        		
            	//health = fakePlayer.health;
            	return true;
            }
        }*/
		return false;
	}
	
	public boolean isFood(ItemStack itemstack) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			if (item instanceof ItemFood) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isWantedItemorEXP(Entity ent) {
		if (ent.isInWater()) return false;
		if (ent instanceof EntityItem) {
			if (((EntityItem) ent).age > 60) {
				return true;
			}
			//if (wantedItems.contains(((EntityItem) ent).item.itemID + 256)) {
				//return true;
			//}
		}
		if (ent instanceof EntityArrow) {
			if (((EntityArrow) ent).canBePickedUp == 1) return true;
		}
		if (ent instanceof EntityXPOrb) {
			return true;
		}
		return false;
		
	}
	
	public void manageInventory() {
		int count = 0;
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			if (player.inventory.mainInventory[i] != null) count++;
		}
		
		if (count != this.lastItemCount) {
			inventoryChanged(count);
			System.out.println("New inv count: " + count);
		}
	}
	
	public void inventoryChanged(int amount) {
		lastItemCount = amount;
		
	}
	
	public void PFCallBackChecker() {
		if (c_CoroAIUtil.newPath) {
			pathToEntity = c_CoroAIUtil.pathToEntity;
			if (pathToEntity != null) {
				pathToEntity.incrementPathIndex();
			}
			c_CoroAIUtil.newPath = false;
		}
		
	}
	
	public void AIPlayerTick() {
		
		if (!enableAI) return;
		
		PFCallBackChecker();
		
		//debug stuff
		cooldown_Pathfind = 40;
		maxReach_Ranged = 40F;
		
		
		
		if (attackPlayers) {
			cooldown_Melee = 1;
			maxReach_Melee = 6F;
		} else {
			cooldown_Melee = 10;
			maxReach_Melee = 5F;
		}
		
		if (zcMode) {
			cooldown_Ranged = 5;
			maxReach_Melee = 1.5F;
			maxReach_Ranged = 60F;
			slot_Ranged = 0;
			slot_Melee = 1;
		}
		
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow) {
			cooldown_Ranged = 30;
		} else {
			cooldown_Ranged = 40;
		}
		
		//Cooldowns
		if (curCooldown_Melee > 0) { curCooldown_Melee--; }
        if (curCooldown_Ranged > 0) { curCooldown_Ranged--; }
        if (c_AIP.i.curCooldown_Pathfind > 0) c_AIP.i.curCooldown_Pathfind--;
        
        //System.out.println("what: " + entityToAttack);
        
		if (entityToAttack != null && entityToAttack.isEntityAlive()) {
			
			float var2 = this.entityToAttack.getDistanceToEntity(player);

            if (entityToAttack instanceof EntityLiving && player.canEntityBeSeen(this.entityToAttack))
            {
            	//mc.playerController.attackEntity(player, entityToAttack)
            	attackEntity(this.entityToAttack, var2);
            }
            else
            {
            	//player.attackBlockedEntity(this.entityToAttack, var2);
            }
		}
		
		if (zcMode) {
			if (jobFleeFrom != null) {
				entityToAttack = jobFleeFrom;
				//if (var2 < maxReach_Ranged) {
		    		//System.out.println("contemplate zc flee ranged");
		    		if (curCooldown_Ranged <= 0) {
		    			setCurrentSlot(slot_Ranged);
		        		rightClickItem();
		        		//System.out.println("try zc flee ranged");
		        		this.curCooldown_Ranged = cooldown_Ranged * 4;
		    		}
		    	//}
			}
		}
		
		dangerLevel = 0;
		if (checkSurroundings() == 1) dangerLevel = 1;
		if (checkHealth()) dangerLevel = 2;
		job.getJobClass().checkHunger();
		
		//Safety overrides
		fleeing = false;
		//Safe
		if (dangerLevel == 0) {
			updateJob();
			//this.moveSpeed = oldMoveSpeed;
			
		//Enemy detected? (by alert system?)
		} else if (dangerLevel == 1) {
			//no change for now
			updateJob();
			//this.moveSpeed = oldMoveSpeed;
			
		//Low health, avoid death
		} else if (dangerLevel == 2) {
			
			//If nothing to avoid
			if (!job.getJobClass().avoid(true)) {
				fleeing = false;
				//no danger in area, try to continue job
				updateJob();
				//this.moveSpeed = oldMoveSpeed;
			} else {
				fleeing = true;
				job.getJobClass().onLowHealth();
				
				//code to look ahead 1 node to speed up the pathfollow escape - WARNING, THIS BREAKS CAREFULL NAV AROUND PITS!!! also cactuses
				/*if (pathToEntity != null && pathToEntity.points != null) {
					int pIndex = pathToEntity.pathIndex+1;
					if (pIndex < pathToEntity.points.length) {
						if (worldObj.rayTraceBlocks(Vec3D.createVector((double)pathToEntity.points[pIndex].xCoord + 0.5D, (double)pathToEntity.points[pIndex].yCoord + 1.5D, (double)pathToEntity.points[pIndex].zCoord + 0.5D), Vec3D.createVector(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ)) == null) {
							pathToEntity.pathIndex++;
						}
					}
				}*/
				
				//this.moveSpeed = fleeSpeed;
			}
		}
		
		wallOfPain();
		
		//System.out.println("isUsingTicks: " + isUsingTicks);
		
		//Aiming fixing depending on using charge or pathfinding
		if (isUsingTicks < cooldown_Ranged-4) {
			if (pathToEntity != null) {
				//System.out.println("pathupdateItemChargeing + " + pathToEntity.pathLength);
				pathFollow();
				player.rotationPitch = 0F;
			} else {
				//System.out.println("not pathing");
			}
			
			
		} else {
			
			if (this.entityToAttack instanceof EntityItem && player.getDistanceToEntity(entityToAttack) < 0.5F) {
				player.faceEntity(entityToAttack , 1F, 1F);
				player.moveFlying(0F, -player.rotationPitch, 0.15F);
			}
			
			//pathFollow();
			//if (this.entityToAttack != null) player.faceEntity(this.entityToAttack, 180, 180);
			//player.rotationPitch -= 3F;
		}
		
		updateItemCharge();
		manageInventory(); //move this to job class?
		
		if (player.isInWater()) player.motionY += 0.03D;
		
		if (currentAction == EnumActState.IDLE && job.getJobState() == EnumJobState.IDLE) {
			
			job.getJobClass().onIdleTick();
			
			if (orders == c_EnumAIPOrders.WANDER) {
				this.updateWanderPath(32);
			}
			
			//Anti clump code
			if (pathToEntity != null && pathToEntity.points != null && pathToEntity.points.length > 0) {
				walkingTimeout--;
				if (walkingTimeout <= 0) {
					//PFQueue.getPath(this, (int)targX, (int)targY, (int)targZ, maxPFRange);
					//PFQueue.getPath(var0, var1, MaxPFRange);
					//setState(EnumActState.WALKING);
					this.pathToEntity = null;
					System.out.println("path reset idle out");
					//walkingTimeout = 600;
				}
			}
			
		} else if (currentAction == EnumActState.FIGHTING) {
			actFight();
		} else if (currentAction == EnumActState.WALKING) {
			actWalk();
		} else if (currentAction == EnumActState.FOLLOWING) {
			actFollow();
		}
	}
	
	public void actFight() {
		//a range check maybe, but why, strafing/dodging techniques or something, lunging forward while using dagger etc...
		if (entityToAttack == null || entityToAttack.isDead || (entityToAttack instanceof EntityLiving && ((EntityLiving)entityToAttack).deathTime > 0)) {
			entityToAttack = null;
			setState(EnumActState.IDLE);
		}
	}
	
	public void actWalk() {
		walkingTimeout--;
		//System.out.println(this.getDistance(targX, targY, targZ));
		if (player.getDistance(targX, targY, targZ) < 1F || i.pathToEntity == null) {
			curCooldown_Pathfind = 0;
			this.setPathToEntity((PathEntityEx)null);
			setState(EnumActState.IDLE);
		} else if (walkingTimeout <= 0) {
			setState(EnumActState.IDLE);
		}
	}
	
	public void setPathToEntity(PathEntityEx pathEntityEx) {
		pathToEntity = pathEntityEx;
		// TODO Auto-generated method stub
		
	}

	//old code, remake entirely
	public void actFollow() {
		//temp
		EntityLiving entityplayer = null;
		followTarget = entityplayer;
		
		if (followTarget != null) {			
			//Player follow logic
	        if(entityplayer != null && rand.nextInt(30) < 3)
	        {
	            float f = entityplayer.getDistanceToEntity(player);
	            if(f > 10F)
	            {
	                getPathOrWalkableBlock(entityplayer, f);
	            }
	        }
		}
	}
	
	public void getPathOrWalkableBlock(Entity entity, float f)
	{
    	PathEntityEx pathentity = null;//worldObj.getPathToEntity(this, entity, 16F, false, false, false, false);
		if(pathentity == null && f > 12F)
		{
			int i = MathHelper.floor_double(entity.posX) - 2;
			int j = MathHelper.floor_double(entity.posZ) - 2;
			int k = MathHelper.floor_double(entity.boundingBox.minY);
			for(int l = 0; l <= 4; l++)
			{
				for(int i1 = 0; i1 <= 4; i1++)
				{
					if((l < 1 || i1 < 1 || l > 3 || i1 > 3) && worldObj.isBlockNormalCube(i + l, k - 1, j + i1) && !worldObj.isBlockNormalCube(i + l, k, j + i1) && !worldObj.isBlockNormalCube(i + l, k + 1, j + i1))
					{
						//replace this with op powered teleport if it is to be used
						player.setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, player.rotationYaw, player.rotationPitch);
						return;
					}
				}

			}

		} else
		{
			setPathToEntity(pathentity);
		}
	}
	
	//hmmmmmmm\\
	
	public void updateWanderPath(int range)
    {
        //Profiler.startSection("stroll");
        boolean flag = false;
        int i = -1;
        int j = -1;
        int k = -1;
        float f = -99999F;
        for (int l = 0; l < 10; l++)
        {
            int i1 = MathHelper.floor_double((player.posX + (double)rand.nextInt(range)) - range/2);
            int j1 = MathHelper.floor_double((player.posY + (double)rand.nextInt(range/2)) - range/4);
            int k1 = MathHelper.floor_double((player.posZ + (double)rand.nextInt(range)) - range/2);
            float f1 = 0F;//player.getBlockPathWeight(i1, j1, k1);
            if (f1 > f)
            {
                f = f1;
                i = i1;
                j = j1;
                k = k1;
                flag = true;
            }
        }

        if (flag)
        {
        	walkTo(player, i, j, k, this.maxPFRange, 600);
        }
        //Profiler.endSection();
    }
	
	public HashMap<EntityLiving, Long> lastHitTime = new HashMap();

	
	
	public void wallOfPain() {
		
		float range = 3.5F;
		float cooldown = 500;
		int hurtTimeMin = 0;
		
		if (zcMode) {
			range = 2.5F;
		}
		
		if (attackPlayers) {
			range = 5.5F;
			cooldown = 100;
			hurtTimeMin = 3;
		}
		
		
		
		if (wallOfPainToggle/* && hitCooldown == 0*/) {
            List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.boundingBox.expand(range, range, range));

            for(int count = 0; count < list.size(); ++count) {
               Entity var5 = (Entity)list.get(count);

               if (var5 instanceof EntityLiving) {
            	   
            	   //System.out.println(var5 + ": " + ((EntityLiving)var5).getAttackTarget());
	               if(isEnemy(var5) && player.canEntityBeSeen(var5) /* && !isFeared(var5)*/ /*&& !(var5 instanceof EntityPlayer) */&& ((EntityLiving) var5).hurtTime <= hurtTimeMin) {
	
		            	if (!lastHitTime.containsKey(((EntityLiving) var5)) || System.currentTimeMillis() - lastHitTime.get(((EntityLiving) var5)).longValue() > cooldown) {
		            		//System.out.println(var5 + ": " + ((EntityLiving) var5).hurtTime);
			               	//hitCooldown = 10;
			               	lastHitTime.put(((EntityLiving) var5), System.currentTimeMillis());
			               	
			               	//if (!this.isUsing) {
			               		this.setCurrentSlot(this.slot_Melee);
			               	//}
			               	mc.playerController.attackEntity(player, var5);
			               	player.swingItem();
		            	}
	               }
               }
            }
        }
	}
	
	public void pathFollow() {
    	
		PathEntityEx path = pathToEntity;
		EntityPlayer theplayer = player;
		
		theplayer = ModLoader.getMinecraftInstance().thePlayer;
	
        Vec3 var5 = path.getPosition(theplayer);
        
        double minDistToNode = (double)(theplayer.width * 1.2F);

        //????
        dangerZone = true;
        
        if (dangerZone) minDistToNode = (double)(theplayer.width * 0.9F);
        
        while(var5 != null && squareDistanceToXZ(theplayer.posX, theplayer.posZ, var5.xCoord, var5.zCoord) < minDistToNode * minDistToNode) {
            path.incrementPathIndex();

            if(path.isFinished()) {
            	//System.out.println("path end");
            	curCooldown_Pathfind = 0;
                var5 = null;
                path = null;
            } else {
                var5 = path.getPosition(theplayer);
            }
        }
        
        if (path != null) { 
        	if (path.pathIndex >= path.pathLength || player.isDead) {
	        	pathToEntity = null;
	        	curCooldown_Pathfind = 0;
	        	//System.out.println("path remove");
        	}
        }

        int var21 = MathHelper.floor_double(theplayer.boundingBox.minY + 0.5D);
        float angle = 0F;

        if(var5 != null) {
            double var8 = var5.xCoord - theplayer.posX;
            double var10 = var5.zCoord - theplayer.posZ;
            double var12 = var5.yCoord - (double)var21;
            float var14 = (float)(Math.atan2(var10, var8) * 180.0D / 3.1415927410125732D) - 90.0F;
            float var15 = var14 - theplayer.rotationYaw;

            for(angle = 0.7F; var15 < -180.0F; var15 += 360.0F) {
                ;
            }

            while(var15 >= 180.0F) {
                var15 -= 360.0F;
            }

            float max = 80F;
            if (dangerZone) max = 160F;
            
            if(var15 > max) {
                var15 = max;
            }

            if(var15 < -max) {
                var15 = -max;
            }

            theplayer.rotationYaw += var15;

            if(var12 > 0.0D || theplayer.handleWaterMovement() || theplayer.handleLavaMovement()) {
                theplayer.isJumping = true;
            }

            if (theplayer.isJumping) {
                //this.A
                if (theplayer.onGround) {
                	jump(theplayer);
                	//theplayer.jump();
                    //performMove(keys[4], false, false);
                }
            }
        }

        //float speed = theplayer.cg;
        //theplayer.cg = 0.1F;
        //theplayer.moveEntityWithHeading(0F,angle);
        
        float speed = 0.15F;
        
        if (theplayer.onGround) {
        	if (fleeing) {
        		speed = 0.2F;
        	} else {
        		
        	}
        } else {
        	speed = 0.02F;
        }
        
        if (theplayer.isInWater()) {
        	speed *= 2F;
        }
        
        theplayer.moveFlying(0F, angle, speed);
        
        if (theplayer.onGround && theplayer.isCollidedHorizontally) {
        	jump(theplayer);
		}
        
        //theplayer.moveEntityWithHeading(0F,angle);
        //theplayer.cg = speed;
    }
	
	public void jump(EntityPlayer theplayer) {
		
		theplayer.motionY = 0.4F;
		theplayer.isAirBorne = true;
	}
	
	public double squareDistanceToXZ(double par1, double par5, double x, double z)
    {
        double var7 = par1 - x;
        double var11 = par5 - z;
        return var7 * var7 + var11 * var11;
    }
	
	public void huntTarget(Entity ent, int pri) {
		//PFQueue.getPath(this, ent, maxPFRange, pri);
		if (PFQueue.instance == null) {
    		new PFQueue(ent.worldObj);
    	}
		//c_AIP.i.pathToEntity = PFQueue.instance.createEntityPathTo(player, ent, 256.0F);
		PFQueue.getPath(player, ent, 256.0F, pri);
		/*c_AIP.i.pathToEntity = PFQueue.instance.convertToPathEntityEx(player.worldObj.getPathEntityToEntity(player, ent, maxPFRange, true, true, true, true));
		PathPointEx var11 = new PathPointEx(MathHelper.floor_float(player.width + 1.0F), MathHelper.floor_float(player.height + 1.0F), MathHelper.floor_float(player.width + 1.0F));
		c_AIP.i.pathToEntity = PFQueue.instance.simplifyPath(c_AIP.i.pathToEntity, var11);*/
		//System.out.println("pl huntTarget call: " + ent.getDistanceToEntity(player) + " - " + ent);
		this.entityToAttack = ent;
		setState(EnumActState.FIGHTING);
	}
	
	public void huntTarget(Entity ent) {
		huntTarget(ent, 0);
	}
	
	public void aimAtEnt(Entity ent) {
    	player.faceEntity(ent, 180, 180);
    	if (player.worldObj.isRemote && player instanceof EntityClientPlayerMP) {
    		((EntityClientPlayerMP)player).sendMotionUpdates();
    		//System.out.println("woot");
    	}
    }
	
	public void setCurrentSlot(int slot) {
    	if (player.inventory == null) { return; }
    	player.inventory.currentItem = slot;
    	//sync();
    }
	
	public boolean shouldUseArrow(Entity target) {
		
		//temp override for testing
		if (zcMode) return true;
		
		if (target instanceof EntitySkeleton) return true;
		if (isThreat(target)) return true;
		return false;
	}
	
	protected void attackEntity(Entity var1, float var2) {
    	//sync();
    	//aimAtEnt(var1);
    	//if (isAimedAtTarget(var1)) {
		
		/*https://gist.github.com/fc60ef90e1a192d37efc
		double var6;

		if (facee instanceof EntityLiving) {
		EntityLiving var10 = (EntityLiving) facee;
		var6 = facer.posY + (double) facer.getEyeHeight() - (var10.posY + (double) var10.getEyeHeight());
		} else {
		var6 = (facee.boundingBox.minY + facee.boundingBox.maxY) / 2.0D - (facer.posY + (double) facer.getEyeHeight());
		}
	
		double var14 = (double) MathHelper.sqrt_double(var4 * var4 + var8 * var8);
		float var12 = (float) (Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
		float var13 = (float) (-(Math.atan2(var6, var14) * 180.0D / Math.PI));
		float yMod = 0F;
		double dist = facer.getDistanceToEntity(facee);
		if(facee.posY > facer.posY){
		double diff = facee.posY - facer.posY;
		yMod -= (((diff/8)*dist)/8)-2;
		}
		facer.rotationPitch = updateRotation(facer.rotationPitch, -var13+yMod, vSpeed);
		facer.rotationYaw = updateRotation(facer.rotationYaw, var12, hSpeed);	*/
		//System.out.println("maxReach_Ranged: " + maxReach_Ranged);
		
	    	if (var2 < maxReach_Melee && var1.boundingBox.maxY > player.boundingBox.minY && var1.boundingBox.minY < player.boundingBox.maxY) {
	    		if (curCooldown_Melee <= 0) {
	    			setCurrentSlot(slot_Melee);
	        		leftClickItem(var1);
	        		this.curCooldown_Melee = cooldown_Melee;
	        	}
	    	} else if (var2 < maxReach_Ranged) {
	    		//System.out.println("contemplate ranged");
	    		if (shouldUseArrow(var1) && curCooldown_Ranged <= 0) {
	    			setCurrentSlot(slot_Ranged);
	        		rightClickItem();
	        		//System.out.println("try ranged");
	        		this.curCooldown_Ranged = cooldown_Ranged;
	    		}
	    	}
    	//}
    }
	
	public void leftClickItem(Entity var1) {
    	try {
    		//player.attackTargetEntityWithCurrentItem(var1);
    		mc.playerController.attackEntity(player, var1);
    		player.swingItem();
    	} catch (Exception ex) {
    		//ex.printStackTrace();
    	}
    }
	
	public void rightClickItem() {
		
        
        //System.out.println(isUsingTicks);
        if (!isUsing) {
        	/*ItemStack var10 = player.inventory.getCurrentItem();

            if (var10 != null && mc.playerController.sendUseItem(player, worldObj, var10))
            {
            	
            }*/
        	isUsing = true;
        	//System.out.println("charge");
        	
        } else {
        	//System.out.println("release");
        	
        }
	}
	
	public void updateItemCharge() {
		
		
		
		if (isUsing) {
			
			
			
			isUsingTicks++;
			
			if (/*isUsingTicks == 2*/isUsingTicks <= cooldown_Ranged-4) {
				if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow) {
					
					
					
					mc.gameSettings.keyBindUseItem.pressed = true;
					ItemStack var10 = player.inventory.getCurrentItem();
		
			        if (var10 != null && mc.playerController.sendUseItem(player, worldObj, var10)) { }
				} else if (isUsingTicks == 2) {
					mc.gameSettings.keyBindUseItem.pressed = true;
					ItemStack var10 = player.inventory.getCurrentItem();
		
			        if (var10 != null && mc.playerController.sendUseItem(player, worldObj, var10)) { }
				}
			} else if (isUsingTicks == cooldown_Ranged) {
				//System.out.println("end");
				
				isUsing = false;
				isUsingTicks = 0;
				mc.gameSettings.keyBindUseItem.pressed = false;
				mc.playerController.onStoppedUsingItem(player);
				
			} else/* if (isUsingTicks > cooldown_Ranged-4)*/ {
				//if (isUsingTicks > cooldown_Ranged-4) {
				
				//System.out.println(isUsingTicks);
				
				if (entityToAttack != null) aimAtEnt(entityToAttack);
				
				
			//}
			
			}
			
			if (/*isUsingTicks > cooldown_Ranged-10*/isUsingTicks > 2) {
				
				
			}
			
			if (isUsingTicks < 3 || (isUsingTicks > cooldown_Ranged-6 && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow)) {
				if (this.entityToAttack != null) {
					
					//this.aimAtEnt(entityToAttack);
					player.faceEntity(entityToAttack, 360, 30);
					
					if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow) {
						this.player.rotationPitch -= 4;
					}
				}
			}
		} else {
			isUsingTicks = 0;
		}
		
		if (player.getCurrentEquippedItem() != null) {
			if (zcMode || player.getCurrentEquippedItem().getItem() instanceof ItemBow || player.getCurrentEquippedItem().getItem() instanceof ItemFood) {
				
			} else if (player.getCurrentEquippedItem().getItem() instanceof ItemFishingRod) {
				isUsing = false;
				isUsingTicks = 0;
				mc.gameSettings.keyBindUseItem.pressed = false;
				mc.playerController.onStoppedUsingItem(player);
			} else {
				isUsing = false;
				isUsingTicks = 0;
				mc.gameSettings.keyBindUseItem.pressed = false;
				mc.playerController.onStoppedUsingItem(player);
			}
		} else {
			isUsing = false;
			isUsingTicks = 0;
			mc.gameSettings.keyBindUseItem.pressed = false;
			mc.playerController.onStoppedUsingItem(player);
		}
		
	}
	
	public void walkTo(Entity var1, int x, int y, int z, float var2, int timeout) {
		walkTo(var1, x, y, z, var2, timeout, 0);
	}
	
	public void walkTo(Entity var1, int x, int y, int z, float var2, int timeout, int priority) {
		
		if (c_AIP.i.curCooldown_Pathfind <= 0) {
			c_AIP.i.curCooldown_Pathfind = c_AIP.i.cooldown_Pathfind;
			PFQueue.getPath(var1, x, y, z, maxPFRange, priority);
			/*c_AIP.i.pathToEntity = null;//PFQueue.instance.createEntityPathTo(var1, x, y, z, maxPFRange);
	    	if (c_AIP.i.pathToEntity == null) {
	    		//System.out.println("failllll");
	    		c_AIP.i.pathToEntity = PFQueue.instance.convertToPathEntityEx(var1.worldObj.getEntityPathToXYZ(var1, x, y, z, maxPFRange, true, true, true, true));
	    		PathPointEx var11 = new PathPointEx(MathHelper.floor_float(var1.width + 1.0F), MathHelper.floor_float(var1.height + 1.0F), MathHelper.floor_float(var1.width + 1.0F));
	    		c_AIP.i.pathToEntity = PFQueue.instance.simplifyPath(c_AIP.i.pathToEntity, var11);
	    	}*/
			c_AIP.i.setState(EnumActState.WALKING);
			walkingTimeout = timeout;
			c_AIP.i.targX = x;
			c_AIP.i.targY = y;
			c_AIP.i.targZ = z;
		}
	}
	
	public void walkTo(Entity var1, Entity target, float var2, int timeout, int priority) {
		if (c_AIP.i.curCooldown_Pathfind <= 0) {
			c_AIP.i.curCooldown_Pathfind = c_AIP.i.cooldown_Pathfind;
			PFQueue.getPath(var1, target, maxPFRange, priority);
			/*c_AIP.i.pathToEntity = null;//PFQueue.instance.createEntityPathTo(var1, x, y, z, maxPFRange);
	    	if (c_AIP.i.pathToEntity == null) {
	    		//System.out.println("failllll");
	    		c_AIP.i.pathToEntity = PFQueue.instance.convertToPathEntityEx(var1.worldObj.getEntityPathToXYZ(var1, x, y, z, maxPFRange, true, true, true, true));
	    		PathPointEx var11 = new PathPointEx(MathHelper.floor_float(var1.width + 1.0F), MathHelper.floor_float(var1.height + 1.0F), MathHelper.floor_float(var1.width + 1.0F));
	    		c_AIP.i.pathToEntity = PFQueue.instance.simplifyPath(c_AIP.i.pathToEntity, var11);
	    	}*/
			c_AIP.i.setState(EnumActState.WALKING);
			walkingTimeout = timeout;
			c_AIP.i.targX = (int)target.posX;
			c_AIP.i.targY = (int)target.posY;
			c_AIP.i.targZ = (int)target.posZ;
			
			//System.out.println("walk to: ");
		}
	}
	
	
	
	
}
