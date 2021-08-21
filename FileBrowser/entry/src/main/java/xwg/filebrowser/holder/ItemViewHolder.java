package xwg.filebrowser.holder;

import static xwg.filebrowser.adapter.ListViewItemProvider.fixNullString;

import xwg.filebrowser.ResourceTable;
import xwg.filebrowser.model.Item;

import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;

/**
 * multi-item-style listview
 */
public class ItemViewHolder extends BaseItemHolder {
    private Text itemName;
    private Text rightName;
    private Image rightCheckBox;

    /**
     * ItemViewHolder
     *
     * @param itemComponent itemComponent
     */
    public ItemViewHolder(Component itemComponent) {
        itemName = (Text) itemComponent.findComponentById(ResourceTable.Id_item_content_text);
        rightName = (Text) itemComponent.findComponentById(ResourceTable.Id_right_text);
        rightCheckBox = (Image) itemComponent.findComponentById(ResourceTable.Id_item_right_tab);
    }

    @Override
    public void processItem(Item viewItem) {
        itemName.setText(fixNullString(viewItem.getItemName(), "null"));
        rightName.setText(fixNullString("right text", "null"));
        rightName.setTextColor(Color.GRAY);
        rightCheckBox.setImageAndDecodeBounds(ResourceTable.Media_right_grey);
    }

    @Override
    public void initItemListener() {
        itemName.setClickedListener(component -> {
            new ToastDialog(component.getContext()).setText("itemName click test").show();
        });
    }
}