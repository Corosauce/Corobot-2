package corobot.ai.behaviors.yd;

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

public class PlanMoveToPos extends PlanPiece {
	
	public State state = State.PATHING;
	
	public Vector3f pos;
	public int ticksPathing = 0;
	public int ticksPathingMax = 300;
	
	public enum State {
		PATHING, WAITING_ON_GUI, GUI_OPEN;
	}
	
	public PlanMoveToPos(String planName, Blackboard blackboard, Vector3f pos) {
		super(planName, blackboard);
		
		this.pos = pos;
	}
	
	@Override
	public void initTask(PlanPiece piece, IWorldStateProperty effectRequirement) {
		super.initTask(piece, effectRequirement);
		
		
		
	}

	@Override
	public EnumBehaviorState tick() {
		
		AIBTAgent agent = Corobot.getPlayerAI().agent;
		IWorld world = Corobot.getPlayerAI().bridgeWorld;
		IEntity player = Corobot.getPlayerAI();
		EntityPlayer playerEnt = Corobot.getPlayerAI().bridgePlayer.getPlayer();
		
		
		
		double dist = VecUtil.getDistSqrd(player.getPos(), pos);
		if (dist < 3) {
			
			return EnumBehaviorState.SUCCESS;
			
		} else {
			state = State.PATHING;
			if (world.getTicksTotal() % 40 == 0) {
				player.setMoveTo(pos);
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
