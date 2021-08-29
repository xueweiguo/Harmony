package xwg.filebrowser.fileitems;

import ohos.agp.components.ComponentContainer;
import ohos.app.Context;
import ohos.data.usage.Volume;

public class VolumeItem extends BrowserItem{
    public VolumeItem(Context context, Volume v) {
        super(context, v.getDescription());
    }

    @Override
    public void buildView(ComponentContainer container) {

    }
}
