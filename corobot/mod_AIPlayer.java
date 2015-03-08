package corobot;
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import CoroAI.PFQueue;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class mod_AIPlayer extends BaseMod
    implements Runnable {

	//Usefull references
	@SideOnly(Side.CLIENT)
    public static Minecraft mc;
    public static World worldRef;
    public static EntityPlayer player;
    
    public static World lastWorld;
    public static boolean ingui;
    public static NBTTagCompound gameData = null;
    
    public static boolean debug = false;
        
    //public static Armies armies;
	private static long lastWorldTime;
	private static boolean toggleKeyPressed;
	
	public static int respawnTimer;
    
    public String getVersion() {
        return "Version 1.1";//"+ModLoader.VERSION.substring(ModLoader.VERSION.indexOf(" ")+1);
        //return "Version 3.0 for MC "+ModLoader.VERSION.substring(ModLoader.VERSION.indexOf(" ")+1);
    }
    
    @SideOnly(Side.CLIENT)
    public void clientInit() {
    	mc = ModLoader.getMinecraftInstance();
		new c_AIP();
		readGameNBT();
		(new Thread(this, "AI Watcher Thread")).start();
    }

    public void load() {
    	
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
    		clientInit();
    	}
    	
    	
    	
    	//armies = new ArmiesSP();
    	//((ArmiesSP)armies).mc = mc;
    	
    	
    	
    	
    	
    	/*ModLoader.registerEntityID(ArmyWorker.class, "ArmyWorker", entID);
    	ModLoader.registerEntityID(ArmyWorker.class, "ArmyWorker", entID, 0x00FF00, 0x00FF00); //egg
    	ModLoaderMp.registerNetClientHandlerEntity(ArmyWorker.class, false, entID++);
    	
    	ModLoader.registerTileEntity(TileArmyBuilding.class, "building", new TileArmyBuildingRenderer());
    	ModLoader.addName(ArmiesBlocks.b_building, "building");*/
    	
    	/*ModLoader.registerTileEntity(TileEntityMobSpawnerWave.class, "z_tspawnblock", new TileEntityMobSpawnerWaveRenderer());
    	ModLoader.addName(ZCBlocks.b_mobSpawnerWave, "SpawnBlockWave");
    	
    	ModLoader.registerTileEntity(TileEntityPurchasePlate.class, "z_tpurchaseblock", new TileEntityPurchasePlateRenderer());
    	ModLoader.addName(ZCBlocks.b_buyBlock, "PurchaseBlock");
    	
    	ModLoader.addName(ZCItems.barricade,"ZC Barricade");
    	ModLoader.addName(ZCItems.editTool,"ZC Editor Tool");
    	ModLoader.addName(ZCItems.buildTool,"ZC Build Tool");
    	ModLoader.addName(ZCBlocks.barrier,"ZC Barrier");
    	
    	ModLoader.registerEntityID(Zombie.class, "EntityZCZombie", entID);
    	ModLoaderMp.registerNetClientHandlerEntity(Zombie.class, false, entID++);
    	
    	ModLoader.registerEntityID(EntityWorldHook.class, "EntityWorldHook", entID);
    	ModLoaderMp.registerNetClientHandlerEntity(EntityWorldHook.class, false, entID++);
    	
    	ModLoader.registerEntityID(Comrade.class, "EntityZCComrade", entID);
    	ModLoader.registerEntityID(Comrade.class, "EntityZCComrade", entID, 0xFFFFFF, 0x000000); //egg
    	ModLoaderMp.registerNetClientHandlerEntity(Comrade.class, false, entID++);
    	
    	ModLoader.registerKey(this, new KeyBinding("ZC_Use", Keyboard.KEY_E), true);
    	ModLoader.addLocalization("ZC_Use", "ZC Use/Buy Key");
    	
    	consoleKey = new KeyBinding("ZC_Menu", Keyboard.KEY_GRAVE);
    	
    	ModLoader.registerKey(this, consoleKey, true);
    	ModLoader.addLocalization("ZC_Menu", "Open ZC Menu");*/
    	
    	
    	
        ModLoader.setInGUIHook(this, true, false);
        ModLoader.setInGameHook(this, true, false);
        
        //should be set by the map editor mode
        //ZCUtil.setBlocksMineable(false);
    }

    @SideOnly(Side.CLIENT)
    public void run() {
        
            while(true) {
            	try {
                if(mc == null) {
                    mc = ModLoader.getMinecraftInstance();
                }

                if(mc == null) {
                    Thread.sleep(100L);
                } else {
                    if(mc.thePlayer == null) {
                        Thread.sleep(100L);
                    } else {
                        if (lastWorld != worldRef) {
                            //worldSaver = null;
                            lastWorld = worldRef;
                            /*System.out.println("Resetting ZombieCraft");
                            iMan.resetGunBinds();
                            iMan = null; //auto resets in zcgamesp when ready
                            zcGame.resetWaveManager = true;//zcGame.wMan = null;
                            zcGame.levelHasInit = false;
                            zcGame.resetWorldHook();*/
                            //getFXLayers();
                        }

                        worldRef = mc.theWorld;
                        player = mc.thePlayer;
                        c_AIP.i.player = player;
                        if (PFQueue.instance == null) {
                    		new PFQueue(worldRef);
                    	}
                        Thread.sleep(100L);
                    }
                }
            	} catch(Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        
    }

    public mod_AIPlayer() {
    	
    }
    
    /*@Override
    public void handlePacket(Packet230ModLoader packet)
    {
    	zcGame.handlePacket(null, packet);
    }
    
    @Override
    public void handleTileEntityPacket(int var1, int var2, int var3, int var4, int[] var5, float[] var6, String[] var7) {
    	zcGame.handleTileEntityPacket(var1, var2, var3, var4, var5, var6, var7);
    }*/
    
    /*public static void sendPacket(int packetType, int[] dataInt) {
    	sendPacket(packetType, dataInt, new String[0]);
    }
    
    public static void sendPacket(int packetType, int[] dataInt, String[] dataString) {
    	Packet230ModLoader packet = new Packet230ModLoader();
        packet.packetType = packetType;
        packet.dataInt = dataInt;
        packet.dataString = dataString;
        ModLoaderMp.sendPacket(ModLoaderMp.getModInstance(mod_AIPlayer.class), packet);
	}*/
    
    public void addRenderer(Map var1) {
        //effRainID = ModLoader.addOverride("/gui/items.png", "/item/raindrop.png");
    	/*var1.put(Zombie.class, new RenderBiped(new ModelZombie(), 0.5F));
    	var1.put(Comrade.class, new RenderBiped(new ModelZombie(), 0.5F));
    	var1.put(EntityWorldHook.class, new RenderEntityWorldHook());*/
    	
    }

    @SideOnly(Side.CLIENT)
    public void modsLoaded() {
        mc = ModLoader.getMinecraftInstance();
        //this.rotEffRenderer = new RotatingEffectRenderer(mc.theWorld, mc.renderEngine);
        //mc.entityRenderer = new EntityRendererProxyWeather(mc);
    }
    
    @SideOnly(Side.CLIENT)
    public void keyboardEvent(KeyBinding var1) {
    	//if (var1.keyDescription.equals("ZC_Use")) {
    		/*if (iMan != null) {
    			iMan.keyEvent(var1);
    		}*/
    	//}
    }

    @SideOnly(Side.CLIENT)
    public boolean onTickInGame(float f, Minecraft var1) {
        //if(!ingui) {
            //playerLastTick = System.currentTimeMillis();
            this.gameTick(var1, false);
        //}

        ingui = false;
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean onTickInGUI(float f, Minecraft var1, GuiScreen gui) {
        if (ModLoader.getMinecraftInstance().thePlayer != null) {
            //long ticksRan = System.currentTimeMillis();
            if ((gui instanceof GuiContainer) && !(gui instanceof GuiChat) && gui != null) {
                ingui = true;
                //lastTickRun = 0;
            }
            if (gui != null && gui.doesGuiPauseGame() && !(gui instanceof GuiChat)) {
            	ingui = true;
            }

            
        } else {
        	if (c_AIP.enableAI) {
	        	if (mc.currentScreen instanceof GuiDisconnected) {
	            	mc.displayGuiScreen(new GuiConnecting(mc, new ServerData("tropi", "s.tropicraft.net")));
	            }
        	}
        }

        onGUITick(f, gui);
        
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void onGUITick(float f, GuiScreen screen) {
		/*if (screen instanceof GuiControls) {
			ModLoader.openGUI(mod_Armies.player, new GuiControlsPages(null, mc.gameSettings));
		}*/
	}

    @SideOnly(Side.CLIENT)
    public static void gameTick(Minecraft minecraft, boolean flag) {
        if (worldRef == null) {
            worldRef = ModLoader.getMinecraftInstance().theWorld;
        }

        if (player == null) {
            player = ModLoader.getMinecraftInstance().thePlayer;
        }
        
        

        if (worldRef == null || player == null) {
            return;
        }
        
        if(timeout > 0 && msg != null) {
            ScaledResolution var8 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            int var4 = var8.getScaledWidth();
            int var10 = var8.getScaledHeight();
            int var6 = mc.fontRenderer.getStringWidth(msg);
            mc.fontRenderer.drawStringWithShadow(msg, 3, 105, 16777215);
            --timeout;
        }
        
        if (c_AIP.enableAI) {
	        //if (mc.thePlayer.getHealth() <= 0) {
        	if (mc.currentScreen instanceof GuiGameOver || (mc.currentScreen instanceof GuiChat && mc.thePlayer.getHealth() <= 0)) {
        		if (respawnTimer < 40) {
        			respawnTimer++;
        		} else {
        			respawnTimer = 0;
        			mc.thePlayer.respawnPlayer();
    	        	mc.displayGuiScreen(new GuiChat(""));
        		}
	            //mc.displayGuiScreen((GuiScreen)null);
	        }
        }
        
        
        
        if (!(player.inventory instanceof c_InventoryPlayerProxy)) {
        	InventoryPlayer oldInv = player.inventory; 
        	player.inventory = new c_InventoryPlayerProxy(player);
        	
        	player.inventory.armorInventory = oldInv.armorInventory;
        	player.inventory.currentItem = oldInv.currentItem;
        	player.inventory.mainInventory = oldInv.mainInventory;
        }
        
        playerTick();
        
        /*Set<c_EnumAIPOrders> myEnumValues = EnumSet.allOf(c_EnumAIPOrders.class);
        
        Iterator it = myEnumValues.iterator();
        
        Object ob = it.next();*/
        
        
        
        if(Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
            if(!toggleKeyPressed) {
                c_AIP.i.enableAI = !c_AIP.i.enableAI;
                c_AIP.i.pathToEntity = null;
                toggleKeyPressed = true;
                displayMessage("Player AI: " + (c_AIP.i.enableAI?"enabled":"disabled"));
            }
        } else if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            if(!toggleKeyPressed) {
            	c_AIP.i.orders = c_AIP.i.orders.prev();
                toggleKeyPressed = true;
                displayMessage("PAI Orders: " + c_AIP.i.orders);//displayMessage("PAI Orders: " + (c_AIP.i.enableAI?"enabled":"disabled"));
            }
        } else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            if(!toggleKeyPressed) {
            	c_AIP.i.orders = c_AIP.i.orders.next();
                toggleKeyPressed = true;
                displayMessage("PAI Orders: " + c_AIP.i.orders);//(c_AIP.i.enableAI?"enabled":"disabled"));
            }
        } else if(Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
            if(!toggleKeyPressed) {
            	c_AIP.i.attackPlayers = !c_AIP.i.attackPlayers;
                toggleKeyPressed = true;
                displayMessage("Attack Players: " + c_AIP.i.attackPlayers);//(c_AIP.i.enableAI?"enabled":"disabled"));
            }
        } else if(Keyboard.isKeyDown(Keyboard.KEY_END)) {
            if(!toggleKeyPressed) {
            	c_AIP.i.attackAnimals = !c_AIP.i.attackAnimals;
                toggleKeyPressed = true;
                displayMessage("Attack Animals: " + c_AIP.i.attackAnimals);//(c_AIP.i.enableAI?"enabled":"disabled"));
            } 
        } else if(Keyboard.isKeyDown(Keyboard.KEY_0)) {
            if(!toggleKeyPressed) {
            	if (mc.objectMouseOver != null) {
	            	c_AIP.i.homeX = mc.objectMouseOver.blockX;
	            	c_AIP.i.homeY = mc.objectMouseOver.blockY;
	            	c_AIP.i.homeZ = mc.objectMouseOver.blockZ;
	            	writeGameNBT();
	            	displayMessage("Saved: ");//(c_AIP.i.enableAI?"enabled":"disabled"));
            	}
            	
                toggleKeyPressed = true;
                
            } 
        } else {
            toggleKeyPressed = false;
        }
        
        //if (armies != null) armies.tick();

    }
    
    @SideOnly(Side.CLIENT)
    public static void playerTick() {
    	if (lastWorldTime != worldRef.getWorldInfo().getWorldTime()) {
    		lastWorldTime = worldRef.getWorldInfo().getWorldTime();
    		
    		if (c_AIP.okToGo()) {
    			
    			if (c_AIP.i.homeY == 0) {
    				c_AIP.i.homeX = (int)mc.thePlayer.posX;
    				c_AIP.i.homeY = (int)mc.thePlayer.posY;
    				c_AIP.i.homeZ = (int)mc.thePlayer.posZ;
    			}
    			
    			c_AIP.i.AIPlayerTick();
    		}
    		
    		// c_EnhAI substituted code \\
    		
    	}
    	
    }

    @SideOnly(Side.CLIENT)
    public static void getFXLayers() {
        //fxLayers
        Field field = null;

        try {
            field = (EffectRenderer.class).getDeclaredField("b");
            field.setAccessible(true);
            //fxLayers = (List[])field.get(ModLoader.getMinecraftInstance().effectRenderer);
        } catch (Exception ex) {
            try {
                field = (EffectRenderer.class).getDeclaredField("fxLayers");
                field.setAccessible(true);
                //fxLayers = (List[])field.get(ModLoader.getMinecraftInstance().effectRenderer);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
	public static void setHardness(int id, float val) {
		Block.blocksList[id].setHardness(val);
		//Sets original resistance, so explosives work, maybe change later?
		//ZCUtil.setPrivateValueBoth(Block.class, Block.blocksList[id], "bQ", "blockResistance", ZCUtil.blockHardness[id]*5F);
	}
    
    public static int timeout;
    public static String msg;
    public static int color;
    public static int defaultColor = 16777215;
    
    @SideOnly(Side.CLIENT)
    public static void displayMessage(String var0, int var1) {
        msg = var0;
        timeout = 85;
        color = var1;
    }

    @SideOnly(Side.CLIENT)
    public static void displayMessage(String var0) {
        displayMessage(var0, defaultColor);
    }

    public static boolean keyDownLastTick = false;
    public static boolean heldItemLastTick = false;

    public static boolean toggle = false;

    public static void writeGameNBT() {
    	//System.out.println("Saving ZC game..." + zcLevel.map_coord_minX);
    	gameData = new NBTTagCompound();
    	try {
    		
    		//Player data
    		
    		//Level position and name data, rest should be done by level
    		//gameData.setString("levelName", mapMan.curLevel);
    		//gameData.setString("texturePack", mapMan.texturePack);
    		gameData.setInteger("homeX", c_AIP.i.homeX);
    		gameData.setInteger("homeY", c_AIP.i.homeY);
    		gameData.setInteger("homeZ", c_AIP.i.homeZ);
    		
    		String saveFolder = "/";
    		
    		//Write out to file
    		FileOutputStream fos = new FileOutputStream(saveFolder + "AIPlayer.dat");
	    	CompressedStreamTools.writeCompressed(gameData, fos);
	    	fos.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
    
    public static void readGameNBT() {
    	gameData = null;
		try {
			
			String saveFolder = "/";
			gameData = CompressedStreamTools.readCompressed(new FileInputStream(saveFolder + "AIPlayer.dat"));
			
			c_AIP.i.homeX = gameData.getInteger("homeX");
			c_AIP.i.homeY = gameData.getInteger("homeY");
			c_AIP.i.homeZ = gameData.getInteger("homeZ");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
    }
    
}
