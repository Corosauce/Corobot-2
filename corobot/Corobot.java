package corobot;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import corobot.ai.PlayerAI;
import corobot.ai.behaviors.MasterPlanSequence;

public class Corobot {

	/**
	 * 
	 * need quick things to work on? check out TODOs
	 * 
	 * Terminology:
	 * Plans: for GOAP, has dynamic data fed into it to be used, may contain sub tasks that it feeds data to
	 * Tasks / SubTasks: Behaviors that are what make up some plans, uses blackboard as primary information
	 * 
	 * Wrap Plans around tasks for max reusability?
	 * - eg: PlanMaintainHouse uses TaskMaintainHouse
	 * 
	 * Ongoing bugs:
	 * - NEED A DELAY BETWEEN GUI OPEN AND SLOT USE, OTHERWISE SLOT USAGE FAILS ON FIRST CLICK OR MORE
	 * -- thread.sleep(50) helps this, but we should do a better latency friendly solution
	 * t still drops workbench contents instead of shift clicking back into inv
	 * -- maybe fixed
	 * - cant mine without guichat open
	 * - sometimes gets stuck in main menu
	 * x keeps mining other tree types but doesnt realize he can use them for crafting
	 * x !!!!!!!!new items missing from active memory!!!!!!
	 * x- fixed i think
	 * - needs furnace for iron ore to iron, etc
	 * x he wouldnt pickup the cobblestone, why? he picks up logs
	 * x block of <ore> recipes causes infinate loops
	 * x- solved removing "block of " recipes
	 * -- vice versa is also annoying (ones that use block of iron, etc to make stuff)
	 * -- so any recipes that has one making the other and vice versa will cause infinate loop or broken order
	 * --- this is still an issue overall
	 * x need special exception for item meta, like damaged weapons, as goal "wooden pickaxe" wont recognize a damaged pickaxe already in inventory currently
	 * 
	 * x partial fixed for recipe needing multiple of things a mine plan can give
	 * x- BUT now we have the bug for smelting only giving 1 of a thing we need multiple of since we've merged stacks for recipes
	 * x- this also means this bug theoretically exists for crafting too now
	 * x-- force 64 for these too? lets try!
	 * x--- seems to work well so far...
	 * 
	 * - smelting gui use has bugged out again somehow, needs fix!
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Reasons to make preconditions parsable like OR and AND:
	 * - furnace, need THIS ITEM and (this fuel or this fuel, etc)
	 * - there was another reason, what was it?
	 * 
	 * BIGGEST ISSUE ATM:
	 * x in planner, effect of 1 stacksize satisfies 3 separate items each with a stacksize of 1, this is an issue if those 3 items are same:
	 * x- 5 coal needed to smelt
	 * x- 3 wood needed to craft pickaxe, got into a broken state when he only had 2, planner only sees need for 1, crafting needs 3
	 * x-- i think this is issue because its 3 itemstacks of wood each with 1 stacksize,
	 * x- how to solve this?
	 * x-- when building plan, pass preconditions to plan
	 * x--- planmineblock will then find real amount needed from the block its to get, and set the needed amount to that
	 * x---- mineblock then mines till it has that many
	 * 
	 * 
	 * 
	 * todo:
	 * - improve combat survival
	 * -- avoidance
	 * - behaviors have a lot of missing resets, like pathing resets on complete or fail, etc
	 * -- working better, still needs more clean up i think
	 * 
	 * potential refactors:
	 * - I think GOAP plans should be sequences that use non goap behaviors as subtasks
	 * -- so you can mix and match things better, less recoding, easier to make thing nav to a point etc
	 * -- this idea came up while using non goap stuff for YDScript
	 * --- GOAP plans and non GOAP plans need return value compatibility
	 * ---- currently all GOAP plans just return SUCCESS which is not good
	 * 
	 * x why do GOAP plans use isComplete? why not use check for return value of SUCCESS?
	 * x- would solve above compatibility issue if they did
	 * x- additionally, replace 'Corobot.getPlayerAI().planGoal.invalidatePlan();' with FAILURE return, and make parent class then call invalidatePlan if FAILURE
	 * 
	 * learning ideas:
	 * - "if a user goes from a to b, and runs out of something one thr trip, to stalk up more next time"
	 * - stalk up on more food if wasnt enough for a trip etc
	 * - sense that a bridge at a location could save lots of time vs pathing around
	 * - remembering how to get around large objects like mountains
	 */
	
	public static PlayerAI playerAI;
	
	public static boolean isKeyDownPause = false;
	public static boolean isBotActive = false;
	
	public static void init() {
		playerAI = new PlayerAI();
	}
	
	public static void tickUpdate() {
		try {
			
			if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
				if (!isKeyDownPause) {
					isBotActive = !isBotActive;
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
						System.out.println("forcing GOAP plan reset");
						MasterPlanSequence.makePlan = true;
					}
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
