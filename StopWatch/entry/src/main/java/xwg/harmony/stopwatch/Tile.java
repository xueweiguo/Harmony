package xwg.harmony.stopwatch;

import ohos.agp.render.PixelMapHolder;
import ohos.agp.utils.Point;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Tile extends PixelMapHolder {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00208, "Tile");
    int x = 0;
    int y = 0;
    int z = 0;
    // 地图来源
    public enum MapSource {
        GAODE_VECTOR, GAODE_ROAD, GAODE_SATELLITE
    }

    public Tile(PixelMap pixelMap) {
        super(pixelMap);
    }

    public void setTileInfo(int tile_x, int tile_y, int zoom) {
        x = tile_x;
        y = tile_y;
        z = zoom;
    }

    public static Tile createTile(MapSource src, int tile_x, int tile_y, int zoom){
        String urlString = String.format(getMapUrlString(src), tile_x, tile_y, zoom);
        PixelMap map = Tile.getImagePixmap(urlString);
        if(map != null) {
            Tile tile = new Tile(map);
            tile.setTileInfo(tile_x, tile_y, zoom);
            return tile;
        }
        else {
            //HiLog.info(LABEL,"createTile Fail: zoom=%{public}d,row=%{public}d,col=%{public}d", zoom, tile_y, tile_x);
            return null;
        }
    }

    public Size calculateOffset(double longitude, double latitude){
        //获取位图尺寸
        Size imageSize = getPixelMap().getImageInfo().size;
        //获取当前坐标所处瓦片位置
        int tile_x = getTileX(longitude, z);
        int tile_y = getTileY(latitude, z);
        //计算瓦片经度范围
        double long_from = getTileLongitude(tile_x, z);
        double long_to = getTileLongitude(tile_x + 1, z);
        //计算玩片纬度范围
        double lat_from = getTileLatitude(tile_y, z);
        double lat_to = getTileLatitude(tile_y + 1, z);
        //计算Tile内偏移量
        int offset_x = (int)((longitude - long_from) / (long_to - long_from) * (imageSize.width));
        int offset_y = (int)((latitude - lat_from) / (lat_to - lat_from) * (imageSize.height));

        offset_x -= (x - tile_x) * imageSize.width;
        offset_y -= (y - tile_y) * imageSize.height;
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d,offset_x=%{public}d,offset_y=%{public}d", x, y, offset_x, offset_y);
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d", x, y);
        return new Size(offset_x, offset_y);
    }

    public static String getMapUrlString(MapSource src){
        // 高德地图 - 矢量
        final String GAODE_V_MAP_URL = "https://webrd02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=8&x=%d&y=%d&z=%d";
        // 高德地图 - 道路
        final String GAODE_R_MAP_URL = "https://webst02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=8&x=%d&y=%d&z=%d";
        // 高德地图 - 卫星
        final String GAODE_S_MAP_URL = "https://webst01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=6&x=%d&y=%d&z=%d";
        switch(src){
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
    static int getTileX(double long_deg, int zoom){
        int total_cols = (int)Math.pow(2, zoom);
        return (int)((long_deg + 180)/360 * total_cols);
    }

    static int getTileY(double lat_deg, int zoom){
        double tan = Math.tan(Math.toRadians(lat_deg));
        double asinh = Math.log(tan + Math.sqrt(tan * tan + 1));
        return (int)((1.0 - asinh / Math.PI) * Math.pow(2, zoom - 1));
    }

    static double getTileLongitude(int tile_x, int zoom){
        return tile_x / Math.pow(2, zoom) * 360 - 180;
    }

    static double getTileLatitude(int tile_y, int zoom){
        return Math.toDegrees(Math.atan(Math.sinh(Math.PI * (1 - 2 * tile_y / Math.pow(2, zoom)))));
    }

    /**
     * 获取网络中的ImagePixmap
     * @param urlString
     * @return
     */
    static PixelMap getImagePixmap(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(500*1000);
            InputStream is = con.getInputStream();
            ImageSource source = ImageSource.create(is, new ImageSource.SourceOptions());
            ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
            options.desiredSize = new Size(512,512);
            PixelMap pixelMap = source.createPixelmap(options);
            is.close();
            return pixelMap;
        } catch (Exception e) {
            return null;
        }
    }
}
