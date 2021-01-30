package com.example.helloharmony.slice;

import com.example.helloharmony.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.FrameAnimationElement;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.Console;

public class TablistAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "TablistAbilitySlice");
    private Component imageContent;
    private Component videoContent;
    private FrameAnimationElement frameAnimationElement;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_tablist);
        TabList tabList = (TabList) findComponentById(ResourceTable.Id_tab_list);
        tabList.setTabLength(200); // 设置Tab的宽度
        tabList.setTabMargin(26); // 设置两个Tab之间的间距
        TabList.Tab tab1 = tabList.new Tab(getContext());
        tab1.setText("Image");
        tabList.addTab(tab1);
        TabList.Tab tab2 = tabList.new Tab(getContext());
        tab2.setText("Video");
        tabList.addTab(tab2);
        AbilitySlice slice = this;
        tabList.addTabSelectedListener(new TabList.TabSelectedListener() {
            @Override
            public void onSelected(TabList.Tab tab) {
                ComponentContainer container = (ComponentContainer) findComponentById(ResourceTable.Id_tab_container);
                if(tab.getText().equals("Image")) {
                    imageContent = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_iamge_tab, null, false);
                    container.addComponent(imageContent);
                }
                else
                {
                    videoContent = LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_video_tab, null, false);
                    frameAnimationElement = new FrameAnimationElement(slice.getContext(), ResourceTable.Graphic_animation_element);
                    Component videoArea = videoContent.findComponentById(ResourceTable.Id_video_area);
                    videoArea.setBackground(frameAnimationElement);
                    frameAnimationElement.start();
                    container.addComponent(videoContent);
                }
            }

            @Override
            public void onUnselected(TabList.Tab tab) {
                if(tab.getText().equals("Video")) {
                    frameAnimationElement.start();
                }
                ComponentContainer container = (ComponentContainer) findComponentById(ResourceTable.Id_tab_container);
                container.removeAllComponents();
            }

            @Override
            public void onReselected(TabList.Tab tab) {
                ComponentContainer container = (ComponentContainer) findComponentById(ResourceTable.Id_tab_container);
                if(tab.getText().equals("Image")) {
                    container.addComponent(imageContent);
                }
                else
                {
                    frameAnimationElement.start();
                    container.addComponent(videoContent);
                }
            }
        });
        //最开始选选择tab1
        tabList.selectTab(tab1);
        // 打印一条日志
        HiLog.info(label, "TablistAbilitySlice.onStart!");
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 打印一条日志
        HiLog.info(label, "TablistAbilitySlice.onStop!");
    }
}
