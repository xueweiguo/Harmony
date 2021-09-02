package xwg.filebrowser.fileitems;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.app.Context;
import xwg.filebrowser.ResourceTable;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BrowserItemProvider extends BaseItemProvider {
    private List<BrowserItem> list = new ArrayList<>();
    private Context context;
    private File currentDir = null;
    BrowserItem.ItemListener itemListener = null;
    private File rootDir = null;
    public BrowserItemProvider(Context c, DirItem.ItemListener listener) {
        context = c;
        itemListener = listener;
        File[] roots = File.listRoots();
        if(roots != null){
            setCurrentDir(roots[0]);
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
        BrowserItem item = list.get(i);
        final Component cpt;
        if (component == null) {
            cpt = item.createUiComponent();
        } else {
            cpt = component;
        }
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_item_name);
        text.setText(item.getName());
        return cpt;
    }

    public void setCurrentDir(File dir) {
        list.clear();
        File[] files = dir.listFiles();
        if(files != null) {
            if(dir.getParent() != null){
                list.add(new ParentItem(context, dir.getParentFile(), itemListener));
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    list.add(new DirItem(context, file, itemListener));
                } else {
                    list.add(new FileItem(context, file));
                }
            }
        }
    }
}
