package xwg.stopwatch.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.TabList;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmMigration;
import ohos.data.rdb.RdbStore;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.RequestParam;
import ohos.rpc.RemoteException;
import xwg.stopwatch.*;
import xwg.stopwatch.MainAbility.IRequestPermissionListener;
import xwg.stopwatch.db.StopWatchDB;
import xwg.stopwatch.StopWatchService;
import xwg.stopwatch.StopWatchServiceConnection;

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
    StopWatchServiceConnection stopWatchconnection = null;

    private static final String PERM_LOCATION = "ohos.permission.LOCATION";
    private RequestParam requestParam;

    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LOG_LABEL, "MainAbilitySlice.onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        OrmContext dbContext = getOrmContext();
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
                int tab_index = -1;
                if(tab == stopwatchTab) {
                    if(stopWatchState == null) {
                        stopWatchState = new StopWatchState(slice, container, dbContext);
                    }
                    try {

                        stopWatchProxy.setCurrentTab(tab_index);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    current_state = stopWatchState;
                    tab_index = 0;
                }
                else if(tab == mapTab) {
                    if(mapState == null) {
                        mapState = new MapState(slice, container, dbContext);
                    }
                    current_state = mapState;
                    tab_index = 1;
                }
                else
                {
                    if(settingState == null) {
                        settingState = new SettingState(slice, container);
                    }
                    current_state = settingState;
                    tab_index = 2;
                }
                HiLog.info(LOG_LABEL, "MainAbilitySlice.current_state.onStart(intent);");
                current_state.onStart(intent);
                current_state.onForeground(intent);
                current_tab = tab;
                try {
                    stopWatchProxy.setCurrentTab(tab_index);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
        startLocalService();
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

    StopWatchServiceConnection.StopWatchEventListener listener = new StopWatchServiceConnection.StopWatchEventListener(){
        @Override
        public void onConnectDone(StopWatchAgentProxy proxy) {
            HiLog.info(LOG_LABEL, "StopWatchEventListener.onConnectDone Start!");
            stopWatchProxy = proxy;
            MainAbility ability = (MainAbility)getAbility();
            ability.setRequestPermissionListener(new IRequestPermissionListener() {
                @Override
                public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                    try {
                        stopWatchProxy.registerLocationEvent();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
            ability.requestPermission();

            int tab_idnex = 0;
            try {
                tab_idnex = stopWatchProxy.getCurrentTab();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            switch(tab_idnex){
                case 0:
                    tabList.selectTab(stopwatchTab);
                    break;
                case 1:
                    tabList.selectTab(mapTab);
                    break;
                case 2:
                    tabList.selectTab(settingTab);
                    break;
                default:
                    tabList.selectTab(stopwatchTab);
            }
        }

        @Override
        public void onDisconnectDone() {
            HiLog.info(LOG_LABEL, "StopWatchEventListener.onDisconnectDone Start!");
            stopWatchProxy = null;
        }

        @Override
        public void onLocationReported() {
            HiLog.info(LOG_LABEL, "StopWatchEventListener.onLocationReported Start!");
            if(tabList.getSelectedTab() == mapTab){
                try {
                    long[] loc =stopWatchProxy.getCurrentLocation();
                    mapState.setLocation(loc[0], (double)loc[1]/100000, (double)loc[2]/100000, (double)loc[3]/100);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * handle message from service to ability slice
     */
    private EventHandler stopWatchEventHandler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            switch (event.eventId) {
                case StopWatchService.EVENT_LOCATION_REPORTED:
                    listener.onLocationReported();
            }
        }
    };

    private static final String LOCAL_BUNDLE = "xwg.stopwatch";
    private static final String FOREGROUND_SERVICE = "xwg.stopwatch.StopWatchService";
    //private static final String FOREGROUND_SERVICE = "xwg.stopwatch.TestService";

    public StopWatchAgentProxy getStopWatchService(){
        return stopWatchProxy;
    }

    private void startLocalService() {
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        startAbility(intent);
    }

    private void connectService() {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.connectService Start!");
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        stopWatchconnection = new StopWatchServiceConnection();
        stopWatchconnection.setStopWatchEventListener(listener);
        connectAbility(intent, stopWatchconnection);
        HiLog.info(LOG_LABEL, "MainAbilitySlice.connectService Stop!");
    }

    private void disConnectService() {
        HiLog.info(LOG_LABEL, "MainAbilitySlice.disConnectService!");
        disconnectAbility(stopWatchconnection);
        stopWatchconnection = null;
    }

    private void stopService() {
        Intent intent = getLocalServiceIntent(LOCAL_BUNDLE, FOREGROUND_SERVICE);
        stopAbility(intent);
    }

    private Intent getLocalServiceIntent(String bundleName, String serviceName) {
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName(bundleName)
                .withAbilityName(serviceName)
                .build();
        Intent intent = new Intent();
        intent.setOperation(operation);
        return intent;
    }

    private OrmContext getOrmContext(){
        DatabaseHelper helper = new DatabaseHelper(this);
        return helper.getOrmContext("StopWatch", "StopWatch.db", StopWatchDB.class,
                     new TestOrmMigration12());

    }

    private static class TestOrmMigration12 extends OrmMigration {
        // 此处用于配置数据库版本迁移的开始版本和结束版本，super(startVersion, endVersion)即数据库版本号从1升到2。
        public TestOrmMigration12() {super(1, 2); }
        @Override
        public void onMigrate(RdbStore rdbStore) {
            rdbStore.executeSql("CREATE TABLE IF NOT EXISTS `TimingInfo` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `startTime` INTEGER , `title` TEXT )");
            rdbStore.executeSql("CREATE UNIQUE INDEX `index_time_index` ON `TimingInfo` (`startTime`)");
        }
    }
}
