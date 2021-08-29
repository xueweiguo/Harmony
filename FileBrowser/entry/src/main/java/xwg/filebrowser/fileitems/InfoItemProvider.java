package xwg.filebrowser.fileitems;

import ohos.agp.components.*;
import ohos.app.Context;
import ohos.utils.Pair;
import xwg.filebrowser.ResourceTable;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoItemProvider extends BaseItemProvider {
    private List<InfoItem> infoList = new ArrayList<>();
    private Context context;
    public InfoItemProvider(Context c) {
        this.context = c;
    }

    public void addItem(InfoItem item){
        infoList.add(item);
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int i) {
        if (i >= 0 && i < infoList.size()){
            return infoList.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        InfoItem item = infoList.get(i);
        final Component cpt;
        if (component == null) {
            cpt = LayoutScatter.getInstance(context).parse(item.getLayoutId(), null, false);
        } else {
            cpt = component;
        }
        item.buildComponent(cpt);
        return cpt;
    }
}
