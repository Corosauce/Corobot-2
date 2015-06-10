package corobot.ai.behaviors.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;
import com.corosus.entity.IEntity;

import corobot.ai.PlayerAI;

public class OpenGUIChatWhenNeeded extends LeafNode {

	private int respawnTimer;

	public OpenGUIChatWhenNeeded(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen == null) {
			if (mc.theWorld.getTotalWorldTime() % 20 == 0) {
				//mc.displayGuiScreen(new GuiChat(""));
			}
        }
		
		return super.tick();
	}

}
