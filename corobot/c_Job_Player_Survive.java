package corobot;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import CoroAI.PathEntityEx;
import CoroAI.entity.EnumActState;
import CoroAI.entity.EnumJobState;
import CoroAI.entity.InfoResource;
import CoroAI.entity.JobManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class c_Job_Player_Survive extends c_Job_Player {
	
	public float maxCastStr = 1; 
	
	public int fishingTimeout;
	
	public int dryCastX;
	public int dryCastY;
	public int dryCastZ;
	
	public int miningTimeout;
	public List<Integer> gatherables;
	InfoResource ir;
	public int closeTryCount;
	
	public Entity lastFleeEnt;

	public float maxPFRange = 96F;
	
	public c_Job_Player_Survive(JobManager jm) {
		super(jm);
		gatherables = new ArrayList();
		gatherables.add(Block.wood.blockID);
		closeTryCount = 0;
	}
	
	@Override
	public void onJobRemove() {
		if (this.ent.fishEntity != null) {
			this.ent.fishEntity.catchFish();
		}
	}
	
	@Override
	public void tick() {
		jobFisherman();
	}
	
	@Override
	public boolean shouldExecute() {
		return true;
	}
	
	@Override
	public boolean shouldContinue() {
		return false;
	}
	

	@Override
	public void onLowHealth() {
		
	}
	
	@Override
	public void onIdleTick() {
		//super.onIdleTick();
	}
	
	protected void jobFisherman() {
		
		//System.out.println(ent.getClass().toString() + occupationState);
		//ent.setDead();
		
		//if (!(state == EnumJobState.IDLE)) { ent.setEntityToAttack(null); }
		//if (true) return;
		//Finding water, might need delay
		if (state == EnumJobState.IDLE) {
			//moveSpeed = oldMoveSpeed;
			//temp disable
			
		//walking to source
		} else if (state == EnumJobState.W1) {
			
			ent.setState(EnumActState.WALKING);
			//moveSpeed = oldMoveSpeed;
			//if (!ent.isInWater()) {
				if (walkingTimeout <= 0 || ent.getNavigator().getPath() == null) {
					float tdist = (float)ent.getDistance((int)ent.targX, (int)ent.targY, (int)ent.targZ);
					/*if (ent.name.startsWith("Akamu")) {
						int ee = 1;
					}*/
					//findResources(10, 45);
					//ent.walkTo(ent, (int)ent.targX, (int)ent.targY, (int)ent.targZ, ent.maxPFRange, 600);
				}
			//} else {
				/*if (findLandClose()) {
					setJobState(EnumJobState.W4);
				}*/
			//}
			
			if (ent.getDistanceXZ(ent.targX, ent.targZ) < 3F/* || ent.isInWater() || ent.facingWater || nextNodeWater()*/) {
				//Aim at location
				//ent.rotationPitch -= 35;
				//if (ent.canCoordBeSeenFromFeet((int)ent.targX, (int)ent.targY, (int)ent.targZ)) {
					
					ent.setState(EnumActState.IDLE);
					setJobState(EnumJobState.W2);
					ent.setPathExToEntity(null);
					ent.getNavigator().clearPathEntity();
					//ent.walkTo(ent, (int)ent.targX, (int)ent.targY, (int)ent.targZ, ent.maxPFRange, 600);
					//castLine();
					
					//???
					//KoaTribeAI.fishing(this);
				/*} else {
					setJobState(EnumJobState.IDLE);
				}*/
				
				
			}
			
		//Waiting on fish
		} else if (state == EnumJobState.W2) {
			ent.setPathToEntity((PathEntityEx)null);
			ent.getNavigator().clearPathEntity();
			ent.faceCoord((int)ent.targX, (int)ent.targY, (int)ent.targZ, 90, 90);
			if (miningTimeout <= 0) {
				/*harvestBlockInstant();
				ent.rightClickItem();
				
				if (getWoodCount() > 10 || (ent.rand.nextInt(1) == 0 && getWoodCount() >= 8)) {
					//return to base!
					ent.walkTo(ent, ent.homeX, ent.homeY, ent.homeZ, ent.maxPFRange, 600);
					setJobState(EnumJobState.W3);
				} else {
					if (findResources(5, 10)) {
						//setJobState(EnumJobState.IDLE);
					}
					ent.walkTo(ent, (int)ent.targX, (int)ent.targY, (int)ent.targZ, ent.maxPFRange, 600);
					setJobState(EnumJobState.W4);
					
				}*/
				
			} else {
				miningTimeout--;
			}
			
		//Return to base
		} else if (state == EnumJobState.W3) {
			//moveSpeed = oldMoveSpeed;
			if (walkingTimeout <= 0 || ent.getNavigator().getPath() == null) {
				//ent.setPathExToEntity(null);
				ent.walkTo(ent, ent.homeX, ent.homeY, ent.homeZ, ent.maxPFRange, 600);
			}
			if (ent.getDistance(ent.homeX, ent.homeY, ent.homeZ) < 2F) {
				//ent.setPathExToEntity(null);
				//drop off fish in nearby tile entity chest, assumably where homeXYZ is
				ent.faceCoord((int)(ent.homeX-0.5F), (int)ent.homeY, (int)(ent.homeZ-0.5F), 180, 180);
				//transferJobItems(ent.homeX, ent.homeY, ent.homeZ);
				//System.out.println(homeX + " - " + homeZ);
				//set to idle, which will go back to fishing mode
				setJobState(EnumJobState.IDLE);
			}
		//Get back to dry cast spot and cast
		} else if (state == EnumJobState.W4) {
			
			if (ent.getDistance(ent.targX, ent.targY, ent.targZ) <= 1.5F/* || ent.isInWater() || ent.facingWater || nextNodeWater()*/) {
				//Aim at location
				//ent.rotationPitch -= 35;
				//if (ent.canCoordBeSeenFromFeet((int)ent.targX, (int)ent.targY, (int)ent.targZ)) {
					
					ent.setState(EnumActState.IDLE);
					setJobState(EnumJobState.IDLE);
					ent.setPathExToEntity(null);
					ent.getNavigator().clearPathEntity();
					//ent.walkTo(ent, (int)ent.targX, (int)ent.targY, (int)ent.targZ, ent.maxPFRange, 600);
					//castLine();
					
					//???
					//KoaTribeAI.fishing(this);
				/*} else {
					setJobState(EnumJobState.IDLE);
				}*/
				
				
			} else if (walkingTimeout <= 0 || ent.getNavigator().getPath() == null) {
				setJobState(EnumJobState.IDLE);
			}
			
		}
	}
	

	@Override
	public void setJobItems() {
		
		c_AIP.i.wantedItems.add(Item.arrow.shiftedIndex);
		c_AIP.i.wantedItems.add(Item.rottenFlesh.shiftedIndex);
		//c_CoroAIUtil.setItems_JobGather(ent);
		
	}
	
	
	
}
