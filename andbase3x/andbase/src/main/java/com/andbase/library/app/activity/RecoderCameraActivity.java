package com.andbase.library.app.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.andbase.library.R;
import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.camera.CameraManager;
import com.andbase.library.camera.Config;
import com.andbase.library.util.AbAppUtil;
import com.andbase.library.util.AbFileUtil;

import java.io.File;
import java.util.Random;

/**
 *  Intent intent = new Intent(DiscoveryActivity.this, RecoderCameraActivity.class);
 *  intent.putExtra("cameraId", 0);
 *  intent.putExtra("orientation", 1);
 *  startActivity(intent);
 */
public class RecoderCameraActivity extends AbBaseActivity implements SurfaceHolder.Callback {

	/** 录像请求. */
	public static int REQUEST_CODE_RECODER = 2;

	/** UI相关. */
	private SurfaceView surfaceView = null;
	
	/** 录制按钮. */
	private Button startBtn;
	
	/** 控制相关. */
	private boolean hasSurface = false;
	
	/** 录制. */
	private MediaRecorder mRecorder;  
	
	/** 录制的文件. */
	private File videoFile;

	/** 录制开关. */
	private boolean isRecoding = false;
	
	/**
	 * 开始.
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//屏幕参数
		int cameraId = this.getIntent().getIntExtra("cameraId", 0);
		int orientation = this.getIntent().getIntExtra("orientation", 0);
		Config.cameraId = cameraId;
		Config.orientation = orientation;
		
		//强制为横屏
		if(Config.orientation==0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		setContentView(R.layout.activity_recoder_camera);

		// 初始化 QRCameraManager
		CameraManager.init(getApplication());
		
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		
		//UI相关
		startBtn = (Button)this.findViewById(R.id.start_button);

		startBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(isRecoding){
					startBtn.setBackgroundResource(R.drawable.ic_start_record);
					isRecoding = false;
					stopRecorder();
					Intent intent = new Intent();
					intent.putExtra("path", videoFile.getPath());
					setResult(RESULT_OK,intent);
					finish();
				}else{
                    startBtn.setBackgroundResource(R.drawable.ic_stop_record);
					isRecoding = true;
					startRecorder();
				}

			}
		});
	}

    /**
     * 打开相机.
     *
     * @param surfaceHolder the surface holder
     */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager manager = CameraManager.get();
			manager.openDriver(surfaceHolder,Config.cameraId);
			manager.startPreview();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Log.e("surfaceChanged", "surfaceChanged  相机界面改变");
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			initCamera(holder);
			hasSurface = true;
		}

	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}
	
	
	/**
	 * 暂停,将相机关闭.
	 */
	@Override
	public void onPause() {
        if(isRecoding){
            startBtn.setBackgroundResource(R.drawable.ic_start_record);
            isRecoding = false;
            stopRecorder();
        }
		CameraManager.get().stopPreview();
        CameraManager.get().closeDriver();
		Log.e("onPause", "onPause  相机界面暂停");
		super.onPause();
	}
	
	/**
	 * 恢复.
	 */
	@Override
	public void onResume() {

		//检查权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           boolean result =  AbAppUtil.requestPermissions(this,new String []{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE
            },1024);
            if(result){
                startCameraPreview();
            }
		}else{
            startCameraPreview();
		}

		//关键代码：恢复数据
		Log.e("onResume", "onResume  相机界面恢复");
		super.onResume();
		
	}

	public void startCameraPreview(){
		//恢复相机
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		Log.e("hasSurface", "hasSurface："+hasSurface);
		if (hasSurface) {
			//SurfaceView存在就重新打开相机
			initCamera(surfaceHolder);
		} else {
			//SurfaceView不存在，重新设置surfaceHolder，同时SurfaceView会被重建
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			CameraManager.get().startPreview();

		}
	}
	
	/**
	 * 完成.
	 */
	@Override
	public void finish() {
		super.finish();
	}
    
    public void startRecorder(){
		try {
			
			// 创建保存录制视频的视频文件
			String photoDir = AbFileUtil.getImageDownloadDir(RecoderCameraActivity.this);
			String fileName = "video_"+new Random().nextInt(1000) + "-" + System.currentTimeMillis() + ".mp4";

			videoFile = new File(photoDir, fileName);

	        try {
				if(videoFile.exists()){
					videoFile.delete();
				}
	   
				if(!videoFile.getParentFile().exists()){
					videoFile.getParentFile().mkdirs();
				}
				if(!videoFile.exists()){
					videoFile.createNewFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 创建MediaPlayer对象  
	        CameraManager.get().getCamera().unlock();
			mRecorder = new MediaRecorder();  
			mRecorder.reset();  
			mRecorder.setCamera(CameraManager.get().getCamera());
			// 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)  
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
			// 设置从摄像头采集图像  
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
			// 设置视频文件的输出格式  
			// 必须在设置声音编码格式、图像编码格式之前设置  
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
			// 设置声音编码的格式  
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);  
			// 设置图像编码的格式  
			mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
			 
			mRecorder.setOutputFile(videoFile.getAbsolutePath());  
			// 指定使用SurfaceView来预览视频
			mRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
			mRecorder.prepare();  
			// 开始录制  
			mRecorder.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void  stopRecorder(){
		try {
			if(mRecorder!=null){
				// 停止录制  
			    mRecorder.stop();  
			    // 释放资源  
			    mRecorder.release();  
			    mRecorder = null;  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1024 && AbAppUtil.hasAllPermissionsGranted(grantResults)) {
			startCameraPreview();
		} else {
			Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + getPackageName()));
			startActivity(intent);
			finish();
		}
	}

}