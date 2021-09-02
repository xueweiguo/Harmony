package xwg.filebrowser.fileitems;

import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

import java.io.File;

public abstract class BrowserItem {
    interface ItemListener{
        public void changeDir(File dir);
    }
    String name;
    Context context;
    public BrowserItem(Context context, String name) {
        this.context = context;
        this.name = name;
    }
    //取得项目名
    public String getName() {
        return name;
    }
    //设定项目名
    public void setName(String name) {
        this.name = name;
    }
    //生成项目列表项
    abstract public Component createUiComponent();
    //生成详情表示画面
    abstract public void buildView(ComponentContainer container);
}