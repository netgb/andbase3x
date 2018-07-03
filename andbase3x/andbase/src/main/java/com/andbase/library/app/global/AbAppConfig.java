package com.andbase.library.app.global;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 框架的设置
 */
public class AbAppConfig {
	
	/** 默认 SharePreferences文件名. */
	public static String SHARED_PATH = "app_share";
	
	/** 下载文件地址. */
	public static  String DOWNLOAD_ROOT_DIR = "download";
	
    /** 下载图片文件地址. */
	public static  String DOWNLOAD_IMAGE_DIR = "images";
	
    /** 下载文件地址. */
	public static  String DOWNLOAD_FILE_DIR = "files";

	/** video目录. */
	public static  String DOWNLOAD_VIDEO_DIR = "video";
	
	/** APP缓存目录. */
	public static  String DOWNLOAD_CACHE_DIR = "cache";
	
	/** DB目录. */
	public static  String DOWNLOAD_DB_DIR = "db";

	/** 默认磁盘缓存超时时间1小时设置，毫秒. */
	public static long DISK_CACHE_EXPIRES_TIME = 24*3600*1000;
	
	/** 内存缓存大小  单位20M. */
	public static int MAX_CACHE_SIZE_INBYTES = 20*1024*1024;
	
	/** 磁盘缓存大小  单位200M. */
	public static int MAX_DISK_USAGE_INBYTES = 200*1024*1024;

    /** 连接超时时间. */
    public static int DEFAULT_CONNECT_TIMEOUT = 5000;

	/** 数据传输超时时间. */
	public static int DEFAULT_READ_TIMEOUT = 5000;

	/** 证书验证模式. 0 忽略  1 验证*/
	public static int trustMode = 0;

	/** 证书文件. */
	public static int caRes = -1;

	/** 证书密码. */
	public static String caPassword = "123456";

	/** HTTP请求安全码，给服务端进行验证使用. */
	public static String httpSecurityCode = "123456";

	/** 主机不可到达. */
	public static String UNKNOWN_HOST_EXCEPTION = "远程服务连接失败";
	
	/** 连接异常. */
	public static String CONNECT_EXCEPTION = "网络连接出错，请重试";
	
	/** 连接超时. */
	public static String CONNECT_TIMEOUT_EXCEPTION = "连接超时，请重试";
	
	/** 协议错误. */
	public static String CLIENT_PROTOCOL_EXCEPTION = "HTTP请求参数错误";
	
	/** 参数个数不够. */
	public static String MISSING_PARAMETERS = "参数没有包含足够的值";
	
	/** 远程服务异常. */
	public static String REMOTE_SERVICE_EXCEPTION = "抱歉，远程服务出错了";
	
	/** 资源未找到. */
	public static String NOT_FOUND_EXCEPTION = "请求的资源无效404";
	
	/** 其他异常. */
	public static String UNTREATED_EXCEPTION = "未处理的异常";
	

}
