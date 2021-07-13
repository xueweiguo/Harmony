package xwg.harmony.stopwatch;

import ohos.bundle.IBundleManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.security.SystemPermission;
import xwg.harmony.stopwatch.slice.MainAbilitySlice;
import xwg.harmony.stopwatch.slice.SettingState;

public class MainAbility extends Ability {
    static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00101, "MainAbility");
    private static final String PERM_LOCATION = "ohos.permission.LOCATION";
    @Override
    public void onStart(Intent intent) {
        HiLog.info(LABEL, "MainAbility.onStart");
        super.onStart(intent);
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        addActionRoute("action.setting", SettingState.class.getName());
        requestPermission(PERM_LOCATION);
    }
    @Override
    public void onStop(){
        HiLog.info(LABEL, "MainAbility.onStop");
        super.onStop();
    }

    @Override
    public void onForeground(Intent intent){
        HiLog.info(LABEL, "MainAbility.onForeground");
        super.onForeground(intent);
    }

    @Override
    public void onBackground(){
        HiLog.info(LABEL, "MainAbility.onBackground");
        super.onBackground();
    }

    private void requestPermission(String permission) {
        if (verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
            HiLog.info(LABEL, "requestPermissionsFromUser!");
            requestPermissionsFromUser(new String[] {permission}, 1);
        }
    }

    public interface IRequestPermissionListener{
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }

    IRequestPermissionListener listener = null;
    public void setRequestPermissionListener(IRequestPermissionListener _listener){
        listener = _listener;
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        HiLog.info(LABEL, "onRequestPermissionsFromUserResult Start!");
        super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);
        if(listener != null){
            listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
