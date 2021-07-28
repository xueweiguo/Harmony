package xwg.stopwatch;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.AttrSet;
import ohos.agp.components.TabList;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class MenuTabList extends TabList {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00213, "MenuTabList");
    TabList.Tab stopwatchTab = null;
    TabList.Tab mapTab = null;
    TabList.Tab settingTab = null;
    TabList.Tab currentTab = null;
    AbilitySlice ownerSlice = null;

    enum TabId{
        STOP_WATCH,
        MAP,
        SETTING
    }

    public MenuTabList(Context context, AttrSet attrSet) {
        super(context, attrSet);
        stopwatchTab = new Tab(getContext());
        stopwatchTab.setText("秒表");
        addTab(stopwatchTab);
        mapTab = new Tab(getContext());
        mapTab.setText("地图");
        addTab(mapTab);
        settingTab = new Tab(getContext());
        settingTab.setText("设定");
        addTab(settingTab);
        setSelectedTabIndicatorColor(Color.CYAN.getValue());
        addTabSelectedListener(new TabSelectedListener() {
            @Override
            public void onSelected(Tab tab) {
                if(tab == stopwatchTab) {
                    //ownerSlice.present(new StopWatchState(), new Intent());
                }
                else if(tab == mapTab){
                    //((AbilitySlice) mContext).present(new MapSlice(), new Intent());
                }
                else{
                    //ownerSlice.present(new SettingSlice(), new Intent());
                }
            }

            @Override
            public void onUnselected(Tab tab) {

            }

            @Override
            public void onReselected(Tab tab) {

            }
        });
    }
    public void setOwnerSlice(AbilitySlice slice){
        HiLog.info(LABEL, slice.getClass().getTypeName());
        String typeName = slice.getClass().getTypeName();
        if(typeName.compareTo("xwg.stopwatch.slice.StopWatchSlice")==0)
            currentTab = stopwatchTab;
        else if(typeName.contentEquals("xwg.stopwatch.slice.SettingSlice"))
            currentTab = settingTab;
        else
            currentTab = null;
        if (currentTab != null) {
            selectTab(currentTab);
        }
        ownerSlice = slice;
    }
}
