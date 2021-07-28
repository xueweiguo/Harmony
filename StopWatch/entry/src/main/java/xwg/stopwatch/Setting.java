package xwg.stopwatch;

import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;

import java.sql.Time;

@Entity(tableName = "setting",
        //ignoredColumns = {"ignoredColumn1", "ignoredColumn2"},
        indices = {@Index(value = {"name"}, name = "setting_index", unique = true)})
public class Setting extends OrmObject {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private Time time;
}