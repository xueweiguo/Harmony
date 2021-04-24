package com.components.radiobuttontest.slice;

import com.components.radiobuttontest.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.AbsButton;
import ohos.agp.components.RadioButton;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        AbsButton.CheckedStateChangedListener listener = new AbsButton.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(AbsButton absButton, boolean b) {
                absButton.setClickable(!b);
            }
        };
        //假设有三个按钮
        RadioButton rb1 = (RadioButton)findComponentById(ResourceTable.Id_radio_huawei);
        rb1.setCheckedStateChangedListener(listener);
        RadioButton rb2 = (RadioButton)findComponentById(ResourceTable.Id_radio_xiaomi);
        rb2.setCheckedStateChangedListener(listener);
        RadioButton rb3 = (RadioButton)findComponentById(ResourceTable.Id_radio_oppo);
        rb3.setCheckedStateChangedListener(listener);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
