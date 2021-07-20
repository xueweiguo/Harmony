package xwg.harmony.stopwatch;

import ohos.aafwk.ability.AbilityPackage;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MyApplication extends AbilityPackage {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00210, "MyApplication");
    @Override
    public void onInitialize() {
        HiLog.info(LABEL, "MyApplication.onInitialize");
        super.onInitialize();
    }

    public void onEnd(){
        HiLog.info(LABEL, "MyApplication.onEnd");
        super.onEnd();
    }
}
