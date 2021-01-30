package com.example.helloharmony.slice;

import com.example.helloharmony.ResourceTable;
import com.example.helloharmony.SampleItem;
import com.example.helloharmony.SampleItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.ArrayList;
import java.util.List;

public class ListAbilitySlice extends AbilitySlice {
    // 定义日志标签
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "ListAbilitySlice");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_page_listcontainer);
        initListContainer();
        // 打印一条日志
        HiLog.info(label, "ListAbilitySlice.onStart!");
    }
    private void initListContainer() {
        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_list_container);
        listContainer.enableScrollBar(Component.AXIS_Y, true);
        listContainer.setScrollbarRoundRect(true);
        listContainer.setScrollbarRadius(listContainer.getScrollbarThickness() / 2);
        List<SampleItem> list = getData();
        SampleItemProvider sampleItemProvider = new SampleItemProvider(list,this);
        listContainer.setItemProvider(sampleItemProvider);
    }
    private ArrayList<SampleItem> getData() {
        ArrayList<SampleItem> list = new ArrayList<>();
        for (int i = 0; i <= 80; i++) {
            if((i % 2) == 0) {
                list.add(new SampleItem("Even" + i / 2, ResourceTable.Layout_item_sample_red, ResourceTable.Id_item_index));
            }
            else
            {
                list.add(new SampleItem("Odd"+ i / 2, ResourceTable.Layout_item_sample_green, ResourceTable.Id_item_index));
            }
        }
        return list;
    }

    @Override
    public void onStop() {
        super.onStop();
        // 打印一条日志
        HiLog.info(label, "ListAbilitySlice.onStop!");
    }
}
