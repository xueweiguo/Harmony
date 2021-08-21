package xwg.filebrowser.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.filebrowser.DynamicLayout;
import xwg.filebrowser.ResourceTable;

/**
 * Slice with MainAbility
 */
public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MainAbilitySlice");
    private ViewCreateHelper viewCreateHelper;

    @Override
    public void onStart(Intent intent) {
        setUIContent(ResourceTable.Layout_browser_ability);
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
