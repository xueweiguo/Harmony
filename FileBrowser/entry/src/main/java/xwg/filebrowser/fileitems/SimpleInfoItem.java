package xwg.filebrowser.fileitems;

import ohos.agp.components.Component;
import ohos.agp.components.Text;
import xwg.filebrowser.ResourceTable;

public class SimpleInfoItem implements InfoItem{
    String keyword;
    String value;

    public SimpleInfoItem(String k, String v) {
        keyword = k;
        value = v;
    }
    @Override
    public int getLayoutId() {
        return ResourceTable.Layout_info_item;
    }

    @Override
    public void buildComponent(Component component) {
        Text k = (Text) component.findComponentById(ResourceTable.Id_info_name);
        k.setText(keyword);
        Text v = (Text) component.findComponentById(ResourceTable.Id_info_value);
        v.setText(value);
    }
}
