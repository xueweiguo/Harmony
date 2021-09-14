package xwg.filebrowser.fileitems;

import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.filebrowser.ResourceTable;

import java.io.File;

public class DirItem extends BrowserItem {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00105, "DirItem");
    File dir = null;
    DirChangeListener listener = null;
    public DirItem(Context context, File dir, int index, DirChangeListener listener) {
        super(context, dir.getName(), index);
        this.dir = dir;
        this.listener = listener;
    }
    @Override
    public int getComponentId(){
        return ResourceTable.Id_dir_layout;
    }
    @Override
    public Component createUiComponent(){
        HiLog.info(LABEL, "DirItem.createUiComponent of %{public}s", name);
        Component comp =  LayoutScatter.getInstance(context).parse(ResourceTable.Layout_dir_item, null, false);
        Button extend = (Button) comp.findComponentById(ResourceTable.Id_extend);
        if(listener != null){
            extend.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    listener.dirChanged(DirItem.this.dir);
                }
            });
        }
        return comp;
    }

    @Override
    public void buildView(ComponentContainer container) {
        container.removeAllComponents();
        ComponentContainer child_container = (ComponentContainer) LayoutScatter.getInstance(context).parse(
                ResourceTable.Layout_info_list,
                null,
                false);
        ListContainer list_container = (ListContainer)child_container.findComponentById(
                ResourceTable.Id_info_list_container);
        InfoItemProvider provider = new InfoItemProvider(context);
        provider.addItem(new SimpleInfoItem("Path", dir.getAbsolutePath()));
        provider.addItem(new SimpleInfoItem("IsDirectory", String.valueOf(true)));
        provider.addItem(new SimpleInfoItem("IsHidden", String.valueOf(dir.isHidden())));
        File[] sub_items = dir.listFiles();
        if(sub_items != null) {
            provider.addItem(new SimpleInfoItem("ChildCount", String.valueOf(sub_items.length)));
        }else{
            provider.addItem(new SimpleInfoItem("ChildCount", String.valueOf(0)));
        }

        list_container.setItemProvider(provider);
        container.addComponent(child_container);
    }
}
