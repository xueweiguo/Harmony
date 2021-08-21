package xwg.filebrowser.slice;

import java.util.ArrayList;
import java.util.List;

import xwg.filebrowser.ResourceTable;

import xwg.filebrowser.adapter.CustomPageSliderProvider;
import xwg.filebrowser.adapter.ListViewItemProvider;
import xwg.filebrowser.adapter.SliderListener;
import xwg.filebrowser.model.Item;
import xwg.filebrowser.utils.AppUtils;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.ListContainer;
import ohos.agp.components.PageSlider;
import ohos.agp.components.PageSliderIndicator;
import ohos.agp.components.Text;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.PixelMapElement;
import ohos.agp.window.dialog.ToastDialog;

/**
 * View Helper
 */
public class ViewCreateHelper implements Component.ClickedListener {
    private static final int PAGES_NUMBER = 3;

    private static final int LIST_NUMBER = 4;

    private static final int INDICATOR_DEFAULT_WIDTH = 12;

    private static final int INDICATOR_ENABLE_WIDTH = 24;

    private static final int INDICATOR_OFFSET = 30;

    private static final String TAG = "ViewCreateHelper";

    private AbilitySlice slice;

    private ComponentContainer rootLayout;

    private ListContainer listContainer;

    private ListViewItemProvider listViewItemProvider;

    /**
     * ViewCreateHelper
     *
     * @param abilitySlice abilitySlice
     */
    public ViewCreateHelper(AbilitySlice abilitySlice) {
        this.slice = abilitySlice;
    }

    /**
     * getRootLayout
     *
     * @return ComponentContainer ComponentContainer
     */
    public ComponentContainer getRootLayout() {
        return rootLayout;
    }

    /**
     * createComponent
     *
     * @return ComponentContainer ComponentContainer
     */
    public ComponentContainer createComponent() {
        Component mainComponent =
                LayoutScatter.getInstance(slice).parse(ResourceTable.Layout_main_ability, null, false);
        if (!(mainComponent instanceof ComponentContainer)) {
            return null;
        }
        rootLayout = (ComponentContainer) mainComponent;

        // 1、PageSlider

        PageSlider viewPage = (PageSlider) rootLayout.findComponentById(ResourceTable.Id_page_view);
        viewPage.setProvider(new CustomPageSliderProvider(slice, initPageSliderData())); // set adapter
        viewPage.addPageChangedListener(new SliderListener(this)); // property setting
        viewPage.setCurrentPage(0);
        viewPage.setReboundEffect(true);
        viewPage.setCentralScrollMode(true);

        // 2、PageSliderIndicator

        PageSliderIndicator pageIndicator = (PageSliderIndicator) rootLayout
                .findComponentById(ResourceTable.Id_page_indicator);
        pageIndicator.setPageSlider(viewPage);
        pageIndicator.setItemOffset(INDICATOR_OFFSET);
        Element tmpElement = new PixelMapElement(AppUtils.getResource(rootLayout, ResourceTable.Media_dot));
        tmpElement.setBounds(0, 0, INDICATOR_DEFAULT_WIDTH, INDICATOR_DEFAULT_WIDTH);
        pageIndicator.setItemNormalElement(tmpElement);
        tmpElement = new PixelMapElement(AppUtils.getResource(rootLayout, ResourceTable.Media_selected_dot));
        tmpElement.setBounds(0, 0, INDICATOR_ENABLE_WIDTH, INDICATOR_DEFAULT_WIDTH);
        pageIndicator.setItemSelectedElement(tmpElement);

        // 3、ListContainer

        listContainer = (ListContainer) rootLayout.findComponentById(ResourceTable.Id_list_view);
        listViewItemProvider = new ListViewItemProvider(this.slice);
        listViewItemProvider.setDataList(initListViewData(), 1);
        listViewItemProvider.setGroupMargin(0);
        listViewItemProvider.setHasGroupDivider(true);
        listContainer.setItemProvider(listViewItemProvider);
        listContainer.setReboundEffect(true);

        // 4、toolbar click event listen

        Image leftArrow = (Image) rootLayout.findComponentById(ResourceTable.Id_left_arrow);
        if (rootLayout.getLayoutDirectionResolved() == Component.LayoutDirection.RTL) {
            leftArrow.setPixelMap(ResourceTable.Media_back_mirror);
        } else {
            leftArrow.setPixelMap(ResourceTable.Media_back);
        }
        leftArrow.setScaleMode(Image.ScaleMode.ZOOM_CENTER);
        leftArrow.setClickedListener(this);
        Image firstItem = (Image) rootLayout.findComponentById(ResourceTable.Id_first_item);
        firstItem.setClickedListener(this);
        Image secondItem = (Image) rootLayout.findComponentById(ResourceTable.Id_second_item);
        secondItem.setClickedListener(this);
        Image thirdItem = (Image) rootLayout.findComponentById(ResourceTable.Id_third_item);
        thirdItem.setClickedListener(this);

        return rootLayout;
    }

