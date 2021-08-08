package xwg.stopwatch.db;

import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmObject;
import ohos.data.orm.OrmPredicates;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;
import ohos.data.rdb.RdbConstraintException;
import ohos.global.icu.util.Calendar;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "TimingInfo",
        ignoredColumns = {"LABEL"},
        indices = {@Index(value = {"startTime"}, name = "time_index", unique = true)})
public class TimingInfo extends OrmObject {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00302, "TimingInfo");
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Date startTime;
    private String title;

    public Integer getId() { return id; }
    public void setId(Integer _id) {
        id = _id;
    }
    public Date getStartTime(){ return startTime;}
    public void setStartTime(Date date) {startTime = date;}
    public String getTitle() { return title; }
    public void setTitle(String t) { title = t; }

    static public void createTimingRecord(OrmContext db){
        HiLog.info(LABEL, "TimingInfo.createTimingRecord!");
        TimingInfo ti = new TimingInfo();
        HiLog.info(LABEL, "ti.id=%{public}d!", ti.id);
        ti.startTime = Calendar.getInstance().getTime();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        ti.title = sdf.format(ti.startTime);
        try {
            db.insert(ti);
            db.flush();
        } catch (Exception e) {
            HiLog.info(LABEL, e.getMessage());
        }
    }
}