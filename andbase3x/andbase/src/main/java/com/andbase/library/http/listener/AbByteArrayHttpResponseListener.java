package com.andbase.library.http.listener;

import com.andbase.library.http.AbHttpUtil;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Http响应监听器，返回Byte 数组数据
 */
public abstract class AbByteArrayHttpResponseListener extends AbHttpResponseListener {
	
    /**
     * 空构造函数.
     */
	public AbByteArrayHttpResponseListener() {
		super();
	}
	
	/**
	 * 获取数据成功会调用这里.
	 *
	 * @param statusCode the status code
	 * @param content the content
	 */
    public abstract void onSuccess(int statusCode,byte[] content);
    

	/**
     * 成功消息.
     *
     * @param statusCode the status code
     * @param content the content
     */
    public void sendSuccessMessage(int statusCode,byte[] content){
    	sendMessage(obtainMessage(AbHttpUtil.SUCCESS_MESSAGE, new Object[]{statusCode, content}));
    }
    

}
