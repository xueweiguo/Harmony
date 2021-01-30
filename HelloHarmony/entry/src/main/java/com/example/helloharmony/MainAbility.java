package com.example.helloharmony;

import com.example.helloharmony.slice.ComponentAbilitySlice;
import com.example.helloharmony.slice.ListAbilitySlice;
import com.example.helloharmony.slice.MainAbilitySlice;
import com.example.helloharmony.slice.TablistAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.TabList;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        // set the action route
        addActionRoute("action.component", ComponentAbilitySlice.class.getName());
        addActionRoute("action.list", ListAbilitySlice.class.getName());
        addActionRoute("action.tab", TablistAbilitySlice.class.getName());
    }
}
