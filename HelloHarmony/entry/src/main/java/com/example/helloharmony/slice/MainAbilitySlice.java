package com.example.helloharmony.slice;

        import com.example.helloharmony.ResourceTable;
        import ohos.aafwk.ability.AbilitySlice;
        import ohos.aafwk.content.Intent;
        import ohos.agp.components.Button;
        import ohos.agp.components.Component;
        import ohos.agp.components.Text;
        import ohos.agp.utils.LayoutAlignment;
        import ohos.agp.window.dialog.ToastDialog;
        import ohos.data.DatabaseHelper;
        import ohos.data.preferences.Preferences;
        import ohos.global.resource.NotExistException;
        import ohos.global.resource.WrongTypeException;
        import ohos.hiviewdfx.HiLog;
        import ohos.hiviewdfx.HiLogLabel;

        import java.io.IOException;

public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x12345, "MainAbilitySlice");
    static final String preferenceFile = "preferences";
    static final String counterKey = "ClickCounter";
    int clickCounter = 0;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        readCounter();
        Text hello = (Text)findComponentById(ResourceTable.Id_text_hello);
        hello.setClickedListener(new Component.ClickedListener() {
            public void onClick(Component component) {
                clickCounter++;
                try {
                    ohos.global.resource.ResourceManager resManager = getContext().getResourceManager();
                    String hello_msg = resManager.getElement(ResourceTable.String_hello_message).getString();
                    new ToastDialog(getContext())
                            .setText(hello_msg + "X" + clickCounter)
                            .setAlignment(LayoutAlignment.BOTTOM)
                            .show();
                } catch (IOException | NotExistException | WrongTypeException e) {
                    e.printStackTrace();
                }
            }
        });
        //组件画面迁移按钮
        Button component_button = (Button) findComponentById(ResourceTable.Id_component_view);
        component_button.setClickedListener(listener -> present(new ComponentAbilitySlice(), new Intent()));
        //列表画面迁移按钮
        Button list_button = (Button) findComponentById(ResourceTable.Id_list_view);
        list_button.setClickedListener(listener -> present(new ListAbilitySlice(), new Intent()));
        //标签页画面迁移按钮
        Button tab_button = (Button) findComponentById(ResourceTable.Id_tab_view);
        tab_button.setClickedListener(listener -> present(new TablistAbilitySlice(), new Intent()));
        // 打印一条日志
        HiLog.info(label, "MainAbilitySlice.onStart!");
    }
    @Override
    public void onActive() {
        super.onActive();
    }
    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
    @Override
    public void onStop() {
        writeCounter();
        super.onStop();
        // 打印一条日志
        HiLog.info(label, "MainAbilitySlice.onStop!");
    }

    private void readCounter(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Preferences preferences = databaseHelper.getPreferences(preferenceFile);
        clickCounter = preferences.getInt(counterKey, 0);
    }

    private void writeCounter(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        Preferences preferences = databaseHelper.getPreferences(preferenceFile);
        preferences.putInt(counterKey, clickCounter);
    }
}
