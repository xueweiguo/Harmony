package com.components.cusomized;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Arc;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

public class MultiRoundProgressBar extends Component implements Component.DrawTask {
    // HiLogLabel
    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MultiRoundProgressBar");
    private float minAngle = 30;
    private float maxAngle = 360;

    public MultiRoundProgressBar(Context context) {
        super(context);
        addDrawTask(this);
     }

    public MultiRoundProgressBar(Context context, AttrSet attrSet) {
        super(context, attrSet);
        addDrawTask(this);
    }

    public MultiRoundProgressBar(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        addDrawTask(this);
    }

    public MultiRoundProgressBar(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);
        addDrawTask(this);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(getProgressRect(0), paint);

        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeCap(Paint.StrokeCap.SQUARE_CAP);
        drawProgressBar(canvas, paint, 1, Color.BLACK, Color.LTGRAY, 25, 0, 100);
        drawProgressBar(canvas, paint, 2, Color.BLACK, Color.RED, 50, 0, 100);
        drawProgressBar(canvas, paint, 3, Color.BLACK, Color.CYAN, 100, 0, 100);
    }

    private RectFloat getProgressRect(int round_index)
    {
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int x_padding = (width - size) / 2;
        int y_padding = (height - size) / 2;
        int stoke_width = 20;
        RectFloat arcRect = new RectFloat(x_padding, y_padding, width - x_padding, height - y_padding);
        arcRect.shrink(stoke_width * round_index * 2 * 2, stoke_width * round_index * 2 * 2);
        return arcRect;
    }

    private void drawProgressBar(Canvas canvas, Paint paint, int index, Color edge, Color bar, float value, float min, float max)
    {
        float startAngle = minAngle - 90;
        float sweepAngle = (value - min)/(max - min) * (maxAngle - minAngle);
        RectFloat arc_rect = getProgressRect(index);
        paint.setColor(edge);
        paint.setStrokeWidth(50);
        canvas.drawArc(arc_rect, new Arc(startAngle, sweepAngle, false), paint);

        paint.setColor(bar);
        paint.setStrokeWidth(40);
        canvas.drawArc(arc_rect, new Arc(startAngle, sweepAngle, false), paint);
    }

}

