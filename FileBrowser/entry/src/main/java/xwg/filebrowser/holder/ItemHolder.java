package xwg.filebrowser.holder;

import xwg.filebrowser.model.Item;

/**
 * item provider interface
 */
public interface ItemHolder {
    /**
     * process Item with an item
     *
     * @param item data binding
     */
    void processItem(Item item);

    /**
     * initItemListener
     */
    void initItemListener();
}
