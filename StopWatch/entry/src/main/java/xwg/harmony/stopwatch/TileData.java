package xwg.harmony.stopwatch;

import ohos.data.orm.Blob;
import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.Index;
import ohos.data.orm.annotation.PrimaryKey;
import ohos.media.image.ImagePacker;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.media.image.common.Size;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Entity(tableName = "tile_data",
        //ignoredColumns = {"ignoredColumn1", "ignoredColumn2"},
        indices = {@Index(value = {"type", "zoom", "tileX", "tileY"}, name = "tile_index", unique = true)})
public class TileData extends OrmObject {

    @PrimaryKey(autoGenerate = true)
    private Integer tileId;
    private Integer type;
    private Integer zoom;
    private Integer tileX;
    private Integer tileY;
    private Blob data;

    Integer getTileId(){
        return tileId;
    }

    void setTileId(Integer id){
        tileId = id;
    }

    Integer getType(){
        return type;
    }

    void setType(Integer t){
        type = t;
    }

    Integer getZoom(){
        return zoom;
    }

    void setZoom(int z){
        zoom = z;
    }

    Integer getTileX(){
        return tileX;
    }

    void setTileX(Integer x){
        tileX = x;
    }

    Integer getTileY(){
        return tileY;
    }

    void setTileY(Integer y){
        tileY = y;
    }

    Blob getData(){
        return data;
    }

    void setData(Blob _data){
        data = _data;
    }

    void setPixelMap(PixelMap image){
        ImagePacker imagePacker = ImagePacker.create();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImagePacker.PackingOptions packingOptions = new ImagePacker.PackingOptions();
        packingOptions.quality = 100;
        imagePacker.initializePacking(os, packingOptions);
        imagePacker.addImage(image);
        imagePacker.finalizePacking();
        Blob blob = new Blob(os.toByteArray());
        setData(blob);
    }

    PixelMap getPixelMap(){
        InputStream is = getData().getBinaryStream();
        ImageSource source = ImageSource.create(is, new ImageSource.SourceOptions());
        ImageSource.DecodingOptions options = new ImageSource.DecodingOptions();
        options.desiredSize = new Size(512,512);
        return source.createPixelmap(options);
    }
}
