package com.components.main.slice;

import com.components.cusomized.ArcProgressBar;
import com.components.cusomized.ArcProgressBarContainer;
import com.components.main.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        ArcProgressBarContainer container = (ArcProgressBarContainer) findComponentById(ResourceTable.Id_container);
        Button increase = (Button)findComponentById(ResourceTable.Id_increase);
        increase.setClickedListener(new Component.ClickedListener(){
            @Override
            public void onClick(Component component) {
                ArcProgressBar bar = container.getActiveProgress();
                if(bar != null) {
                    bar.setValue(bar.getValue() + 1);
                }
            }
        });
        Button decrease = (Button)findComponentById(ResourceTable.Id_decrease);
        decrease.setClickedListener(new Component.ClickedListener(){
            @Override
            public void onClick(Component component) {
                ArcProgressBar bar = container.getActiveProgress();
                if(bar != null) {
                    bar.setValue(bar.getValue() - 1);
                }
            }
        });
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
