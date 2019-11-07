package com.example.sockwork;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author:lixiaobiao
 * @date:On2019/11/6
 *
 */
public class BaseApplication extends Application {
    //创建一个Map集合，把activity加载到这个集合中
    private static Map<String, Activity>destroyMap=new HashMap<>();
    /**
     * 添加到销毁的队列中
     * <p/>
     * 要销毁的activity
     */
    public   static void addDestroyActivity(Activity activity,String activityName){
        destroyMap.put(activityName,activity);
    }

    /**
     * 销毁指定的activity
     */
    public static void destrouActivity(String activityName){
        Set<String> keySet=destroyMap.keySet();
        for (String key:keySet){
            destroyMap.get(key).finish();
        }
    }
}
