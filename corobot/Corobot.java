package corobot;

import net.minecraft.client.Minecraft;
import corobot.ai.PlayerAI;

public class Corobot {

	/**
	 * Ongoing bugs:
	 * - still drops workbench contents instead of shift clicking back into inv
	 * - cant mine without guichat open
	 * - sometimes gets stuck in main menu
	 * - keeps mining other tree types but doesnt realize he can use them for crafting
	 * - !!!!!!!!new items missing from active memory!!!!!!
	 */
	
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
	
	public static void dbg(Object msg) {
		System.out.println(msg);
	}
	
}
