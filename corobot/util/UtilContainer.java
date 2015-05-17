package corobot.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import corobot.Corobot;

public class UtilContainer {
	
	public static int mouse2StepClick = 0;
	public static int mouseShiftClick = 1;
	
	public static int mouseLeftClick = 0;
	public static int mouseRightClick = 1;

	public static void clickSlot(int slot, int mouseButton, int clickModifier) {
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		Minecraft mc = Minecraft.getMinecraft();
		
		if (player.openContainer != null) {
			mc.playerController.windowClick(player.openContainer.windowId, slot, mouseButton, clickModifier, player);
		} else {
			Corobot.dbg("WARNING: Tried to click slot without an open container");
		}
		
	}
	
	public static void openContainer(int x, int y, int z) {
		EntityPlayer player = Corobot.playerAI.bridgePlayer.getPlayer();
		Minecraft mc = Minecraft.getMinecraft();
		mc.playerController.onPlayerRightClick(player, player.worldObj, null, x, y, z, 0, Vec3.createVectorHelper(x, y, z));
		//c_AIP.i.mc.playerController.onPlayerRightClick(c_AIP.i.player, c_AIP.i.worldObj, c_AIP.i.player.getCurrentEquippedItem(), x, y, z, 0, Vec3.createVectorHelper(x, y, z));
	}
	
}
