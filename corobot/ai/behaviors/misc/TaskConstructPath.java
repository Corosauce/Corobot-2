package corobot.ai.behaviors.misc;

import java.util.Random;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.tree.SelectorBoolean;
import com.corosus.ai.bt.nodes.tree.Sequence;
import com.corosus.entity.IEntity;
import com.corosus.util.MutableBoolean;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.behaviors.resources.TaskMineBlock;
import corobot.ai.memory.helper.HelperWorldPatterns;
import corobot.ai.memory.helper.OrePattern;
import corobot.ai.memory.pieces.BlockLocation;

public class TaskConstructPath extends Sequence {

	public boolean startedBuilding = false;
	public Vector3f posStart = null;
	public Vector3f posCur = null;
	public float curDist = 0;
	
	//public Sequence sequenceTasks;
	public MutableBoolean boolShouldMine = new MutableBoolean();
	public MutableBoolean boolShouldWanderSurface = new MutableBoolean();
	
	public TaskConstructPath(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		
		//sequenceTasks = new Sequence(this, getBlackboard());
		
		IdleWander wander = new IdleWander(this, getBlackboard());
		TaskMoveToPos moveTo = new TaskMoveToPos(this, getBlackboard());
		
		SelectorBoolean selBoolWander = new SelectorBoolean(this, getBlackboard(), boolShouldWanderSurface);
		
		SelectorBoolean selBoolMine = new SelectorBoolean(this, getBlackboard(), boolShouldMine);
		selBoolMine.add(new TaskPlaceBlock(selBoolMine, getBlackboard()));
		selBoolMine.add(new TaskMineBlock(selBoolMine, getBlackboard()));

		Sequence seq = new Sequence(this, getBlackboard());
		seq.add(moveTo);
		seq.add(selBoolMine);
		
		selBoolWander.add(seq);
		selBoolWander.add(wander);
		
		add(selBoolWander);
	}

