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
import xwg.harmony.stopwatch.StopWatchAgentProxy;

public class StopWatchState extends SliceState {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00202, "StopWatchState");
    StopWatchAgentProxy stopWatchService = null;
    AnalogStopWatch stopwatch = null;
    Button start_stop = null;
    Button reset_lap = null;
    Text lap_time = null;

    public StopWatchState(AbilitySlice slice, ComponentContainer container) {
        super(slice, container);
        stopWatchService = ((MainAbilitySlice)owner_slice).stopWatchProxy;
    }

    void setStopWatchService(StopWatchAgentProxy service){
        stopWatchService = service;
        if(stopwatch != null){
            stopwatch.setStopWatchService(service);
        }
        updateButton();
        updateLapTimes();
    }

    @Override
    public int getLayoutId(){ return ResourceTable.Layout_stopwatch;};

    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LABEL, "StopWatchState.onStart");
        super.onStart(intent);
        //秒表组件
        stopwatch = (AnalogStopWatch)owner_slice.findComponentById(ResourceTable.Id_analog_stop_watch);
        stopwatch.setStopWatchService(stopWatchService);
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
                    stopwatch.recordTime();
                    updateLapTimes();
                }
                stopwatch.start_stop();
                updateButton();
            }
        });

        reset_lap.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (stopwatch.isRunning()){
                    stopwatch.recordTime();
                    updateLapTimes();
                }
                else{
                    stopwatch.reset();
                    clearTime();
                    start_stop.setTextColor(Color.BLACK);
                }
            }
        });
        updateButton();
        updateLapTimes();
        HiLog.info(LABEL, "StopWatchState.onStart End!");
    }

    @Override
    public void onStop(){
        HiLog.info(LABEL, "onStop");
        super.onStop();
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "StopWatchState.onForeground");
        super.onForeground(intent);
        stopwatch.onForeground(intent);
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "StopWatchState.onBackground");
        super.onBackground();
        saveStatus();
        stopwatch.onBackground();
    }

    //清除计时结果
    private void clearTime(){
        lap_time.setText("");
    }

    //记录当前时间
    private void updateLapTimes(){
        String[] time_strings = stopwatch.getLapTimes();
        if(time_strings != null && time_strings.length > 0) {
            String lap_string = new String();
            for (String current_time : time_strings) {
                lap_string += current_time;
            }
            lap_time.setText(lap_string);
        }
    }

    private void updateButton(){
        HiLog.info(LABEL, "StopWatchState.updateButton");
        if(stopwatch.isRunning()){
            HiLog.info(LABEL, "StopWatchState.updateButton.isRunning()==true");
            start_stop.setText(ResourceTable.String_Stop);
            start_stop.setTextColor(Color.BLACK);
            reset_lap.setText(ResourceTable.String_Lap);
        }
        else{
            HiLog.info(LABEL, "StopWatchState.updateButton.isRunning()==false");
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
        HiLog.info(LABEL, "StopWatchState.loadStatus");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("StopWatch");
        //stopwatch.setStartTime(preferences.getLong("start_time", 0));
        //stopwatch.setMilliseconds(preferences.getLong("milliseconds", 0));
        //lap_time.setText(preferences.getString("lap_times", ""));
        //record_count = preferences.getInt("record_count", 0);
        if(preferences.getBoolean("running",false)){
            stopwatch.start();
        }
        updateButton();
    }
    
    private void saveStatus(){
        HiLog.info(LABEL, "StopWatchState.saveStatus");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("StopWatch");
        preferences.putBoolean("running", stopwatch.isRunning());
        preferences.putLong("start_time", stopwatch.getStartTime());
        preferences.putLong("milliseconds", stopwatch.getMilliseconds());
        preferences.putString("lap_times", lap_time.getText());
        //preferences.putInt("record_count", record_count);
    }
}
