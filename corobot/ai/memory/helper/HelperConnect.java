package corobot.ai.memory.helper;

public class HelperConnect {

	private static boolean firstTimeInitSinceConnect = true;

	public static boolean isFirstTimeInitSinceConnect() {
		return firstTimeInitSinceConnect;
	}

	public static void setFirstTimeInitSinceConnect(
			boolean firstTimeInitSinceConnect) {
		HelperConnect.firstTimeInitSinceConnect = firstTimeInitSinceConnect;
	}
	
	
	
}
