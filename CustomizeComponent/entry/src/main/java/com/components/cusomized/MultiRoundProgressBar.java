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

import java.util.ArrayList;
import java.util.List;

public class MultiRoundProgressBar extends Component implements Component.DrawTask {
    // HiLogLabel
    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MultiRoundProgressBar");
    private float minAngle = 30;
    private float maxAngle = 360;
    List<RoundProgressBar> barList;
    private int active_bar = 0;

    class RoundProgressBar{
        MultiRoundProgressBar ownerBar;
        private Color edgeColor;
        private Color barColor;
        private float minValue;
        private float maxValue;
        private float progressValue;

        RoundProgressBar(MultiRoundProgressBar owner, Color edge, Color bar, float min, float max){
            ownerBar = owner;
            edgeColor = edge;
            barColor = bar;
            minValue = min;
            maxValue = max;
            progressValue = 0;
        }

        void setValue(float value){
            progressValue = value;
        }

        void onDraw(Canvas canvas, Paint paint, RectFloat rect, float width, boolean active){
            float startAngle = ownerBar.minAngle - 90;
            float sweepAngle = (progressValue - minValue)/(maxValue - minValue) * (ownerBar.maxAngle - ownerBar.minAngle);
            if(active){
                width *= 0.8f;
            }
            else{
                width *= 0.6f;
            }

            paint.setColor(edgeColor);
            paint.setStrokeWidth(width);
            canvas.drawArc(rect, new Arc(startAngle, sweepAngle, false), paint);

            paint.setColor(barColor);
            paint.setStrokeWidth(width * 0.8f);
            canvas.drawArc(rect, new Arc(startAngle, sweepAngle, false), paint);
        }
    }

    public MultiRoundProgressBar(Context context) {
        super(context);
        addDrawTask(this);
        barList = new ArrayList<RoundProgressBar>();
    }

    public MultiRoundProgressBar(Context context, AttrSet attrSet) {
        super(context, attrSet);
        addDrawTask(this);
        barList = new ArrayList<RoundProgressBar>();
    }

    public MultiRoundProgressBar(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        addDrawTask(this);
        barList = new ArrayList<RoundProgressBar>();
    }

    public MultiRoundProgressBar(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);
        addDrawTask(this);
        barList = new ArrayList<RoundProgressBar>();
    }

    public void addBar(Color edge, Color bar, float min, float max){
        barList.add(new RoundProgressBar(this, edge, bar, min, max));
    }

    public void setValue(int index, float value){
        barList.get(index).setValue(value);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(getBoundRect(), paint);

        paint.setStyle(Paint.Style.STROKE_STYLE);
        paint.setStrokeCap(Paint.StrokeCap.SQUARE_CAP);
        for(int i = 0; i < barList.size(); i++){
            barList.get(i).onDraw(canvas, paint, getProgressRect(i), barWidth(), i==active_bar);
        }
    }

    private RectFloat getBoundRect(){
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int x_padding = (width - size) / 2;
        int y_padding = (height - size) / 2;
        return new RectFloat(x_padding, y_padding, width - x_padding, height - y_padding);
    }

    private float barWidth()
    {
        RectFloat bound = getBoundRect();
        if(barList.size() > 0) {
            return bound.getWidth() / 2 * 0.7f / barList.size();
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

