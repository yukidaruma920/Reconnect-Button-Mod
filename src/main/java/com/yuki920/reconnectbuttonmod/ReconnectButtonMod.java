package com.yuki920.reconnectbuttonmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = ReconnectButtonMod.MODID, version = ReconnectButtonMod.VERSION)
public class ReconnectButtonMod {
    public static final String MODID = "ReconnectButtonMod";
    public static final String VERSION = "1.0";

    // 直前に接続していたサーバー情報を保持する変数
    public static ServerData lastServerData;
    
    // 追加するボタンのID（既存のボタンと被らないように適当な数字にする）
    private static final int RECONNECT_BUTTON_ID = 9999;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // イベントバスにこのクラスを登録
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * サーバーに接続成功した際、そのサーバー情報を保存する
     */
    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // ローカルサーバーでなければサーバーデータを保存
        if (!event.isLocal) {
            ServerData data = Minecraft.getMinecraft().getCurrentServerData();
            if (data != null) {
                lastServerData = data;
            }
        }
    }

    /**
     * GUIが表示される(初期化される)時に呼ばれるイベント
     * ここでボタンを追加する
     */
    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        // 現在の画面が「切断画面(GuiDisconnected)」の場合のみ処理
        if (event.gui instanceof GuiDisconnected) {
            int width = event.gui.width;
            int height = event.gui.height;

            // ボタンの配置位置計算 (既存の"Back to server list"の下に配置)
            // height / 2 + 100 くらいがリストへ戻るボタンの位置なので、その下(+25)あたり
            int buttonX = width / 2 - 100; // ボタンの幅が200なので、中心から-100
            int buttonY = height / 2 + 100 + 25;

            // ボタンリストに追加
            // 引数: ID, x, y, 幅, 高さ, テキスト
            event.buttonList.add(new GuiButton(RECONNECT_BUTTON_ID, buttonX, buttonY, 200, 20, "Reconnect"));
        }
    }

    /**
     * ボタンがクリックされた時に呼ばれるイベント
     */
    @SubscribeEvent
    public void onGuiAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        // 切断画面かつ、押されたボタンが「Reconnect」ボタンの場合
        if (event.gui instanceof GuiDisconnected && event.button.id == RECONNECT_BUTTON_ID) {
            if (lastServerData != null) {
                // 再接続処理
                connectToServer(lastServerData);
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