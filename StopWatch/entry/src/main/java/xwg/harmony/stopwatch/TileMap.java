package xwg.harmony.stopwatch;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.PixelMapHolder;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.multimodalinput.event.TouchEvent;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import ohos.net.NetStatusCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TileMap extends Component implements Component.DrawTask, Component.TouchEventListener{
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00205, "TileMap");
    PixelMapHolder tile = null;

    // 地图来源
    public enum MapSource {
        GAODE_VECTOR, GAODE_ROAD, GAODE_SATELLITE
    }

    MapSource mapSource = MapSource.GAODE_VECTOR;
    double longtitude = 121.618622;
    double latitude = 38.91459;
    int zoom = 15;

    public TileMap(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.addDrawTask(this);
        tile = null;
        this.setTouchEventListener(this);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        if(tile != null) {
            Size imageSize = tile.getPixelMap().getImageInfo().size;
            canvas.drawPixelMapHolder(tile,
                                (getWidth() - imageSize.width) / 2,
                                (getHeight() - imageSize.height) / 2,
                                    new Paint());
        }
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        return false;
    }

    public void setMapSource(MapSource src){
        mapSource = src;
        loadMapTile();
    }

    public void setLocation(double long_deg, double lat_deg){
        longtitude = long_deg;
        latitude = lat_deg;
        loadMapTile();
    }

    public void zoomIn(){
        if(zoom < 18) {
            zoom++;
            loadMapTile();
        }
    }

    public void zoomOut(){
        if(zoom > 3) {
            zoom--;
            loadMapTile();
        }
    }

    public void loadMapTile(){
        getContext().getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(new Runnable() {
            @Override
            public void run() {
                int tileCol = getTileCol(longtitude, zoom);
                int tileRow = getTileRow(latitude, zoom);
                String urlString = String.format(getMapUrlString(), tileCol, tileRow, zoom);
                PixelMap map = getImagePixmap(urlString);
                tile = new PixelMapHolder(map);
                getContext().getUITaskDispatcher().asyncDispatch(new Runnable() {
                    @Override
                    public void run() {
                        TileMap.this.invalidate();
                    }
                });
            }
        });
    }

    public final String getMapUrlString(){
        // 高德地图 - 矢量
        final String GAODE_V_MAP_URL = "https://webrd02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=8&x=%d&y=%d&z=%d";
        // 高德地图 - 道路
        final String GAODE_R_MAP_URL = "https://webst02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=8&x=%d&y=%d&z=%d";
        // 高德地图 - 卫星
        final String GAODE_S_MAP_URL = "https://webst01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=6&x=%d&y=%d&z=%d";
        switch(mapSource){
            case GAODE_VECTOR:
                return GAODE_V_MAP_URL;
            case GAODE_ROAD:
                return GAODE_R_MAP_URL;
            case GAODE_SATELLITE:
                return GAODE_S_MAP_URL;
            default:
                return null;
        }
    }

    //https://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    int getTileCol(double long_deg, int zoom){
        int total_cols = (int)Math.pow(2, zoom);
        return (int)((long_deg + 180)/360 * total_cols);
    }

    int getTileRow(double lat_deg, int zoom){
        double tan = Math.tan(Math.toRadians(lat_deg));
        double asinh = Math.log(tan + Math.sqrt(tan * tan + 1));
        return (int)((1.0 - asinh / Math.PI) * Math.pow(2, zoom - 1));
    }

    /**
     * 获取网络中的ImagePixmap
     * @param urlString
     * @return
     */
    public static PixelMap getImagePixmap(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(500*1000);
            InputStream is = con.getInputStream();
            ImageSource source = ImageSource.create(is, new ImageSource.SourceOptions());
            ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
            options.desiredSize = new Size(513,513);
            PixelMap pixelMap = source.createPixelmap(options);
            is.close();
            return pixelMap;
        } catch (Exception e) {
            return null;
        }
    }
}
