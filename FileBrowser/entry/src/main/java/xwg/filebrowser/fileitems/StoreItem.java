package xwg.filebrowser.fileitems;

import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.ListContainer;
import ohos.app.Context;
import xwg.filebrowser.ResourceTable;

import java.io.IOException;
import java.nio.file.FileStore;

public class StoreItem extends BrowserItem{
    FileStore store;
    public StoreItem(Context context, FileStore s) {
        super(context, s.name());
        store = s;
    }

    @Override
    public void buildView(ComponentContainer container) {
        container.removeAllComponents();
        ComponentContainer child_container = (ComponentContainer)LayoutScatter.getInstance(context).parse(
                ResourceTable.Layout_info_list,
                null,
                false);
        ListContainer list_container = (ListContainer)child_container.findComponentById(
                ResourceTable.Id_info_list_container);
        InfoItemProvider provider = new InfoItemProvider(context);
        try {
            provider.addItem(new SimpleInfoItem("Name", store.name()));
            provider.addItem(new SimpleInfoItem("ReadOnly", String.valueOf(store.isReadOnly())));
            provider.addItem(new SimpleInfoItem("TotalSpace", String.valueOf(store.getTotalSpace()/1024) + " KB"));
            provider.addItem(new SimpleInfoItem("UsableSpace", String.valueOf(store.getUsableSpace()/1024) + " KB"));
            provider.addItem(new SimpleInfoItem("Type", store.type()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        list_container.setItemProvider(provider);
        container.addComponent(child_container);
    }
}
