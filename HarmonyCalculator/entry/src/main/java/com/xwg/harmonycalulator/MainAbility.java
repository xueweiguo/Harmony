package com.xwg.harmonycalulator;

import com.xwg.harmonycalulator.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.AbilityInfo;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import static ohos.bundle.AbilityInfo.DisplayOrientation.PORTRAIT;

public class MainAbility extends Ability {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x00200, "MainAbility");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        HiLog.warn(label, "displayOrientation=" + getDisplayOrientation());
        AbilityInfo info = getAbilityInfo();
        HiLog.warn(label, "abilityinfo=" + info.toString());
    }

    @Override
    public void onOrientationChanged(AbilityInfo.DisplayOrientation displayOrientation){
        HiLog.warn(label, displayOrientation.toString());
    }
}
