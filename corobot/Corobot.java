package corobot;

import net.minecraft.client.Minecraft;
import corobot.ai.PlayerAI;

public class Corobot {

	/**
	 * Ongoing bugs:
	 * t still drops workbench contents instead of shift clicking back into inv
	 * -- maybe fixed
	 * - cant mine without guichat open
	 * - sometimes gets stuck in main menu
	 * - keeps mining other tree types but doesnt realize he can use them for crafting
	 * t !!!!!!!!new items missing from active memory!!!!!!
	 * -- fixed i think
	 * - needs furnace for iron ore to iron, etc
	 * - he wouldnt pickup the cobblestone, why? he picks up logs
	 * - block of <ore> recipes causes infinate loops
	 * -- vice versa is also annoying (ones that use block of iron, etc to make stuff)
	 * -- so any recipes that has one making the other and vice versa will cause infinate loop or broken order
	 * 
	 * Reasons to make preconditions parsable like OR and AND:
	 * - furnace, need THIS ITEM and (this fuel or this fuel, etc)
	 * - there was another reason, what was it?
	 * 
	 * BIGGEST ISSUE ATM:
	 * in planner, effect of 1 stacksize satisfies 3 separate items each with a stacksize of 1, this is an issue if those 3 items are same:
	 * - 5 coal needed to smelt
	 * - 3 wood needed to craft pickaxe, got into a broken state when he only had 2, planner only sees need for 1, crafting needs 3
	 * -- i think this is issue because its 3 itemstacks of wood each with 1 stacksize,
	 * - how to solve this?
	 * 
	 * 
	 * 
	 */
	
	public static PlayerAI playerAI;
	
	public static void init() {
		playerAI = new PlayerAI();
	}
	
	public static void tickUpdate() {
		try {
			if (!Minecraft.getMinecraft().isGamePaused()) {
				//playerAI.tickUpdate();
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
