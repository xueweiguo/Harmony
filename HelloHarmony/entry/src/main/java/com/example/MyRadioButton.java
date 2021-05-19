package com.example;

import ohos.agp.components.AttrSet;
import ohos.agp.components.RadioButton;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MyRadioButton extends RadioButton {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x45678, "MyRadioButton");
    public MyRadioButton(Context context, AttrSet attrSet) {
        super(context, attrSet);
        HiLog.info(label, "RadioContainer.Construtor!");
    }

    public void toggle(){
        HiLog.info(label, "RadioContainer.toggle!");
    }

    public void setChecked(boolean value){
        HiLog.info(label, "RadioContainer.setChecked!");
    }
}
