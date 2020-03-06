package cn.kody.debug.mod.mods.RENDER;

import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import com.darkmagician6.eventapi.*;

import cn.kody.debug.events.EventRenderPlayer;
import cn.kody.debug.mod.Category;
import cn.kody.debug.mod.Mod;
import cn.kody.debug.utils.color.Colors;
import cn.kody.debug.value.Value;

public class Chams extends Mod
{
    public Value<Boolean> colors;
    public Value<String> colormode;
    public Value<Boolean> flat;
    
    public Chams() {
        super("Chams", Category.RENDER);
        this.colors = new Value<Boolean>("Chams_Color", true);
        this.colormode = new Value<String>("Chams", "ColorMode", 0);
        this.flat = new Value<Boolean>("Chams_Flat", true);
        this.colormode.addValue("Rainbow");
        this.colormode.addValue("Team");
    }
    
    @EventTarget(1)
    public void onRenderEntity(EventRenderPlayer e) {
        boolean booleanValue = this.colors.getValueState();
        if (e.getEntity() instanceof EntityPlayer && e.getEntity() != this.mc.thePlayer && e.isPre()) {
            if (booleanValue) {
                e.setCancelled(true);
                try {
                    Render entityRenderObject = this.mc.getRenderManager().getEntityRenderObject(e.getEntity());
                    if (entityRenderObject != null && this.mc.getRenderManager().renderEngine != null && entityRenderObject instanceof RendererLivingEntity) {
                        GL11.glPushMatrix();
                        GL11.glDisable(2929);
                        GL11.glBlendFunc(770, 771);
                        GL11.glDisable(3553);
                        GL11.glEnable(3042);
                        if (this.flat.getValueState()) {
                            GlStateManager.disableLighting();
                        }
                        Color rainbow = new Color(-1);
                        if (this.colormode.isCurrentMode("Rainbow")) {
                            rainbow = rainbow(3000);
                        }
                        else if (this.colormode.isCurrentMode("Team")) {
                            String formattedText = e.getEntity().getDisplayName().getFormattedText();
                            if (Character.toLowerCase(formattedText.charAt(0)) == '¡ì') {
                                int index = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(formattedText.charAt(1)));
                                if (index < 16) {
                                    try {
                                        int n = this.mc.fontRendererObj.colorCode[index];
                                        rainbow = new Color(Colors.getColor(n >> 16, n >> 8 & 0xFF, n & 0xFF, 255));
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex2) {}
                                }
                            }
                            else {
                                rainbow = new Color(Colors.getColor(255, 255, 255, 255));
                            }
                        }
                        this.glColor(1.0f, rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue());
                        ((RendererLivingEntity<EntityLivingBase>)entityRenderObject).renderModel(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                        GL11.glEnable(2929);
                        Color rainbow2 = new Color(-1);
                        if (this.colormode.isCurrentMode("Rainbow")) {
                            rainbow2 = rainbow(3000);
                        }
                        else if (this.colormode.isCurrentMode("Team")) {
                            String formattedText2 = e.getEntity().getDisplayName().getFormattedText();
                            if (Character.toLowerCase(formattedText2.charAt(0)) == '¡ì') {
                                int index2 = "0123456789abcdefklmnorg".indexOf(Character.toLowerCase(formattedText2.charAt(1)));
                                if (index2 < 16) {
                                    try {
                                        int n2 = this.mc.fontRendererObj.colorCode[index2];
                                        rainbow2 = new Color(Colors.getColor(n2 >> 16, n2 >> 8 & 0xFF, n2 & 0xFF, 255));
                                    }
                                    catch (ArrayIndexOutOfBoundsException ex3) {}
                                }
                            }
                            else {
                                rainbow2 = new Color(Colors.getColor(255, 255, 255, 255));
                            }
                        }
                        this.glColor(1.0f, rainbow2.getRed(), rainbow2.getGreen(), rainbow2.getBlue());
                        ((RendererLivingEntity<EntityLivingBase>)entityRenderObject).renderModel(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                        GL11.glEnable(3553);
                        GL11.glDisable(3042);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        if (this.flat.getValueState()) {
                            GlStateManager.enableLighting();
                        }
                        GL11.glPopMatrix();
                        ((RendererLivingEntity<EntityLivingBase>)entityRenderObject).renderLayers(e.getEntity(), e.getLimbSwing(), e.getLimbSwingAmount(), this.mc.timer.renderPartialTicks, e.getAgeInTicks(), e.getRotationYawHead(), e.getRotationPitch(), e.getOffset());
                        GL11.glPopMatrix();
                    }
                    return;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
            }
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1100000.0f);
        }
        else if (!booleanValue && e.getEntity() instanceof EntityPlayer && e.isPost()) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0f, 1100000.0f);
        }
    }
    public static Color rainbow(final int n) {
        return Color.getHSBColor((float)(Math.ceil((System.currentTimeMillis() + n) / 20.0) % 360.0 / 360.0), 0.8f, 1.0f).brighter();
    }
    public void glColor(float n, int n2, int n3, int n4) {
        GL11.glColor4f(0.003921569f * n2, 0.003921569f * n3, 0.003921569f * n4, n);
    }
}
