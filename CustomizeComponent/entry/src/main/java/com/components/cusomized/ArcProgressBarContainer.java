package com.components.cusomized;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class ArcProgressBarContainer extends ComponentContainer implements Component.DrawTask {
    // HiLogLabel
    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0x00101, "RoundProgressBarContainer");
    private TouchEventListener mTouchEventListener = (component, touchEvent) -> this.onTouchEvent(component, touchEvent);

    private int active_bar = 1;

    public ArcProgressBarContainer(Context context) {
        super(context);
        addDrawTask(this);
        setTouchEventListener(mTouchEventListener);
    }

    public ArcProgressBarContainer(Context context, AttrSet attrSet) {
        super(context, attrSet);
        HiLog.warn(Label, attrSet.toString());
        addDrawTask(this);
        setTouchEventListener(mTouchEventListener);
    }

    public ArcProgressBarContainer(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        addDrawTask(this);
        setTouchEventListener(mTouchEventListener);
    }

    public ArcProgressBar getActiveProgress()
    {
        if(active_bar >= 0 && active_bar < getChildCount()){
            return (ArcProgressBar)getComponentAt(active_bar);
        }
        else{
            return null;
        }
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(getBoundRect(), paint);

        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeCap(Paint.StrokeCap.SQUARE_CAP);
        for(int i = 0; i < getChildCount(); i++){
            Component child = getComponentAt(i);
            ((ArcProgressBar) child).onDraw(canvas, paint, getProgressRect(i), barWidth(), i == active_bar);
        }
        paint.setColor(Color.LTGRAY);
    }

    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN: {
                MmiPoint point = touchEvent.getPointerPosition(touchEvent.getIndex());
                active_bar = hitTest(getComponentPoint(point));
                invalidate();
                HiLog.warn(Label, "point=%{public}f,%{public}f,hit=%{public}d",
                        point.getX(), point.getY(), active_bar);
                return true;
            }
            case TouchEvent.PRIMARY_POINT_UP:
                HiLog.debug(Label, "TouchEvent.PRIMARY_POINT_UP");
                break;
            case TouchEvent.POINT_MOVE: {
                HiLog.debug(Label, "TouchEvent.POINT_MOVE");
                MmiPoint point = touchEvent.getPointerPosition(touchEvent.getIndex());
                break;
            }

        }
        return false;
    }

    private MmiPoint getComponentPoint(MmiPoint point){
        return new MmiPoint(point.getX() - getLeft(), point.getY() - getTop());
    }

    private int hitTest(MmiPoint point){
        int hit = -1;
        for(int i = 0; i < getChildCount(); ++i){
            RectFloat barRect = getProgressRect(i);
            Point center = barRect.getCenter();
            float radius = barRect.getWidth() / 2;
            float distance = (float) Math.sqrt((point.getX() - center.getPointX()) * (point.getX() - center.getPointX())
                    + (point.getY() - center.getPointY()) * (point.getY() - center.getPointY()));
            HiLog.warn(Label, "distance=%{public}f", distance);
            if(distance <= radius){
                hit = i;
            }
            else{
                break;
            }
        }
        return hit;
    }

    private RectFloat getBoundRect(){
        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height);
        float x_padding = (width - size) / 2;
        float y_padding = (height - size) / 2;
        return new RectFloat(x_padding, y_padding, width - x_padding, height - y_padding);
    }

    private float barWidth()
    {
        RectFloat bound = getBoundRect();
        if(getChildCount() > 0) {
            return bound.getWidth() / 2 * 0.7f / getChildCount();
        }
        else{
            return 0;
        }
    }

    private RectFloat getProgressRect(int round_index)
    {
        RectFloat arcRect = getBoundRect();
        arcRect.shrink(barWidth() * round_index, barWidth() * round_index);
        return arcRect;
    }
}

