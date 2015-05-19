package corobot.ai.memory.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.corosus.ai.minigoap.IWorldState;
import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.Corobot;
import corobot.ai.memory.pieces.ItemEntry;
import corobot.ai.memory.pieces.inventory.InventorySource;
import corobot.ai.memory.pieces.inventory.InventorySourceSelf;

public class HelperInventory {

	/*
	 * This will be an ongoing optimization....
	 * - To start lets organize by inventory source
	 */
	
	public static InventorySourceSelf selfInventory = new InventorySourceSelf();
	public static int selfInventoryHash = 0;
	
	public static HashMap<Object, List<ItemEntry>> lookupInventorySources = new HashMap<Object, List<ItemEntry>>();
	
	public static List<ItemEntry> getInventoryList(Object hash) {
		List<ItemEntry> listItems = null;
		
		if (!lookupInventorySources.containsKey(hash)) {
			listItems = new ArrayList<ItemEntry>();
			lookupInventorySources.put(hash, listItems);
		} else {
			listItems = lookupInventorySources.get(hash);
		}
		
		return listItems;
	}
	
	public static void addEntry(IWorldState memory, IWorldStateProperty prop) {
		ItemEntry source = (ItemEntry) prop;
		List<ItemEntry> listItems = getInventoryList(source.getSource().getHash());
		
		listItems.add(source);
		memory.getProperties().add(prop);
	}
	
	public static void removeEntry(IWorldState memory, IWorldStateProperty prop) {
		removeEntry(memory, prop, false);
	}
	
	public static void removeEntry(IWorldState memory, IWorldStateProperty prop, boolean justMemory) {
		ItemEntry source = (ItemEntry) prop;
		if (!justMemory) {
			List<ItemEntry> listItems = getInventoryList(source.getSource().getHash());
			listItems.remove(source);
		}
		memory.getProperties().remove(prop);
	}
	
	public static void updateCache(IWorldState memory, InventorySource source, IInventory inv) {
		//Corobot.dbg("updateCache");
		List<ItemEntry> listItems = getInventoryList(source.getHash());
		
		for (ItemEntry entry : listItems) {
			removeEntry(memory, entry, true);
		}
		
		//should already be clear
		listItems.clear();
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack != null) {
				addEntry(memory, new ItemEntry(stack, source));
				//listItems.add(new ItemEntry(stack, source));
			}
		}
	}
	
}
