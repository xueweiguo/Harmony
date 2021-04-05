package gameboard;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentParent;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class Gameboard extends Component implements Component.DrawTask {
    // HiLogLabel
    private static final HiLogLabel Label = new HiLogLabel(HiLog.LOG_APP, 0x00101, "Gameboard");
    private TouchEventListener mTouchEventListener = (component, touchEvent) -> this.onTouchEvent(component, touchEvent);

    public Gameboard(Context context, AttrSet attrSet) {
        super(context, attrSet);
        HiLog.warn(Label, attrSet.toString());
        addDrawTask(this);
        setTouchEventListener(mTouchEventListener);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawRect(getBoundRect(), paint);
        getScreenBoundRect().toString();
    }

    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN: {
                MmiPoint point = touchEvent.getPointerPosition(touchEvent.getIndex());
                HiLog.warn(Label, "point=%{public}f,%{public}f", point.getX(), point.getY());
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

    private RectFloat getBoundRect(){
        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height);
        float x_padding = (width - size) / 2;
        float y_padding = (height - size) / 2;
        return new RectFloat(x_padding, y_padding, width - x_padding, height - y_padding);
    }

    private Rect getScreenBoundRect(){
        HiLog.warn(Label, "----------getScreenBoundRect----------------");
        Rect bound = new Rect(getLeft(), getTop(), getRight(), getBottom());
        HiLog.warn(Label, "Class:%{public}s Rect:%{public}s",
                getClass().toString(),
                bound.toString());
        Component parent = (Component)getComponentParent();
        while(parent != null){
            bound.translate(parent.getLeft(), parent.getTop());
            HiLog.warn(Label, "Class:%{public}s Rect:%{public}s",
                    parent.getClass().toString(), bound.toString());
            parent = (Component)parent.getComponentParent();
        }
        return bound;
    }
}
