package com.upu173.andbase.main.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
	private LinearLayout splashView;
	private static final String SKIP_TEXT = "跳过 %d";

	public boolean canJump = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);

		container = (ViewGroup) this.findViewById(R.id.splash_container);
		skipView = (TextView) findViewById(R.id.skip_view);
		splashView = (LinearLayout) findViewById(R.id.splash_view);
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
			Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			finish();
		}
	}

	private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
							   String appId, String posId, SplashADListener adListener, int fetchDelay) {
		splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
	}

	@Override
	public void onADPresent() {
		splashView.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onADClicked() {
	}


	@Override
	public void onADTick(long millisUntilFinished) {
        skipView.setVisibility(View.VISIBLE);
		skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
	}

	@Override
	public void onADDismissed() {
		next();
	}

	@Override
	public void onNoAD(int errorCode) {
		gotoMain();
	}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

