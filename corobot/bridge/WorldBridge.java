package corobot.bridge;

import com.corosus.world.IWorld;

import corobot.ai.PlayerAI;

public class WorldBridge implements IWorld {

	public PlayerAI playerAI;
	
	public WorldBridge(PlayerAI playerAI) {
		this.playerAI = playerAI;
	}
	
	@Override
	public long getTicksTotal() {
		return playerAI.bridgePlayer.getPlayer().worldObj.getTotalWorldTime();
	}

}
