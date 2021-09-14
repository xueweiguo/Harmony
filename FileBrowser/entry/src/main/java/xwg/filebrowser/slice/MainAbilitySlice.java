package xwg.filebrowser.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import xwg.filebrowser.ResourceTable;
import xwg.filebrowser.fileitems.BrowserItem;
import xwg.filebrowser.fileitems.BrowserItemProvider;
import xwg.filebrowser.fileitems.FileListContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Slice with MainAbility
 */
public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MainAbilitySlice");
    @Override
    public void onStart(Intent intent) {
        setUIContent(ResourceTable.Layout_browser_ability);
        Image leftArrow = (Image) findComponentById(ResourceTable.Id_left_arrow);
        leftArrow.setPixelMap(ResourceTable.Media_return_gray50);
        leftArrow.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                FileListContainer listContainer = (FileListContainer) findComponentById(ResourceTable.Id_list_container);
                listContainer.backToParent();
            }
        });
        initListContainer();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    FileListContainer.SelectedListener listener = new FileListContainer.SelectedListener() {
        @Override
        public void onItemSelected(FileListContainer listContainer, BrowserItem item) {
            HiLog.info(LABEL, "MainAbilitySlice.onItemSelected, item=%{public}s!", item.getName());
            ComponentContainer container =
                    (ComponentContainer)(MainAbilitySlice.this.findComponentById(ResourceTable.Id_detail_view_container));
            item.buildView(container);
            container.invalidate();
        }
    };

    private void initListContainer() {
        FileListContainer listContainer = (FileListContainer) findComponentById(ResourceTable.Id_list_container);
        listContainer.setSelectedListener(listener);
        listContainer.setDirChangeListener(new BrowserItem.DirChangeListener() {
            @Override
            public void dirChanged(File dir) {
                String parent = dir.getParent();
                Image leftArrow = (Image) findComponentById(ResourceTable.Id_left_arrow);
                if(parent != null){
                    leftArrow.setPixelMap(ResourceTable.Media_return_black50);
                }
                else{
                    leftArrow.setPixelMap(ResourceTable.Media_return_gray50);
                }
            }
        });
    }
}
