package com.example.textdisplay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = TextDisplayMod.MODID, version = TextDisplayMod.VERSION, clientSideOnly = true)
public class TextDisplayMod {
    public static final String MODID = "textdisplay";
    public static final String VERSION = "1.0.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        // イベントハンドラーを登録
        MinecraftForge.EVENT_BUS.register(new PlayerTextDisplay());
    }
}