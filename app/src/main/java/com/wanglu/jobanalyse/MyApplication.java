package com.wanglu.jobanalyse;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.vondear.rxtools.RxTool;

/**
 * Created by WangLu on 2018/4/17.
 */

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        MultiDex.install(this);
        RxTool.init(this);
        Logger.init("  WANG LU   ")
                .methodCount(3)
                .hideThreadInfo()
                .methodOffset(2);
    }

}
