package com.y2w.uikit.common;

/**
 * Created by maa2 on 2016/1/21.
 */
public enum SessionTypeEnum {
    None(-1),
    P2P(0),
    Team(1),
    System(10001);

    private int value;

    private SessionTypeEnum(int var3) {
        this.value = var3;
    }

    public final int getValue() {
        return this.value;
    }

    public static SessionTypeEnum typeOfValue(int var0) {
        SessionTypeEnum[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            SessionTypeEnum var4;
            if((var4 = var1[var3]).getValue() == var0) {
                return var4;
            }
        }

        return P2P;
    }
}
