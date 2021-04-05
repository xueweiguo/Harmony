package com.components.pageslider.slice;

import com.components.pageslider.ResourceTable;
import com.components.pageslider.TestPagerProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.PageSlider;

import java.util.ArrayList;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_page_slider_owner);
        initPageSlider();
    }

    private void initPageSlider() {
        PageSlider pageSlider = (PageSlider) findComponentById(ResourceTable.Id_page_slider);
        pageSlider.setProvider(new TestPagerProvider(getData(), this));
    }
    private ArrayList<TestPagerProvider.DataItem> getData() {
        ArrayList<TestPagerProvider.DataItem> dataItems = new ArrayList<>();
        dataItems.add(new TestPagerProvider.DataItem("Page A", ResourceTable.Layout_ability_main1));
        dataItems.add(new TestPagerProvider.DataItem("Page B", ResourceTable.Layout_ability_main2));
        dataItems.add(new TestPagerProvider.DataItem("Page C", ResourceTable.Layout_ability_main1));
        dataItems.add(new TestPagerProvider.DataItem("Page D", ResourceTable.Layout_ability_main2));
        return dataItems;
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
