package cn.kody.debug.mod.mods.WORLD;

import java.util.Random;

public class 搜索的户籍 {
    String name;
    String telephone;
    String motherName;
    String address;
    
    
    public 搜索的户籍(final String name, final String telephone, final String motherName, final String address) {
        super();
        this.name = name;
        this.telephone = telephone;
        this.motherName = motherName;
        this.address = address;
    }
    
    public String getSB() {
        final String[] array = { "我" + this.motherName + "孝子" + this.name + "勇敢在" + this.address + "杀你妈", "我是" + this.name + "我妈妈" + this.motherName + "在" + this.address + "卖yin约pao热线" + this.telephone, "我" + this.name + "在" + this.address + "勇敢屠杀绿色玩家婊子妈", "购买由" + this.name + "编写的我的世界Debug送妈妈客户端请拨打热线" + this.telephone, "对不起我叫李佳乐来自四川广安我今年当兵兵检没过我只能上完初中回家辍学写端圈钱求求你们可怜可怜我吧", "我叫" + this.name + "我在" + this.address + "用邻水脐橙给你妈妈洗B呢购买Debug拨打热线" + this.telephone };
        return array[new Random().nextInt(array.length)];
    }
}
