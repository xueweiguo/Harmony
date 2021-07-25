package xwg.harmony.stopwatch;

import ohos.location.Location;

import java.util.Date;

public class TrailPoint {
    public long millisecond;
    public int x;
    public int y;
    TrailPoint(long millis, int _x, int _y){
        millisecond = millis;
        x = _x;
        y = _y;
    }
}
