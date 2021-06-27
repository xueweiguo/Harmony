package xwg.harmony.stopwatch;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;
import java.util.Map;

public class TileMapData {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00207, "TileMapData");
    Map<Long, Tile> mapData = new HashMap<Long, Tile>();

    void setData(int zoom, int tile_x, int tile_y, Tile tile){
        //HiLog.info(LABEL, "TileMapData.setData!");
        mapData.put(getKey(zoom, tile_x, tile_y), tile);
    }

    Tile getData(int zoom, int tile_x, int tile_y){
        //HiLog.info(LABEL, "TileMapData.getData!");
        Tile tile = mapData.get(getKey(zoom, tile_x, tile_y));
        return tile;
    }

    void clear(){
        mapData.clear();
    }

    private Long getKey(int zoom, int tile_x, int tile_y){
        return new Long((zoom  << 50) + (tile_x << 20) + tile_y);
    }
}
