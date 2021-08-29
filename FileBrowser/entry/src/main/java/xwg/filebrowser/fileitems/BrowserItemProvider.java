package xwg.filebrowser.fileitems;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.app.Context;
import xwg.filebrowser.ResourceTable;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class BrowserItemProvider extends BaseItemProvider {
    private List<BrowserItem> list = new ArrayList<>();
    private Context context;
    public BrowserItemProvider(Context c) {
        this.context = c;
        FileSystem fs = FileSystems.getDefault();
        Iterable<FileStore> stores = fs.getFileStores();
        for(FileStore store : stores) {
            list.add(new StoreItem(c, store));
        }

    }
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        if (list != null && i >= 0 && i < list.size()){
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        final Component cpt;
        if (component == null) {
            cpt = LayoutScatter.getInstance(context).parse(ResourceTable.Layout_dir_item, null, false);
        } else {
            cpt = component;
        }
        BrowserItem item = list.get(i);
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_item_index);
        text.setText(item.getName());
        return cpt;
    }
}
