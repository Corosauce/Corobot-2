package corobot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.DamageSource;

import CoroAI.entity.EnumJobState;
import CoroAI.entity.JobManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class c_Job_Player_Follow extends c_Job_Player {
	
	public long followRange = 8;
	public boolean dontStray = true;
	
	//public String owner = "Corosus";
	
	
	
	public c_Job_Player_Follow(JobManager jm) {
		super(jm);
		
		
		//owners.add("George");
	}
	
	@Override
	public void tick() {
		jobHunter();
	}
	
	@Override
	public boolean shouldExecute() {
		return !c_AIP.i.enemyClose;
	}
	
	@Override
	public boolean shouldContinue() {
		/*EntityPlayer entP = getOwner();
		if (entP != null && entP.getDistanceToEntity(c_AIP.i.player) < followRange) {
			return false;
		}*/
		return true;//ent.entityToAttack == null;
	}

	@Override
	public void onLowHealth() {
		/*if (hitAndRunDelay == 0 && ent.getDistanceToEntity(ent.lastFleeEnt) > 6F) {
			hitAndRunDelay = ent.cooldown_Ranged+1;
			ent.entityToAttack = ent.lastFleeEnt;
			if (ent.entityToAttack != null) ent.faceEntity(ent.entityToAttack, 180F, 180F);
		} else {
			ent.entityToAttack = null;
		}*/
	}
	
	@Override
	public void hitHook(DamageSource ds, int damage) {
		if (c_AIP.i.isEnemy(ds.getEntity())) {
			c_AIP.i.entityToAttack = ds.getEntity();
		}
		
		/*if (ent.getHealth() < ent.getMaxHealth() / 2 && ds.getEntity() == c_CoroAIUtil.getFirstPlayer()) {
			ent.dipl_hostilePlayer = true;
			ent.getGroupInfo(EnumInfo.DIPL_WARN);
		}*/
		 
		
		//temp fun code
		/*if (ds.getEntity() instanceof ZCSdkEntitySentry) {
			ent.entityToAttack = ds.getEntity();
		}*/
	}
	
	@Override
	public void setJobItems() {
		
		//c_CoroAIUtil.setItems_JobHunt(ent);
		
		
	}
	
	public EntityPlayer getOwner() {
		EntityPlayer entP = null;
		for (int i = 0; i < c_AIP.owners.size(); i++) {
			for (int ii = 0; ii < ModLoader.getMinecraftInstance().theWorld.playerEntities.size(); ii++) {
				EntityPlayer ent = (EntityPlayer)ModLoader.getMinecraftInstance().theWorld.playerEntities.get(ii);
				
				if (!ent.isDead && ent.username.equals(c_AIP.owners.get(i)) && ent != c_AIP.i.player) {
					return ent;
				}
			}
			/*entP = ModLoader.getMinecraftInstance().theWorld.getPlayerEntityByName(c_AIP.owners.get(i));
			
			if (entP != null && entP != c_AIP.i.player && entP.getDistanceToEntity(c_AIP.i.player) < 32F) {
				break;
			}*/
		}
		
		
		/*if (entP == null) {
			for (int i = 0; i < ModLoader.getMinecraftInstance().theWorld.playerEntities.size(); i++) {
				EntityPlayer ent = (EntityPlayer)ModLoader.getMinecraftInstance().theWorld.playerEntities.get(i);
				
				if (!ent.isDead && c_AIP.owners.contains(ent.username) && ent != c_AIP.i.player) {
					return ent;
				}
			}
		}*/
		
		
		return entP;
	}
	
	protected void jobHunter() {
	
		//if (true) return;
		
		//
		
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		if (c_AIP.i.orders == c_EnumAIPOrders.STAY_CLOSE) {
			followRange = 6;
		} else if (c_AIP.i.orders == c_EnumAIPOrders.STAY_AROUND) {
			followRange = 24;
		} else if (c_AIP.i.orders == c_EnumAIPOrders.WANDER) {
			followRange = 60;
		}
		
		
		setJobState(EnumJobState.IDLE);
		
		EntityPlayer entP = getOwner();
		
		if (c_AIP.i.entityToAttack == null && entP != null && entP.getDistanceToEntity(c_AIP.i.player) <= 4) {
			//c_AIP.i.entityToAttack = null;
			c_AIP.i.pathToEntity = null;
		}
		
		if (entP != null && (entP.getDistanceToEntity(c_AIP.i.player) > followRange || !entP.canEntityBeSeen(c_AIP.i.player))) {
			c_AIP.i.walkTo(c_AIP.i.player, entP, c_AIP.i.maxPFRange, 600, -1);
			//PFQueue.getPath(c_AIP.i.player, entP, c_AIP.i.maxPFRange);
		}
		
		//Random come home code while idle
		if (entP != null && (c_AIP.i.entityToAttack == null && entP.worldObj.rand.nextInt(100) == 0)) {
			c_AIP.i.walkTo(c_AIP.i.player, entP, c_AIP.i.maxPFRange, 600, -1);
			//PFQueue.getPath(c_AIP.i.player, entP, c_AIP.i.maxPFRange);
		}
	
		if (entP == null && c_AIP.i.player.worldObj.rand.nextInt(100) == 0 && c_AIP.i.entityToAttack == null) {
			c_AIP.i.walkTo(ent, c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ, c_AIP.i.maxPFRange, 600);
		}
			
			//derp
			/*if (ent.entityToAttack == null && ent.rand.nextInt(6000) == 0) {
				ent.walkTo(ent, ent.homeX, ent.homeY, ent.homeZ, ent.maxPFRange, 600);
			}*/
			
		//}
		ent.prevHealth = ent.getHealth();
	}
	
	
	
	public void hunterHitHook(DamageSource ds, int damage) {
		
		/*if (health < getMaxHealth() / 4 * 3) {
			if (ds.getEntity() != null) {
				lastFleeEnt = ds.getEntity();
				tryingToFlee = true;
				//fleeFrom(ds.getEntity());
			}
		}
		prevKoaHealth = health;*/
	}
	
	public boolean sanityCheck(Entity target) {
		//ent field rerouting!
		EntityLiving ent = c_AIP.i.player;
		if (ent.getHealth() < 10) {
			return false;
		}
		
		if (/*target instanceof EntityCreeper || */target instanceof EntityEnderman || target instanceof EntityWolf) {
			return false;
		}
		
		if (dontStray) {
			if (target.getDistance(c_AIP.i.homeX, c_AIP.i.homeY, c_AIP.i.homeZ) > c_AIP.i.maxDistanceFromHome) {
				return false;
			}
		}
		return true;
	}
	
	
	
}
