package corobot.ai.memory.helper;

import java.util.ArrayList;
import java.util.HashMap;

public class HelperPath {

	/**
	 * consider 2 layers:
	 * - usual pathing (attacking, etc)
	 * - flee pathing
	 * 
	 * each have their own independantly tracked repath delay
	 * for immediate path response when switching from one to the other
	 * requires that there is not frequent regular switching between them
	 */
	
	public enum Repaths {
		MAIN, FLEE
	}
	
	public static HashMap<Repaths, Long> lookupRepaths = new HashMap<Repaths, Long>();
	
	static {
		lookupRepaths.put(Repaths.MAIN, 0L);
		lookupRepaths.put(Repaths.FLEE, 0L);
	}
	
	public static long repathInterval = 1000;

	public static boolean pathNow(Repaths var) {
		return System.currentTimeMillis() - lookupRepaths.get(var) > repathInterval;
	}
	
	public static void pathed(Repaths var) {
		lookupRepaths.put(var, System.currentTimeMillis());
	}
	
}