	@Override
	public EnumBehaviorState tick() {
		
		if (getActiveBehaviorIndex() != -1) {
			if (getState() == EnumBehaviorState.RUNNING) {
				EnumBehaviorState result = super.tick();
				//to prevent it resetting everything once 1 dig happens, seems like a sketchy fix, might need better solution later
				if (result == EnumBehaviorState.SUCCESS) {
					return EnumBehaviorState.RUNNING;
				} else {
					return result;
				}
			}
		}
		
		BlackboardImpl bb = (BlackboardImpl) getBlackboard();
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		World worldMC = Minecraft.getMinecraft().theWorld;
		Minecraft mc = Minecraft.getMinecraft();
		
		Vector3f posPlayer = player.getPos();
		posPlayer.y--;

		//TODO: surface block support, atm he goes to 128
		//mostly implemented, reroutes to wandering
		
		//MORE TEMP
		bb.setPathConstructEnd(new Vector3f(posPlayer));
		bb.getPathConstructEnd().add(new Vector3f(50, 0, 0));
		OrePattern orePattern = HelperWorldPatterns.lookupBlockToPattern.get(bb.getBlockToMine());
		if (orePattern != null) {
			
		} else {
			Corobot.dbg("WARNING: missing ore pattern for " + bb.getBlockToMine());
			orePattern = HelperWorldPatterns.lookupBlockToPattern.get(Blocks.redstone_ore);
		}
		
		if (orePattern.isOnSurface()) {
			boolShouldWanderSurface.set(true);
			return super.tick();
		} else {
			boolShouldWanderSurface.set(false);
			bb.getPathConstructEnd().y = orePattern.getYMiddle();
		}
		
		
		//startedBuilding = false;
		
		if (!startedBuilding) {
			
			//TEMP FOR TESTING
			/*bb.setPathConstructEnd(new Vector3f(posPlayer));
			Random rand = new Random();
			if (rand.nextBoolean()) {
				bb.getPathConstructEnd().add(new Vector3f(50, 0, 0));
			} else {
				bb.getPathConstructEnd().add(new Vector3f(0, 0, 50));
			}
			
			if (orePattern.isOnSurface()) {
				
			} else {
				bb.getPathConstructEnd().y = orePattern.getYMiddle();
			}*/
			
			System.out.println("searching for: " + new ItemStack(bb.getBlockToMine()).getDisplayName());
			curDist = 0;
			startedBuilding = true;
			posStart = new Vector3f(posPlayer);
			posCur = new Vector3f(posPlayer);
		}
		
		Vector3f posEnd = bb.getPathConstructEnd();
		Vector3f angle = VecUtil.getAngle(posStart, posEnd);
		//not too steep!
		if (angle.y < -0.49F) {
			angle.y = -0.49F;
		}
		
		if (angle.y > 0.49F) {
			angle.y = 0.49F;
		}
		
		Vector3f posConstruct = new Vector3f(angle);
		posConstruct.scaleAdd(curDist, posStart);
		
		if (VecUtil.getDistSqrd(posConstruct, posEnd) < 2) {
			return EnumBehaviorState.SUCCESS;
		}
		
		int x = MathHelper.floor_float(posConstruct.x);
		int y = MathHelper.floor_float(posConstruct.y);
		int z = MathHelper.floor_float(posConstruct.z);
		
		Block posGround = worldMC.getBlock(x, y, z);
		Block posAir1 = worldMC.getBlock(x, y+1, z);
		Block posAir2 = worldMC.getBlock(x, y+2, z);
		Block posAir3 = worldMC.getBlock(x, y+3, z);
		
		
		System.out.println("cur coord test: " + posConstruct + " - dist: " + VecUtil.getDistSqrd(posConstruct, posEnd));
		
		if (canMine(posAir3)) {
			//need to dig
			boolShouldMine.set(true);
			bb.setMoveToBest(new Vector3f(x, y + 3, z));
			bb.setBlockLocationToMine(new BlockLocation(bb.getMoveTo(), posAir3));
			System.out.println("need to dig 3");
			return super.tick();
		} else if (canMine(posAir2)) {
			//need to dig
			boolShouldMine.set(true);
			bb.setMoveToBest(new Vector3f(x, y + 2, z));
			bb.setBlockLocationToMine(new BlockLocation(bb.getMoveTo(), posAir2));
			System.out.println("need to dig 2");
			return super.tick();
		} else if (canMine(posAir1)) {
			//need to dig
			boolShouldMine.set(true);
			bb.setMoveToBest(new Vector3f(x, y + 1, z));
			bb.setBlockLocationToMine(new BlockLocation(bb.getMoveTo(), posAir1));
			System.out.println("need to dig 1");
			return super.tick();
		} else if (!orePattern.isOnSurface() && !posGround.getMaterial().isSolid() && posGround.getMaterial() != Material.water) {
			//need to place
			boolShouldMine.set(false);
			bb.setMoveToBest(new Vector3f(x, y + 0, z));
			bb.setBlockLocationToPlace(new BlockLocation(bb.getMoveTo(), posGround));
			System.out.println("need to place");
			return super.tick();
		} else {
			//TODO: bug, this doesnt increase x z enough, must guarantee new x z val for stairs
			//curDist += 1;
			
			int xx = x;
			int zz = z;
			
			//TODO: new bug, y val goes too deep..... we need to lock in angle better
			//TODO: yet another bug, he resets mining job probably because sequence hits end and decides to do full reset, we dont want this
			//also, he mines 1 too far away from player, so theres a wall infront of where hes mining, 
			//lets stop and redo all this logic, we need to rethink this stuff, perhaps copy zombie miner code where usefull
			while (x == xx && z == zz) {
				Vector3f angleTest = VecUtil.getAngle(posStart, posEnd);
				if (angle.y < -0.49F) {
					angle.y = -0.49F;
				}
				
				if (angle.y > 0.49F) {
					angle.y = 0.49F;
				}
				
				Vector3f posConstructTest = new Vector3f(angleTest);
				posConstructTest.scaleAdd(curDist, posStart);
				
				xx = MathHelper.floor_float(posConstructTest.x);
				//int yy = MathHelper.floor_float(posConstructTest.y);
				zz = MathHelper.floor_float(posConstructTest.z);
				curDist += 0.5;
			}
			
			System.out.println("increase distance of mine to " + curDist);
			
			//TODO: TEST!!!
			//return super.tick();
		}
		
		//TEMP TESTING, delete these if statements later
		/*if (posAir2 != Blocks.air) {
			//need to dig
			System.out.println("need to dig 1");
		}
		if (posAir1 != Blocks.air) {
			//need to dig
			System.out.println("need to dig 2");
		}
		if (!posGround.getMaterial().isSolid()) {
			//need to place
			System.out.println("need to place");
		}
		curDist += 1;
		*/
		
		
		
		return EnumBehaviorState.RUNNING;
	}
	
	public boolean canMine(Block block) {
		return block != Blocks.air && block.getMaterial() != Material.water;
	}
	
	@Override
	public void reset() {
		System.out.println("path construct reset");
		startedBuilding = false;
		curDist = 0;
		posStart = null;
		posCur = null;
		super.reset();
	}
}
