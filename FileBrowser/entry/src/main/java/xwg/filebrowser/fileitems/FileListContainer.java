package xwg.filebrowser.fileitems;

import ohos.agp.colors.RgbPalette;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.File;

public class FileListContainer extends ListContainer {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00102, "FileListContainer");
    public interface SelectedListener{
        public void onItemSelected(FileListContainer listContainer, BrowserItem item);
    }
    SelectedListener selectedListener = null;
    BrowserItemProvider sampleItemProvider = null;

    public FileListContainer(Context context, AttrSet attrSet) {
        super(context, attrSet);
        enableScrollBar(Component.AXIS_Y, true);
        setScrollbarThickness(50);
        setScrollbarRoundRect(true);
        setScrollbarRadius(20);
        setScrollbarBackgroundColor(Color.LTGRAY);
        setScrollbarColor(Color.DKGRAY);
        disableFadeEffect(FadeEffectEnum.FADEEFFECT_SCROLLBAR);
        sampleItemProvider = new BrowserItemProvider(context, new BrowserItem.ItemListener() {
            @Override
            public void changeDir(File dir) {
                setSelectedItemIndex(-1);
                sampleItemProvider.setCurrentDir(dir);
                setItemProvider(sampleItemProvider);
            }
        });
        setItemProvider(sampleItemProvider);
        setItemSelectedListener(itemSelectedListener);
    }

    public void setSelectedListener(SelectedListener listener){
        selectedListener = listener;
    }

    ListContainer.ItemSelectedListener itemSelectedListener = new ListContainer.ItemSelectedListener(){
        public Component prevSelected = null;
        @Override
        public void onItemSelected(ListContainer listContainer, Component component, int i, long l) {
            if(prevSelected != null){
                FileListContainer.this.setComponentActive(prevSelected, false);
            }
            if(component != null) {
                if (FileListContainer.this.selectedListener != null) {
                    FileListContainer.this.selectedListener.onItemSelected(FileListContainer.this,
                            (BrowserItem) (FileListContainer.this.getItemProvider().getItem(i)));
                }
                FileListContainer.this.setComponentActive(component, true);
            }
            prevSelected = component;
        }
    };

    void setComponentActive(Component component, boolean active){
        ShapeElement bg = new ShapeElement();
        if(active) {
            bg.setRgbColor(RgbPalette.LIGHT_GRAY);
            bg.setShape(ShapeElement.RECTANGLE);
            component.setBackground(bg);
        }
        else{
            component.setBackground(getBackgroundElement());
        }
    }
}
