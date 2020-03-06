package cn.kody.debug.mod.mods.COMBAT;

import cn.kody.debug.Client;
import cn.kody.debug.events.EventAttack;
import cn.kody.debug.events.EventPostMotion;
import cn.kody.debug.events.EventPreMotion;
import cn.kody.debug.events.EventRender;
import cn.kody.debug.events.EventRender2D;
import cn.kody.debug.events.EventUpdate;
import cn.kody.debug.mod.Category;
import cn.kody.debug.mod.Mod;
import cn.kody.debug.mod.mods.COMBAT.AntiBot;
import cn.kody.debug.ui.font.FontManager;
import cn.kody.debug.ui.font.UnicodeFontRenderer;
import cn.kody.debug.utils.angle.AngleUtility;
import cn.kody.debug.utils.angle.RotationUtil;
import cn.kody.debug.utils.color.Colors;
import cn.kody.debug.utils.render.R3DUtil;
import cn.kody.debug.utils.render.RenderUtil;
import cn.kody.debug.utils.time.TimeHelper;
import cn.kody.debug.value.Value;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

public class KillAura
extends Mod {
    public Value<String> priority = new Value("KillAura", "Priority", 0);
    public Value<String> mode = new Value("KillAura", "Mode", 0);
    public Value<Boolean> targethud = new Value<Boolean>("KillAura_TargetHUD", true);
    public Value<Boolean> players = new Value<Boolean>("KillAura_Players", true);
    public Value<Boolean> mobs = new Value<Boolean>("KillAura_Mobs", false);
    public Value<Boolean> animals = new Value<Boolean>("KillAura_Animals", false);
    public Value<Boolean> invis = new Value<Boolean>("KillAura_Invisible", false);
    public static Value<Boolean> autoBlock = new Value<Boolean>("KillAura_AutoBlock", false);
    public Value<Double> fov = new Value<Double>("KillAura_Fov", 180.0, 1.0, 180.0, 1.0);
    public static Value<Double> range = new Value<Double>("KillAura_Range", 4.2, 3.5, 7.0, 0.1);
    public Value<Double> cps = new Value<Double>("KillAura_CPS", 9.0, 1.0, 20.0, 1.0);
    public Value<Boolean> walls = new Value<Boolean>("KillAura_ThroughWalls", true);
    public Value<Boolean> aac = new Value<Boolean>("KillAura_AAC", false);
    public Value<Boolean> teams = new Value<Boolean>("KillAura_Teams", true);
    public Value<Boolean> autodisable = new Value<Boolean>("KillAura_AutoDisable", true);
    public static Value<Double> blockrange = new Value<Double>("Killaura_BlockRange", 7.0, 3.5, 7.0, 0.1);
    private Value<Double> hitchance = new Value<Double>("Killaura_HitChance", 100.0, 0.0, 100.0, 5.0);
    public boolean isBlocking;
    TimeHelper timer = new TimeHelper();
    int hit;
    private List<EntityLivingBase> loaded = new CopyOnWriteArrayList<EntityLivingBase>();
    private List<EntityLivingBase> attacktargets = new CopyOnWriteArrayList<EntityLivingBase>();
    public static EntityLivingBase target;
    public static EntityLivingBase attacktarget;
    public float[] lastRotations = new float[]{0.0f, 0.0f};
    
    
    public EntityLivingBase lastEnt;
    public float lastHealth;
    public float damageDelt;
    public float lastPlayerHealth;
    public float damageDeltToPlayer;
    public double animation;
    private DecimalFormat format;

    public KillAura() {
        super("KillAura", "Kill Aura", Category.COMBAT);
        this.priority.mode.add("Angle");
        this.priority.mode.add("Range");
        this.priority.mode.add("Fov");
        this.priority.mode.add("Health");
        this.mode.addValue("Switch");
        this.mode.addValue("Single");
        this.format = new DecimalFormat("0.0");
        this.lastHealth = -1.0f;
        this.damageDelt = 0.0f;
        this.lastPlayerHealth = -1.0f;
        this.damageDeltToPlayer = 0.0f;
        this.animation = 0.0;
    }

    @EventTarget
    public void onRender(EventRender event) {
        if (target instanceof EntityPlayer) {
            this.mc.getRenderManager();
            double x = KillAura.target.lastTickPosX + (KillAura.target.posX - KillAura.target.lastTickPosX) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosX;
            this.mc.getRenderManager();
            double y = KillAura.target.lastTickPosY + (KillAura.target.posY - KillAura.target.lastTickPosY) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosY;
            this.mc.getRenderManager();
            double z = KillAura.target.lastTickPosZ + (KillAura.target.posZ - KillAura.target.lastTickPosZ) * (double)this.mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            double width = KillAura.target.getEntityBoundingBox().maxX - KillAura.target.getEntityBoundingBox().minX;
            double height = KillAura.target.getEntityBoundingBox().maxY - KillAura.target.getEntityBoundingBox().minY + 0.25;
            float red = 0.0f;
            float green = 0.5f;
            float blue = 1.0f;
            float alpha = 0.5f;
            float lineRed = 0.0f;
            float lineGreen = 0.5f;
            float lineBlue = 1.0f;
            if (KillAura.target.hurtTime > 0) {
                red = 1.0f;
                blue = 0.0f;
                green = 0.0f;
                lineRed = 1.0f;
                lineGreen = 0.0f;
                lineBlue = 0.0f;
            } else {
                red = 0.0f;
                blue = 1.0f;
                green = 0.5f;
                lineRed = 0.0f;
                lineGreen = 0.5f;
                lineBlue = 1.0f;
            }
            float lineAlpha = 0.5f;
            float lineWdith = 0.5f;
            RenderUtil.drawEntityESP(x, y, z, width / 1.5, height, red, green, blue, alpha, lineRed, lineGreen, lineBlue, 0.5f, 0.5f);
        }
    }
    
    @EventTarget
    public void onRender2D(EventRender2D class112) {
        if (this.targethud.getValueState() && target != null) {
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            if (target != null) {
                EntityLivingBase target1 = target;
                if (target1 != this.lastEnt) {
                    this.lastEnt = target1;
                    this.lastHealth = target1.getHealth();
                    this.damageDelt = 0.0f;
                    this.damageDeltToPlayer = 0.0f;
                }
                if (this.lastHealth != target1.getHealth() && target1.getHealth() - this.lastHealth < 1.0f) {
                    this.damageDelt = target1.getHealth() - this.lastHealth;
                    this.lastHealth = target1.getHealth();
                }
                if (!this.mc.thePlayer.isEntityAlive()) {
                    this.lastPlayerHealth = -1.0f;
                }
                if (this.lastPlayerHealth == -1.0f && this.mc.thePlayer.isEntityAlive()) {
                    this.lastPlayerHealth = this.mc.thePlayer.getHealth();
                }
                if (this.lastPlayerHealth != this.mc.thePlayer.getHealth()) {
                    this.damageDeltToPlayer = this.mc.thePlayer.getHealth() - this.lastPlayerHealth;
                    this.lastPlayerHealth = this.mc.thePlayer.getHealth();
                }
                String replaceAll = target.getName().replaceAll("¡ì.", "");
                String string = "HP: " + String.valueOf(this.format.format(target.getHealth()));
                String string2 = "In: " + this.format.format(this.damageDeltToPlayer).replace("-", "") + "/Out: " + this.format.format(this.damageDelt).replace("-", "");
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(scaledResolution.getScaledWidth() / 2), (float)(scaledResolution.getScaledHeight() / 2), 0.0f);
                if (!target.isDead) {
                    float n = target.getHealth() / target.getMaxHealth() * 100.0f;
                    this.animation = RenderUtil.getAnimationState(this.animation, n, Math.max(10.0, Math.abs(this.animation - n) * 30.0) * 0.3);
                    RenderUtil.drawArc(1.0f, 1.0f, 15.0, Colors.RED.c, 180, 180.0 + 3.5999999046325684 * this.animation, 5);
                    RenderUtil.drawArc(1.0f, 1.0f, 16.0, Colors.BLUE.c, 180, 180.0f + 3.6f * (target.getTotalArmorValue() * 4), 3);
                    Gui.drawCenteredString(this.mc.fontRendererObj, replaceAll, 0, -30, Colors.WHITE.c);
                    Client.instance.fontMgr.tahoma16.drawCenteredString(string2, 0.0f, 20.0f, Colors.WHITE.c);
                    Client.instance.fontMgr.tahoma16.drawCenteredString(string, 0.0f, 30.0f, Colors.WHITE.c);
                }
                GL11.glPopMatrix();
            }
        }
    }

    public void color(int color) {
        float f = (float)(color >> 24 & 255) / 255.0f;
        float f2 = (float)(color >> 16 & 255) / 255.0f;
        float f3 = (float)(color >> 8 & 255) / 255.0f;
        float f4 = (float)(color & 255) / 255.0f;
        GL11.glColor4f((float)f2, (float)f3, (float)f4, (float)f);
    }

    public void drawRect(double x1, double y1, double x2, double y2, int color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        this.color(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glPopMatrix();
    }

    public void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        this.drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        Object color = null;
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
        int[] indicies = KillAura.getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        return KillAura.blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color = null;
        try {
            color = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.getItemRenderer().block = false;
        this.loaded = new CopyOnWriteArrayList<EntityLivingBase>();
        if (this.isBlocking) {
            Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.isBlocking = false;
        }
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        boolean isblock = !this.getTargets(blockrange.getValueState(), 360.0f).isEmpty();
        this.mc.getItemRenderer().block = this.isBlocking && isblock && this.canBlock();
    }

    @EventTarget
    public void onPreMotion(EventPreMotion event) {
        List<EntityLivingBase> targets;
        if (!Minecraft.thePlayer.isEntityAlive() && this.autodisable.getValueState().booleanValue()) {
            this.set(false);
        }
        if (this.mode.isCurrentMode("Switch")) {
            this.setDisplayName("Switch");
        }
        if (this.mode.isCurrentMode("Single")) {
            this.setDisplayName("Single");
        }
        if (autoBlock.getValueState().booleanValue() && this.canBlock() && this.isBlocking) {
            Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            this.isBlocking = false;
        }
        if ((targets = this.sortList(this.getTargets(range.getValueState(), this.fov.getValueState().floatValue()))).isEmpty() && !this.attacktargets.isEmpty()) {
            this.attacktargets.clear();
        }
        this.loaded = this.sortList(this.getTargets(range.getValueState(), this.fov.getValueState().floatValue()));
        if (this.loaded.isEmpty()) {
            target = null;
        } else {
            target = this.attacktargets == targets.get(0) && this.loaded.size() > 1 ? this.loaded.get(1) : this.loaded.get(0);
            float[] rotation = RotationUtil.getEntityRotations(target, this.lastRotations, this.aac.getValueState(), 100);
            if (target.getDistanceSqToEntity(Minecraft.thePlayer) <= (double)KillAura.target.width * 0.2) {
                rotation = this.lastRotations;
            }
            if (rotation != null) {
                event.yaw = rotation[0];
                event.pitch = rotation[1];
            }
        }
        this.lastRotations = new float[]{event.yaw, event.pitch};
    }

    @EventTarget
    public void onPost(EventPostMotion event) {
        boolean isblock;
        if (target != null && this.shouldattack()) {
            this.attack();
            this.timer.reset();
        }
        boolean bl = isblock = !this.getTargets(blockrange.getValueState(), 360.0f).isEmpty();
        if (isblock && this.canBlock() && !this.isBlocking && autoBlock.getValueState().booleanValue()) {
            Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Minecraft.thePlayer.getCurrentEquippedItem()));
            this.isBlocking = true;
        }
    }

    public boolean shouldattack() {
        if (this.isValidEntity(target, range.getValueState(), this.fov.getValueState().floatValue())) {
            return this.timer.isDelayComplete(1000L / Math.max(this.cps.getValueState().longValue(), 1L));
        }
        target = null;
        return false;
    }

    public void attack() {
        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
        if (sharpLevel > 0.0f) {
            Minecraft.thePlayer.onEnchantmentCritical(target);
        }
        if (this.isBlocking && this.canBlock()) {
            Minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            this.isBlocking = false;
        }
        if (AngleUtility.randomFloat(0.0f, 100.0f) <= this.hitchance.getValueState().floatValue()) {
            Minecraft.thePlayer.swingItem();
            EventAttack attack = new EventAttack(target);
            EventManager.call(attack);
            Minecraft.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.ATTACK));
        } else {
            Minecraft.thePlayer.swingItem();
        }
        if (this.mode.isCurrentMode("Switch")) {
            ++this.hit;
            if (this.hit >= 3) {
                this.attacktargets.add(target);
                attacktarget = target;
                this.hit = 0;
            }
        }
    }

    private List<EntityLivingBase> getTargets(double range, float fov) {
        ArrayList<EntityLivingBase> load = new ArrayList<EntityLivingBase>();
        for (Entity o : this.mc.theWorld.getLoadedEntityList()) {
            EntityLivingBase entity;
            if (!(o instanceof EntityLivingBase) || !this.isValidEntity(entity = (EntityLivingBase)o, range, fov) || this.attacktargets.contains(entity)) continue;
            load.add(entity);
        }
        return load;
    }

    public boolean canBlock() {
        return Minecraft.thePlayer.getHeldItem() != null && Minecraft.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean isValidEntity(EntityLivingBase ent, double range, float fov) {
        if (ent == null) {
            return false;
        }
        if (ent == Minecraft.thePlayer) {
            return false;
        }
        if (!ent.isEntityAlive()) {
            return false;
        }
        if (ent instanceof EntityPlayer && !this.players.getValueState().booleanValue()) {
            return false;
        }
        if ((ent instanceof EntityAnimal || ent instanceof EntityVillager || ent instanceof EntitySquid || ent instanceof EntityArmorStand) && !this.animals.getValueState().booleanValue()) {
            return false;
        }
        if ((ent instanceof EntityMob || ent instanceof EntityBat || ent instanceof EntityDragon || ent instanceof EntityGolem) && !this.mobs.getValueState().booleanValue()) {
            return false;
        }
        if ((double)Minecraft.thePlayer.getDistanceToEntity(ent) > range) {
            return false;
        }
        if (ent.isDead || ent.getHealth() <= 0.0f) {
            return false;
        }
        if (ent.isInvisible() && !this.invis.getValueState().booleanValue()) {
            return false;
        }
        if (ent instanceof EntityPlayer && KillAura.isOnSameTeam(ent) && this.teams.getValueState().booleanValue()) {
            return false;
        }
        if (!Minecraft.thePlayer.canEntityBeSeen(ent) & this.walls.getValueState() == false) {
            return false;
        }
        if (Minecraft.thePlayer.isDead) {
            return false;
        }
        if (AngleUtility.angleDifference(RotationUtil.getEntityRotations(ent, new float[]{fov, 90.0f}, false, 100)[0], fov) > fov) {
            return false;
        }
        if (ent instanceof EntityPlayer && AntiBot.isBot(ent)) {
            return false;
        }
        return true;
    }

    public static boolean isOnSameTeam(Entity entity) {
        Minecraft.getMinecraft();
        if (Minecraft.thePlayer.getDisplayName().getUnformattedText().startsWith("\u00a7")) {
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            if (Minecraft.thePlayer.getDisplayName().getUnformattedText().length() <= 2 || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            Minecraft.getMinecraft();
            Minecraft.getMinecraft();
            if (Minecraft.thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        return false;
    }

    private List<EntityLivingBase> sortList(List<EntityLivingBase> weed) {
        if (this.priority.isCurrentMode("Range")) {
            weed.sort((o1, o2) -> (int)(o1.getDistanceToEntity(Minecraft.thePlayer) - o2.getDistanceToEntity(Minecraft.thePlayer)));
        }
        if (this.priority.isCurrentMode("Fov")) {
            weed.sort(Comparator.comparingDouble(o -> KillAura.getDistanceBetweenAngles(Minecraft.thePlayer.rotationPitch, RotationUtil.getEntityRotations(o, this.lastRotations, false, 0)[0])));
        }
        if (this.priority.isCurrentMode("Angle")) {
            weed.sort((o1, o2) -> {
                float rot1 = RotationUtil.getEntityRotations(o1, this.lastRotations, false, 0)[0];
                float rot2 = RotationUtil.getEntityRotations(o2, this.lastRotations, false, 0)[0];
                float rot3 = AngleUtility.angleDifference(rot1, this.lastRotations[0]);
                float rot4 = AngleUtility.angleDifference(rot2, this.lastRotations[0]);
                return Float.compare(rot3, rot4);
            });
        }
        if (this.priority.isCurrentMode("Health")) {
            weed.sort((o1, o2) -> (int)(o1.getHealth() - o2.getHealth()));
        }
        return weed;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }
}

