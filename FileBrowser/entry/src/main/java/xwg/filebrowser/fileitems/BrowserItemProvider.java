package xwg.filebrowser.fileitems;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.filebrowser.ResourceTable;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BrowserItemProvider extends BaseItemProvider {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00103, "BrowserItemProvider");
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
        HiLog.info(LABEL, "getComponent, i=%{public}d!", i);
        BrowserItem item = list.get(i);
        Component cpt = null;
        if (component == null) {
            cpt = item.createUiComponent();
        } else {
            cpt = component;
        }
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_item_name);
        text.setText(item.getName());
        return cpt;
    }
/*
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        HiLog.info(LABEL, "getComponent, i=%{public}d!", i);
        BrowserItem item = list.get(i);
        Component cpt = null;
        if (component == null) {
            cpt = item.createUiComponent();
        } else {
            if(component.getId() == item.getComponentId()) {
                cpt = component;
            }
            else{
                cpt = item.createUiComponent();
            }
        }
        Text text = (Text) cpt.findComponentById(ResourceTable.Id_item_name);
        text.setText(item.getName());
        return cpt;
    }
*/
    public void setCurrentDir(File dir) {
        list.clear();
        if(dir.getParent() != null){
            list.add(new ParentItem(context, dir.getParentFile(), itemListener));
        }
        File[] files = dir.listFiles();
        if(files != null) {
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
