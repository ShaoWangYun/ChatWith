package com.dxxy.chatwith;

import android.app.Application;
import android.content.Context;

import com.dxxy.chatwith.utils.AppUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import static com.dxxy.chatwith.Constant.CurrentMode;

public class ChatApplication extends LitePalApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    //全局获取Context
    public static Context getInstance() {
        return mContext;
    }
}
