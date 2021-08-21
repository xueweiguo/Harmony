package xwg.filebrowser;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.DragInfo;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.common.Size;

import java.util.ConcurrentModificationException;

public class LayoutSeparator extends Component {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00501, "LayoutSeparator");
    public LayoutSeparator(Context context, AttrSet attrSet) {
        super(context, attrSet);
    }
}
