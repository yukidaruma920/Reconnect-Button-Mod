package com.yuki920.textdisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerTextDisplay {
    
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.entityPlayer;
        
        // 自分自身には表示しない（オプション）
        // if (player == Minecraft.getMinecraft().thePlayer) {
        //    return;
        // }
        
        // 表示するテキスト
        String text = "Hello World!";
        
        // フォントレンダラーを取得
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        
        // テキストの幅を計算（中央揃えのため）
        int textWidth = fontRenderer.getStringWidth(text);
        
        // OpenGLの状態を保存
        GlStateManager.pushMatrix();
        
        // プレイヤーの位置に移動
        // Y座標: プレイヤーの高さ + 0.5（ネームタグの上）
        double x = event.x;
        double y = event.y + player.height + 0.5;
        double z = event.z;
        
        GlStateManager.translate(x, y, z);
        
        // カメラの方を向かせる（ビルボード効果）
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        
        // スケールを調整（0.02がちょうどいいサイズ）
        float scale = 0.02F;
        GlStateManager.scale(-scale, -scale, scale);
        
        // 深度テストを無効化（他のオブジェクトに隠れないようにする）
        GlStateManager.disableDepth();
        
        // ライティングを無効化
        GlStateManager.disableLighting();
        
        // 背景を描画（見やすくするため）
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        // 背景の矩形を描画
        int backgroundColor = 0x40000000; // 半透明の黒
        fontRenderer.drawString(text, -textWidth / 2, 0, 0xFFFFFFFF, false);
        
        // テキストを描画（白色）
        // drawStringの最後の引数がtrueだと影付き
        // fontRenderer.drawString(text, -textWidth / 2, 0, 0xFFFFFFFF, true);
        
        // ライティングを有効化
        GlStateManager.enableLighting();
        
        // 深度テストを有効化
        GlStateManager.enableDepth();
        
        // OpenGLの状態を復元
        GlStateManager.popMatrix();
    }
}