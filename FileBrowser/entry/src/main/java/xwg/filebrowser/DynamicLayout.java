package xwg.filebrowser;

import ohos.agp.colors.RgbPalette;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.common.Size;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicLayout extends ComponentContainer
        implements ComponentContainer.EstimateSizeListener,
        ComponentContainer.ArrangeListener {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00502, "DynamicLayout");

    int layoutLeft = 0;
    int layoutTop = 0;
    int layoutWidth = 0;
    int layoutHeight = 0;
    int total_weight = 0;
    double weight_rate = 0;

    public DynamicLayout(Context context) {
        super(context);
    }

    //如需支持xml创建自定义布局，必须添加该构造方法
    public DynamicLayout(Context context, AttrSet attrSet) {
        super(context, attrSet);
        setEstimateSizeListener(this);
        setArrangeListener(this);
        setDraggedListener(DRAG_HORIZONTAL_VERTICAL, dragListener);
    }

    public DynamicLayout(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
    }

    Component.DraggedListener dragListener = new Component.DraggedListener(){
        Point dragStart = null;
        LayoutSeparator draggedSeparator = null;
        @Override
        public void onDragDown(Component component, DragInfo dragInfo) {
            //HiLog.info(LABEL, "DynamicLayout.onDragDown!");
            draggedSeparator = null;
            dragStart = null;
            for (int idx = 1; idx < getChildCount()-1; idx++) {
                Component childView = DynamicLayout.this.getComponentAt(idx);
                if (childView instanceof LayoutSeparator) {
                    LayoutSeparator separator = (LayoutSeparator) childView;
                    Rect visibleRect = new Rect(separator.getLeft(), separator.getTop(),
                            separator.getRight(), separator.getBottom());
                    if(visibleRect.isInclude(dragInfo.downPoint)){
                        draggedSeparator = separator;
                        ShapeElement bg = new ShapeElement();
                        bg.setRgbColor(RgbPalette.GREEN);
                        bg.setShape(ShapeElement.RECTANGLE);
                        draggedSeparator.setBackground(bg);
                    }

                }
            }
        }

        @Override
        public void onDragStart(Component component, DragInfo dragInfo) {
            //HiLog.info(LABEL, "DynamicLayout.onDragStart Start!");
            if(draggedSeparator != null){
                DynamicLayout.this.onSeparatorDragStart(draggedSeparator);
                dragStart = dragInfo.startPoint;
            }
            //HiLog.info(LABEL, "DynamicLayout.onDragStart End!");
        }

        @Override
        public void onDragUpdate(Component component, DragInfo dragInfo) {
            //HiLog.info(LABEL, "LayoutSeparator.onDragUpdate Start!");
            if(draggedSeparator != null) {
                //HiLog.info(LABEL, "dragStart=%{public}s,dragUpdate=%{public}s!",
                //        dragStart.toString(), dragInfo.updatePoint.toString());
                Size offset = new Size((int) (dragInfo.updatePoint.getPointX() - dragStart.getPointX()),
                        (int) (dragInfo.updatePoint.getPointY() - dragStart.getPointY()));
                DynamicLayout.this.onSeparatorDragUpdate(draggedSeparator, offset);
            }
            //HiLog.info(LABEL, "LayoutSeparator.onDragUpdate End!");
        }

        @Override
        public void onDragEnd(Component component, DragInfo dragInfo) {
            //HiLog.info(LABEL, "DynamicLayout.onDragEnd!");
            if(draggedSeparator != null){
                draggedSeparator.setActive(false);
            }
            draggedSeparator = null;
        }

        @Override
        public void onDragCancel(Component component, DragInfo dragInfo) {
            //HiLog.info(LABEL, "DynamicLayout.onDragCancel!");
            draggedSeparator = null;
            invalidate();
        }

        @Override
        public boolean onDragPreAccept(Component component, int dragDirection) {
            return true;
        }
    };

    int up_height = 0;
    int down_height = 0;
    int up_weight = 0;
    int down_weight = 0;
    int lastConfigWidth = 0;
    int lastConfigHeight = 0;

    public void onSeparatorDragStart(LayoutSeparator separator){
        up_height = -1;
        down_height = -1;
        up_weight = -1;
        down_weight = -1;
        for (int idx = 1; idx < getChildCount()-1; idx++) {
            Component childView = getComponentAt(idx);
            if(childView == separator) {
                Component comp_up = getComponentAt(idx - 1);
                DynamicLayout.LayoutConfig lc_up = (DynamicLayout.LayoutConfig)comp_up.getLayoutConfig();
                Component comp_down = getComponentAt(idx + 1);
                DynamicLayout.LayoutConfig lc_down = (DynamicLayout.LayoutConfig)comp_down.getLayoutConfig();
                if(lc_up.height >= 0 && lc_down.height >= 0) {
                    up_height = comp_up.getHeight();
                    down_height = comp_down.getHeight();
                }
                up_weight = lc_up.weight;
                down_weight = lc_down.weight;
            }
        }
    }

    public void onSeparatorDragUpdate(LayoutSeparator separator, Size offset){
        //HiLog.info(LABEL, "DynamicLayout.onSeparatorDragUpdate!offset.height=%{public}d", offset.height);
        if((up_height > 0 && (up_height + offset.height) >= 0)
            && (down_height >0 && (down_height - offset.height) >= 0)) {
            for (int idx = 1; idx < getChildCount() - 1; idx++) {
                Component childView = getComponentAt(idx);
                if (childView == separator) {
                    adjustHeight(getComponentAt(idx - 1), getComponentAt(idx + 1), offset.height);
                    break;
                }
            }
        }
    }

    void adjustHeight(Component up, Component down, int offset){
        //HiLog.info(LABEL, "DynamicLayout.adjustHeight!");
        DynamicLayout.LayoutConfig lc_up = (DynamicLayout.LayoutConfig)up.getLayoutConfig();
        DynamicLayout.LayoutConfig lc_down = (DynamicLayout.LayoutConfig)down.getLayoutConfig();
        if(lc_up.height > 0 && lc_down.height > 0){
            lc_up.height = up_height + offset;
            lc_down.height = down_height - offset;
        }
        else if(lc_up.height == 0 && lc_down.height==0 && weight_rate > 0){
            offset = (int)(offset / weight_rate);
            lc_up.weight = up_weight + offset;
            lc_down.weight = down_weight - offset;
        }
        else{
            //do nothing.
        }
        arrange();
    }

    @Override
    public boolean onEstimateSize(int widthEstimatedConfig, int heightEstimatedConfig) {
        lastConfigWidth = widthEstimatedConfig;
        lastConfigHeight = heightEstimatedConfig;
        invalidateValues();

        //通知子组件进行测量
        measureChildren(widthEstimatedConfig, heightEstimatedConfig);

        //关联子组件的索引与其布局数据
        for (int idx = 0; idx < getChildCount(); idx++) {
            Component childView = getComponentAt(idx);
            addChild(childView, idx, EstimateSpec.getSize(widthEstimatedConfig));
        }

        //测量自身
        measureSelf(widthEstimatedConfig, heightEstimatedConfig);

        measureChildrenWithWeight(widthEstimatedConfig, heightEstimatedConfig);
        invalidateValues();
        //关联子组件的索引与其布局数据
        for (int idx = 0; idx < getChildCount(); idx++) {
            Component childView = getComponentAt(idx);
            addChild(childView, idx, EstimateSpec.getSize(widthEstimatedConfig));
        }
        //测量自身
        measureSelf(widthEstimatedConfig, heightEstimatedConfig);
        return true;
    }

    private void measureChildren(int widthEstimatedConfig, int heightEstimatedConfig) {
        total_weight = 0;
        for (int idx = 0; idx < getChildCount(); idx++) {
            Component childView = getComponentAt(idx);
            if (childView != null) {
                DynamicLayout.LayoutConfig lc = (DynamicLayout.LayoutConfig)childView.getLayoutConfig();
                int childWidthMeasureSpec;
                int childHeightMeasureSpec;

                if (lc.width == LayoutConfig.MATCH_CONTENT) {
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(lc.width, EstimateSpec.NOT_EXCEED);
                } else if (lc.width == LayoutConfig.MATCH_PARENT) {
                    int parentWidth = EstimateSpec.getSize(widthEstimatedConfig);
                    int childWidth = parentWidth - childView.getMarginLeft() - childView.getMarginRight();
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(childWidth, EstimateSpec.PRECISE);
                } else {
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(lc.width, EstimateSpec.PRECISE);
                }

                if (lc.height == LayoutConfig.MATCH_CONTENT) {
                    childHeightMeasureSpec = EstimateSpec.getSizeWithMode(lc.height, EstimateSpec.NOT_EXCEED);
                } else if (lc.height == LayoutConfig.MATCH_PARENT) {
                    int parentHeight = EstimateSpec.getSize(heightEstimatedConfig);
                    int childHeight = parentHeight - childView.getMarginTop() - childView.getMarginBottom();
                    childHeightMeasureSpec = EstimateSpec.getSizeWithMode(childHeight, EstimateSpec.PRECISE);
                } else {
                    childHeightMeasureSpec = EstimateSpec.getSizeWithMode(lc.height, EstimateSpec.PRECISE);
                    if(lc.height == 0 && lc.weight > 0){
                        total_weight += lc.weight;
                    }
                }
                childView.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    private void measureChildrenWithWeight(int widthEstimatedConfig, int heightEstimatedConfig) {
        int layout_height = getEstimatedHeight();
        int weight_height = layout_height - maxHeight;
        weight_rate = (double)weight_height / total_weight;
        for (int idx = 0; idx < getChildCount(); idx++) {
            Component childView = getComponentAt(idx);
            if (childView != null) {
                DynamicLayout.LayoutConfig lc = (DynamicLayout.LayoutConfig)childView.getLayoutConfig();
                int childWidthMeasureSpec;
                int childHeightMeasureSpec;

                if (lc.width == LayoutConfig.MATCH_CONTENT) {
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(lc.width, EstimateSpec.NOT_EXCEED);
                } else if (lc.width == LayoutConfig.MATCH_PARENT) {
                    int parentWidth = EstimateSpec.getSize(widthEstimatedConfig);
                    int childWidth = parentWidth - childView.getMarginLeft() - childView.getMarginRight();
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(childWidth, EstimateSpec.PRECISE);
                } else {
                    childWidthMeasureSpec = EstimateSpec.getSizeWithMode(lc.width, EstimateSpec.PRECISE);
                }
                if (lc.height == LayoutConfig.MATCH_CONTENT) {
                    childHeightMeasureSpec = EstimateSpec.getSizeWithMode(lc.height, EstimateSpec.NOT_EXCEED);
                } else if (lc.height == LayoutConfig.MATCH_PARENT) {
                    int parentHeight = EstimateSpec.getSize(heightEstimatedConfig);
                    int childHeight = parentHeight - childView.getMarginTop() - childView.getMarginBottom();
                    childHeightMeasureSpec = EstimateSpec.getSizeWithMode(childHeight, EstimateSpec.PRECISE);
                } else {
                    if(lc.height ==0 && lc.weight >0){
                        childHeightMeasureSpec = EstimateSpec.getSizeWithMode((int)(lc.weight * weight_rate), EstimateSpec.PRECISE);
                    }
                    else{
                        childHeightMeasureSpec = EstimateSpec.getSizeWithMode(lc.height, EstimateSpec.PRECISE);
                    }
                }
                childView.estimateSize(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    private void measureSelf(int widthEstimatedConfig, int heightEstimatedConfig) {
        int widthSpce = EstimateSpec.getMode(widthEstimatedConfig);
        int heightSpce = EstimateSpec.getMode(heightEstimatedConfig);
        int widthConfig = 0;
        switch (widthSpce) {
            case EstimateSpec.UNCONSTRAINT:
            case EstimateSpec.PRECISE:
                int width = EstimateSpec.getSize(widthEstimatedConfig);
                widthConfig = EstimateSpec.getSizeWithMode(width, EstimateSpec.PRECISE);
                break;
            case EstimateSpec.NOT_EXCEED:
                widthConfig = EstimateSpec.getSizeWithMode(maxWidth, EstimateSpec.PRECISE);
                break;
            default:
                break;
        }

        int heightConfig = 0;
        switch (heightSpce) {
            case EstimateSpec.UNCONSTRAINT:
            case EstimateSpec.PRECISE:
                int height = EstimateSpec.getSize(heightEstimatedConfig);
                heightConfig = EstimateSpec.getSizeWithMode(height, EstimateSpec.PRECISE);
                break;
            case EstimateSpec.NOT_EXCEED:
                heightConfig = EstimateSpec.getSizeWithMode(maxHeight, EstimateSpec.PRECISE);
                break;
            default:
                break;
        }
        setEstimatedSize(widthConfig, heightConfig);
    }

    private int xx = 0;
    private int yy = 0;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private int lastHeight = 0;

    // 子组件索引与其布局数据的集合
    private final Map<Integer, Layout> axis = new HashMap<>();

    public void arrange(){
        onEstimateSize(lastConfigWidth, lastConfigHeight);
        onArrange(layoutLeft, layoutTop, layoutWidth, layoutHeight);
    }

    @Override
    public boolean onArrange(int left, int top, int width, int height) {
        layoutLeft = left;
        layoutTop = top;
        layoutWidth = width;
        layoutHeight = height;
        // 对各个子组件进行布局
        for (int idx = 0; idx < getChildCount(); idx++) {
            Component childView = getComponentAt(idx);
            Layout layout = axis.get(idx);
            if (layout != null) {
                childView.arrange(left + layout.positionX, top + layout.positionY, layout.width, layout.height);
            }
        }
        return true;
    }

    private static class Layout {
        int positionX = 0;
        int positionY = 0;
        int width = 0;
        int height = 0;
    }

    private void invalidateValues() {
        xx = 0;
        yy = 0;
        maxWidth = 0;
        maxHeight = 0;
        axis.clear();
    }

    private void addChild(Component component, int id, int layoutWidth) {
        Layout layout = new Layout();
        layout.positionX = xx + component.getMarginLeft();
        layout.positionY = yy + component.getMarginTop();
        layout.width = component.getEstimatedWidth();
        layout.height = component.getEstimatedHeight();
        xx = 0;
        axis.put(id, layout);
        yy += Math.max(lastHeight, layout.height + component.getMarginBottom());
        //xx += layout.width + component.getMarginRight();
        maxWidth = Math.max(maxWidth, layout.positionX + layout.width + component.getMarginRight());
        maxHeight = Math.max(maxHeight, layout.positionY + layout.height + component.getMarginBottom());
    }

    public class LayoutConfig extends ComponentContainer.LayoutConfig{
        int weight = 0;
        LayoutConfig(Context context, AttrSet attrSet){
            super(context, attrSet);
            Optional<Attr> attr = attrSet.getAttr("weight");
            if(attr.isPresent()){
                weight = attr.get().getIntegerValue();
            }
        }
    }

    public ComponentContainer.LayoutConfig createLayoutConfig​(Context context, AttrSet attrSet){
        return new LayoutConfig(context, attrSet);
    }
}
