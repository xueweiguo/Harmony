package xwg.harmony.stopwatch.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.TabList;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.bundle.ElementName;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;
import xwg.harmony.stopwatch.ResourceTable;
import xwg.harmony.stopwatch.StopWatchDB;
import xwg.harmony.stopwatch.StopWatchAgentProxy;

public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel LOG_LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MainAbilitySlice");
    TabList tabList = null;
    TabList.Tab stopwatchTab = null;
    StopWatchState stopWatchState = null;
    TabList.Tab mapTab = null;
    MapState mapState = null;
    TabList.Tab settingTab = null;
    SettingState settingState = null;
    TabList.Tab current_tab = null;
    private SliceState current_state = null;
    StopWatchAgentProxy stopWatchProxy = null;

    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LOG_LABEL, "MainAbilitySlice.onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext dbContext = helper.getOrmContext("StopWatch", "StopWatch.db", StopWatchDB.class);
        tabList = (TabList) findComponentById(ResourceTable.Id_tab_list);
        stopwatchTab = tabList.new Tab(getContext());
        stopwatchTab.setText("秒表");
        tabList.addTab(stopwatchTab);
        mapTab = tabList.new Tab(getContext());
        mapTab.setText("地图");
        tabList.addTab(mapTab);
        settingTab = tabList.new Tab(getContext());
        settingTab.setText("设定");
        tabList.addTab(settingTab);
        AbilitySlice slice = this;
        tabList.addTabSelectedListener(new TabList.TabSelectedListener() {
            @Override
            public void onSelected(TabList.Tab tab) {
                HiLog.info(LOG_LABEL, "MainAbilitySlice.onSelected");
                ComponentContainer container = (ComponentContainer) findComponentById(ResourceTable.Id_tab_container);
                if(tab == stopwatchTab) {
                    if(stopWatchState == null) {
                        stopWatchState = new StopWatchState(slice, container);
                    }
                    current_state = stopWatchState;
                }
                else if(tab == mapTab) {
                    if(mapState == null) {
                        mapState = new MapState(slice, container, dbContext);
                    }
                    current_state = mapState;
                }
                else
                {
                    if(settingState == null) {
                        settingState = new SettingState(slice, container);
                    }
                    current_state = settingState;
                }
                HiLog.info(LOG_LABEL, "MainAbilitySlice.current_state.onStart(intent);");
                current_state.onStart(intent);
                current_state.onForeground(intent);
                current_tab = tab;
            }

            @Override
            public void onUnselected(TabList.Tab tab) {
                current_state.onBackground();
                current_state = null;
            }

            @Override
            public void onReselected(TabList.Tab tab) {
                onSelected(tab);
            }
        });
        //最开始选选择tab1
        HiLog.info(LOG_LABEL, "MainAbilitySlice.tabList.selectTab(stopwatchTab);");
        tabList.selectTab(stopwatchTab);
        startLocalService(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        connectService();
    }
    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.onForeground");
        super.onForeground(intent);
        tabList.selectTab(current_tab);
        //current_state.onForeground(intent);
    }

    @Override
    public void onBackground(){
        HiLog.info(LOG_LABEL, "MainAbilitySlice.onBackground");
        super.onBackground();
        current_state.onBackground();
    }

    @Override
    public void onStop() {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.onStop!");
        super.onStop();
        current_state.onStop();
        try {
            if(!stopWatchProxy.isRunning()){
                stopService();
            }
            else{
                //disConnectService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static final int EVENT_ABILITY_CONNECT_DONE = 0x1000001;

    private static final int EVENT_ABILITY_DISCONNECT_DONE = 0x1000002;

    private static final String LOCAL_BUNDLE = "xwg.harmony.stopwatch";

    private static final String FOREGROUND_SERVICE = "StopWatchService";

    StopWatchAgentProxy getStopWatchService(){
        return stopWatchProxy;
    }

    private EventHandler eventHandler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            switch (event.eventId) {
                case EVENT_ABILITY_CONNECT_DONE:
                    showTips(MainAbilitySlice.this, "Service connect succeeded23");
                    HiLog.info(LOG_LABEL, "Service connect succeeded");
                    if(tabList != null && tabList.getSelectedTab() == stopwatchTab) {
                        ((StopWatchState) current_state).setStopWatchService(stopWatchProxy);
                        try {
                            HiLog.info(LOG_LABEL, "stopWatchProxy.isRunning() == %{public}b", stopWatchProxy.isRunning());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case EVENT_ABILITY_DISCONNECT_DONE:
                    //showTips(MainAbilitySlice.this, "Service disconnect succeeded");
                    HiLog.info(LOG_LABEL, "Service disconnect succeeded");
                    break;
                default:
                    break;
            }
        }
    };

    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int resultCode) {
            HiLog.info(LOG_LABEL, "%{public}s", "onAbilityConnectDone resultCode : " + resultCode);
            stopWatchProxy = new StopWatchAgentProxy(iRemoteObject);
            eventHandler.sendEvent(EVENT_ABILITY_CONNECT_DONE);
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
            HiLog.info(LOG_LABEL, "%{public}s", "onAbilityDisconnectDone resultCode : " + resultCode);
            eventHandler.sendEvent(EVENT_ABILITY_DISCONNECT_DONE);
            stopWatchProxy = null;
        }
    };

    private void startLocalService(String bundleName, String serviceName) {
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        startAbility(intent);
    }

    private void connectService() {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.connectService!");
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        connectAbility(intent, connection);
    }

    private void disConnectService() {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.disConnectService!");
        disconnectAbility(connection);
    }

    private void stopService() {
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        stopAbility(intent);
    }

    private Intent getLocalServiceIntent(String bundleName, String serviceName) {
        Operation operation = new Intent.OperationBuilder().withDeviceId("")
                .withBundleName(bundleName)
                .withAbilityName(serviceName)
                .build();
        Intent intent = new Intent();
        intent.setOperation(operation);
        return intent;
    }

    private void showTips(Context context, String msg) {
        //new ToastDialog(context).setText(msg).show();
    }
}
