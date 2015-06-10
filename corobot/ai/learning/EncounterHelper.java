package corobot.ai.learning;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class EncounterHelper {

	//0 - 1, 0 being no response, 1 being strong response
	
	//index name to name encounter history results
	
	//name - attack - name
	
	//merge attack and death, but make death contributions weigh more than attack ones
	//what about weighing deaths -> source vs deaths -> target?
	
	//save a 0 - 1 val weights
	//save a total weights count
	
	//how to keep relevant? trim down total weights count once in a while?
	//detect quick shift in chances and trim down fast?
	
	public static NBTTagCompound data;
	
	public static void check() {
		if (data == null) {
			//try load from file
			
			//for now...
			load();
		}
	}
	
	public static void load() {
		data = new NBTTagCompound();
	}
	
	public static void save() {
		
	}
	
	public static float getShouldAvoid(Entity source, Entity target) {
		check();
		String strVal = "attack-value-" + target.getCommandSenderName() + "-" + source.getCommandSenderName();
		String strCount = "attack-count-" + target.getCommandSenderName() + "-" + source.getCommandSenderName();
		float val = data.getFloat(strVal);
		int count = data.getInteger(strCount);
		
		float weight = val / count;
		
		return weight;
	}
	
	public static float getShouldAttack(Entity source, Entity target) {
		check();
		String strVal = "attack-value-" + target.getCommandSenderName() + "-" + source.getCommandSenderName();
		String strCount = "attack-count-" + target.getCommandSenderName() + "-" + source.getCommandSenderName();
		float val = data.getFloat(strVal);
		int count = data.getInteger(strCount);
		
		float weight = 1F - (val / count);
		
		return weight;
	}
	
	//log target damage vs source total health (and other resistances)
	public static void logDamageFrom(EntityLivingBase source, Entity target, float parDamage) {
		check();
		String strVal = "attack-value-" + source.getCommandSenderName() + "-" + target.getCommandSenderName();
		String strCount = "attack-count-" + source.getCommandSenderName() + "-" + target.getCommandSenderName();
		float val = data.getFloat(strVal);
		int count = data.getInteger(strCount)+1;
		
		float newVal = parDamage / source.getMaxHealth(); //damage is partially bad!
		
		data.setInteger(strCount, count);
		data.setFloat(strVal, val+newVal);
	}
	
	public static void logDeathFrom(Entity source, Entity target) {
		check();
		String strVal = "attack-value-" + source.getCommandSenderName() + "-" + target.getCommandSenderName();
		String strCount = "attack-count-" + source.getCommandSenderName() + "-" + target.getCommandSenderName();
		float val = data.getFloat(strVal);
		int count = data.getInteger(strCount)+1;
		
		float newVal = 1F; //death is fully bad!
		
		data.setInteger(strCount, count);
		data.setFloat(strVal, val+newVal);
	}
	
}
