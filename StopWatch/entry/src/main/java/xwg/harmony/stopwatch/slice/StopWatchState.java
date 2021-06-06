package xwg.harmony.stopwatch.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.harmony.stopwatch.AnalogStopWatch;
import xwg.harmony.stopwatch.ResourceTable;

public class StopWatchState extends SliceState {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00102, "StopWatchState");
    AnalogStopWatch stopwatch = null;
    Text lap_time = null;
    int record_count = 0;
    Button start_stop = null;
    Button reset_lap = null;

    public StopWatchState(AbilitySlice slice, ComponentContainer container) {
        super(slice, container);
    }

    @Override
    public int getLayoutId(){ return ResourceTable.Layout_stopwatch;};

    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LABEL, "onStart");
        super.onStart(intent);
        //秒表组件
        stopwatch = (AnalogStopWatch)owner_slice.findComponentById(ResourceTable.Id_analog_stop_watch);
        //计时结果Text组件
        lap_time = (Text)owner_slice.findComponentById(ResourceTable.Id_lap_times);
        //开始或停止按钮
        start_stop = (Button)owner_slice.findComponentById(ResourceTable.Id_start_stop);
        //清零或保存结果按钮
        reset_lap = (Button)owner_slice.findComponentById(ResourceTable.Id_reset_lap);
        start_stop.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if(stopwatch.isRunning()){
                    recordTime();
                }
                stopwatch.start_stop();
                updateButton();
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
                    start_stop.setTextColor(Color.BLACK);
                }
            }
        });
        loadStatus();
    }

    @Override
    public void onStop(){
        HiLog.info(LABEL, "onStop");
        super.onStop();
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "MainAbilitySlice.onForeground");
        super.onForeground(intent);
        stopwatch.onForeground(intent);
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MainAbilitySlice.onBackground");
        super.onBackground();
        saveStatus();
        stopwatch.onBackground();
    }

    //清除计时结果
    private void clearTime(){
        lap_time.setText("");
        record_count = 0;
    }

    //记录当前时间
    private void recordTime(){
        String lap_string = lap_time.getText();
        long milliseconds = stopwatch.getMilliseconds();
        String current_time = String.format("Lap%02d %02dh:%02dm%02ds.%03dms\n",
                record_count,
                milliseconds / 1000 / 60 / 60 % 60,  //hour
                milliseconds / 1000 / 60 % 60,       //minute
                milliseconds / 1000 % 60,            //second
                milliseconds % 1000);                //milisecond
        lap_time.setText(lap_string + current_time);
        record_count++;
    }

    private void updateButton(){
        if(stopwatch.isRunning()){
            start_stop.setText(ResourceTable.String_Stop);
            start_stop.setTextColor(Color.BLACK);
            reset_lap.setText(ResourceTable.String_Lap);
        }
        else{
            start_stop.setText(ResourceTable.String_Start);
            if(stopwatch.getMilliseconds() == 0) {
                start_stop.setTextColor(Color.BLACK);
            }
            else {
                start_stop.setTextColor(Color.LTGRAY);
            }
            reset_lap.setText(ResourceTable.String_Reset);
        }
    }

    private void loadStatus(){
        HiLog.info(LABEL, "MainAbilitySlice.loadStatus");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("StopWatch");
        stopwatch.setStartTime(preferences.getLong("start_time", 0));
        stopwatch.setMilliseconds(preferences.getLong("milliseconds", 0));
        lap_time.setText(preferences.getString("lap_times", ""));
        record_count = preferences.getInt("record_count", 0);
        if(preferences.getBoolean("running",false)){
            stopwatch.start();
        }
        updateButton();
    }
    
    private void saveStatus(){
        HiLog.info(LABEL, "MainAbilitySlice.saveStatus");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("StopWatch");
        preferences.putBoolean("running", stopwatch.isRunning());
        preferences.putLong("start_time", stopwatch.getStartTime());
        preferences.putLong("milliseconds", stopwatch.getMilliseconds());
        preferences.putString("lap_times", lap_time.getText());
        preferences.putInt("record_count", record_count);
    }
}
