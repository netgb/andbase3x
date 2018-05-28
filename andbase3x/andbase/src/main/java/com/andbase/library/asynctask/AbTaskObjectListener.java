package com.andbase.library.asynctask;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据监听器
 */
public abstract class AbTaskObjectListener<T> extends AbTaskListener{
	
	/**
	 * 执行开始
	 * @return 返回的结果对象
	 */
    public abstract T getObject();
    
    /**
     * 执行开始后调用.
     * @param obj
     */
    public abstract void update(T obj);
    
	
}
