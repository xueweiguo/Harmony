package xwg.filebrowser;

import ohos.agp.colors.RgbPalette;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragInfo;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.common.Size;

import java.util.ConcurrentModificationException;

public class LayoutSeparator extends Component {
    public LayoutSeparator(Context context, AttrSet attrSet) {
        super(context, attrSet);
    }

    public void setActive(boolean active){
        ShapeElement bg = new ShapeElement();
        if(active) {
            bg.setRgbColor(RgbPalette.GREEN);
        }
        else{
            bg.setRgbColor(RgbPalette.GRAY);
        }
        bg.setShape(ShapeElement.RECTANGLE);
        setBackground(bg);
    }
}
