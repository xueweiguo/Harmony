package com.components.cusomized;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Arc;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ArcProgressBar extends Component{
    // HiLogLabel
    private Color edgeColor;
    private Color barColor;
    private float minValue;
    private float maxValue;
    private float startAngle;
    private float maxAngle;
    private float progressValue;

    public ArcProgressBar(Context context) {
        super(context);
    }

    public ArcProgressBar(Context context, AttrSet attrSet) {
        super(context, attrSet);
        Initialize(attrSet);
    }

    public ArcProgressBar(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        Initialize(attrSet);
    }

    public ArcProgressBar(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);
        Initialize(attrSet);
     }

    public void setValue(float value)
    {
        progressValue = value;
        invalidate();
    }

    public float getValue(){ return progressValue; }

    void onDraw(Canvas canvas, Paint paint, RectFloat rect, float width, boolean active){
        float minAngle = startAngle - 90;
        float sweepAngle = (progressValue - minValue)/(maxValue - minValue) * (maxAngle - minAngle);

        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        canvas.drawRect(rect, paint);

        if(active){
            width *= 0.8f;
        }
        else{
            width *= 0.6f;
        }

        rect.shrink(width/ 2, width /2);

        paint.setStrokeWidth(width);
        paint.setColor(edgeColor);
        canvas.drawArc(rect, new Arc(minAngle, maxAngle - minAngle, false), paint);

        paint.setStrokeWidth(width * 0.8f);
        paint.setColor(barColor);
        canvas.drawArc(rect, new Arc(minAngle, sweepAngle, false), paint);
        paint.setColor(Color.WHITE);
        canvas.drawArc(rect, new Arc(minAngle + sweepAngle, maxAngle - sweepAngle - minAngle, false), paint);
    }

    private void Initialize(AttrSet attrSet){
        edgeColor = Color.BLACK;
        if(attrSet.getAttr("progress_element").isPresent()){
            barColor = attrSet.getAttr("progress_element").get().getColorValue();
        }else {
            barColor = Color.GRAY;
        }
        if(attrSet.getAttr("start_angle").isPresent()){
            startAngle = attrSet.getAttr("start_angle").get().getFloatValue();
        }else {
            startAngle = 0;
        }
        if(attrSet.getAttr("max_angle").isPresent()){
            maxAngle = attrSet.getAttr("max_angle").get().getFloatValue();
        }else {
            maxAngle = 0;
        }
        if(attrSet.getAttr("min").isPresent()){
            minValue = attrSet.getAttr("min").get().getFloatValue();
        }else {
            minValue = 0;
        }
        if(attrSet.getAttr("max").isPresent()){
            maxValue = attrSet.getAttr("max").get().getFloatValue();
        }else {
            maxValue = 0;
        }
        if(attrSet.getAttr("progress").isPresent()){
            progressValue = attrSet.getAttr("progress").get().getFloatValue();
        }else {
            progressValue = 0;
        }
    }
}
