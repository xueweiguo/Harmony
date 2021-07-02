package xwg.harmony.stopwatch.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.TabList;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.harmony.stopwatch.ResourceTable;
import xwg.harmony.stopwatch.StopWatchDB;

public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MainAbilitySlice");
    TabList.Tab stopwatchTab = null;
    TabList.Tab mapTab = null;
    TabList.Tab settingTab = null;
    private SliceState current_state = null;
    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LABEL, "onStart");
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext dbContext = helper.getOrmContext("StopWatch", "StopWatch.db", StopWatchDB.class);
        TabList tabList = (TabList) findComponentById(ResourceTable.Id_tab_list);
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
                ComponentContainer container = (ComponentContainer) findComponentById(ResourceTable.Id_tab_container);
                if(tab == stopwatchTab) {
                    current_state = new StopWatchState(slice, container);
                    current_state.onStart(intent);
                }
                else if(tab == mapTab) {
                    current_state = new MapState(slice, container, dbContext);
                    current_state.onStart(intent);
                }
                else
                {
                    current_state = new SettingState(slice, container);
                    current_state.onStart(intent);
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
        //最开始选选择tab1
        tabList.selectTab(stopwatchTab);
    }
    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        HiLog.info(LABEL, "MainAbilitySlice.onForeground");
        super.onForeground(intent);
        current_state.onBackground();
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MainAbilitySlice.onBackground");
        super.onBackground();
        current_state.onBackground();
    }

    @Override
    public void onStop() {
        HiLog.info(LABEL, "MainAbilitySlice.onStop!");
        super.onStop();
        current_state.onStop();

    }
}
