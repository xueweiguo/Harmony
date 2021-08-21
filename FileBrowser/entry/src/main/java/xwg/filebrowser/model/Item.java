package xwg.filebrowser.model;

import java.util.ArrayList;
import java.util.List;

/**
 * multi-item-style listview
 */
public class Item {
    private int logoImageId;

    private int checkBoxId;

    private String itemName;

    private String itemNameDescription;

    private boolean isItemHaveDescription;

    private int itemLayoutId;

    private String groupName;

    private boolean isGroup;

    private boolean isNewDot;

    private String statusDescription;

    private List<Item> group = new ArrayList<>();

    private int itemType;

    /**
     * simple Single item
     *
     * @param logoImageId logoImageId
     * @param itemName    itemName
     */
    public Item(int logoImageId, String itemName) {
        this.logoImageId = logoImageId;
        this.itemName = itemName;
    }

    /**
     * simple Single item
     *
     * @param logoImageId logoImageId
     * @param itemName    itemName
     * @param itemType    itemType
     */
    public Item(int logoImageId, String itemName, int itemType) {
        this.logoImageId = logoImageId;
        this.itemName = itemName;
        this.itemType = itemType;
    }

    /**
     * simple Single item with Layout
     *
     * @param logoImageId         logoImageId
     * @param checkBoxId          checkBoxId
     * @param itemName            itemName
     * @param itemNameDescription itemNameDescription
     * @param itemLayoutId        itemLayoutId
     */
    public Item(int logoImageId, int checkBoxId, String itemName, String itemNameDescription, int itemLayoutId) {
        this.logoImageId = logoImageId;
        this.checkBoxId = checkBoxId;
        this.itemName = itemName;
        this.statusDescription = itemNameDescription;
        this.itemLayoutId = itemLayoutId;
    }

    /**
     * simple Single item with Layout
     *
     * @param logoImageId         logoImageId
     * @param checkBoxId          checkBoxId
     * @param itemName            itemName
     * @param itemNameDescription itemNameDescription
     * @param itemLayoutId        itemLayoutId
     * @param itemType            itemType
     */
    public Item(
            int logoImageId,
            int checkBoxId,
            String itemName,
            String itemNameDescription,
            int itemLayoutId,
            int itemType) {
        this.logoImageId = logoImageId;
        this.checkBoxId = checkBoxId;
        this.itemName = itemName;
        this.statusDescription = itemNameDescription;
        this.itemLayoutId = itemLayoutId;
        this.itemType = itemType;
    }

    /**
     * Group item
     *
     * @param groupName groupName
     * @param isGroup   isGroup
     * @param group     group
     */
    public Item(String groupName, boolean isGroup, List<Item> group) {
        this.groupName = groupName;
        this.isGroup = isGroup;
        this.group = group;
    }

    /**
     * Group item
     *
     * @param groupName groupName
     * @param isGroup   isGroup
     * @param group     group
     * @param itemType  itemType
     */
    public Item(String groupName, boolean isGroup, List<Item> group, int itemType) {
        this.groupName = groupName;
        this.isGroup = isGroup;
        this.group = group;
        this.itemType = itemType;
    }

    /**
     * Item
     *
     * @param itemName            itemName
     * @param itemNameDescription itemNameDescription
     * @param itemLayoutId        itemLayoutId
     */
    public Item(String itemName, String itemNameDescription, int itemLayoutId) {
        this.itemName = itemName;
        this.itemNameDescription = itemNameDescription;
        this.itemLayoutId = itemLayoutId;
    }

    public int getLogoImageId() {
        return logoImageId;
    }

    public void setLogoImageId(int logoImageId) {
        this.logoImageId = logoImageId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemNameDescription() {
        return itemNameDescription;
    }

    public void setItemNameDescription(String itemNameDescription) {
        this.itemNameDescription = itemNameDescription;
    }

    public boolean isItemHaveDescription() {
        return isItemHaveDescription;
    }

    public void setItemHaveDescription(boolean isHaveDescription) {
        this.isItemHaveDescription = isHaveDescription;
    }

    public int getItemLayoutId() {
        return itemLayoutId;
    }

    public void setItemLayoutId(int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
    }

    public List<Item> getGroup() {
        return group;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(ArrayList<Item> group) {
        this.group = group;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public boolean isNewDot() {
        return isNewDot;
    }

    public void setNewDot(boolean isNew) {
        this.isNewDot = isNew;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getCheckBoxId() {
        return checkBoxId;
    }

    public void setCheckBoxId(int checkBoxId) {
        this.checkBoxId = checkBoxId;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    /**
     * Type
     */
    public enum Type {
        /**
         * listView item type A
         */
        A_TYPE,
        /**
         * listView item type B
         */
        B_TYPE,
        /**
         * listView item type C
         */
        C_TYPE,
        /**
         * listView item type D
         */
        D_TYPE,
        /**
         * listView item type E
         */
        E_TYPE,
        /**
         * listView item type F
         */
        F_TYPE
    }
}
