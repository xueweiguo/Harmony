package xwg.harmony.stopwatch;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.TouchEvent;

public class TileMap extends Component implements Component.DrawTask, Component.TouchEventListener{
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00205, "TileMap");
    TileMapData mapData = new TileMapData();

    Tile.MapSource mapSource = Tile.MapSource.GAODE_VECTOR;
    double longitude = 121.618622;
    double latitude = 38.91459;
    int zoom = 15;

    public TileMap(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.addDrawTask(this);
        this.setTouchEventListener(this);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        HiLog.info(LABEL, "TileMap.onDraw!");
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
                            getWidth() / 2 - offset.width,
                            getHeight() / 2 - offset.height,
                            new Paint());
                }
                else{
                    //HiLog.info(LABEL,"onDraw,need:zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, row, col);
                    need_load = true;
                }

            }
        }
        if(need_load){
            //HiLog.info(LABEL,"onDraw,loadMapTile();");
            loadMapTile(false);
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        return false;
    }

    public void setMapSource(Tile.MapSource src){
        mapSource = src;
        mapData.clear();
        loadMapTile(true);
    }

    public void setLocation(double long_deg, double lat_deg){
        /*
        longitude = long_deg;
        latitude = lat_deg;
        loadMapTile(true);
        invalidate();
         */
    }

    public void zoomIn(){
        if(zoom < 17) {
            zoom++;
            loadMapTile(true);
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
        getContext().getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(new Runnable() {
            @Override
            public void run() {
                HiLog.info(LABEL, "TileMap.loadMapTile.run!");
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
                        }
                    });
                }
            }
        });
    }
}
