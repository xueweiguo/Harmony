package xwg.harmony.stopwatch;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.event.notification.NotificationRequest;
import ohos.eventhandler.InnerEvent;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

import java.util.ArrayList;

public class StopWatchService extends Ability {
    private static final int NOTIFICATION_ID = 0XD0000002;
    private static final String TAG = StopWatchService.class.getSimpleName();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0x00209, TAG);
    private static final String DESCRIPTOR = "xwg.harmony.stopwatch.StopWatchService";

    private boolean running = false;//执行状态
    private long start_time = 0;    //计时开始时刻，毫秒单位
    private long millisecond = 0;   //计时时间，毫秒单位

    ArrayList<String> lap_times = new ArrayList<String>();

    StopWatchAgentStub remoteAgentStub = new StopWatchAgentStub(DESCRIPTOR) {
        @Override
        public boolean isRunning() throws RemoteException {
            //HiLog.info(LABEL_LOG, "StopWatchService.isRunning=%{public}b!", running);
            return running;
        }

        @Override
        public long getStartTime() throws RemoteException {
            return start_time;
        }

        @Override
        public long getTime() throws RemoteException {
            if(isRunning()){
                millisecond = Calendar.getInstance().getTimeInMillis() - start_time;
            }
            return millisecond;
        }

        @Override
        public void resetTime() throws RemoteException {
            HiLog.info(LABEL_LOG, "StopWatchService.resetTime!");
            lap_times.clear(); 
            millisecond = 0;
        }

        @Override
        public void stop() throws RemoteException {
            HiLog.info(LABEL_LOG, "StopWatchService.Stop!");
            running = false;
        }

        @Override
        public void recordTime() throws RemoteException {
            long milliseconds = getTime();
            String current_time = String.format("Lap%02d %02dh:%02dm%02ds.%03dms\n",
                    lap_times.size(),
                    milliseconds / 1000 / 60 / 60 % 60,  //hour
                    milliseconds / 1000 / 60 % 60,       //minute
                    milliseconds / 1000 % 60,            //second
                    milliseconds % 1000);                //milisecond
            lap_times.add(current_time);
        }

        @Override
        public String[] getLapTimes() throws RemoteException {
            String[] times = null;
            HiLog.info(LABEL_LOG, "StopWatchService.getLapTimes Start!");
            times = (String[]) lap_times.toArray(new String[lap_times.size()]);
            HiLog.info(LABEL_LOG, "StopWatchService.getLapTimes End!");
            return times;
        }

        @Override
        public boolean start() throws RemoteException{
            HiLog.info(LABEL_LOG, "StopWatchService.Start!");
            start_time = Calendar.getInstance().getTimeInMillis();
            running = true;
            return running;
        }

        @Override
        public double[] getCurrentLocation() throws RemoteException {
            HiLog.info(LABEL_LOG, "StopWatchService.getCurrentLocation!");
            return new double[0];
        }
    };

    @Override
    public void onStart(Intent intent) {
        HiLog.info(LABEL_LOG, "StopWatchService.onStart!");
        startForeground();
        super.onStart(intent);
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
        HiLog.info(LABEL_LOG, "StopWatchService.onCommand!");
        super.onCommand(intent, restart, startId);
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        HiLog.info(LABEL_LOG, "StopWatchService.onConnect!");
        return remoteAgentStub;
    }

    @Override
    public void onDisconnect(Intent intent) {
        HiLog.info(LABEL_LOG, "StopWatchService.onDisconnect!");
        super.onDisconnect(intent);
    }

    @Override
    public void onStop() {
        HiLog.info(LABEL_LOG, "StopWatchService.onStop()<<<<<<<<<<<<<<<<<!");
        super.onStop();
        cancelBackgroundRunning();
    }

    private void startForeground() {
        NotificationRequest request = new NotificationRequest(NOTIFICATION_ID).setTapDismissed(true);
        NotificationRequest.NotificationNormalContent content = new NotificationRequest.NotificationNormalContent();
        content.setTitle("秒表服务").setText("前台服务运行中...");
        NotificationRequest.NotificationContent notificationContent = new NotificationRequest.NotificationContent(
                content);
        request.setContent(notificationContent);
        keepBackgroundRunning(NOTIFICATION_ID, request);
    }
}
