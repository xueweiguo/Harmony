package com.components.pageslider;

import com.components.pageslider.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        if (verifySelfPermission("ohos.permission.SENSOR_TYPE_SCREEN_ROTATION") != 0) {
            if (canRequestPermission("ohos.permission.SENSOR_TYPE_SCREEN_ROTATION")) {
                requestPermissionsFromUser(new String[] {"ohos.permission.SENSOR_TYPE_SCREEN_ROTATION"}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
        // 匹配requestPermissionsFromUser的requestCode
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                // 权限被授予
            } else {
                // 权限被拒绝
            }
        }
    }
}
