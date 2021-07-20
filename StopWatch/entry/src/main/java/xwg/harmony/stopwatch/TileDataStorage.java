package xwg.harmony.stopwatch;

import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.data.rdb.RdbConstraintException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileDataStorage {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00207, "TileMapData");
    Map<Tile.MapSource, Integer> typeMap = new HashMap<Tile.MapSource, Integer>(){{
        put(Tile.MapSource.GAODE_ROAD, 0);
        put(Tile.MapSource.GAODE_SATELLITE,1);
        put(Tile.MapSource.GAODE_VECTOR, 2);
    }};

    Map<String, Tile> mapData = new HashMap<String, Tile>();
    OrmContext db;

    void setDbContext(OrmContext context){
        db = context;
    }

    void setData(Tile.MapSource source, int zoom, int tile_x, int tile_y, Tile tile){
        //HiLog.info(LABEL, "TileDataStorage.setData Start x=%{public}d, y=%{public}d, z=%{public}d!",
        //                        tile_x, tile_y, zoom);
        int type = typeMap.get(source);
        mapData.put(getKey(type, zoom, tile_x, tile_y), tile);
        TileData td = new TileData();
        td.setType(type);
        td.setZoom(zoom);
        td.setTileX(tile_x);
        td.setTileY(tile_y);
        td.setPixelMap(tile.getPixelMap());
        //HiLog.info(LABEL, "TileDataStorage.setData1!");
        boolean isSuccessed = db.insert(td);
        //HiLog.info(LABEL, "TileDataStorage.setData2!");
        try {
            isSuccessed = db.flush();
        } catch (RdbConstraintException e) {
            HiLog.info(LABEL, "TileDataStorage.setData Exception!!!");
            db.update(td);
            db.flush();
            HiLog.info(LABEL, "TileDataStorage.insert->update!!!");
        }
        //HiLog.info(LABEL, "TileDataStorage.setData End!");
    }

    Tile getData(Tile.MapSource source, int zoom, int tile_x, int tile_y){
        //HiLog.info(LABEL, "TileDataStorage.getData!");
        int type = typeMap.get(source);
        //HiLog.info(LABEL, "TileDataStorage.getData,type=%{public}d, zoom=%{public}d,x=%{public}d,y=%{public}d!",
        //        type, zoom, tile_x, tile_y);
        Tile tile = mapData.get(getKey(type, zoom, tile_x, tile_y));
        if(tile != null){
            //HiLog.info(LABEL, "TileDataStorage.getData from mapData!");
            return tile;
        }
        //HiLog.info(LABEL, "TileDataStorage.getData 1!");
        OrmPredicates query = db.where(TileData.class).equalTo("type", type).and().equalTo("zoom", zoom).and().equalTo("tileX", tile_x).and().equalTo("tileY", tile_y);
        List<TileData> tds = db.query(query);
        //HiLog.info(LABEL, "TileDataStorage.getData 2!");
        if (tds.size() > 0) {
            //HiLog.info(LABEL, "TileDataStorage.getData 3!");
            TileData td = (TileData) tds.get(0);
            //HiLog.info(LABEL, "TileDataStorage.getData 4!");
            tile = new Tile(td.getPixelMap());
            tile.setTileInfo(td.getTileX(), td.getTileY(), td.getZoom());
            //HiLog.info(LABEL, "TileDataStorage.getData 5!");
            mapData.put(getKey(type, zoom, tile_x, tile_y), tile);
            //HiLog.info(LABEL, "TileDataStorage.getData success！");
            return tile;
        } else {
            HiLog.info(LABEL, "TileDataStorage.getData null！");
            return null;
        }
    }

    void clear(){
        mapData.clear();
    }

    static String getKey(int type, int zoom, int tile_x, int tile_y){
        String key = String.format("%d.%d.%d.%d", type, zoom, tile_x, tile_y);
        return key;
    }
}
