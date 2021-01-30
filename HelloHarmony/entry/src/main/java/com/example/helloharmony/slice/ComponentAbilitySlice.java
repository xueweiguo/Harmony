package com.example.helloharmony.slice;
import com.example.helloharmony.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ComponentAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "ComponentAbilitySlice");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_component);
        //获取TimePicker组件
        TimePicker picker = (TimePicker) findComponentById(ResourceTable.Id_time_picker);
        //获取ProgressBar组件
        updateProgressValue(picker);
        //为TimePicker设定事件响应
        ComponentAbilitySlice this_slice = this;
        picker.setTimeChangedListener(
            new TimePicker.TimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int hour, int minute, int second) {
                    this_slice.updateProgressValue(timePicker);
                }
            }
        );
        // 打印一条日志
        HiLog.info(label, "ComponentAbilitySlice.onStart!");
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
        super.onStop();
        // 打印一条日志
        HiLog.info(label, "ComponentAbilitySlice.onStop!");
    }

    private void updateProgressValue(TimePicker picker){
        int seconds = (picker.getHour() * 60 + picker.getMinute()) * 60 + picker.getSecond();
        setProgressValue(ResourceTable.Id_hour_progress, picker.getHour(), 24);
        setProgressValue(ResourceTable.Id_minute_progress, picker.getMinute(), 60);
        setProgressValue(ResourceTable.Id_second_progress, picker.getSecond(), 60);
        setProgressValue(ResourceTable.Id_round_progress, seconds, 86400);
    }

    private void setProgressValue(int id, int value, int span)
    {
        ProgressBar progress = (ProgressBar)findComponentById(id);
        progress.setProgressValue(progress.getMin() + value * (progress.getMax() - progress.getMin()) / span);
    }
}
