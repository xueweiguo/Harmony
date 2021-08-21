package xwg.filebrowser.adapter;

import ohos.aafwk.ability.AbilitySlice;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.PageSliderProvider;

import java.util.List;

/**
 * CustomPageSliderProvider
 */
public class CustomPageSliderProvider extends PageSliderProvider {
    private List<Component> pages;

    private AbilitySlice slice;

    /**
     * CustomPageSliderProvider
     *
     * @param abilitySlice abilitySlice
     * @param pages        pages
     */
    public CustomPageSliderProvider(AbilitySlice abilitySlice, List<Component> pages) {
        this.pages = pages;
        slice = abilitySlice;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object createPageInContainer(ComponentContainer componentContainer, int position) {
        componentContainer.addComponent(pages.get(position));
        return pages.get(position);
    }

    @Override
    public void destroyPageFromContainer(ComponentContainer componentContainer, int position, Object object) {
        componentContainer.removeComponent(pages.get(position));
    }

    @Override
    public boolean isPageMatchToObject(Component component, Object object) {
        return component == object;
    }
}
