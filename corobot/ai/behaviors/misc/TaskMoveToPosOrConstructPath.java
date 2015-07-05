package corobot.ai.behaviors.misc;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.corosus.ai.AIBTAgent;
import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.minigoap.IWorldStateProperty;
import com.corosus.ai.minigoap.PlanPiece;
import com.corosus.entity.IEntity;
import com.corosus.util.VecUtil;
import com.corosus.world.IWorld;

import corobot.Corobot;
import corobot.ai.BlackboardImpl;
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.util.UtilContainer;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class TaskMoveToPosOrConstructPath extends BehaviorNode {
	
	//for less dynamic scripts
	public Vector3f staticPos;
	
	public int ticksPathing = 0;
	//should be more dynamic, based on distance, or detect if it stops making progress
	public int ticksPathingMax = 300;
	
	public TaskConstructPath taskConstructPath = null;
	
	public TaskMoveToPosOrConstructPath(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
		taskConstructPath = new TaskConstructPath(this, getBlackboard());
	}
	
	public TaskMoveToPosOrConstructPath(BehaviorNode parParent, Blackboard blackboard, Vector3f staticPos) {
		this(parParent, blackboard);
		this.staticPos = staticPos;
	}

	@Override
	public EnumBehaviorState tick() {
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		BlackboardImpl bb = (BlackboardImpl) agent.getBlackboard();
		
		Vector3f pos = staticPos;
		if (staticPos == null) {
			pos = getBlackboard().getMoveTo();
		}
		
		if (pos == null) {
			Corobot.dbg("CRITICAL: getBlackboard().getMoveTo() is null!");
			return EnumBehaviorState.FAILURE;
		}
		
		double dist = VecUtil.getDistSqrd(player.getPos(), pos);
		if (dist < 2) {
			
			return EnumBehaviorState.SUCCESS;
			
		} else {
			boolean triedToPath = getBlackboard().setMoveToBest(pos);
			
			if (triedToPath) {
				boolean pathFail = false;
				//detect success of path setting
				if (getBlackboard().getPath().listPathnodes.size() == 0) {
					pathFail = true;
				//verify we can compare like this
				//for checking of path actually completed entirely
				} else if (!getBlackboard().getPath().getLastMoveTo().equals(pos)) {
					Corobot.dbg("maybe not an issue, but, path ended this far from target pos: " + VecUtil.getDistSqrd(pos, getBlackboard().getPath().getLastMoveTo()));
					//hmm
				}
				
				if (pathFail) {
					Corobot.dbg("pathfind to " + pos + " failed to get any path! dist to dest: " + VecUtil.getDistSqrd(pos, player.getPos()));
					bb.setPathConstructEnd(pos);
					return taskConstructPath.tick();
					//return EnumBehaviorState.FAILURE;
				}
			}
			
			boolean alwaysLook = true;
			int lookSpeed = 5;
			if (alwaysLook) {
				UtilEnt.facePos(Corobot.playerAI.bridgePlayer.getPlayer(), pos, lookSpeed, 90);
				Corobot.playerAI.bridgePlayer.getPlayer().rotationPitch += 30;
			}
			
			ticksPathing++;
			if (ticksPathing >= ticksPathingMax) {
				return EnumBehaviorState.FAILURE;
			}
		}
		
		return EnumBehaviorState.RUNNING;
	}
	
	@Override
	public void reset() {
		super.reset();
		ticksPathing = 0;
	}

}
