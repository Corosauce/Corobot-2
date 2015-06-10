package corobot.ai.behaviors.misc;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;

import com.corosus.ai.Blackboard;
import com.corosus.ai.EnumBehaviorState;
import com.corosus.ai.bt.BehaviorNode;
import com.corosus.ai.bt.nodes.leaf.LeafNode;

import corobot.ai.memory.helper.HelperConnect;
import corobot.ai.memory.helper.HelperLogs;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ReconnectToServer extends LeafNode {

	private int reconnectTimer;
	private String serverURL = "";
	private int serverPort = -1;

	public ReconnectToServer(BehaviorNode parParent, Blackboard blackboard) {
		super(parParent, blackboard);
	}

	@Override
	public EnumBehaviorState tick() {
		
		//TODO: obfuscated lookups
		if (serverURL.equals("")) {
			String currentServerURL = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "serverName");
			if (!currentServerURL.equals("")) {
				serverURL = currentServerURL;
				int currentServerPort = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "serverPort");
				if (currentServerPort != 0) {
					serverPort = currentServerPort;
				} else {
					serverPort = 25565;
				}
			}
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.currentScreen instanceof GuiDisconnected) {
			HelperConnect.setFirstTimeInitSinceConnect(true);
    		if (reconnectTimer < 40) {
    			reconnectTimer++;
    		} else {
    			reconnectTimer = 0;
    			if (!serverURL.equals("")) {
    				HelperLogs.appendLogLineToFile("Lost connection, retrying connection to: " + serverURL + " at port " + serverPort, HelperLogs.getLogPath() + File.separator + "state.txt");
    				mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, serverURL, serverPort));
    			}
	        	
    		}
            //mc.displayGuiScreen((GuiScreen)null);
        }
		
		return EnumBehaviorState.SUCCESS;
	}

}
