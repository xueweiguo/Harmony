package xwg.harmony.stopwatch.slice;

import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.Revocable;
import ohos.bundle.IBundleManager;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Location;
import ohos.location.Locator;
import ohos.location.LocatorCallback;
import ohos.location.RequestParam;
import xwg.harmony.stopwatch.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import xwg.harmony.stopwatch.StopWatchDB;
import xwg.harmony.stopwatch.Tile;
import xwg.harmony.stopwatch.TileMap;


public class MapState extends SliceState {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00203, "MapState");

    private MyLocatorCallback locatorCallback = new MyLocatorCallback();
    private Context context;
    private Locator locator;
    private static final String PERM_LOCATION = "ohos.permission.LOCATION";
    private RequestParam requestParam;

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

        register(owner_slice);
        registerLocationEvent();

        //tileMap.loadMapTile(true);
        HiLog.info(LABEL, "onStart End!");
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "MapState.onForeground");
        super.onForeground(intent);
        loadLocation();
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MapState.onBackground");
        super.onBackground();
        saveLocation();
    }

    private void register(Context ability) {
        context = ability;
        requestPermission(PERM_LOCATION);
    }

    private void registerLocationEvent() {
        if (hasPermissionGranted(PERM_LOCATION)) {
            locator = new Locator(context);
            requestParam = new RequestParam(RequestParam.SCENE_NAVIGATION);
            //locator.requestOnce(requestParam, locatorCallback);
            locator.startLocating(requestParam, locatorCallback);
        }
    }

    private void unregisterLocationEvent() {
        if (locator != null) {
            locator.stopLocating(locatorCallback);
        }
    }

    private boolean hasPermissionGranted(String permission) {
        return context.verifySelfPermission(permission) == IBundleManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        if (context.verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
            context.requestPermissionsFromUser(new String[] {permission}, 0);
        }
    }

    private class MyLocatorCallback implements LocatorCallback {
        @Override
        public void onLocationReport(Location location) {
            HiLog.info(LABEL, "onLocationReport Start!");
            TaskDispatcher uiTaskDispatcher = owner_slice.getUITaskDispatcher();
            Revocable revocable = uiTaskDispatcher.asyncDispatch(new Runnable() {
                @Override
                public void run() {
                    tileMap.setWgs84Location(location);
                }
            });
            HiLog.info(LABEL, "onLocationReport End!");
        }

        @Override
        public void onStatusChanged(int type) {
        }

        @Override
        public void onErrorReport(int type) {
        }
    }

    private void loadLocation(){
        HiLog.info(LABEL, "MapState.loadLocation");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("TileMap");
        double latitude = preferences.getFloat("latitude", 0);
        double longitude = preferences.getFloat("longitude", 0);
        if(latitude != 0 && longitude != 0){
            tileMap.setGcj02Location(new Location(latitude, longitude));
        }
    }

    private void saveLocation(){
        HiLog.info(LABEL, "MapState.saveLocation");
        DatabaseHelper databaseHelper = new DatabaseHelper(owner_slice); // context入参类型为ohos.app.Context。
        Preferences preferences = databaseHelper.getPreferences("TileMap");
        Location location = tileMap.getGcj02Location();
        preferences.putFloat("latitude", (float)location.getLatitude());
        preferences.putFloat("longitude", (float)location.getLongitude());
    }
}
