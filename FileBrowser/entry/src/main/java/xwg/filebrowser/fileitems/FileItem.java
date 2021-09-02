package xwg.filebrowser.fileitems;

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.ListContainer;
import ohos.app.Context;
import xwg.filebrowser.ResourceTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class FileItem extends BrowserItem {
    File file = null;
    public FileItem(Context context, File file) {
        super(context, file.getName());
        this.file = file;
    }
    @Override
    public Component createUiComponent(){
        return LayoutScatter.getInstance(context).parse(ResourceTable.Layout_file_item, null, false);
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
        provider.addItem(new SimpleInfoItem("Path", file.getAbsolutePath()));
        provider.addItem(new SimpleInfoItem("Name", file.getName()));
        provider.addItem(new SimpleInfoItem("Length", String.valueOf(file.length()/1024) + " KB"));
        provider.addItem(new SimpleInfoItem("IsHidden", String.valueOf(file.isHidden())));

        list_container.setItemProvider(provider);
        container.addComponent(child_container);
    }
}