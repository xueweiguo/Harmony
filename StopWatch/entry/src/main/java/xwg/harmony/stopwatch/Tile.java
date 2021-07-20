package xwg.harmony.stopwatch;

import ohos.agp.render.PixelMapHolder;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.location.Location;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;
import ohos.agp.utils.Point;

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
        HiLog.info(LABEL,"Tile.createTile Start!");
        String urlString = String.format(getMapUrlString(src), tile_x, tile_y, zoom);
        PixelMap map = Tile.getImagePixmap(urlString);
        HiLog.info(LABEL,"Tile.createTile 1!");
        if(map != null) {
            Tile tile = new Tile(map);
            HiLog.info(LABEL,"Tile.createTile 2!");
            tile.setTileInfo(tile_x, tile_y, zoom);
            HiLog.info(LABEL,"Tile.createTile End1!");
            return tile;
        }
        else {
            HiLog.info(LABEL,"Tile.createTile End2!");
            return null;
        }
    }

    //从longitude和latitude指定的位置计算当前瓦片显示位置的偏移量
    public Size calculateOffset(Location loc){
        //获取位图尺寸
        Size imageSize = getPixelMap().getImageInfo().size;
        //获取当前坐标所处瓦片位置
        int tile_x = getTileX(loc.getLongitude(), z);
        int tile_y = getTileY(loc.getLatitude(), z);
        //计算瓦片经度范围
        double long_from = getTileLongitude(tile_x, z);
        double long_to = getTileLongitude(tile_x + 1, z);
        //计算玩片纬度范围
        double lat_from = getTileLatitude(tile_y, z);
        double lat_to = getTileLatitude(tile_y + 1, z);
        //计算Tile内偏移量
        int offset_x = -(int)((loc.getLongitude() - long_from) / (long_to - long_from) * (imageSize.width));
        int offset_y = -(int)((loc.getLatitude() - lat_from) / (lat_to - lat_from) * (imageSize.height));

        offset_x += (x - tile_x) * imageSize.width;
        offset_y += (y - tile_y) * imageSize.height;
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d,offset_x=%{public}d,offset_y=%{public}d", x, y, offset_x, offset_y);
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d", x, y);
        return new Size(offset_x, offset_y);
    }

    //从像素偏移量计算经纬度偏移量
    static public Point calculateOffset(int image_size, int zoom, Location loc, double offset_x, double offset_y){
        //获取当前坐标所处瓦片位置
        int tile_x = getTileX(loc.getLongitude(), zoom);
        int tile_y = getTileY(loc.getLatitude(), zoom);
        //计算瓦片经度范围
        double long_from = getTileLongitude(tile_x, zoom);
        double long_to = getTileLongitude(tile_x + 1, zoom);
        //计算玩片纬度范围
        double lat_from = getTileLatitude(tile_y, zoom);
        double lat_to = getTileLatitude(tile_y + 1, zoom);
        //计算Tile内偏移量
        float offset_lon = (float)(offset_x / image_size * (long_to - long_from));
        float offset_lat = (float)(offset_y / image_size * (lat_to - lat_from));

        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d,offset_x=%{public}d,offset_y=%{public}d", x, y, offset_x, offset_y);
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d", x, y);
        return new Point(offset_lon, offset_lat);
    }

    //从longitude和latitude指定的位置计算当前瓦片显示位置的偏移量
    static public Size calculateOffset(int image_size, int zoom, Location from, Location to){
        //获取当前坐标所处瓦片位置
        int from_x = getTileX(from.getLongitude(), zoom);
        int from_y = getTileY(from.getLatitude(), zoom);
        //计算瓦片经度范围
        double from_long_left = getTileLongitude(from_x, zoom);
        double from_long_right = getTileLongitude(from_x + 1, zoom);
        //计算瓦片纬度范围
        double from_lat_top = getTileLatitude(from_y, zoom);
        double from_lat_bottom = getTileLatitude(from_y + 1, zoom);
        //计算Tile内偏移量
        double from_offset_x = ((from.getLongitude() - from_long_left) / (from_long_right - from_long_left) * image_size);
        double from_offset_y = ((from.getLatitude() - from_lat_top) / (from_lat_bottom - from_lat_top) * image_size);

        //获取当前坐标所处瓦片位置
        int to_x = getTileX(to.getLongitude(), zoom);
        int to_y = getTileY(to.getLatitude(), zoom);
        //计算瓦片经度范围
        double to_long_left = getTileLongitude(to_x, zoom);
        double to_long_right = getTileLongitude(to_x + 1, zoom);
        //计算瓦片纬度范围
        double to_lat_top = getTileLatitude(to_y, zoom);
        double to_lat_bottom = getTileLatitude(to_y + 1, zoom);
        //计算Tile内偏移量
        double to_offset_x = ((to.getLongitude() - to_long_left) / (to_long_right - to_long_left) * image_size);
        double to_offset_y = ((to.getLatitude() - to_lat_top) / (to_lat_bottom - to_lat_top) * image_size);

        int offset_x = (to_x - from_x) * image_size + (int)(to_offset_x - from_offset_x);
        int offset_y = (to_y - from_y) * image_size + (int)(to_offset_y - from_offset_y);
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d,offset_x=%{public}d,offset_y=%{public}d", x, y, offset_x, offset_y);
        //HiLog.info(LABEL,"calculateOffset: x=%{public}d,y=%{public}d", offset_x, offset_y);
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
            HiLog.info(LABEL,"Tile.getImagePixmap Start!");
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(5*1000);
            HiLog.info(LABEL,"Tile.getImagePixmap 1!");
            InputStream is = con.getInputStream();
            ImageSource source = ImageSource.create(is, new ImageSource.SourceOptions());
            HiLog.info(LABEL,"Tile.getImagePixmap 2!");
            ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
            options.desiredSize = new Size(512,512);
            HiLog.info(LABEL,"Tile.getImagePixmap 3!");
            PixelMap pixelMap = source.createPixelmap(options);
            HiLog.info(LABEL,"Tile.getImagePixmap 4!");
            is.close();
            HiLog.info(LABEL,"Tile.getImagePixmap 5!");
            return pixelMap;
        } catch (Exception e) {
            HiLog.info(LABEL,"Tile.getImagePixmap Exception!");
            return null;
        }
    }
}
