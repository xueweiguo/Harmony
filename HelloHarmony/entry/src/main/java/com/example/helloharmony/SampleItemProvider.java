package com.example.helloharmony;

        import ohos.aafwk.ability.AbilitySlice;
        import ohos.agp.components.*;
        import java.util.List;

public class SampleItemProvider extends RecycleItemProvider {
    private List<SampleItem> list;
    private AbilitySlice slice;
    public SampleItemProvider(List<SampleItem> list, AbilitySlice slice) {
        this.list = list;
        this.slice = slice;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Component getComponent(int position, Component convertComponent, ComponentContainer componentContainer) {
        SampleItem sampleItem = list.get(position);
        Component cpt = convertComponent;
        if(cpt == null || cpt.getId() != sampleItem.getLayout()) {
            cpt = LayoutScatter.getInstance(slice).parse(sampleItem.getLayout(), null, false);
        }
        Text text = (Text) cpt.findComponentById(sampleItem.getItem());
        text.setText(sampleItem.getName());
        return cpt;
    }
}