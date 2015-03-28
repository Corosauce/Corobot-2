package corobot;

import net.minecraft.client.Minecraft;
import corobot.ai.PlayerAI;

public class Corobot {

	public static PlayerAI playerAI;
	
	public static void init() {
		playerAI = new PlayerAI();
	}
	
	public static void tickUpdate() {
		try {
			if (!Minecraft.getMinecraft().isGamePaused()) {
				playerAI.tickUpdate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static PlayerAI getPlayerAI() {
		return playerAI;
	}
	
}
