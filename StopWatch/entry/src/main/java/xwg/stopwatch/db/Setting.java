package xwg.stopwatch.db;

import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmObject;
import ohos.data.orm.OrmPredicates;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;
import ohos.data.rdb.RdbConstraintException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.stopwatch.map.Tile;

import java.sql.Time;
import java.util.List;

@Entity(tableName = "setting",
        ignoredColumns = {"LABEL"},
        indices = {@Index(value = {"segment", "item"}, name = "setting_index", unique = true)})
public class Setting extends OrmObject {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00301, "Setting");
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String segment;
    private String item;
    private String value;

    public Integer getId(){
        return id;
    }

    public String getSegment(){
        return segment;
    }

    public String getItem(){
        return item;
    }

    public String getValue(){
        return value;
    }

    public void setId(Integer integer) {
        id = integer;
    }

    public void setSegment(String string) { segment = string; }

    public void setItem(String string) { item = string; }

    public void setValue(String string) { value = string;}


    static public void setStringValue(OrmContext db, String seg, String i, String v){
        HiLog.info(LABEL, "Setting.setStringValue,seg=%{public}s, item=%{public}s, v=%{public}s!", seg, i, v);
        OrmPredicates query = db.where(Setting.class).equalTo("segment", seg).and().equalTo("item", i);
        List<Setting> svs = db.query(query);
        String ret = null;
        if (svs.size() > 0) {
            Setting sv = (Setting) svs.get(0);
            sv.value = v;
            db.update(sv);
            HiLog.info(LABEL, "Setting.setStringValue update!");
        }
        else {
            Setting sv = new Setting();
            sv.segment = seg;
            sv.item = i;
            sv.value = v;
            db.insert(sv);
            HiLog.info(LABEL, "Setting.setStringValue insert!");
        }
        db.flush();
    }


    static public String getStringValue(OrmContext db, String seg, String i, String v){
        //HiLog.info(LABEL, "Setting.getStringValue,seg=%{public}s, item=%{public}s, v=%{public}s!", seg, i, v);
        OrmPredicates query = db.where(Setting.class).equalTo("segment", seg).and().equalTo("item", i);
        List<Setting> svs = db.query(query);
        String ret = null;
        if (svs.size() > 0) {
            Setting sv = (Setting) svs.get(0);
            ret = sv.value;
        } else {
            ret = v;
        }
        return ret;
    }

    static public void setIntValue(OrmContext db, String seg, String i, int v){
        setStringValue(db, seg, i, String.format("%d", v));
    }

    static public int getIntValue(OrmContext db, String seg, String i, int v){
        String str_v = String.format("%d", v);
        String str_ret = getStringValue(db, seg, i, str_v);
        int ret = (int)Double.parseDouble(str_ret);
        return ret;
    }

    static public void setDoubleValue(OrmContext db, String seg, String i, double v){
        HiLog.info(LABEL, "Setting.setDoubleValue,seg=%{public}s, item=%{public}s, v=%{public}f!",
                    seg, i, v);
        setStringValue(db, seg, i, String.format("%f", v));
        HiLog.info(LABEL, "Setting.setDoubleValue End!");
    }

    static public double getDoubleValue(OrmContext db, String seg, String i, double v){
        HiLog.info(LABEL, "Setting.getDoubleValue,seg=%{public}s, item=%{public}s, v=%{public}f!",
                seg, i, v);
        String ret = getStringValue(db, seg, i, String.format("%f", v));
        double d_ret = Double.valueOf(ret).doubleValue();
        HiLog.info(LABEL, "Setting.getDoubleValue,ret = %{public}f!", d_ret);
        return d_ret;
    }
}