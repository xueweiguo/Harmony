package xwg.harmony.stopwatch;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

import ohos.bundle.IBundleManager;
import ohos.event.notification.NotificationRequest;

import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Location;
import ohos.location.Locator;
import ohos.location.LocatorCallback;
import ohos.location.RequestParam;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

import java.util.ArrayList;

public class StopWatchService extends Ability {
    private static final int NOTIFICATION_ID = 0XD0000002;
    private static final String TAG = StopWatchService.class.getSimpleName();
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0x00209, TAG);
    private static final String DESCRIPTOR = "xwg.harmony.stopwatch.StopWatchService";

    private int current_tab = 0;

    private boolean running = false;//执行状态
    private long start_time = 0;    //计时开始时刻，毫秒单位
    private long millisecond = 0;   //计时时间，毫秒单位
    ArrayList<String> lap_times = new ArrayList<>();
    ArrayList<Long> trailData = new ArrayList<>();

    final private MyLocatorCallback locatorCallback = new MyLocatorCallback();
    private Locator locator;
    long[] lastLocation = null;
    private static final String PERM_LOCATION = "ohos.permission.LOCATION";

    StopWatchAgentStub remoteAgentStub = new StopWatchAgentStub(DESCRIPTOR) {
        @Override
        public void setCurrentTab(int index) throws RemoteException {
            current_tab = index;
        }

        @Override
        public int getCurrentTab() throws RemoteException {
            return current_tab;
        }

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
            trailData.clear();
        }

        @Override
        public boolean start() throws RemoteException{
            HiLog.info(LABEL_LOG, "StopWatchService.Start!");
            start_time = Calendar.getInstance().getTimeInMillis();
            running = true;
            return running;
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
        public long[] getCurrentLocation() throws RemoteException {
            HiLog.info(LABEL_LOG, "onLocationReport getCurrentLocation=%{public}d,%{public}d,%{public}d!",
                    lastLocation[0], lastLocation[1], lastLocation[2]);
            return lastLocation;
        }

        @Override
        public long[] getTrailData() throws RemoteException {
            if(trailData.size() > 0) {
                long[] ret = new long[trailData.size()];
                for (int i = 0; i < trailData.size(); ++i) {
                    ret[i] = trailData.get(i);
                }
                return ret;
            }
            else{
                return null;
            }
        }

        @Override
        public void registerLocationEvent() throws RemoteException {
            StopWatchService.this.registerLocationEvent();
        }
    };

    @Override
    public void onStart(Intent intent) {
        HiLog.info(LABEL_LOG, "StopWatchService.onStart!");
        startForeground();
        super.onStart(intent);
        registerLocationEvent();
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

    private void registerLocationEvent() {
        if (hasPermissionGranted(PERM_LOCATION)) {
            HiLog.info(LABEL_LOG, "hasPermissionGranted = true");
            locator = new Locator(this);
            //RequestParam requestParam = new RequestParam(RequestParam.SCENE_NAVIGATION);
            RequestParam requestParam = new RequestParam(RequestParam.SCENE_TRAJECTORY_TRACKING);
            locator.startLocating(requestParam, locatorCallback);
        }else{
            HiLog.info(LABEL_LOG, "hasPermissionGranted = false");
        }
    }

    private void unregisterLocationEvent() {
        if (locator != null) {
            locator.stopLocating(locatorCallback);
        }
    }

    private boolean hasPermissionGranted(String permission) {
        return verifySelfPermission(permission) == IBundleManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        if (verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
            HiLog.info(LABEL_LOG, "requestPermissionsFromUser!");
            this.requestPermissionsFromUser(new String[] {permission}, 1);
        }
    }

    public static final int EVENT_LOCATION_REPORTED = 0x1000003;
    private class MyLocatorCallback implements LocatorCallback {
        @Override
        public void onLocationReport(Location location) {
            HiLog.info(LABEL_LOG, "onLocationReport Start!");
            lastLocation = new long[3];
            lastLocation[0] = Calendar.getInstance().getTimeInMillis();
            lastLocation[1] = (long)(location.getLatitude() * 100000);
            lastLocation[2] = (long)(location.getLongitude() * 100000);
            //lastLocation[1] += 1000 + 200 * Math.sin((double)trailData.size() / 10);
            //lastLocation[2] += 2000 + trailData.size() * 10;
            if(running) {
                trailData.add(lastLocation[0]);
                trailData.add(lastLocation[1]);
                trailData.add(lastLocation[2]);
            }
            //HiLog.info(LABEL_LOG, "onLocationReport lastLocation=%{public}d,%{public}d,%{public}d!",
            //        lastLocation[0], lastLocation[1], lastLocation[2]);
            StopWatchServiceConnection.getEventHandler().sendEvent(EVENT_LOCATION_REPORTED);
            HiLog.info(LABEL_LOG, "onLocationReport End!");
        }

        @Override
        public void onStatusChanged(int type) {
        }

        @Override
        public void onErrorReport(int type) {
        }
    }
}
