package xwg.stopwatch.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.LayoutScatter;
import ohos.app.Context;

public abstract class SliceState {
    AbilitySlice owner_slice = null;
    ComponentContainer component_container = null;

    //构造函数
    public SliceState(AbilitySlice slice, ComponentContainer container) {
        owner_slice = slice;
        component_container = container;
    }

    int getDirection(){
        return owner_slice.getResourceManager().getConfiguration().direction;
    }

    public abstract int getLayoutId();

    public void onStart(Intent intent)
    {
        Component state_layout = LayoutScatter.getInstance(owner_slice).parse(getLayoutId(),
                                                                        null,
                                                                        false);
        component_container.addComponent(state_layout);
    }
    public void onStop(){

    }

    public void onForeground(Intent intent)
    {

    }

    public void onBackground(){
        component_container.removeAllComponents();
    }
}
