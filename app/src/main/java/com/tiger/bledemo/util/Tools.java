package com.tiger.bledemo.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tonghu lei on 2016/11/2.
 * 相关常用工具类
 */
public class Tools {

    /**
     * 弹出Toast,在屏幕中间显示
     *
     * @param mContext
     * @param resId
     */

    public static final void toastInCenter(Context mContext, String resId) {
        Toast toast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static final void toastInCenter(Context mContext, int resId) {
        if(mContext!= null){
            Toast toast = Toast.makeText(mContext, mContext.getString(resId),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
