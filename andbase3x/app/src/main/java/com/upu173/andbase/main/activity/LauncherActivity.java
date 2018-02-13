package com.upu173.andbase.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.util.AbAppUtil;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.upu173.andbase.R;
import com.upu173.andbase.global.Constant;

/**
 * 程序启动
 */
public class LauncherActivity extends AbBaseActivity implements SplashADListener {

	private SplashAD splashAD;
	private ViewGroup container;
	private TextView skipView;
	private ImageView splashHolder;
	private static final String SKIP_TEXT = "跳过 %d";

	public boolean canJump = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);

		container = (ViewGroup) this.findViewById(R.id.splash_container);
		skipView = (TextView) findViewById(R.id.skip_view);
		splashHolder = (ImageView) findViewById(R.id.splash_holder);
        skipView.setVisibility(View.INVISIBLE);

		//检查权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			boolean result =  AbAppUtil.requestPermissions(this,new String []{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.READ_PHONE_STATE
			},1024);
			if(result){
				showAD();
			}
		}else{
			showAD();
		}
	}

	public  void showAD(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
				fetchSplashAD(LauncherActivity.this, container, skipView, Constant.APPID, Constant.SplashPosID, LauncherActivity.this, 0);
			}
		},3000);
    }


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1024 && AbAppUtil.hasAllPermissionsGranted(grantResults)) {
			fetchSplashAD(this, container, skipView, Constant.APPID, Constant.SplashPosID, this, 0);
		} else {
			// 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
			Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
	 *
	 * @param activity        展示广告的activity
	 * @param adContainer     展示广告的大容器
	 * @param skipContainer   自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
	 * @param appId           应用ID
	 * @param posId           广告位ID
	 * @param adListener      广告状态监听器
	 * @param fetchDelay      拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
	 */
	private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
							   String appId, String posId, SplashADListener adListener, int fetchDelay) {
		splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
	}

	@Override
	public void onADPresent() {
		Log.i("AD_DEMO", "SplashADPresent");
        // 广告展示后一定要把预设的开屏图片隐藏起来
		splashHolder.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onADClicked() {
		Log.i("AD_DEMO", "SplashADClicked");
	}

	/**
	 * 倒计时回调，返回广告还将被展示的剩余时间。
	 * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
	 *
	 * @param millisUntilFinished 剩余毫秒数
	 */
	@Override
	public void onADTick(long millisUntilFinished) {
		Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        skipView.setVisibility(View.VISIBLE);
		skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
	}

	@Override
	public void onADDismissed() {
		Log.i("AD_DEMO", "SplashADDismissed");
		next();
	}

	@Override
	public void onNoAD(int errorCode) {
		Log.i("AD_DEMO", "LoadSplashADFail, eCode=" + errorCode);
		/** 如果加载广告失败，则直接跳转 */
		gotoMain();
	}

	/**
	 * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
	 * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
	 */
	private void next() {
		if (canJump) {
			gotoMain();
		} else {
			canJump = true;
		}
	}

	public void gotoMain(){
		Intent intent = new Intent();
		intent.setClass(LauncherActivity.this, WebActivity.class);
		startActivity(intent);
		finish();
	}


	@Override
	protected void onPause() {
		super.onPause();
		canJump = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (canJump) {
			next();
		}
		canJump = true;
	}

	/** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

