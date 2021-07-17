package xwg.harmony.stopwatch;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragInfo;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.orm.OrmContext;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Location;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;
import ohos.rpc.RemoteException;

import static java.lang.Math.abs;

public class TileMap extends Component implements Component.DrawTask{
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00205, "TileMap");
    TileDataStorage mapData = new TileDataStorage();
    Tile.MapSource mapSource = Tile.MapSource.GAODE_VECTOR;
    int zoom = 15;

    Location location = null;
    double[] trail_data = null;
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
            //HiLog.info(LABEL, "TileMap.onDraw 0!");
            int tileCol = Tile.getTileX(location.getLongitude(), zoom);
            int tileRow = Tile.getTileY(location.getLatitude(), zoom);
            boolean need_load = false;
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
                                new Paint());
                    } else {
                        //HiLog.info(LABEL,"onDraw,need:zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                        need_load = true;
                    }
                    //HiLog.info(LABEL, "TileMap.onDraw col=%{public}d, row=%{public}d! End", col, row);
                }
            }
            //HiLog.info(LABEL, "TileMap.onDraw 1!");
            if(need_load){
                loadMapTile(false);
            }
            //HiLog.info(LABEL, "TileMap.onDraw 2!");
            if((trail_data != null) && (trail_data.length >= 2)) {
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(10);
                paint.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
                int from_x = 0;
                int from_y = 0;
                int data_count = trail_data.length / 2;
                for (int index = 0; index < data_count; index++){
                    Location current = new Location(trail_data[index * 2], trail_data[index * 2 + 1]);
                    Size offset = Tile.calculateOffset(512, zoom,
                            location, current);
                    if(index == 0){
                        from_x = getWidth() / 2 + offset.width;
                        from_y = getHeight() / 2 + offset.height;
                    }
                    if(index == (data_count - 1)){
                        try {
                            //如果最新位置超出显示位置
                            if(stopWatchService != null && stopWatchService.isRunning()){
                                if((abs(offset.width) > (getWidth() / 2))
                                    || (abs(offset.height) > (getHeight() / 2))){
                                    location = current;
                                    invalidate();
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        int to_x = getWidth() / 2 + offset.width;
                        int to_y = getHeight() / 2 + offset.height;
                        canvas.drawLine(from_x, from_y, to_x, to_y, paint);
                        from_x = to_x;
                        from_y = to_y;
                    }
                }
            }
            //HiLog.info(LABEL, "TileMap.onDraw 3!");
        }
        //HiLog.info(LABEL, "TileMap.onDraw 4!");
        Paint linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(2);
        final int line_size = 20;
        canvas.drawLine(getWidth() / 2 - line_size, getHeight()/2,
                        getWidth() / 2 + line_size, getHeight()/2,
                        linePaint);
        canvas.drawLine(getWidth() / 2, getHeight()/2 - line_size,
                        getWidth() / 2, getHeight() / 2 + line_size,
                        linePaint);
        //HiLog.info(LABEL, "TileMap.onDraw End!");
    }

    Component.DraggedListener dragListener = new Component.DraggedListener(){

        @Override
        public void onDragDown(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragDown!");
        }

        @Override
        public void onDragStart(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragStart!");
            dragStart = dragInfo.startPoint;
            dragStartLocation = location;
        }

        @Override
        public void onDragUpdate(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragUpdate!");
            Point offset = Tile.calculateOffset(512, zoom, location,
                    dragInfo.updatePoint.getPointX() - dragStart.getPointX(),
                    dragInfo.updatePoint.getPointY() - dragStart.getPointY());
            location = new Location(dragStartLocation.getLatitude() - offset.getPointY(),
                    dragStartLocation.getLongitude() - offset.getPointX());
            invalidate();
        }

        @Override
        public void onDragEnd(Component component, DragInfo dragInfo) {
            HiLog.info(LABEL, "TileMap.onDragEnd!");
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
        if(trail_data == null) {
            double ret[] = GpsUtil.toGCJ02Point(loc.getLatitude(), loc.getLongitude());
            location = new Location(ret[0], ret[1]);
            invalidate();
        }
        HiLog.info(LABEL, "TileMap.setWgs84Location End!");
    }

    public void setWgs84TrailData(double[] data){
        HiLog.info(LABEL, "TileMap.setWgs84TrailData Start!");
        if(data != null) {
            int data_count = data.length / 2;
            if(data_count > 0) {
                for(int index = 0; index < data_count; index++) {
                    double ret[] = GpsUtil.toGCJ02Point(data[index * 2], data[index * 2 + 1]);
                    data[index * 2] = ret[0];
                    //data[index * 2] = ret[0] + index * 0.0001;
                    data[(index * 2) + 1] = ret[1];
                    //data[(index * 2) + 1] = ret[1] + index * 0.0001;
                }
            }

        }
        trail_data = data;
        invalidate();
        HiLog.info(LABEL, "TileMap.setWgs84TrailData End!");
    }

    public void autoCenter(){
        if(trail_data != null) {
            int data_count = trail_data.length / 2;
            if(data_count > 0) {
                double lat_min = 0;
                double lat_max = 0;
                double lon_min = 0;
                double lon_max = 0;
                for(int index = 0; index < data_count; index++) {
                    double lat = trail_data[index * 2];
                    double lon = trail_data[index * 2 + 1];
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
        if(zoom < 17) {
            zoom++;
            invalidate();
         }
        HiLog.info(LABEL, "TileMap.zoomIn,zoom=%{public}d", zoom);
    }

    public void zoomOut(){
        HiLog.info(LABEL, "TileMap.zoomIn!");
        if(zoom > 3) {
            zoom--;
            loadMapTile(true);
         }
        HiLog.info(LABEL, "TileMap.zoomOut,zoom=%{public}d", zoom);
    }

    public void loadMapTile(boolean invalidate){
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
                for(int col = tileCol - 1; col <= tileCol + 1; col++) {
                    for (int row = tileRow - 1; row <= tileRow + 1; row++) {
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 1!");
                        Tile tile = mapData.getData(mapSource, zoom, col, row);
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 1.5!");
                        if (tile == null) {
                            HiLog.info(LABEL,"loadMapTile: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                            tile = Tile.createTile(mapSource, col, row, zoom);
                            HiLog.info(LABEL, "TileMap.loadMapTile.run 2!");
                            if(tile != null) {
                                HiLog.info(LABEL, "TileMap.loadMapTile.run 3!");
                                //HiLog.info(LABEL,"createTile Succefully!: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                                mapData.setData(mapSource, zoom, col, row, tile);
                                HiLog.info(LABEL, "TileMap.loadMapTile.run 4!");
                                need_update = true;
                            }
                        }
                        HiLog.info(LABEL, "TileMap.loadMapTile.run 5!");
                    }
                }
                HiLog.info(LABEL, "TileMap.loadMapTile.run 6!");
                if(need_update || invalidate)
                {
                    getContext().getUITaskDispatcher().asyncDispatch(new Runnable() {
                        @Override
                        public void run() {
                            HiLog.info(LABEL, "TileMap.loadMapTile.run 7!");
                            //HiLog.info(LABEL, "TileMap.loadMapTile.run.TileMap.this.invalidate!");
                            TileMap.this.invalidate();
                            loading = false;
                            HiLog.info(LABEL, "TileMap.loadMapTile.run 8!");
                        }
                    });
                }
                HiLog.info(LABEL, "TileMap.loadMapTile.run End!");
            }
        });
        HiLog.info(LABEL, "TileMap.loadMapTile End!");
    }
}
