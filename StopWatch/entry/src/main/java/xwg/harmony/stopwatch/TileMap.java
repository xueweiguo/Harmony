package xwg.harmony.stopwatch;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragInfo;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.orm.OrmContext;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Location;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;
import ohos.rpc.RemoteException;
import ohos.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.abs;

public class TileMap extends Component implements Component.DrawTask{
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00205, "TileMap");
    TileDataStorage mapData = new TileDataStorage();
    Tile.MapSource mapSource = Tile.MapSource.GAODE_VECTOR;
    int zoom = 15;

    Location location = null;
    Location firstLocation = null;
    Location lastLocation = null;
    ArrayList<Size> trail_point = new ArrayList<Size>();

    StopWatchAgentProxy stopWatchService = null;
    boolean loading = false;

    Point dragStart = null;
    Location dragStartLocation;
    public TileMap(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.addDrawTask(this);
        this.setDraggedListener(DRAG_HORIZONTAL_VERTICAL, dragListener);
    }

    public void setDbContext(OrmContext context){
        mapData.setDbContext(context);
    }

    public void setStopWatchService(StopWatchAgentProxy proxy){
        stopWatchService = proxy;
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        //HiLog.info(LABEL, "TileMap.onDraw Start!");
        if(location != null) {
            drawTiles(canvas);
            drawTrailPoints(canvas);
            //HiLog.info(LABEL, "TileMap.onDraw 4!");
            Paint linePaint = new Paint();
            linePaint.setColor(Color.RED);
            linePaint.setStrokeWidth(2);
            final int line_size = 20;
            canvas.drawLine(getWidth() / 2 - line_size, getHeight() / 2,
                    getWidth() / 2 + line_size, getHeight() / 2,
                    linePaint);
            canvas.drawLine(getWidth() / 2, getHeight() / 2 - line_size,
                    getWidth() / 2, getHeight() / 2 + line_size,
                    linePaint);
            //HiLog.info(LABEL, "TileMap.onDraw 5!");
        }
        //HiLog.info(LABEL, "TileMap.onDraw End!");
    }

    private void drawTiles(Canvas canvas){
        //HiLog.info(LABEL, "TileMap.onDraw 0!");
        int tileCol = Tile.getTileX(location.getLongitude(), zoom);
        int tileRow = Tile.getTileY(location.getLatitude(), zoom);
        Map<Pair<Integer,Integer>, Tile> missingTile = new HashMap<Pair<Integer,Integer>, Tile>();
        Paint paint = new Paint();
        for (int col = tileCol - 1; col <= tileCol + 1; col++) {
            for (int row = tileRow - 1; row <= tileRow + 1; row++) {
                //HiLog.info(LABEL, "TileMap.onDraw col=%{public}d, row=%{public}d Start!", col, row);
                Tile tile = mapData.getData(mapSource, zoom, col, row);
                if (tile != null) {
                    Size offset = tile.calculateOffset(location);
                    //HiLog.info(LABEL,"onDraw, tile!=null: zoom=%{public}d,row=%{public}d,col=%{public}d, " +
                    //                "offset.width=%{public}d, offset.height=%{public}d",
                    //                zoom, row, col, offset.width, offset.height);
                    canvas.drawPixelMapHolder(tile,
                            getWidth() / 2 + offset.width,
                            getHeight() / 2 + offset.height,
                            paint);
                } else {
                    //HiLog.info(LABEL,"onDraw,need:zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                    missingTile.put(new Pair<Integer, Integer>(row, col), null);
                }
                //HiLog.info(LABEL, "TileMap.onDraw col=%{public}d, row=%{public}d! End", col, row);
            }
        }
        //HiLog.info(LABEL, "TileMap.onDraw 1!");
        if (missingTile.size() > 0) {
            loadMapTile(missingTile);
        }
    }

    void drawTrailPoints(Canvas canvas){
        //HiLog.info(LABEL, "TileMap.onDraw 2!");
        Rect bound = new Rect(0, 0, getWidth(), getHeight());
        if (trail_point.size() > 0) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(10);
            paint.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
            Size first_offset = Tile.calculateOffset(512, zoom, location, firstLocation);
            int first_x = getWidth() / 2 + first_offset.width;
            int first_y = getHeight() / 2 + first_offset.height;
            int from_x = 0;
            int from_y = 0;
            int to_x = 0;
            int to_y = 0;
            for (int index = 0; index < trail_point.size(); index++) {
                if (index == 0) {
                    from_x = first_x;
                    from_y = first_y;
                } else {
                    Size offset = trail_point.get(index);
                    to_x = first_x + offset.width;
                    to_y = first_y + offset.height;
                    if(bound.isInclude(from_x, from_y) || bound.isInclude(to_x, to_y)) {
                        canvas.drawLine(from_x, from_y, to_x, to_y, paint);
                    }
                    from_x = to_x;
                    from_y = to_y;
                }
            }
            //如果最新位置超出显示位置
            if (lastLocation != null && isOperationTimeout()){
                if(!bound.isInclude(to_x, to_y)) {
                    location = lastLocation;
                    invalidate();
                }
            }
            //HiLog.info(LABEL, "TileMap.onDraw 3!");
        }
    }

    Component.DraggedListener dragListener = new Component.DraggedListener(){

        @Override
        public void onDragDown(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragDown!");
            resetOperationTimer();
        }

        @Override
        public void onDragStart(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragStart Start!");
            dragStart = dragInfo.startPoint;
            dragStartLocation = location;
            resetOperationTimer();
            HiLog.info(LABEL, "TileMap.onDragStart End!");
        }

        @Override
        public void onDragUpdate(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragUpdate Start!");
            Point offset = Tile.calculateOffset(512, zoom, location,
                    dragInfo.updatePoint.getPointX() - dragStart.getPointX(),
                    dragInfo.updatePoint.getPointY() - dragStart.getPointY());
            location = new Location(dragStartLocation.getLatitude() - offset.getPointY(),
                    dragStartLocation.getLongitude() - offset.getPointX());
            resetOperationTimer();
            invalidate();
            HiLog.info(LABEL, "TileMap.onDragUpdate End!");
        }

        @Override
        public void onDragEnd(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragEnd!");
            resetOperationTimer();
        }

        @Override
        public void onDragCancel(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragCancel!");
            Point offset = Tile.calculateOffset(512, zoom, location,
                    dragInfo.xOffset, dragInfo.yOffset);
            location = new Location(location.getLatitude() + offset.getPointY(),
                    location.getLongitude() + offset.getPointY());
            invalidate();
        }

        @Override
        public boolean onDragPreAccept(Component component, int dragDirection) {
            return true;
        }
    };

    public void setMapSource(Tile.MapSource src){
        mapSource = src;
        invalidate();
    }

    public void setWgs84Location(Location loc){
        HiLog.info(LABEL, "TileMap.setWgs84Location Start!");
        double ret[] = GpsUtil.toGCJ02Point(loc.getLatitude(), loc.getLongitude());
        loc = new Location(ret[0], ret[1]);
        try {
            if (stopWatchService != null && stopWatchService.isRunning()) {
                if(trail_point.size() == 0) {
                    loadTrailData();
                }
                else{
                    trail_point.add(Tile.calculateOffset(512, zoom, firstLocation, loc));
                    lastLocation = loc;
                }
                if(location == null){
                    location = lastLocation;
                }
            }
            else{
                location = loc;
            }
            invalidate();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        HiLog.info(LABEL, "TileMap.setWgs84Location End!");
    }

    public void loadTrailData(){
        HiLog.info(LABEL, "TileMap.loadTrailData Start!");
        trail_point.clear();
        firstLocation = null;
        lastLocation = null;
        if(stopWatchService != null) {
            try {
                double[] data = stopWatchService.getTrailData();
                int data_count = data.length / 2;
                if(data_count > 0) {
                    for(int index = 0; index < data_count; index++) {
                        double ret[] = GpsUtil.toGCJ02Point(data[index * 2], data[index * 2 + 1]);
                        Location current = new Location(ret[0], ret[1]);
                        if(index == 0){
                            firstLocation = current;
                            trail_point.add(new Size(0, 0));
                        }
                        else{
                            trail_point.add(Tile.calculateOffset(512, zoom, firstLocation, current));
                        }
                        if(index == data_count - 1){
                            lastLocation = current;
                        }
                    }
                }
                HiLog.info(LABEL, "TileMap.loadTrailData count=%{public}d!", data_count);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        HiLog.info(LABEL, "TileMap.loadTrailData End!");
    }

    public void autoCenter(){
        if(trail_point.size() > 0) {
            double lat_min = 0;
            double lat_max = 0;
            double lon_min = 0;
            double lon_max = 0;
            for(int index = 0; index < trail_point.size(); index++) {
                double lat = trail_point.get(index).height;
                double lon = trail_point.get(index).width;
                if(index == 0){
                    lat_min = lat_max = lat;
                    lon_min = lon_max = lon;
                }
                else{
                    if(lat < lat_min){
                        lat_min = lat;
                    }
                    if(lat > lat_max){
                        lat_max = lat;
                    }
                    if(lon < lon_min){
                        lon_min = lon;
                    }
                    if(lon > lon_max){
                        lon_max = lon;
                    }
                }
            }
            location = new Location((lat_min + lat_max) / 2, (lon_min + lon_max) / 2);
        }
    }

    public void setGcj02Location(Location loc){
        HiLog.info(LABEL, "TileMap.setGcj02Location Start!");
        location = loc;
        invalidate();
        HiLog.info(LABEL, "TileMap.setGcj02Location End!");
    }

    public Location getGcj02Location()
    {
        return location;
    }

    public void zoomIn(){
        HiLog.info(LABEL, "TileMap.zoomIn Start,zoom=%{public}d", zoom);
        if(zoom < 17) {
            zoom++;
            loadTrailData();
            invalidate();
         }
        HiLog.info(LABEL, "TileMap.zoomIn,zoom=%{public}d", zoom);
    }

    public void zoomOut(){
        HiLog.info(LABEL, "TileMap.zoomIn!");
        if(zoom > 3) {
            zoom--;
            loadTrailData();
            invalidate();
         }
        HiLog.info(LABEL, "TileMap.zoomOut,zoom=%{public}d", zoom);
    }

    public void loadMapTile(Map<Pair<Integer,Integer>, Tile> missingTile){
        HiLog.info(LABEL, "TileMap.loadMapTile Start!");
        if((location == null) || loading) {
            HiLog.info(LABEL, "TileMap.loadMapTile About!");
            return;
        }
        loading = true;
        getContext().getGlobalTaskDispatcher(TaskPriority.HIGH).asyncDispatch(new Runnable() {
            @Override
            public void run() {
                HiLog.info(LABEL, "TileMap.loadMapTile.run Start!");
                int tileCol = Tile.getTileX(location.getLongitude(), zoom);
                int tileRow = Tile.getTileY(location.getLatitude(), zoom);
                boolean need_update = false;
                for(Pair<Integer, Integer> pair : missingTile.keySet()){
                    HiLog.info(LABEL,"loadMapTile: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, pair.f, pair.s);
                    Tile tile = Tile.createTile(mapSource, pair.s, pair.f, zoom);
                    HiLog.info(LABEL, "TileMap.loadMapTile.run 2!");
                    if(tile != null) {
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 3!");
                        missingTile.put(pair, tile);
                        //HiLog.info(LABEL,"createTile Succefully!: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 4!");
                        need_update = true;
                    }
                    HiLog.info(LABEL, "TileMap.loadMapTile.run 5!");
                }

                HiLog.info(LABEL, "TileMap.loadMapTile.run 6!");
                getContext().getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 7!");
                        for(Pair<Integer, Integer> pair : missingTile.keySet()){
                            Tile tile = missingTile.get(pair);
                            if(tile != null){
                                mapData.setData(mapSource, zoom, pair.s, pair.f, tile);
                            }
                        }
                        //HiLog.info(LABEL, "TileMap.loadMapTile.run.TileMap.this.invalidate!");
                        TileMap.this.invalidate();
                        loading = false;
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 8!");
                    }
                });
                HiLog.info(LABEL, "TileMap.loadMapTile.run End!");
            }
        });
        HiLog.info(LABEL, "TileMap.loadMapTile End!");
    }

    private long lastOperation = 0;
    void resetOperationTimer(){
        lastOperation = Calendar.getInstance().getTimeInMillis();
    }

    boolean isOperationTimeout(){
        return (Calendar.getInstance().getTimeInMillis() - lastOperation) > 5000;
    }
}
