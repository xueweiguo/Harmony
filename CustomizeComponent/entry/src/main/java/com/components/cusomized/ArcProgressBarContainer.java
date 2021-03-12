package com.components.cusomized;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ArcProgressBarContainer extends ComponentContainer implements Component.DrawTask {
    // HiLogLabel
    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0x00101, "RoundProgressBarContainer");
    private int active_bar = 1;

    public ArcProgressBarContainer(Context context) {
        super(context);
        addDrawTask(this);
    }

    public ArcProgressBarContainer(Context context, AttrSet attrSet) {
        super(context, attrSet);
        HiLog.warn(Label, attrSet.toString());
        addDrawTask(this);
    }

    public ArcProgressBarContainer(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        addDrawTask(this);
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
        arcRect.shrink(barWidth(), barWidth());
        arcRect.shrink(barWidth() * round_index, barWidth() * round_index);
        return arcRect;
    }
}

