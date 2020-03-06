package cn.kody.debug.mod.mods.WORLD;

import java.util.ArrayList;
import java.util.Random;
import com.darkmagician6.eventapi.EventTarget;

import cn.kody.debug.events.EventChatComponent;
import cn.kody.debug.mod.Category;
import cn.kody.debug.mod.Mod;
import cn.kody.debug.mod.mods.COMBAT.KillAura;
import cn.kody.debug.utils.time.TimeHelper;
import cn.kody.debug.value.Value;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

public class AutoL extends Mod
{
    public static TimeHelper LTimer;
    public static Value<Boolean> ad;
    public static Value<Boolean> wdr;
    public static Value<Boolean> abuse;
    public static List<String> wdred;
    public static List<EntityPlayerSP> power;
    static 搜索的户籍 baizhijun;
    static 搜索的户籍 fanyangxiao;
    static 搜索的户籍 lilejie;
    
    public AutoL() {
        super("AutoL", Category.WORLD);
    }
    
    @EventTarget
    public void onChat(final EventChatComponent class313) {
        try {
            if (class313.getComponent().getUnformattedText().contains(this.mc.thePlayer.getName()) && class313.getComponent().getUnformattedText().contains(KillAura.target.getName()) && AutoL.LTimer.isDelayComplete(0xD32CFF0F866F9988L ^ 0xD32CFF0F866F9A60L)) {
                final String sb = getSB();
                if (AutoL.wdr.getValueState() && !AutoL.wdred.contains(KillAura.target.getName())) {
                    AutoL.wdred.add(KillAura.target.getName());
                    this.mc.thePlayer.sendChatMessage("/wdr " + KillAura.target.getName() + " ka fly reach nokb jesus ac");
                }
                final StringBuilder append = new StringBuilder().append(KillAura.target.getName()).append(" L");
                String string;
                if (AutoL.abuse.getValueState()) {
                    string = " " + sb;
                }
                else {
                    string = "";
                }
                final StringBuilder append2 = append.append(string);
                String s;
                if (AutoL.ad.getValueState()) {
                    s = " Buy Power at power.maikama.cn";
                }
                else {
                    s = "";
                }
                mc.thePlayer.sendChatMessage(append2.append(s).toString());
                AutoL.LTimer.reset();
            }
        }
        catch (Throwable t) {}
    }
    
    public static String getSB() {
        final int nextInt = new Random().nextInt(3);
        if (nextInt == 0) {
            return AutoL.baizhijun.getSB();
        }
        if (nextInt == 1) {
            return AutoL.fanyangxiao.getSB();
        }
        if (nextInt == 2) {
            return AutoL.lilejie.getSB();
        }
        return AutoL.baizhijun.getSB();
    }
    
    static {
        AutoL.LTimer = new TimeHelper();
        AutoL.ad = new Value<Boolean>("AutoL_AD", true);
        AutoL.wdr = new Value<Boolean>("AutoL_WatchdogReport", true);
        AutoL.abuse = new Value<Boolean>("AutoL_Abuse", false);
        AutoL.wdred = new ArrayList<String>();
        AutoL.power = new ArrayList<EntityPlayerSP>();
        AutoL.baizhijun = new 搜索的户籍("李佳乐", "没钱买手机", "没有母亲", "山东");
        AutoL.fanyangxiao = new 搜索的户籍("范杨孝", "18361960482", "没有妈妈", "江苏苏州");
        AutoL.lilejie = new 搜索的户籍("李乐杰", "13546806422", "陈少燕", "广东汕头");
    }
}
