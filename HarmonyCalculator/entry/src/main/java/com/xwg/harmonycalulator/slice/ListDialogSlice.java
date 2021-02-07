package com.xwg.harmonycalulator.slice;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ListDialogSlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x00202, "ListDialogSlice");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_list_dialog);
        HiLog.warn(label, "ListDialogSlice onStart!");
        Button button = (Button)findComponentById(ResourceTable.Id_ok_button);
        button.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component v){
                terminate();
                HiLog.warn(label, "OK button clicked!");
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    protected void onStop(){
        super.onStop();
        //setResult();
    }
}