package com.example.helloharmony.slice;

import com.example.helloharmony.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.utils.Rect;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.PopupDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;

public class DialogAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "DialogAbilitySlice");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_dialog);
         // 打印一条日志
        HiLog.info(label, "DialogAbilitySlice.onStart!");

        //标准CommonDialog
        Button std_common_button = (Button) findComponentById(ResourceTable.Id_std_common_dialog);
        std_common_button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                showStdCommon();
            }
        });
        //自定义CommonDialog
        Button customize_common_button = (Button) findComponentById(ResourceTable.Id_customize_common_dialog);
        customize_common_button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                showCustomizeCommon();
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
    @Override
    public void onStop() {
        // 打印一条日志
        HiLog.info(label, "DialogAbilitySlice.onStop!");
    }

    private void showStdCommon(){
        CommonDialog dlg = new CommonDialog(this);
        dlg.setContentImage(ResourceTable.Media_icon);
        dlg.setTitleText("CommonDialog");
        dlg.setTitleSubText("Standard Demo");
        dlg.setContentText("This is a CommonDialog Test!");
        dlg.setMovable(true);
        dlg.setButton(1, "OK", new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                dlg.destroy();
            }
        });
    }


    private void showCustomizeCommon(){
        CommonDialog dlg = new CommonDialog(this);
        Component layout = LayoutScatter.getInstance(this).parse(ResourceTable.Layout_common_dialog, null, true);
        dlg.setTransparent(true);
        dlg.setContentCustomComponent(layout);
        Component.ClickedListener listener = new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                new ToastDialog(getContext())
                        .setText(((Button)component).getText())
                        .show();
            }
        };
        Button good = (Button)layout.findComponentById(ResourceTable.Id_good);
        good.setClickedListener(listener);
        Button ordinary = (Button)layout.findComponentById(ResourceTable.Id_ordinary);
        ordinary.setClickedListener(listener);
        Button bad = (Button)layout.findComponentById(ResourceTable.Id_bad);
        bad.setClickedListener(listener);
        dlg.show();
    }
}
