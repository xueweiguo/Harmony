package com.example.customize;

import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

public class ScrollViewEx extends ScrollView {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x23456, "ScrollViewEx");

    private TouchEventListener mTouchEventListenerEx = (component, touchEvent) -> this.onTouchEvent(component, touchEvent);
    private TouchEventListener mSuperListener = null;

    private Component.ScrolledListener mScrolledListenerEx = (component, i, i1, i2, i3)->this.onContentScrolled(component, i, i1, i2, i3);
    private ScrolledListener mSuperScrolledListener = null;

    private DraggedListener mDraggedListenerEx = new DraggedListener() {
        @Override
        public void onDragDown(Component component, DragInfo dragInfo) {
            HiLog.info(label, "onDragDown");
        }

        @Override
        public void onDragStart(Component component, DragInfo dragInfo) {
            HiLog.info(label, "onDragStart");
        }

        @Override
        public void onDragUpdate(Component component, DragInfo dragInfo) {
            HiLog.info(label, "onDragUpdate");
        }

        @Override
        public void onDragEnd(Component component, DragInfo dragInfo) {
            HiLog.info(label, "onDragEnd");
        }

        @Override
        public void onDragCancel(Component component, DragInfo dragInfo) {
            HiLog.info(label, "onDragCancel");
        }
    };

    public ScrollViewEx(Context context) {
        super(context);
        HiLog.info(label, "ScrollViewEx(Context context)");

    }
    public ScrollViewEx(Context context, AttrSet attrSet) {
        super(context, attrSet);
        HiLog.info(label, "ScrollViewEx(Context context)");
    }

    public ScrollViewEx(Context context, AttrSet attrSet, String styleName){
        super(context, attrSet, styleName);
        HiLog.info(label, "ScrollViewEx(Context context)");
    }

    public void  changeListener(){
        HiLog.info(label, "changeScrollEventListener()");
        //setTouchEventListener(mTouchEventListenerEx);
        //setScrolledListener(mScrolledListenerEx);
        setDraggedListener(DRAG_VERTICAL, mDraggedListenerEx);
    }

    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        HiLog.info(label, "onTouchEvent(Component component, TouchEvent touchEvent)");
        //return mSuperListener.onTouchEvent(component, touchEvent);
        return false;
    }

    public void onContentScrolled(Component component, int i, int i1, int i2, int i3) {
        HiLog.info(label, "onContentScrolled()");
    }

}
