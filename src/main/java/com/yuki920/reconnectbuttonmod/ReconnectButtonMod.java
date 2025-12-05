package com.yuki920.reconnectbuttonmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import java.lang.reflect.Field;

@Mod(modid = ReconnectButtonMod.MODID, version = ReconnectButtonMod.VERSION)
public class ReconnectButtonMod {
    public static final String MODID = "ReconnectButtonMod";
    public static final String VERSION = "1.0";

    // Store the last server data
    private ServerData lastServerData;
    
    // A unique ID for the reconnect button
    private static final int RECONNECT_BUTTON_ID = 9999;

    private Field serverDataField;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        try {
            serverDataField = GuiScreenAddServer.class.getDeclaredField("field_146311_h");
            serverDataField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture the server data when the connection GUI is opened.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiConnecting) {
            ServerData data = Minecraft.getMinecraft().getCurrentServerData();
            if (data != null) {
                this.lastServerData = data;
            }
        } else if (event.gui instanceof GuiScreenAddServer) {
            try {
                this.lastServerData = (ServerData) serverDataField.get(event.gui);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * GUIが表示される(初期化される)時に呼ばれるイベント
     * ここでボタンを追加する
     */
    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiDisconnected) {
            GuiButton backButton = null;
            // "Back to server list" button has an ID of 0 on the disconnect screen
            for (GuiButton button : event.buttonList) {
                if (button.id == 0) {
                    backButton = button;
                    break;
                }
            }

            if (backButton != null) {
                // Position the "Reconnect" button 4 pixels below the "Back to server list" button
                int buttonX = backButton.xPosition;
                int buttonY = backButton.yPosition + backButton.height + 4;

                event.buttonList.add(new GuiButton(RECONNECT_BUTTON_ID, buttonX, buttonY, backButton.width, 20, "Reconnect"));
            }
        }
    }

    /**
     * Handle button clicks on the disconnect screen.
     */
    @SubscribeEvent
    public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.gui instanceof GuiDisconnected && event.button.id == RECONNECT_BUTTON_ID) {
            if (this.lastServerData != null) {
                connectToServer(this.lastServerData);
            }
        }
    }

    /**
     * サーバーへの接続処理を行うヘルパーメソッド
     */
    private void connectToServer(ServerData server) {
        // 接続処理はFMLClientHandlerを使うのが安全
        FMLClientHandler.instance().connectToServer(new GuiMultiplayer(new GuiMainMenu()), server);
    }
}