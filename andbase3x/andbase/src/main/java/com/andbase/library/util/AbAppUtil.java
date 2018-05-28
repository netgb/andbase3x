package com.andbase.library.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.andbase.library.cache.disk.AbDiskCacheImpl;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info APP工具类
 */
public class AbAppUtil {

	/**
	 * 打开并安装文件.
	 *
	 * @param context the context
	 * @param file apk文件路径
	 */
	public static void installApk(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/**
	 * 卸载程序.
	 *
	 * @param context the context
	 * @param packageName 包名
	 */
	public static void uninstallApk(Context context,String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}


	/**
	 * 用来判断服务是否运行.
	 *
	 * @param context the context
	 * @param className 判断的服务名字 "com.xxx.xx..XXXService"
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> servicesList = activityManager.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> l = servicesList.iterator();
		while (l.hasNext()) {
			RunningServiceInfo si = (RunningServiceInfo) l.next();
			if (className.equals(si.service.getClassName())) {
				isRunning = true;
			}
		}
		return isRunning;
	}

	/**
	 * 停止服务.
	 *
	 * @param context the context
	 * @param className the class name
	 * @return true, if successful
	 */
	public static boolean stopRunningService(Context context, String className) {
		Intent intent_service = null;
		boolean ret = false;
		try {
			intent_service = new Intent(context, Class.forName(className));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent_service != null) {
			ret = context.stopService(intent_service);
		}
		return ret;
	}

	/**
	 * 判断是否安装了APP
	 * @param context the context
	 */
	public static boolean isInstallApk(Context context,String packageName) {
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packageInfo = packageInfos.get(i).packageName;
				if (packageInfo.equals(packageName)) {
					return true;
				}
			}
		}
		return false;
	}
	

	/** 
	 * Gets the number of cores available in this device, across all processors. 
	 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu" 
	 * @return The number of cores, or 1 if failed to get result 
	 */ 
	public static int getNumCores() { 
		try { 
			//Get directory containing CPU info 
			File dir = new File("/sys/devices/system/cpu/"); 
			//Filter to only list the devices we care about 
			File[] files = dir.listFiles(new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					//Check if filename is "cpu", followed by a single digit number 
					if(Pattern.matches("cpu[0-9]", pathname.getName())) { 
					   return true; 
				    } 
				    return false; 
				}
				
			}); 
			//Return the number of cores (virtual CPU devices) 
			return files.length; 
		} catch(Exception e) { 
			e.printStackTrace();
			return 1; 
		} 
	} 
	
	
	/**
	 * 判断网络是否有效.
	 *
	 * @param context the context
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * Gps是否打开
	 * 需要<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />权限
	 *
	 * @param context the context
	 * @return true, if is gps enabled
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
	    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}


	/**
	 * 判断当前网络是否是移动数据网络.
	 *
	 * @param context the context
	 * @return boolean
	 */
	public static boolean isMobile(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 导入数据库.
	 *
	 * @param context the context
	 * @param dbName the db name
	 * @param rawRes the raw res
	 * @return true, if successful
	 */
    public static boolean importDatabase(Context context,String dbName,int rawRes) {
		int buffer_size = 1024;
		InputStream is = null;
		FileOutputStream fos = null;
		boolean flag = false;
		
		try {
			String dbPath = "/data/data/"+context.getPackageName()+"/databases/"+dbName; 
			File dbfile = new File(dbPath);
			//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
			if (!dbfile.exists()) {
				//欲导入的数据库
				if(!dbfile.getParentFile().exists()){
					dbfile.getParentFile().mkdirs();
				}
				dbfile.createNewFile();
				is = context.getResources().openRawResource(rawRes); 
				fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[buffer_size];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
				   fos.write(buffer, 0, count);
				}
				fos.flush();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
		return flag;
	}
    
    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null){
            mResources = Resources.getSystem();
            
        }else{
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }
    
    /**
     * 打开键盘.
     *
     * @param context the context
     */
    public static void showSoftInput(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity)context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    /**
     * 获取包信息.
     *
     * @param context the context
     */
    public static PackageInfo getPackageInfo(Context context) {
    	PackageInfo info = null;
	    try {
	        String packageName = context.getPackageName();
	        info = context.getPackageManager().getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return info;
    }
    
    /**
     * 
     * 根据进程名返回应用程序.
     * @param context
     * @param processName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context,String processName) {
        if (processName == null) {
            return null;
        }
    	
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
        	if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }
    
    /**
     * 
     * kill进程.
     * @param context
     * @param pid
     */
    public static void killProcesses(Context context,int pid,String processName) {
    	/*String cmd = "kill -9 "+pid;
    	Process process = null;
	    DataOutputStream os = null;
    	try {
			process = Runtime.getRuntime().exec("su"); 
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	AbLogUtil.d(AbAppUtil.class, "#kill -9 "+pid);*/
    	
    	ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	String packageName = null;
    	try {
    		if(processName.indexOf(":")==-1){
    			packageName = processName;
		    }else{
		    	packageName = processName.split(":")[0];
		    }
    		
			activityManager.killBackgroundProcesses(packageName);
			
			//
			Method forceStopPackage = activityManager.getClass().getDeclaredMethod("forceStopPackage", String.class);
			forceStopPackage.setAccessible(true);
			forceStopPackage.invoke(activityManager, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
	
	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean getRootPermission(Context context) {
		String path = context.getPackageCodePath();  
	    return getRootPermission(path);
	}
	
	/**
	 * 修改文件权限
	 * @return 文件路径
	 */
	public static boolean getRootPermission(String path) {
		Process process = null;
		DataOutputStream os = null;
		try {
			File  file = new File(path);
			if(!file.exists()){
				return false;
			}
			String cmd = "chmod 777 " + path;
			// 切换到root帐号
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

    /**
     * 
     * 获取可用内存.
     * @param context
     * @return
     */
	public static long getAvailMemory(Context context){  
        //获取android当前可用内存大小  
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo memoryInfo = new MemoryInfo();  
        activityManager.getMemoryInfo(memoryInfo);  
        //当前系统可用内存 ,将获得的内存大小规格化  
        return memoryInfo.availMem;  
    }  
	
	/**
	 * 
	 * 总内存.
	 * @param context
	 * @return
	 */
	public static long getTotalMemory(Context context){  
		//系统内存信息文件  
        String file = "/proc/meminfo";
        String memInfo;  
        String[] strs;  
        long memory = 0;  
          
        try{  
            FileReader fileReader = new FileReader(file);  
            BufferedReader bufferedReader = new BufferedReader(fileReader,8192);  
            //读取meminfo第一行，系统内存大小 
            memInfo = bufferedReader.readLine(); 
            strs = memInfo.split("\\s+");  
            for(String str:strs){  
                AbLogUtil.d(AbAppUtil.class,str+"\t");  
            }  
            //获得系统总内存，单位KB  
            memory = Integer.valueOf(strs[1]).intValue()*1024;
            bufferedReader.close();  
        }catch(Exception e){  
            e.printStackTrace();
        }  
        //Byte转位KB或MB
        return memory;  
    }

	/**
	 * 
	 * 获取IMSI.
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getSubscriberId() == null) {
			return null;
		} else {
			return telephonyManager.getSubscriberId();
		}
	}

	/**
	 * 
	 * 获取IMEI.
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getDeviceId() == null) {
			return null;
		} else {
			return telephonyManager.getDeviceId();
		}
	}
	
	/**
	 * 手机号码
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getLine1Number() == null || telephonyManager.getLine1Number().length() < 11) {
			return null;
		} else {
			return telephonyManager.getLine1Number();
		}
	}
	
	/**
	 * 
	 * 获取QQ号.
	 * @return
	 */
	public static String getQQNumber(Context context) {
		String path = "/data/data/com.tencent.mobileqq/shared_prefs/Last_Login.xml";
		getRootPermission(context);
		File file = new File(path);
		getRootPermission(path);
		boolean flag = file.canRead();
		String qq = null;
		if(flag){
			try {
				FileInputStream is = new FileInputStream(file);
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, "UTF-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {

					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("map".equals(parser.getName())) {
						}
						if ("string".equals(parser.getName())) {
							String uin = parser.getAttributeValue(null, "name");
							if (uin.equals("uin")) {
								qq = parser.nextText();
								return qq;
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
		
	/**
	 * 
	 * 获取WEIXIN号.
	 * @return
	 */
	public static String getWeiXinNumber(Context context) {
		String path = "/data/data/com.tencent.mm/shared_prefs/com.tencent.mm_preferences.xml";
		getRootPermission(context);
		File file = new File(path);
		getRootPermission(path);
		boolean flag = file.canRead();
		String weixin = null;
		if(flag){
			try {
				FileInputStream is = new FileInputStream(file);
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, "UTF-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {

					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("map".equals(parser.getName())) {
						}
						if ("string".equals(parser.getName())) {
							String nameString = parser.getAttributeValue(null, "name");
							if (nameString.equals("login_user_name")) {
								weixin = parser.nextText();
								return weixin;
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 调用拨打电话页面
	 * @param tel
     */
	public static void showActionCall(Context context,String tel){
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + tel));
		context.startActivity(intent);
	}

	/**
	 * 判断申请的权限是否全部通过
	 * @param grantResults
	 * @return
	 */
	public static boolean hasAllPermissionsGranted(int[] grantResults) {
		for (int grantResult : grantResults) {
			if (grantResult == PackageManager.PERMISSION_DENIED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断某个权限是否开启
	 * @param context
	 * @param permission
	 * @return
	 */
	public static boolean hasPermission(Context context, String permission){

		int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
		if(permissionCheck == PackageManager.PERMISSION_GRANTED){
			return true;
		}
		return false;
	}

	/**
	 * 申请蓝牙权限
	 * @param activity
	 * @param requestCode
	 */
	public static boolean requestBlueToothPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.BLUETOOTH,
					Manifest.permission.BLUETOOTH_ADMIN,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE

			}, requestCode);
		}else{
            return true;
        }

	}

	/**
	 * 申请SD卡权限
	 * @param activity
	 * @param requestCode
	 */
	public static boolean requestSDCardPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE

			}, requestCode);
		}else{
            return true;
        }

	}

	/**
	 * 申请照相机权限
	 * @param activity
	 * @param requestCode
     *
	 */
	public static boolean requestCameraPermission(Activity activity, int requestCode) {
		if (Build.VERSION.SDK_INT >= 23) {
			return requestPermissions(activity,new String []{
					Manifest.permission.CAMERA

			}, requestCode);
		}else{
            return true;
        }

	}


	/**
	 * 申请权限 在6.0才需要程序内获取
	 * @param activity
	 * @param permissions
	 * SD卡 Manifest.permission.WRITE_EXTERNAL_STORAGE
	 * 照相机 Manifest.permission.CAMERA
	 * 蓝牙 Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE
	 * @param requestCode
	 *
	 */
	/*
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
			//OK
		} else {
			Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			finish();
		}
	}*/
	public static boolean requestPermissions(Activity activity, String[] permissions, int requestCode){

		List<String> lackedPermission = new ArrayList<String>();
		for(String reqPermission :permissions) {
			if (!hasPermission(activity, reqPermission)) {
				lackedPermission.add(reqPermission);
			}
		}
		if (lackedPermission.size() == 0) {
			return true;
		} else {
			String[] requestPermissions = new String[lackedPermission.size()];
			lackedPermission.toArray(requestPermissions);
			ActivityCompat.requestPermissions(activity,requestPermissions, requestCode);
		}
		return false;
	}

	/**
	 * 获取系统版本
	 * @return
     */
	public static int getSDKVersion() {
		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			sdkVersion = 0;
		}
		return sdkVersion;
	}

	/**
	 * 清除所有缓存
	 * @param context
	 */
	public static void clearAllCache(Context context){
		try {
			PackageInfo info = AbAppUtil.getPackageInfo(context);
			File cacheDir = null;
			if (!AbFileUtil.isCanUseSD()) {
				cacheDir = new File(context.getCacheDir(), info.packageName);
			} else {
				cacheDir = new File(AbFileUtil.getCacheDownloadDir(context));
			}
			File[] files = cacheDir.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			AbLogUtil.d(AbDiskCacheImpl.class,"清除缓存成功.");
		}catch(Exception e){
			e.printStackTrace();
			AbLogUtil.d(AbDiskCacheImpl.class,"清除缓存失败.");
		}

	}

	/**
	 * 清除所有下载的数据
	 * @param context
	 */
	public static void clearAllDownload(Context context){
		try {
			PackageInfo info = com.andbase.library.util.AbAppUtil.getPackageInfo(context);
			File downloadDir = null;
			if (!AbFileUtil.isCanUseSD()) {
				downloadDir = new File(context.getCacheDir(), info.packageName);
			} else {
				downloadDir = new File(AbFileUtil.getDownloadRootDir(context));
			}
			File[] files = downloadDir.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
			AbLogUtil.d(AbDiskCacheImpl.class,"清除缓存成功.");
		}catch(Exception e){
			e.printStackTrace();
			AbLogUtil.d(AbDiskCacheImpl.class,"清除缓存失败.");
		}

	}

	/**
	 * 获取状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 设置透明状态栏
	 * @param activity 当前展示的activity
	 * @return
	 */
	public static void setTranslucentStatusBar(@NonNull Activity activity,Toolbar toolbar) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
			activity.getWindow()
					.getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			activity.getWindow()
					.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		if (toolbar != null) {
			ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
			layoutParams.setMargins(
					layoutParams.leftMargin,
					layoutParams.topMargin + AbAppUtil.getStatusBarHeight(activity),
					layoutParams.rightMargin,
					layoutParams.bottomMargin);
		}

		return;
	}

	/**
	 * 设置沉浸式
	 * @param activity 当前展示的activity
	 * @return
	 */
	public static void setTranslucentNavigationBar(@NonNull Activity activity, Toolbar toolbar) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		} else {
			toolbar.setPadding(0, AbAppUtil.getStatusBarHeight(activity) >> 1, 0, 0);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
		} else {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		return;
	}
}
