package xwg.harmony.stopwatch;

import ohos.agp.colors.RgbColor;
import ohos.agp.components.AttrSet;
import ohos.agp.components.ComponentState;
import ohos.agp.components.Switch;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.components.element.StateElement;
import ohos.agp.utils.Color;
import ohos.app.Context;

public class ColorSwitch extends Switch {
    Color textColorOn = Color.BLACK;
    Color textColorOff = Color.BLACK;
    public ColorSwitch(Context context, AttrSet attrSet) {
        super(context, attrSet);
        Initialize(attrSet);
    }

    private void Initialize(AttrSet attrSet) {
        if (attrSet.getAttr("text_color_on").isPresent()) {
            textColorOn = attrSet.getAttr("text_color_on").get().getColorValue();
        }
        if (attrSet.getAttr("text_color_off").isPresent()) {
            textColorOff = attrSet.getAttr("text_color_off").get().getColorValue();
        }
        float switch_radius = 50;
        if (attrSet.getAttr("switch_radius").isPresent()) {
            switch_radius = attrSet.getAttr("switch_radius").get().getFloatValue();
        }

        // 开启状态下滑块的样式
        ShapeElement elementThumbOn = new ShapeElement();
        elementThumbOn.setShape(ShapeElement.OVAL);
        elementThumbOn.setRgbColor(RgbColor.fromArgbInt(0xFF1E90FF));
        elementThumbOn.setCornerRadius(switch_radius);
        // 开启状态下轨迹样式
        ShapeElement elementTrackOn = new ShapeElement();
        elementTrackOn.setShape(ShapeElement.RECTANGLE);
        elementTrackOn.setRgbColor(RgbColor.fromArgbInt(0xFF87CEFA));
        elementTrackOn.setCornerRadius(switch_radius);
        elementTrackOn.setStroke(1, RgbColor.fromRgbaInt(0x000000));
        // 关闭状态下滑块的样式
        ShapeElement elementThumbOff = new ShapeElement();
        elementThumbOff.setShape(ShapeElement.OVAL);
        elementThumbOff.setRgbColor(RgbColor.fromArgbInt(0xFFAFAFAF));
        elementThumbOff.setCornerRadius(switch_radius);
        // 关闭状态下轨迹样式
        ShapeElement elementTrackOff = new ShapeElement();
        elementTrackOff.setShape(ShapeElement.RECTANGLE);
        elementTrackOff.setRgbColor(RgbColor.fromArgbInt(0xFF808080));
        elementTrackOff.setCornerRadius(switch_radius);
        elementTrackOff.setStroke(1, RgbColor.fromRgbaInt(0x000000));

        setTrackElement(trackElementInit(elementTrackOn, elementTrackOff));
        setThumbElement(thumbElementInit(elementThumbOn, elementThumbOff));
    }

    private StateElement trackElementInit(ShapeElement on, ShapeElement off){
        StateElement trackElement = new StateElement();
        trackElement.addState(new int[]{ComponentState.COMPONENT_STATE_CHECKED}, on);
        trackElement.addState(new int[]{ComponentState.COMPONENT_STATE_EMPTY}, off);
        return trackElement;
    }
    private StateElement thumbElementInit(ShapeElement on, ShapeElement off) {
        StateElement thumbElement = new StateElement();
        thumbElement.addState(new int[]{ComponentState.COMPONENT_STATE_CHECKED}, on);
        thumbElement.addState(new int[]{ComponentState.COMPONENT_STATE_EMPTY}, off);
        return thumbElement;
    }
}
