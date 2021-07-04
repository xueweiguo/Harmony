package xwg.harmony.stopwatch;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.data.orm.OrmContext;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.TouchEvent;

public class TileMap extends Component implements Component.DrawTask, Component.TouchEventListener{
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00205, "TileMap");
    TileMapData mapData = new TileMapData();

    Tile.MapSource mapSource = Tile.MapSource.GAODE_VECTOR;
    double longitude = 121.547792;
    double latitude = 38.875539;
    int zoom = 15;

    boolean loading = false;

    public TileMap(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.addDrawTask(this);
        this.setTouchEventListener(this);
    }

    public void setDbContext(OrmContext context){
        mapData.setDbContext(context);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        //HiLog.info(LABEL, "TileMap.onDraw Start!");
        int tileCol = Tile.getTileX(longitude, zoom);
        int tileRow = Tile.getTileY(latitude, zoom);
        boolean need_load = false;
        for(int col = tileCol - 1; col <= tileCol + 1; col++){
            for(int row = tileRow - 1; row <= tileRow + 1; row++){
                Tile tile = mapData.getData(zoom, col, row);
                if(tile != null) {
                    Size imageSize = tile.getPixelMap().getImageInfo().size;
                    Size offset = tile.calculateOffset(longitude, latitude);
                    /*
                    HiLog.info(LABEL,"onDraw, tile!=null: zoom=%{public}d,row=%{public}d,col=%{public}d, " +
                                    "offset.width=%{public}d, offset.height=%{public}d",
                                    zoom, row, col, offset.width, offset.height);
                     */
                    canvas.drawPixelMapHolder(tile,
                            getWidth() / 2 + offset.width,
                            getHeight() / 2 + offset.height,
                            new Paint());
                }
                else{
                    //HiLog.info(LABEL,"onDraw,need:zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                    need_load = true;
                }

            }
        }
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
        if(need_load){
            //HiLog.info(LABEL,"onDraw,loadMapTile();");
            loadMapTile(false);
        }
        //HiLog.info(LABEL, "TileMap.onDraw End!");
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        return false;
    }

    public void setMapSource(Tile.MapSource src){
        mapSource = src;
        mapData.clear();
        invalidate();
    }

    public void setLocation(double long_deg, double lat_deg){
        //longitude = GCJ2WGSUtils.GCJLon(lat_deg, long_deg);
        //latitude = GCJ2WGSUtils.GCJLat(lat_deg, long_deg);
        double ret[] = GpsUtil.toGCJ02Point(lat_deg, long_deg);
        latitude = ret[0];
        longitude = ret[1];
        invalidate();
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
        if(loading) return;
        loading = true;
        getContext().getGlobalTaskDispatcher(TaskPriority.HIGH).asyncDispatch(new Runnable() {
            @Override
            public void run() {
                int tileCol = Tile.getTileX(longitude, zoom);
                int tileRow = Tile.getTileY(latitude, zoom);
                boolean need_update = false;
                for(int col = tileCol - 1; col <= tileCol + 1; col++) {
                    for (int row = tileRow - 1; row <= tileRow + 1; row++) {
                        Tile tile = mapData.getData(zoom, col, row);
                        if (tile == null) {
                            //HiLog.info(LABEL,"loadMapTile: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                            tile = Tile.createTile(mapSource, col, row, zoom);
                            if(tile != null) {
                                //HiLog.info(LABEL,"createTile Succefully!: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                                mapData.setData(zoom, col, row, tile);
                                need_update = true;
                            }
                        }
                    }
                }
                if(need_update || invalidate)
                {
                    getContext().getUITaskDispatcher().asyncDispatch(new Runnable() {
                        @Override
                        public void run() {
                            //HiLog.info(LABEL, "TileMap.loadMapTile.run.TileMap.this.invalidate!");
                            TileMap.this.invalidate();
                            loading = false;
                        }
                    });
                }
            }

        });
    }
}
