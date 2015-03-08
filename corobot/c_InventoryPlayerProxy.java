package corobot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class c_InventoryPlayerProxy extends InventoryPlayer implements IInventory
{

    public c_InventoryPlayerProxy(EntityPlayer par1EntityPlayer)
    {
    	super(par1EntityPlayer);
        this.player = par1EntityPlayer;
    }
    
    @Override
    public void onInventoryChanged()
    {
        this.inventoryChanged = true;
    }
    
    @Override
    public boolean addItemStackToInventory(ItemStack par1ItemStack) {
    	return super.addItemStackToInventory(par1ItemStack);
    }
}
