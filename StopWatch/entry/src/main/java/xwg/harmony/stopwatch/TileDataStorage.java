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

    Map<Long, Tile> mapData = new HashMap<Long, Tile>();
    OrmContext db;

    void setDbContext(OrmContext context){
        db = context;
    }

    void setData(Tile.MapSource source, int zoom, int tile_x, int tile_y, Tile tile){
        HiLog.info(LABEL, "TileMapData.setData Start x=%{public}d, y=%{public}d, z=%{public}d!",
                                tile_x, tile_y, zoom);
        int type = typeMap.get(source);
        mapData.put(getKey(type, zoom, tile_x, tile_y), tile);
        TileData td = new TileData();
        td.setType(type);
        td.setZoom(zoom);
        td.setTileX(tile_x);
        td.setTileY(tile_y);
        td.setPixelMap(tile.getPixelMap());
        HiLog.info(LABEL, "TileMapData.setData1!");
        boolean isSuccessed = db.insert(td);
        HiLog.info(LABEL, "TileMapData.setData2!");
        try {
            isSuccessed = db.flush();
        } catch (RdbConstraintException e) {
            HiLog.info(LABEL, "TileMapData.setData Exception!!!");
            db.update(td);
            db.flush();
            HiLog.info(LABEL, "TileMapData.insert->update!!!");
        }
        HiLog.info(LABEL, "TileMapData.setData End!");
    }

    Tile getData(Tile.MapSource source, int zoom, int tile_x, int tile_y){
        HiLog.info(LABEL, "TileMapData.getData!");
        int type = typeMap.get(source);
        Tile tile = mapData.get(getKey(type, zoom, tile_x, tile_y));
        if(tile != null) return tile;
        OrmPredicates query = db.where(TileData.class).equalTo("type", type).and().equalTo("zoom", zoom).and().equalTo("tileX", tile_x).and().equalTo("tileY", tile_y);
        List<TileData> tds = db.query(query);
        if (tds.size() > 0) {
            TileData td = (TileData) tds.get(0);
            tile = new Tile(td.getPixelMap());
            tile.setTileInfo(td.getTileX(), td.getTileY(), td.getZoom());
            mapData.put(getKey(type, zoom, tile_x, tile_y), tile);
            HiLog.info(LABEL, "TileMapData.getData success！");
            return tile;
        } else {
            HiLog.info(LABEL, "TileMapData.getData null！");
            return null;
        }
    }

    void clear(){
        mapData.clear();
    }

    private Long getKey(int type, int zoom, int tile_x, int tile_y){
        return new Long((type << 60) + (zoom  << 50) + (tile_x << 20) + tile_y);
    }
}
