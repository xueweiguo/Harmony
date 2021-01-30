package com.xwg.harmonycalulator.CalculateEngine;

import com.xwg.harmonycalulator.ResourceTable;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;

import java.io.IOException;

class CalculatorContext {
    private Context ohosContext = null;
    private DatabaseHelper databaseHelper = null;

    CalculatorContext(Context context)
    {
        ohosContext = context;
        databaseHelper = new DatabaseHelper(ohosContext);
    }

    ResourceManager getResourceManager()
    {
        return ohosContext.getResourceManager();
    }

    Preferences getPreferences(String fileName)
    {
        return databaseHelper.getPreferences(fileName);
    }

    String getText(int id){
        ohos.global.resource.ResourceManager resManager = getResourceManager();
        try {
            return resManager.getElement(id).getString();
        } catch (IOException | NotExistException | WrongTypeException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setErrorMessage(String prototype, int id)
    {

    }
}
