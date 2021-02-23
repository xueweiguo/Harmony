package com.xwg.harmonycalulator.slice;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Rect;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ListDialog;
import ohos.app.Context;
import ohos.global.resource.NotExistException;
import ohos.global.resource.WrongTypeException;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;

public class CustomListDialog{
    public interface IListListener{
        public void onSelect(int index, String text);
    }

    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x00203, "CustomListDialog");
    ohos.global.resource.ResourceManager resManager = null;
    IListListener listListener = null;
    private ListDialog dialog = null;


    public CustomListDialog(Context context) {
        dialog = new ListDialog(context);
        resManager = context.getResourceManager();
    }

    public void setListener(IListListener listener){
        listListener = listener;
    }

    public void setTitle(int id){
        dialog.setTitleText(getText(id));
    }

    public void setItems(String[] items){
        dialog.setItems(items);
    }

    void show()
    {
        dialog.setOnSingleSelectListener(new IDialog.ClickedListener() {
            @Override
            public void onClick(IDialog iDialog, int i) {
                String msg = String.format("Item %d is clicked!", i);
                HiLog.warn(label, msg);
                if(listListener != null){
                    ListContainer container = (ListContainer) dialog.getListContainer();
                    listListener.onSelect(i, container.getItemProvider().getItem(i).toString());
                }
                dialog.destroy();
            }
        });
        dialog.show();
        ListContainer container = (ListContainer) dialog.getListContainer();
        container.setBoundarySwitch(true);
    }

    String getText(int id){
        try {
            return resManager.getElement(id).getString();
        } catch (IOException | NotExistException | WrongTypeException e) {
            e.printStackTrace();
            return "";
        }
    }
}
