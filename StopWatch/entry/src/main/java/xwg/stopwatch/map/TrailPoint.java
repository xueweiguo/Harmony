package xwg.stopwatch.map;

import ohos.location.Location;
import ohos.media.image.common.Size;

import java.util.Date;

public class TrailPoint {
    public long millisecond;
    public double lat;
    public double lon;
    public double alt;
    public Size offset;

    public TrailPoint(double _lat, double _lon){
        millisecond = 0;
        lat = _lat;
        lon = _lon;
        alt = 0;
        offset = new Size(0, 0);
    }
    TrailPoint(long millis, double _lat, double _lon, double _alt){
        millisecond = millis;
        lat = _lat;
        lon = _lon;
        alt = _alt;
        offset = new Size(0, 0);
    }

    void calculateOffset(int zoom, int image_size, TrailPoint ref){
        offset = Tile.calculateOffset(image_size, zoom, ref, this);
    }

    void toGCJ02Point()
    {
        double ret[] = GpsUtil.toGCJ02Point(lat, lon);
        lat = ret[0];
        lon = ret[1];
    }
}
