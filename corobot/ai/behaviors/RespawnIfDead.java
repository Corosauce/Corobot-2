package corobot.ai.behaviors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNodeBB;
import com.corosus.entity.IEntity;

import corobot.ai.PlayerAI;

public class RespawnIfDead extends LeafNodeBB {

	private int respawnTimer;

	public RespawnIfDead(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof GuiGameOver || (mc.currentScreen instanceof GuiChat && mc.thePlayer.getHealth() <= 0)) {
    		if (respawnTimer < 40) {
    			respawnTimer++;
    		} else {
    			respawnTimer = 0;
    			mc.thePlayer.respawnPlayer();
	        	mc.displayGuiScreen(new GuiChat(""));
    		}
            //mc.displayGuiScreen((GuiScreen)null);
        }
		
		return super.tick();
	}

}
