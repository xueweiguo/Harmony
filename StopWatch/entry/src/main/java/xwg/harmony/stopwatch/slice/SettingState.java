package xwg.harmony.stopwatch.slice;

import ohos.agp.components.ComponentContainer;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.harmony.stopwatch.MenuTabList;
import xwg.harmony.stopwatch.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class SettingState extends SliceState {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00204, "SettingState");

    public SettingState(AbilitySlice slice, ComponentContainer container) {
        super(slice, container);
    }

    @Override
    public int getLayoutId() {
        return ResourceTable.Layout_setting;
    }

    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LABEL, "onStart");
        super.onStart(intent);
    }
}
