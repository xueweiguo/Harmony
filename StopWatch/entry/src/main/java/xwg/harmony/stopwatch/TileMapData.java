package xwg.harmony.stopwatch;

import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileMapData {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00207, "TileMapData");
    Map<Long, Tile> mapData = new HashMap<Long, Tile>();
    OrmContext db;

    void setDbContext(OrmContext context){
        db = context;
    }

    void setData(int zoom, int tile_x, int tile_y, Tile tile){
        HiLog.info(LABEL, "TileMapData.setData Start x=%{public}d, y=%{public}d, z=%{public}d!",
                                tile_x, tile_y, zoom);
        //mapData.put(getKey(zoom, tile_x, tile_y), tile);
        TileData td = new TileData();
        td.setType(1);
        td.setZoom(zoom);
        td.setTileX(tile_x);
        td.setTileY(tile_y);
        td.setPixelMap(tile.getPixelMap());
        HiLog.info(LABEL, "TileMapData.setData 1!");
        boolean isSuccessed = db.insert(td);
        HiLog.info(LABEL, "TileMapData.setData 2!");
        isSuccessed = db.flush();
        HiLog.info(LABEL, "TileMapData.setData End!");
    }

    Tile getData(int zoom, int tile_x, int tile_y){
        //HiLog.info(LABEL, "TileMapData.getData!");
        //Tile tile = mapData.get(getKey(zoom, tile_x, tile_y));
        OrmPredicates query = db.where(TileData.class).equalTo("zoom", zoom).and().equalTo("tileX", tile_x).and().equalTo("tileY", tile_y);
        List<TileData> tds = db.query(query);
        if(tds.size() > 0) {
            TileData td = (TileData)tds.get(0);
            Tile tile = new Tile(td.getPixelMap());
            tile.setTileInfo(td.getTileX(), td.getTileY(), td.getZoom());
            //HiLog.info(LABEL, "TileMapData.getData success！");
            return tile;
        }
        else{
            //HiLog.info(LABEL, "TileMapData.getData null！");
            return null;
        }
    }

    void clear(){
        mapData.clear();
    }

    private Long getKey(int zoom, int tile_x, int tile_y){
        return new Long((zoom  << 50) + (tile_x << 20) + tile_y);
    }
}
