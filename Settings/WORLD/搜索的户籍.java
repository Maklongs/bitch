package cn.kody.debug.mod.mods.WORLD;

import java.util.Random;

public class �����Ļ��� {
    String name;
    String telephone;
    String motherName;
    String address;
    
    
    public �����Ļ���(final String name, final String telephone, final String motherName, final String address) {
        super();
        this.name = name;
        this.telephone = telephone;
        this.motherName = motherName;
        this.address = address;
    }
    
    public String getSB() {
        final String[] array = { "��" + this.motherName + "Т��" + this.name + "�¸���" + this.address + "ɱ����", "����" + this.name + "������" + this.motherName + "��" + this.address + "��yinԼpao����" + this.telephone, "��" + this.name + "��" + this.address + "�¸���ɱ��ɫ��������", "������" + this.name + "��д���ҵ�����Debug������ͻ����벦������" + this.telephone, "�Բ����ҽ�����������Ĵ��㰲�ҽ��굱������û����ֻ��������лؼ��ѧд��ȦǮ�������ǿ��������Ұ�", "�ҽ�" + this.name + "����" + this.address + "����ˮ��ȸ�������ϴB�ع���Debug��������" + this.telephone };
        return array[new Random().nextInt(array.length)];
    }
}
