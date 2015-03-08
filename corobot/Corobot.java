package corobot;

import corobot.ai.PlayerAI;

public class Corobot {

	public static PlayerAI playerAI;
	
	public static void init() {
		playerAI = new PlayerAI();
	}
	
	public static void tickUpdate() {
		playerAI.tickUpdate();
	}
	
}
