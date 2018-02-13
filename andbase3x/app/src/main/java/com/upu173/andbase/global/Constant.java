package com.upu173.andbase.global;

import com.andbase.library.util.AbStrUtil;

/**
 * Created by Administrator on 2017/1/13.
 */

public class Constant {

    /**服务地址*/
    public static final String SERVER_ADDRESS = "http://www.upu173.com/";



    /**腾讯广告*/
    public static final String APPID = "1106041570";
    public static final String SplashPosID = "7040423484517161";

    public static final String BannerPosID = "9079537218417626401";
    public static final String InterteristalPosID = "8575134060152130849";
    public static final String NativePosID = "5000709048439488";
    public static final String NativeVideoPosID = "2050206699818455";
    public static final String NativeExpressPosID = "7030020348049331";

    /**
     * 获取URL
     * @param action
     * @return
     */
    public static String getUrl(String action){
        if(AbStrUtil.isEmpty(action)){
            return null;
        }
        if(action.indexOf("http")!= -1){
            return action;
        }
       return SERVER_ADDRESS + action;
    }

}
