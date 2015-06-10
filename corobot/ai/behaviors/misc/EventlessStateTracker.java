package corobot.ai.behaviors.misc;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;

import corobot.ai.PlayerAI;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class EventlessStateTracker extends LeafNode {
	
	/**
	 * So there arent many events on the client we can rely on
	 * - use this class to track what was closest to you on death
	 * - validate death by mob
	 * -- account for proximity
	 * --- melee mobs must be super close
	 * --- ranged mobs, uhhhhhh
	 * ---- ranged mobs with melee mobs close
	 * -- account for falldeaths
	 * - VERIFY BY DEATH MESSAGE!
	 */
	
	public boolean wasAlive = true;
	public boolean scanningForReason = false;
	public int waitTime = 0;
	public String lastReasonOfDeath = "";
	
	public EventlessStateTracker(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		PlayerAI playerAI = (PlayerAI) this.getBlackboard().getAgent().getActor();
		EntityPlayer player = playerAI.bridgePlayer.getPlayer();
		Minecraft mc = Minecraft.getMinecraft();
		
		if (player.getHealth() <= 0) {
			if (wasAlive) {
				System.out.println("SCAN FOR REASON!");
				scanningForReason = true;
				waitTime = 10;
			}
			wasAlive = false;
		} else {
			wasAlive = true;
		}
		
		if (scanningForReason && waitTime > 0) {
			waitTime--;
			//TODO: reflection for obf name
			List msgs = ReflectionHelper.getPrivateValue(GuiNewChat.class, mc.ingameGUI.getChatGUI(), "chatLines");
			if (msgs.size() > 0) {
				String str = ((ChatLine) msgs.get(0)).func_151461_a().getFormattedText();
				if (str.contains(player.getGameProfile().getName())) {
					//TODO: filter down messages more to entity name, regex
					lastReasonOfDeath = str;
					scanningForReason = false;
					System.out.println("DEATH: " + lastReasonOfDeath);
					mc.ingameGUI.getChatGUI().clearChatMessages();
				}
				
			}
			
		}
		
		return super.tick();
	}

}
