package xwg.filebrowser.fileitems;

import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

public abstract class BrowserItem {
    String name;
    Context context;
    public BrowserItem(Context context, String name) {
        this.context = context;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    abstract public void buildView(ComponentContainer container);
}