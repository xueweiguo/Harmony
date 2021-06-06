package xwg.harmony.stopwatch;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import xwg.harmony.stopwatch.slice.MainAbilitySlice;
import xwg.harmony.stopwatch.slice.SettingState;

public class MainAbility extends Ability {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MainAbility");
    @Override
    public void onStart(Intent intent) {
        HiLog.info(LABEL, "MainAbility.onStart");
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        addActionRoute("action.setting", SettingState.class.getName());
    }
    @Override
    public void onStop(){
        HiLog.info(LABEL, "MainAbility.onStop");
        super.onStop();
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "MainAbility.onForeground");
        super.onForeground(intent);
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MainAbility.onBackground");
        super.onBackground();
    }
}
