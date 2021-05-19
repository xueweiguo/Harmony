package xwg.harmony.stopwatch;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;

public class AnalogStopWatch extends Component implements Component.DrawTask {
    double second = 0;
    public AnalogStopWatch(Context context) {
        super(context);
        Initialize(null);
    }

    public AnalogStopWatch(Context context, AttrSet attrSet) {
        super(context, attrSet);
        Initialize(attrSet);
    }

    public AnalogStopWatch(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        Initialize(attrSet);
    }

    public AnalogStopWatch(Context context, AttrSet attrSet, int resId) {
        super(context, attrSet, resId);
        Initialize(attrSet);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        drawPanel(canvas);

    }

    public void setSecond(double sec)
    {
        second = sec;
        invalidate();
    }

    private void drawPanel(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        RectFloat bound = getBoundRect();
        float radius = bound.getWidth() / 2;
        float len5sec = radius / 5;
        float len1sec = radius / 10;
        float len02sec = radius / 20;
        Point center = bound.getCenter();

        canvas.drawOval(bound, paint);
        paint.setColor(Color.BLACK);
        for(int i = 0; i < 360; i++){
            float insideRaduis = radius;
            if ((i % 30)==0){
                insideRaduis -= len5sec;
                paint.setStrokeWidth(radius / 60);
            }
            else if((i % 5)==0){
                insideRaduis -= len1sec;
                paint.setStrokeWidth(radius / 80);
            }
            else{
                insideRaduis -= len02sec;
                paint.setStrokeWidth(radius / 120);
            }
            drawRadius(canvas, paint, insideRaduis, radius, i);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(radius / 40);
        paint.setStrokeCap(Paint.StrokeCap.ROUND_CAP);
        drawRadius(canvas, paint, 0, radius * 0.9f, second * 6);
        float oval_radius = radius / 20;
        canvas.drawOval(new RectFloat(center.getPointX() - oval_radius, center.getPointY() - oval_radius,
                                    center.getPointX() + oval_radius, center.getPointY() + oval_radius),
                        paint);

    }

    private void drawRadius(Canvas canvas, Paint paint, float from, float to, double degree){
        double angle = Math.PI * degree / 180;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        Point center = getBoundRect().getCenter();
        canvas.drawLine(new Point(center.getPointX() + (float)(from * sin),
                        center.getPointY() - (float)(from * cos)),
                        new Point(center.getPointX() + (float)(to * sin),
                        center.getPointY() - (float)(to * cos)),
                        paint);
    }

    private RectFloat getBoundRect(){
        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height);
        float x_padding = (width - size) / 2;
        float y_padding = (height - size) / 2;
        return new RectFloat(x_padding, y_padding, width - x_padding, height - y_padding);
    }

    private void Initialize(AttrSet attrSet){
        addDrawTask(this);
    }
}
