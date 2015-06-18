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
import corobot.ai.memory.helper.HelperInventory;
import corobot.ai.memory.pieces.BlockLocation;
import corobot.util.UtilContainer;
import corobot.util.UtilEnt;
import corobot.util.UtilInventory;
import corobot.util.UtilMemory;

public class TaskMoveToPos extends BehaviorNode {
	
	//for less dynamic scripts
	public Vector3f staticPos;
	
	public int ticksPathing = 0;
	//should be more dynamic, based on distance, or detect if it stops making progress
	public int ticksPathingMax = 300;
	
	public TaskMoveToPos(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}
	
	public TaskMoveToPos(BehaviorNode parParent, Blackboard blackboard, Vector3f staticPos) {
		super(parParent, blackboard);
		this.staticPos = staticPos;
	}

	@Override
	public EnumBehaviorState tick() {
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		Vector3f pos = staticPos;
		if (staticPos == null) {
			pos = getBlackboard().getMoveTo();
		}
		
		if (pos == null) {
			Corobot.dbg("CRITICAL: getBlackboard().getMoveTo() is null!");
			return EnumBehaviorState.FAILURE;
		}
		
		double dist = VecUtil.getDistSqrd(player.getPos(), pos);
		if (dist < 3) {
			
			return EnumBehaviorState.SUCCESS;
			
		} else {
			//TODO: confirm this is ok, other logic should determine where to go, this just retries
			getBlackboard().setMoveToBest(pos);
			
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
