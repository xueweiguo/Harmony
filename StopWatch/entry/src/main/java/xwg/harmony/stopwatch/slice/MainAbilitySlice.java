package xwg.harmony.stopwatch.slice;

import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.Text;
import ohos.agp.components.TimePicker;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.Stopwatch;
import xwg.harmony.stopwatch.AnalogStopWatch;
import xwg.harmony.stopwatch.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class MainAbilitySlice extends AbilitySlice {
    AnalogStopWatch stopwatch = null;
    Text lap_time = null;
    int record_count = 0;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        //秒表组件
        stopwatch = (AnalogStopWatch)findComponentById(ResourceTable.Id_analog_stop_watch);
        //计时结果Text组件
        lap_time = (Text)findComponentById(ResourceTable.Id_lap_times);
        //开始或停止按钮
        Button start_stop = (Button)findComponentById(ResourceTable.Id_start_stop);
        //清零或保存结果按钮
        Button reset_lap = (Button)findComponentById(ResourceTable.Id_reset_lap);
        start_stop.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                stopwatch.start_stop();
                if(stopwatch.isRunning()){
                    start_stop.setText(ResourceTable.String_Stop);
                    reset_lap.setText(ResourceTable.String_Lap);
                    clearTime();
                }
                else{
                    start_stop.setText(ResourceTable.String_Start);
                    reset_lap.setText(ResourceTable.String_Reset);
                    recordTime();
                }
            }
        });

        reset_lap.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (stopwatch.isRunning()){
                    recordTime();
                }
                else{
                    stopwatch.reset();
                    clearTime();
                }
            }
        });
    }
    //清除计时结果
    private void clearTime(){
        lap_time.setText("");
        record_count = 0;
    }

    //记录当前时间
    private void recordTime(){
        String lap_string = lap_time.getText();
        long milliseconds = stopwatch.getMiliseconds();
        String current_time = String.format("Lap%02d %02d:%02d:%02d.%03d",
                record_count,
                milliseconds / 1000 / 60 / 60 % 60,  //hour
                milliseconds / 1000 / 60 % 60,       //minute
                milliseconds / 1000 % 60,            //second
                milliseconds % 1000);                //milisecond
        lap_time.setText(lap_string + "\n" + current_time);
        record_count++;
    }
}
