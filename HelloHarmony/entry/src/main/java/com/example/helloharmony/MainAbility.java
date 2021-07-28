package com.example.helloharmony;

import com.example.helloharmony.slice.*;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.TabList;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends Ability {
    // 定义日志标签
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x01234, "MainAbility");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        //setTransitionAnimation(0, 0);
        super.setMainRoute(MainAbilitySlice.class.getName());
        // set the action route
        addActionRoute("action.component", ComponentAbilitySlice.class.getName());
        addActionRoute("action.list", ListAbilitySlice.class.getName());
        addActionRoute("action.tab", TablistAbilitySlice.class.getName());
        addActionRoute("action.dialog", DialogAbilitySlice.class.getName());
        HiLog.info(label, "MainAbility.onStart!");
    }

    public void startAbility(Intent intent, int requestCode){
        super.startAbility(intent, requestCode);
        HiLog.info(label, "MainAbility.startAbility!");
    }

    public void setTransitionAnimation(int enterAnim, int exitAnim)
    {
        super.setTransitionAnimation(enterAnim, exitAnim);
        HiLog.info(label, "MainAbility.setTransitionAnimation!");
    }
}
