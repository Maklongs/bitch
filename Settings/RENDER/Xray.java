/*
 * Decompiled with CFR 0_132 Helper by Lightcolour E-mail wyy-666@hotmail.com.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package cn.kody.debug.mod.mods.RENDER;

import cn.kody.debug.events.EventRender;
import cn.kody.debug.mod.Category;
import cn.kody.debug.mod.Mod;
import cn.kody.debug.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;

public class Xray
extends Mod {
    public static Value<Double> opacityvalue = new Value<Double>("Xray_Opacity", 160.0, 0.0, 255.0, 5.0);
    public static Value<Boolean> cave = new Value<Boolean>("Xray_Cave", false);
    public static Value<String> cavemode = new Value("Xray", "CaveMode", 0);
    public static int opacity = 160;
    public Value<Double> limit = new Value<Double>("Xray_BlockLimit", 250.0, 10.0, 1000.0, 1.0);
    public static ArrayList<BlockPos> toRender = new ArrayList();
    private List<Integer> KEY_IDS = Lists.newArrayList(new Integer[]{10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154});
    public static ArrayList<Integer> blockIDs = new ArrayList();
    public static ArrayList<Integer> blockIDs2 = new ArrayList();

    public Xray() {
        super("Xray", Category.RENDER);
        cavemode.addValue("Box");
        cavemode.addValue("Normal");
    }

    @Override
    public void onEnable() {
        opacity = opacityvalue.getValueState().intValue();
        blockIDs.add(14);
        blockIDs.add(56);
        try {
            for (Integer o : this.KEY_IDS) {
                blockIDs2.add(o);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.mc.renderGlobal.loadRenderers();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        toRender.clear();
        this.mc.renderGlobal.loadRenderers();
        super.onDisable();
    }

    @EventTarget
    public void onRender(EventRender event) {
        for (BlockPos blockPos : toRender) {
        }
    }

    public static boolean containsID(int id) {
        return blockIDs2.contains(id);
    }

    public static boolean containsID2(int id) {
        return blockIDs2.contains(id);
    }

    public static int getOpacity() {
        return opacity;
    }
}

