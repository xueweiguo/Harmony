package xwg.harmony.stopwatch;

import ohos.aafwk.ability.IAbilityConnection;
import ohos.bundle.ElementName;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;

public class StopWatchServiceConnection  implements IAbilityConnection {
    static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00211, "StopWatchServiceConnection");
    private static StopWatchEventListener stopWatchEventListener;

    public interface StopWatchEventListener{
        void onConnectDone(StopWatchAgentProxy proxy);
        void onDisconnectDone();
        void onLocationReported();
    }

    public static void setStopWatchEventListener(StopWatchEventListener listener){
        stopWatchEventListener = listener;
    }

    public static final int EVENT_CONNECT_DONE = 0x1000001;
    public static final int EVENT_DISCONNECT_DONE = 0x1000002;
    public static final int EVENT_LOCATION_REPORTED = 0x1000003;

    /**
     * handle message from service to ability slice
     */
    private static EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            switch(event.eventId){
                case EVENT_CONNECT_DONE:
                    if(event.object instanceof StopWatchAgentProxy){
                        stopWatchEventListener.onConnectDone((StopWatchAgentProxy)event.object);
                    }
                    break;
                case EVENT_DISCONNECT_DONE:
                    stopWatchEventListener.onDisconnectDone();
                    break;
                case EVENT_LOCATION_REPORTED:
                    stopWatchEventListener.onLocationReported();
                    break;
            }
            /*
            if (event.eventId == Const.HANDLER_EVENT_ID && event.object instanceof String) {
                String message = (String) event.object;
                downloadStateChangeListener.onDownloadStateChange(message);
            }
            */
        }
    };

    /**
     * getEventHandler
     *
     * @return EventHandler
     */
    public static EventHandler getEventHandler() {
        return handler;
    }

    @Override
    public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int resultCode) {
        HiLog.info(LOG_LABEL, "%{public}s", "onAbilityConnectDone resultCode : " + resultCode);
        InnerEvent innerEvent = InnerEvent.get(EVENT_CONNECT_DONE, resultCode, new StopWatchAgentProxy(iRemoteObject));
        handler.sendEvent(innerEvent);
    }

    @Override
    public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
        HiLog.info(LOG_LABEL, "%{public}s", "onAbilityDisconnectDone resultCode : " + resultCode);
        InnerEvent innerEvent = InnerEvent.get(EVENT_DISCONNECT_DONE, resultCode);
        handler.sendEvent(innerEvent);
    }
}
