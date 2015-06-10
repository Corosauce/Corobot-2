package corobot;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import corobot.ai.PlayerAI;

public class Corobot {

	/**
	 * Ongoing bugs:
	 * - NEED A DELAY BETWEEN GUI OPEN AND SLOT USE, OTHERWISE SLOT USAGE FAILS ON FIRST CLICK OR MORE
	 * -- thread.sleep(50) helps this, but we should do a better latency friendly solution
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
	 * -- when building plan, pass preconditions to plan
	 * --- planmineblock will then find real amount needed from the block its to get, and set the needed amount to that
	 * ---- mineblock then mines till it has that many
	 * 
	 * 
	 * 
	 * todo:
	 * - improve combat survival
	 * -- avoidance
	 * - behaviors have a lot of missing resets, like pathing resets on complete or fail, etc
	 * 
	 * potential refactors:
	 * - I think GOAP plans should be sequences that use non goap behaviors as subtasks
	 * -- so you can mix and match things better, less recoding, easier to make thing nav to a point etc
	 * -- this idea came up while using non goap stuff for YDScript
	 * --- GOAP plans and non GOAP plans need return value compatibility
	 * ---- currently all GOAP plans just return SUCCESS which is not good
	 * 
	 * - why do GOAP plans use isComplete? why not use check for return value of SUCCESS?
	 * -- would solve above compatibility issue if they did
	 * -- additionally, replace 'Corobot.getPlayerAI().planGoal.invalidatePlan();' with FAILURE return, and make parent class then call invalidatePlan if FAILURE
	 * 
	 */
	
	public static PlayerAI playerAI;
	
	public static boolean isKeyDownPause = false;
	public static boolean isBotActive = true;
	
	public static void init() {
		playerAI = new PlayerAI();
	}
	
	public static void tickUpdate() {
		try {
			
			if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
				if (!isKeyDownPause) {
					isBotActive = !isBotActive;
					System.out.println("bot is " + (isBotActive ? "active" : "inactive"));
				}
				isKeyDownPause = true;
			} else {
				isKeyDownPause = false;
			}
			
			if (!Minecraft.getMinecraft().isGamePaused()) {
				if (isBotActive) playerAI.tickUpdate();
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
