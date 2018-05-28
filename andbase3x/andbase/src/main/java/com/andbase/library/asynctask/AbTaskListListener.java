
package com.andbase.library.asynctask;

import java.util.List;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 数据监听器
 */
public abstract class AbTaskListListener<T> extends AbTaskListener {

	/**
	 * 执行开始.
	 * 
	 * @return 返回的结果列表
	 */
	public abstract List<T> getList();

	/**
	 * 执行完成后回调. 不管成功与否都会执行
	 * 
	 * @param paramList 返回的List
	 */
	public abstract void update(List<T> paramList);

}
