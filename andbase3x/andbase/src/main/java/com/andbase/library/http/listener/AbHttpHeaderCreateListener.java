package com.andbase.library.http.listener;

import java.util.HashMap;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info HTTP请求头创建
 */
public interface AbHttpHeaderCreateListener {

    HashMap<String,String> onCreateHeader(String url,String method);



}
