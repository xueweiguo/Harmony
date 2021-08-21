package xwg.filebrowser.holder;

import static xwg.filebrowser.adapter.ListViewItemProvider.fixNullString;
import static xwg.filebrowser.adapter.ListViewItemProvider.fixZeroInteger;

import xwg.filebrowser.ResourceTable;
import xwg.filebrowser.model.Item;

import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;

/**
 * CheckBoxItemHolder
 */
public class CheckBoxItemHolder extends BaseItemHolder {
    private Image logoImageId;

    private Text itemName;

    private Text itemStatusName;

    private Image rightCheckBox;

    /**
     * CheckBoxItemProvider
     *
     * @param itemComponent itemComponent
     */
    public CheckBoxItemHolder(Component itemComponent) {
        logoImageId = (Image) itemComponent.findComponentById(ResourceTable.Id_item_icon);
        itemName = (Text) itemComponent.findComponentById(ResourceTable.Id_item_content_text);
        itemStatusName = (Text) itemComponent.findComponentById(ResourceTable.Id_item_status_text);
        rightCheckBox = (Image) itemComponent.findComponentById(ResourceTable.Id_item_right_checkbox);
    }

    @Override
    public void processItem(Item viewItem) {
        if (viewItem.getLogoImageId() != 0) {
            logoImageId.setImageAndDecodeBounds(fixZeroInteger(viewItem.getLogoImageId(), ResourceTable.Media_ic));
        } else {
            logoImageId.setVisibility(Component.HIDE);
        }
        itemName.setText(fixNullString(viewItem.getItemName(), "null"));
        itemStatusName.setText(fixNullString(viewItem.getItemNameDescription(), "null"));
        itemStatusName.setTextColor(Color.GRAY);
        rightCheckBox.setImageAndDecodeBounds(fixZeroInteger(viewItem.getCheckBoxId(), ResourceTable.Media_Enabled));
    }

    @Override
    public void initItemListener() {
        rightCheckBox.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                boolean status = false;
                if (rightCheckBox.getTag() != null) {
                    status = (boolean) rightCheckBox.getTag();
                }
                if (status) {
                    rightCheckBox.setPixelMap(ResourceTable.Media_Enabled);
                } else {
                    rightCheckBox.setPixelMap(ResourceTable.Media_disenable);
                }
                rightCheckBox.setTag(!status);
            }
        });

        itemName.setClickedListener(component -> {
            new ToastDialog(component.getContext()).setText("itemName click test").show();
        });
    }
}
