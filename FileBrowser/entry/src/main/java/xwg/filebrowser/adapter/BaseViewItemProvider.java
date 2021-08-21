package xwg.filebrowser.adapter;

import xwg.filebrowser.holder.ItemHolder;
import xwg.filebrowser.model.Item;

import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.DirectionalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseViewItemProvider
 */
public abstract class BaseViewItemProvider extends BaseItemProvider {
    private static final int CROSS_NUMBER = 2;
    private List<Item> dataList = new ArrayList<>();
    private int typeCount;

    /**
     * fixNullString
     *
     * @param original original
     * @param replace  replace
     * @return String
     */
    public static String fixNullString(String original, String replace) {
        if (original == null) {
            return replace;
        } else {
            return original;
        }
    }

    /**
     * fixZeroInteger
     *
     * @param original original
     * @param replace  replace
     * @return int
     */
    public static int fixZeroInteger(int original, int replace) {
        if (original == 0) {
            return replace;
        } else {
            return original;
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Item item = dataList.get(position);
        if (item.getGroup().size() == 0) {
            ArrayList<Item> mGroup = new ArrayList<>();
            mGroup.add(item);
            item.setGroup(mGroup);
        }

        if (component != null) {
            DirectionalLayout parentLayout = (DirectionalLayout) component;
            for (int i = 0; i < parentLayout.getChildCount(); i = i + CROSS_NUMBER) { // crossNumber for adding divider
                Item itemInGroup = item.getGroup().get(i / CROSS_NUMBER);
                Component itemComponent = parentLayout.getComponentAt(i);
                ItemHolder itemHolder = (ItemHolder) itemComponent.getTag();
                itemHolder.processItem(itemInGroup);
                itemHolder.initItemListener();
            }
            return component;
        } else {
            DirectionalLayout parentLayout = loadParentLayout(componentContainer);
            for (int i = 0; i < item.getGroup().size(); i++) {
                Item itemInGroup = item.getGroup().get(i);

                Component itemComponent = loadItemLayout(componentContainer, itemInGroup);

                ItemHolder itemHolder = createHolder(itemComponent, itemInGroup);
                itemHolder.processItem(itemInGroup);
                itemHolder.initItemListener();
                itemComponent.setTag(itemHolder);

                parentLayout.addComponent(itemComponent);

                dealWithItem(parentLayout, itemComponent, componentContainer, item, i, position);
            }
            return parentLayout;
        }
    }

    @Override
    public int getComponentTypeCount() {
        return typeCount;
    }

    @Override
    public int getItemComponentType(int position) {
        return dataList.get(position).getItemType();
    }

    /**
     * createHolder
     *
     * @param component   component
     * @param itemInGroup itemInGroup
     * @return ItemHolder
     */
    protected abstract ItemHolder createHolder(Component component, Item itemInGroup);

    /**
     * deal With Item
     *
     * @param parentLayout       parent Layout
     * @param component          component
     * @param componentContainer componentContainer
     * @param item               item
     * @param positionInGroup    position In Item Group
     * @param position           position in List
     */
    protected abstract void dealWithItem(
            ComponentContainer parentLayout,
            Component component,
            ComponentContainer componentContainer,
            Item item,
            int positionInGroup,
            int position);

    /**
     * loadItemLayout
     *
     * @param componentContainer componentContainer
     * @param itemInGroup        itemInGroup
     * @return Component
     */
    protected abstract Component loadItemLayout(ComponentContainer componentContainer, Item itemInGroup);

    /**
     * loadParentLayout
     *
     * @param componentContainer componentContainer
     * @return DirectionalLayout
     */
    protected abstract DirectionalLayout loadParentLayout(ComponentContainer componentContainer);

    /**
     * setDataList
     *
     * @param dataListIn  dataListIn
     * @param typeCountIn typeCountIn
     */
    public void setDataList(List<Item> dataListIn, int typeCountIn) {
        this.dataList = dataListIn;
        this.typeCount = typeCountIn;
    }
}
