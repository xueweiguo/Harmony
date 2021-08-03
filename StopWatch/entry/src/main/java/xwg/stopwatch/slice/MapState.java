package xwg.stopwatch.slice;

import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.RemoteException;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import xwg.stopwatch.ResourceTable;
import xwg.stopwatch.StopWatchAgentProxy;
import xwg.stopwatch.db.Setting;
import xwg.stopwatch.map.Tile;
import xwg.stopwatch.map.TileMap;
import xwg.stopwatch.map.TrailPoint;


public class MapState extends SliceState {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00203, "MapState");

    private TileMap tileMap = null;
    private OrmContext dbContext = null;

    public MapState(AbilitySlice slice, ComponentContainer container, OrmContext context) {
        super(slice, container);
        dbContext = context;
    }

    @Override
    public int getLayoutId() {
        return ResourceTable.Layout_map;
    }
    @Override
    public void onStart(Intent intent) {
        //HiLog.warn(LABEL, "Failed to visit %{private}s, reason:%{public}d.", url, errno);
        HiLog.info(LABEL, "onStart Begin!");
        super.onStart(intent);
        // obtain the map object
        tileMap = (TileMap)owner_slice.findComponentById(ResourceTable.Id_map);
        tileMap.setDbContext(dbContext);
        tileMap.setStopWatchService(((MainAbilitySlice)owner_slice).getStopWatchService());

        // zoom in
        Button btnZoomIn = (Button) owner_slice.findComponentById(ResourceTable.Id_btn_zoomin);
        btnZoomIn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                tileMap.zoomIn();
            }
        });

        // zoom out
        Button btnZoomOut = (Button) owner_slice.findComponentById(ResourceTable.Id_btn_zoomout);
        btnZoomOut.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                tileMap.zoomOut();
            }
        });

        // change base map
        Button btnChangeBaseMapType = (Button) owner_slice.findComponentById(ResourceTable.Id_btn_changebasemaptype);
        btnChangeBaseMapType.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                ListDialog listDialog = new ListDialog(owner_slice, ListDialog.SINGLE);
                listDialog.setItems(new String[]{"高德地图 - 矢量", "高德地图 - 卫星"});
                listDialog.setOnSingleSelectListener(new IDialog.ClickedListener() {
                    @Override
                    public void onClick(IDialog iDialog, int i) {
                        if (i == 0)
                            tileMap.setMapSource(Tile.MapSource.GAODE_VECTOR);
                        if (i == 1)
                            tileMap.setMapSource(Tile.MapSource.GAODE_SATELLITE);
                        listDialog.hide();
                    }
                });
                listDialog.setButton(0, "取消", new IDialog.ClickedListener() {
                    @Override
                    public void onClick(IDialog iDialog, int i) {
                        listDialog.hide();
                    }
                });
                listDialog.setSize(600, 400);
                listDialog.show();
            }
        });

        if(!updateLocation()){
            loadSettings();
        }
        loadLocation();
        HiLog.info(LABEL, "onStart End!");
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "MapState.onForeground Start!");
        super.onForeground(intent);
        loadLocation();
        loadSettings();
        HiLog.info(LABEL, "MapState.onForeground End!");
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MapState.onBackground Start");
        super.onBackground();
        saveLocation();
        saveSettings();
        HiLog.info(LABEL, "MapState.onBackground End");
    }

    public void setLocation(long millis, double lat, double lon, double alt){
        HiLog.info(LABEL, "MapState.setLocation Start");
        if(tileMap != null) {
            tileMap.setWgs84Location(millis, lat, lon, alt);
        }
        HiLog.info(LABEL, "MapState.setLocation End");
    }

    private boolean updateLocation(){
        HiLog.info(LABEL, "MapState.updateLocation Start");
        StopWatchAgentProxy proxy = ((MainAbilitySlice)owner_slice).getStopWatchService();
        if(proxy != null) {
            HiLog.info(LABEL, "MapState.updateLocation1");
            try {
                long[] loc = proxy.getCurrentLocation();
                HiLog.info(LABEL, "MapState.updateLocation2,loc=%{public}d", loc);
                if(loc != null) {
                    HiLog.info(LABEL, "MapState.updateLocation2.1,loc=%{public}d,%{public}d,%{public}d,%{public}",
                            loc[0], loc[1], loc[2], loc[3]);
                    setLocation(loc[0], (double) loc[1] / 100000, (double) loc[2] / 100000, (double) loc[3] / 100);
                }
                return true;
            } catch (RemoteException e) {
                HiLog.info(LABEL, "MapState.updateLocation3");
                e.printStackTrace();
            }
        }
        HiLog.info(LABEL, "MapState.updateLocation End");
        return false;
    }

    private void loadLocation(){
        HiLog.info(LABEL, "MapState.loadLocation Start");
        double latitude = Setting.getDoubleValue(dbContext, getClass().getName(), "latitude", 0);
        double longitude = Setting.getDoubleValue(dbContext, getClass().getName(), "longitude", 0);
        if(latitude != 0 && longitude != 0){
            tileMap.setGcj02Location(new TrailPoint(latitude, longitude));
        }
        HiLog.info(LABEL, "MapState.loadLocation Stop");
    }

    private void saveLocation(){
        HiLog.info(LABEL, "MapState.saveLocation Start");
        TrailPoint location = tileMap.getGcj02Location();
        HiLog.info(LABEL, "MapState.saveLocation 2");
        if(location != null) {
            Setting.setDoubleValue(dbContext, getClass().getName(), "latitude", location.lat);
            Setting.setDoubleValue(dbContext, getClass().getName(), "longitude", location.lon);
        }
        HiLog.info(LABEL, "MapState.saveLocation Stop");
    }

    private void loadSettings(){
        HiLog.info(LABEL, "MapState.loadSettings Start");
        int zoom = Setting.getIntValue(dbContext, getClass().getName(), "zoom", 0);
        if(zoom != 0){
            tileMap.setZoom(zoom);
        }
        HiLog.info(LABEL, "MapState.loadSettings Stop");
    }

    private void saveSettings(){
        HiLog.info(LABEL, "MapState.saveSettings Start");
        Setting.setIntValue(dbContext, getClass().getName(), "zoom", tileMap.getZoom());
        HiLog.info(LABEL, "MapState.saveSettings Stop");
    }

    private void loadLocationFromDbHelper(){
        HiLog.info(LABEL, "MapState.loadLocation Start");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("TileMap");
        double latitude = preferences.getFloat("latitude", 0);
        double longitude = preferences.getFloat("longitude", 0);
        if(latitude != 0 && longitude != 0){
            tileMap.setGcj02Location(new TrailPoint(latitude, longitude));
        }
        HiLog.info(LABEL, "MapState.loadLocation Stop");
    }

    private void saveLocationToDbHelper(){
        HiLog.info(LABEL, "MapState.saveLocation Start");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("TileMap");
        HiLog.info(LABEL, "MapState.saveLocation 1");
        TrailPoint location = tileMap.getGcj02Location();
        HiLog.info(LABEL, "MapState.saveLocation 2");
        if(location != null) {
            preferences.putFloat("latitude", (float) location.lat);
            preferences.putFloat("longitude", (float) location.lon);
        }
        HiLog.info(LABEL, "MapState.saveLocation Stop");
    }
}
