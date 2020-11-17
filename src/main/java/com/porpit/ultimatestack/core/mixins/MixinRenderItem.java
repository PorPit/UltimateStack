package com.porpit.ultimatestack.core.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(RenderItem.class)

public abstract class MixinRenderItem implements IResourceManagerReloadListener {
    private final static Map<Character,Character> digit = new HashMap<>();
    static{
        digit.put('0', '₀');
        digit.put('1', '₁');
        digit.put('2', '₂');
        digit.put('3', '₃');
        digit.put('4', '₄');
        digit.put('5', '₅');
        digit.put('6', '₆');
        digit.put('7', '₇');
        digit.put('8', '₈');
        digit.put('9', '₉');
    }
    @Shadow
    public abstract void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderItemOverlayIntoGUI", at = @At("HEAD"), cancellable = true)
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text, CallbackInfo ci){
        if (!stack.isEmpty())
        {
            if (stack.getCount() != 1 || text != null)
            {
                String s = text == null ? String.valueOf(stack.getCount()) : text;

                char[] textChars=s.toCharArray();
                if(textChars.length>3){
                    for (int i=0;i<textChars.length;i++){
                        if(Character.isDigit(textChars[i])){
                            textChars[i]=digit.get(textChars[i]);
                        }
                    }
                    s=new String(textChars);
                }
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fr.drawStringWithShadow(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                // Fixes opaque cooldown overlay a bit lower
                // TODO: check if enabled blending still screws things up down the line.
                GlStateManager.enableBlend();
            }

            if (stack.getItem().showDurabilityBar(stack))
            {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
                int i = Math.round(13.0F - (float)health * 13.0F);
                int j = rgbfordisplay;
                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }

            EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
            float f3 = entityplayersp == null ? 0.0F : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

            if (f3 > 0.0F)
            {
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                Tessellator tessellator1 = Tessellator.getInstance();
                BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
                this.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
        ci.cancel();
    }
}
