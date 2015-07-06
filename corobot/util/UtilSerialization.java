package corobot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import CoroUtil.quest.quests.ActiveQuest;

import com.corosus.ai.minigoap.IWorldStateProperty;

import corobot.Corobot;

public class UtilSerialization {

	public static String fileMemory = "memory.dat";
	public static String dataLoc = "./corobot_data/".replace("/", File.separator);
	
	public static NBTTagCompound getNBT(String fileName) {
		NBTTagCompound data = new NBTTagCompound();
		//try load
		
		String saveFolder = dataLoc;
		
		if ((new File(saveFolder + fileName)).exists()) {
			try {
				data = CompressedStreamTools.readCompressed(new FileInputStream(saveFolder + fileName));
			} catch (Exception ex) {
				System.out.println("UtilSerialization: Error loading " + saveFolder + fileName);
			}
			
			//NBTTagList var14 = gameData.getTagList("playerData");
		}
		
		return data;
	}
	
	public static void setNBT(String fileName, NBTTagCompound data) {
		try {
    		
    		String saveFolder = dataLoc;
    		
    		//Write out to file
    		FileOutputStream fos = new FileOutputStream(saveFolder + fileName);
	    	CompressedStreamTools.writeCompressed(data, fos);
	    	fos.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void loadNBTIntoMemory(List<IWorldStateProperty> listProps) {
		NBTTagCompound nbt = getNBT(fileMemory);
		
		if (!nbt.hasNoTags()) {
			Iterator it = nbt.func_150296_c().iterator();
		
			while (it.hasNext()) {
				String tagName = (String) it.next();
				NBTTagCompound data = nbt.getCompoundTag(tagName);
				String className = data.getString("className");
				
				IWorldStateProperty prop = makeProp(className);
				if (prop != null) {
					prop.read(data);
					listProps.add(prop);
				} else {
					Corobot.dbg("CRITICAL: couldnt instantiate IWorldStateProperty");
				}
				
			}
		}
	}
	
	public static void exportMemoryToNBT(List<IWorldStateProperty> listProps) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < listProps.size(); i++) {
			IWorldStateProperty prop = listProps.get(i);
			NBTTagCompound nbtEntry = new NBTTagCompound();
			String className = prop.getClass().getCanonicalName();
			
			nbtEntry.setString("className", className);
			try {
				prop.write(nbtEntry);
			} catch (Exception e) {
				e.printStackTrace();
			}
			nbt.setTag("entry_" + i, nbtEntry);
		}
		setNBT(fileMemory, nbt);
	}
	
	public static IWorldStateProperty makeProp(String parFullClassName) {
		try {
			Class createClass = Class.forName(parFullClassName);
			Constructor constructor = createClass.getConstructor();
			Object createObject = constructor.newInstance();
			if (createObject instanceof IWorldStateProperty) {
				return (IWorldStateProperty) createObject;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
