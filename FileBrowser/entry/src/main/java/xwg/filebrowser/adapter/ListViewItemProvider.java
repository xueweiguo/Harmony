package xwg.filebrowser.adapter;

import xwg.filebrowser.ResourceTable;
import xwg.filebrowser.holder.CheckBoxItemHolder;
import xwg.filebrowser.holder.ItemHolder;
import xwg.filebrowser.holder.ItemViewHolder;
import xwg.filebrowser.model.Item;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.LayoutScatter;

/**
 * ListViewItemProvider
 */
public class ListViewItemProvider extends BaseViewItemProvider {
    private AbilitySlice slice;

    private int groupMargin;

    private Boolean hasGroupDivider;

    /**
     * ListViewItemProvider
     *
     * @param abilitySlice abilitySlice
     */
    public ListViewItemProvider(AbilitySlice abilitySlice) {
        this.slice = abilitySlice;
        this.hasGroupDivider = true;
    }

    @Override
    protected ItemHolder createHolder(Component component, Item itemInGroup) {
        ItemHolder itemComponent;
        if (itemInGroup.getItemLayoutId() == 0) { // default layout
            itemComponent = new ItemViewHolder(component);
        } else { // load different layout by id
            itemComponent = new CheckBoxItemHolder(component);
        }
        return itemComponent;
    }

    @Override
    protected void dealWithItem(
            ComponentContainer parentLayout,
            Component itemComponent,
            ComponentContainer componentContainer,
            Item item,
            int i,
            int position) {
        itemComponent.setClickedListener(component -> {
        }); // add listener
        Component divider =
                LayoutScatter.getInstance(slice.getContext())
                        .parse(ResourceTable.Layout_divider, componentContainer, false);
        if (i < item.getGroup().size() - 1) { // add divider line
            parentLayout.addComponent(divider);
        }
        if (i == (item.getGroup().size() - 1) && hasGroupDivider) {
            if (position != (getCount() - 1)) { // non-last item
                parentLayout.addComponent(divider);
            }
        }
    }

    @Override
    protected Component loadItemLayout(ComponentContainer componentContainer, Item itemInGroup) {
        if (itemInGroup.getItemLayoutId() != 0) {
            return LayoutScatter.getInstance(slice.getContext())
                    .parse(itemInGroup.getItemLayoutId(), componentContainer, false);
        }
        return LayoutScatter.getInstance(slice.getContext())
                .parse(ResourceTable.Layout_list_item, componentContainer, false);
    }

    @Override
    protected DirectionalLayout loadParentLayout(ComponentContainer componentContainer) {
        // load parent layout
        DirectionalLayout layout = (DirectionalLayout) LayoutScatter.getInstance(slice.getContext())
                .parse(ResourceTable.Layout_item_background, componentContainer, false);
        layout.setMarginTop(groupMargin);
        return layout;
    }

    public void setGroupMargin(int groupMargin) {
        this.groupMargin = groupMargin;
    }

    public void setHasGroupDivider(Boolean hasGroupDivider) {
        this.hasGroupDivider = hasGroupDivider;
    }
}