    private List<Item> initListViewData() {
        final List<Item> dataList = new ArrayList<>();
        int[] resourceIdList = {
                ResourceTable.String_double_line_0,
                ResourceTable.String_item_description_0,
                ResourceTable.String_double_line_1,
                ResourceTable.String_item_description_1,
                ResourceTable.String_double_line_2,
                ResourceTable.String_item_description_2,
                ResourceTable.String_double_line_3,
                ResourceTable.String_item_description_3
        };

        for (int i = 0; i < LIST_NUMBER; i++) {
            int idx = i * 2;
            Item dataItem =
                    new Item(
                            AppUtils.getStringResource(rootLayout, resourceIdList[idx]),
                            AppUtils.getStringResource(rootLayout, resourceIdList[idx + 1]),
                            ResourceTable.Layout_checkbox_item);
            dataList.add(dataItem);
        }
        return dataList;
    }

    private List<Component> initPageSliderData() {
        // add five pages
        List<Component> pages = new ArrayList<>();

        int[][] resourceID = {
                {
                        ResourceTable.Media_slide_right,
                        ResourceTable.String_page_description_0,
                        ResourceTable.String_page_description_bot_0
                },
                {
                        ResourceTable.Media_slide_left,
                        ResourceTable.String_page_description_1,
                        ResourceTable.String_page_description_bot_1
                },
                {
                        ResourceTable.Media_slide_up,
                        ResourceTable.String_page_description_2,
                        ResourceTable.String_page_description_bot_2
                }
        };

        for (int i = 0; i < PAGES_NUMBER; i++) {
            Component parse = LayoutScatter.getInstance(slice.getContext()).parse(ResourceTable.Layout_viewpage,
                    null, false);
            Image image = (Image) parse.findComponentById(ResourceTable.Id_image1);
            image.setPixelMap(resourceID[i][0]);
            image.setScaleMode(Image.ScaleMode.ZOOM_CENTER);

            Text titleText = (Text) parse.findComponentById(ResourceTable.Id_index_text_top);
            titleText.setText(AppUtils.getStringResource(rootLayout, resourceID[i][1]));

            Text botText = (Text) parse.findComponentById(ResourceTable.Id_index_text_bot);
            botText.setText(AppUtils.getStringResource(rootLayout, resourceID[i][2]));

            pages.add(parse);
        }
        return pages;
    }

    @Override
    public void onClick(Component component) {
        switch (component.getId()) {
            case ResourceTable.Id_left_arrow:
                slice.terminateAbility();
                break;
            case ResourceTable.Id_first_item:
            case ResourceTable.Id_second_item:
                new ToastDialog(slice.getContext()).setText("add").show();
                break;
            case ResourceTable.Id_third_item:
                new ToastDialog(slice.getContext()).setText("more").show();
                break;
            default:
                break;
        }
    }
}