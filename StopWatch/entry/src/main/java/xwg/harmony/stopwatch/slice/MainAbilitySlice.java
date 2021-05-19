package xwg.harmony.stopwatch.slice;

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
    static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MainAbilitySlice");
    long lastSecond = 0;
    TimePicker timePicker = null;
    Text guiText = null;
    AnalogStopWatch stopwatch = null;
    long startMillis = 0;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        guiText = (Text)findComponentById(ResourceTable.Id_gui_thread_time);
        stopwatch = (AnalogStopWatch)findComponentById(ResourceTable.Id_analog_stop_watch);
        startMillis = Calendar.getInstance().getTimeInMillis();
        onTimer();
    }
    void onTimer(){
        final long delayTime = 10L;
        TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
        Revocable revocable = uiTaskDispatcher.delayDispatch(new Runnable() {
            @Override
            public void run() {
                Calendar rightNow = Calendar.getInstance();
                long currentMillis = rightNow.getTimeInMillis();
                long time = currentMillis - startMillis;
                String now = String.format("%02d:%02d:%02d.%03d",
                        time/1000/3600,   //hour
                        time/1000/60%60,  //minute
                        time/1000%60,     //second
                        time%1000);       //millisecond
                guiText.setText(now);
                stopwatch.setSecond(time/1000f);
                onTimer();
            }
        }, delayTime);
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
