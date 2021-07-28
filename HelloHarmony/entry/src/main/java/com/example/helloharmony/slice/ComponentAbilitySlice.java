package com.example.helloharmony.slice;
import com.example.customize.ScrollViewEx;
import com.example.helloharmony.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.components.element.StateElement;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ohos.agp.components.Component.DRAG_VERTICAL;

public class ComponentAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "ComponentAbilitySlice");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_component);

        RadioContainer rc = (RadioContainer)findComponentById(ResourceTable.Id_radio_container);
        rc.setMarkChangedListener(new RadioContainer.CheckedStateChangedListener(){
            @Override
            public void onCheckedChanged(RadioContainer radioContainer, int i) {
                HiLog.info(label, "RadioContainer.CheckedStateChangedListener.onCheckedChanged!");
            }
        });

        AbsButton.CheckedStateChangedListener rb_listener = new AbsButton.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(AbsButton absButton, boolean b) {
                absButton.setClickable(!b);
            }
        };
        RadioButton rb1 = (RadioButton)findComponentById(ResourceTable.Id_radio_button_1);
        rb1.setCheckedStateChangedListener(rb_listener);
        RadioButton rb2 = (RadioButton)findComponentById(ResourceTable.Id_radio_button_2);
        rb2.setCheckedStateChangedListener(rb_listener);
        RadioButton rb3 = (RadioButton)findComponentById(ResourceTable.Id_radio_button_3);
        rb3.setCheckedStateChangedListener(rb_listener);

        Switch sw = (Switch)findComponentById(ResourceTable.Id_btn_switch);
        ShapeElement elementThumbOn = new ShapeElement();
        elementThumbOn.setShape(ShapeElement.OVAL);
        elementThumbOn.setRgbColor(RgbColor.fromArgbInt(0xFF1E90FF));
        elementThumbOn.setCornerRadius(50);
// 关闭状态下滑块的样式
        ShapeElement elementThumbOff = new ShapeElement();
        elementThumbOff.setShape(ShapeElement.OVAL);
        elementThumbOff.setRgbColor(RgbColor.fromArgbInt(0xFFFFFFFF));
        elementThumbOff.setCornerRadius(50);
// 开启状态下轨迹样式
        ShapeElement elementTrackOn = new ShapeElement();
        elementTrackOn.setShape(ShapeElement.RECTANGLE);
        elementTrackOn.setRgbColor(RgbColor.fromArgbInt(0xFF87CEFA));
        elementTrackOn.setCornerRadius(50);
// 关闭状态下轨迹样式
        ShapeElement elementTrackOff = new ShapeElement();
        elementTrackOff.setShape(ShapeElement.RECTANGLE);
        elementTrackOff.setRgbColor(RgbColor.fromArgbInt(0xFF808080));
        elementTrackOff.setCornerRadius(50);
        //sw.setTrackElement(trackElementInit(elementTrackOn, elementTrackOff));
        //sw.setThumbElement(thumbElementInit(elementThumbOn, elementThumbOff));
        sw.setCheckedStateChangedListener(new AbsButton.CheckedStateChangedListener() {
            @Override
            public void onCheckedChanged(AbsButton absButton, boolean b) {
                if(b){
                    sw.setTextColor(Color.GREEN);
                }else{
                    sw.setTextColor(Color.WHITE);
                }
            }
        });
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
        ScrollViewEx scrollView = (ScrollViewEx) findComponentById(ResourceTable.Id_scrollview);
        Component.DraggedListener draggedListener = new Component.DraggedListener() {
            @Override
            public void onDragDown(Component component, DragInfo dragInfo) {}

            @Override
            public void onDragStart(Component component, DragInfo dragInfo) {}

            @Override
            public void onDragUpdate(Component component, DragInfo dragInfo) {}

            @Override
            public void onDragEnd(Component component, DragInfo dragInfo) {}

            @Override
            public void onDragCancel(Component component, DragInfo dragInfo) {}
        };
        Button button = (Button) findComponentById(ResourceTable.Id_hello_button);
        button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                //scrollView.changeListener();
                //scrollView.setDraggedListener(DRAG_VERTICAL, draggedListener);
            }
        });
        //scrollView.setDraggedListener(DRAG_VERTICAL, draggedListener);
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

    private StateElement trackElementInit(ShapeElement on, ShapeElement off){
        StateElement trackElement = new StateElement();
        trackElement.addState(new int[]{ComponentState.COMPONENT_STATE_CHECKED}, on);
        trackElement.addState(new int[]{ComponentState.COMPONENT_STATE_EMPTY}, off);
        return trackElement;
    }
    private StateElement thumbElementInit(ShapeElement on, ShapeElement off) {
        StateElement thumbElement = new StateElement();
        thumbElement.addState(new int[]{ComponentState.COMPONENT_STATE_CHECKED}, on);
        thumbElement.addState(new int[]{ComponentState.COMPONENT_STATE_EMPTY}, off);
        return thumbElement;
    }
}
