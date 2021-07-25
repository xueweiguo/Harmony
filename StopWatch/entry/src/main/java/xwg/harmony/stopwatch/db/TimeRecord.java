package xwg.harmony.stopwatch.db;

import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;

import java.sql.Time;

@Entity(tableName = "time_record",
        //ignoredColumns = {"ignoredColumn1", "ignoredColumn2"},
        indices = {@Index(value = {"recordId", "lapNumber"}, name = "record_index", unique = true)})
public class TimeRecord extends OrmObject {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer recordId;
    private Integer lapNumber;
    private Time time;

    Integer getId(){
        return id;
    }

    void setId(Integer _id){
        id = _id;
    }

    Integer getRecordId(){
        return recordId;
    }

    void setRecordId(Integer rid){
        recordId = rid;
    }

    Integer getLapNumber(){
        return lapNumber;
    }

    void setLapNumber(Integer number){
        lapNumber = number;
    }

    Time getTime(){
        return time;
    }

    void setTime(Time t){
        time = t;
    }
}