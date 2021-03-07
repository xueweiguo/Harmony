package com.components.main.slice;

import com.components.cusomized.MultiRoundProgressBar;
import com.components.main.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.utils.Color;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        MultiRoundProgressBar bar = (MultiRoundProgressBar) findComponentById(ResourceTable.Id_mrprogressId);
        bar.addBar(Color.BLACK, Color.LTGRAY, 0, 100);
        bar.setValue(0, 50);
        bar.addBar(Color.BLUE, Color.WHITE, 0, 100);
        bar.setValue(1, 60);
        bar.addBar(Color.BLACK, Color.CYAN, 0, 100);
        bar.setValue(2, 70);
        bar.addBar(Color.BLACK, Color.RED, 0, 100);
        bar.setValue(3, 80);
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
