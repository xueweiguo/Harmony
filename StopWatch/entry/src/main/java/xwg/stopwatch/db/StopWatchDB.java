package xwg.stopwatch.db;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;

//@Database(entities = {TimeRecord.class, TileData.class, Setting.class, TimingInfo.class}, version = 1)
@Database(entities = {TimeRecord.class, TileData.class, Setting.class, TimingInfo.class}, version = 2)
public abstract class StopWatchDB extends OrmDatabase {

}
