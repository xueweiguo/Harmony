package xwg.filebrowser.fileitems;

import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.app.Context;
import xwg.filebrowser.ResourceTable;

import java.io.File;

public class ParentItem extends BrowserItem {
    File dir = null;
    ItemListener listener = null;
    public ParentItem(Context context, File dir, ItemListener listener) {
        super(context, dir.toString());
        this.dir = dir;
        this.listener = listener;
    }
    @Override
    public Component createUiComponent(){
        Component comp =  LayoutScatter.getInstance(context).parse(ResourceTable.Layout_parent_item, null, false);
        Button back = (Button) comp.findComponentById(ResourceTable.Id_extend);
        if(listener != null && dir.listFiles() != null){
            back.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    listener.changeDir(ParentItem.this.dir);
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

