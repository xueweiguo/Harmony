package xwg.stopwatch.db;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;

@Database(entities = {TimeRecord.class, TileData.class, Setting.class}, version = 1)
public abstract class StopWatchDB extends OrmDatabase {

}
