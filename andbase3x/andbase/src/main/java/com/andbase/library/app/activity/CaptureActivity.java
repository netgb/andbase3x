package com.andbase.library.app.activity;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.andbase.library.R;
import com.andbase.library.app.base.AbBaseActivity;
import com.andbase.library.camera.CameraManager;
import com.andbase.library.camera.Config;
import com.andbase.library.util.AbAppUtil;
import com.andbase.library.util.AbImageUtil;
import com.andbase.library.util.AbToastUtil;

import java.io.OutputStream;

/**
 *  Intent intent = new Intent(RecorderActivity.this, CaptureActivity.class);
 *  intent.putExtra("cameraId", 0);
 *  intent.putExtra("orientation", 1);
 *  startActivity(intent);
 */

public class CaptureActivity extends AbBaseActivity implements SurfaceHolder.Callback {

	/** 拍照请求. */
	public static int REQUEST_CODE_TAKEPICTURE = 1;

	/** UI相关. */
	private SurfaceView surfaceView = null;
	
	/** 拍照按钮. */
	private Button camBtn;

	/** 操作按钮. */
	private Button flashBtn,focusBtn,okBtn,cancleBtn;

	/** 布局. */
	private FrameLayout camLayout,previewLayout;

	/** 图片结果. */
	private ImageView imgResult;
	
	/** 控制相关. */
	private boolean hasSurface = false;
	
	/** 当前路径. */
	private String  path = null;

    /** 闪光灯. */
    private boolean isFlashOn = false;

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
        Config.focusMode = 1;
		
		//强制为横屏
		if(Config.orientation==0){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		setContentView(R.layout.activity_capture);
		
		// 初始化CameraManager
		CameraManager.init(getApplication());
		
		surfaceView = (SurfaceView) findViewById(R.id.preview_view);

		//UI相关
		camBtn = (Button)this.findViewById(R.id.shot_btn);
		flashBtn = (Button)this.findViewById(R.id.flash_btn);
		focusBtn = (Button)this.findViewById(R.id.focus_btn);
		okBtn = (Button)this.findViewById(R.id.ok_btn);
		cancleBtn = (Button)this.findViewById(R.id.cancle_btn);
		camLayout = (FrameLayout)this.findViewById(R.id.cam_layout);
		previewLayout = (FrameLayout)this.findViewById(R.id.preview_layout);
		camLayout.setVisibility(View.VISIBLE);
		previewLayout.setVisibility(View.GONE);
		imgResult = (ImageView)this.findViewById(R.id.imgResult);
		
		if(cameraId==1){
			flashBtn.setVisibility(View.GONE);
		}
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("path", path);
				setResult(RESULT_OK,intent); 
				finish();
			}
		});
		
		//拍照的回调
		CameraManager.get().setPictureCallback(new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				//关闭预览
				CameraManager.get().stopPreview();
				
				Bitmap cameraBitmap = AbImageUtil.getBitmap(data,1280,720);

				camLayout.setVisibility(View.INVISIBLE);
				previewLayout.setVisibility(View.VISIBLE);
				
				imgResult.setImageBitmap(cameraBitmap);


                //插入到相册数据库
                try{
                    String  url = insertImage(CaptureActivity.this.getContentResolver(), cameraBitmap, "andbase", "andbase");
                    path = getRealFilePath(CaptureActivity.this,Uri.parse(url));
                    Log.e("onPictureTaken", "onPictureTaken insertImage："+path);
                    AbToastUtil.showToast(CaptureActivity.this,"insertImage:"+path);
                }catch (Exception e){
                    e.printStackTrace();
                }

			}
		});
		
		//对焦的控制
		CameraManager.get().setAutoFocusCallback(new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if(success){
					//对焦成功
				}else{

				}
                if(Config.focusMode == 1) {
                    CameraManager.get().startPreview();
                }
			}
		});
		
		
		
		// 预览
		CameraManager.get().setPreviewCallback(new PreviewCallback() {
			
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				
			}
		});
		
		
		//拍照
		camBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CameraManager.get().takePicture();
				//声音
				shootSound();
			}
		});
		
		//点击屏幕
		surfaceView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
                if(Config.focusMode == 0){
                    CameraManager.get().requestAutoFocus();
                }else {
                    CameraManager.get().startPreview();
                }
			}
		});
		
		//闪光灯
		flashBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                if(isFlashOn){
                    flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
                }else{
                    flashBtn.setBackgroundResource(R.drawable.ic_flash_on);
                }
                isFlashOn = !isFlashOn;
				CameraManager.get().toogleFlash();		
			}
		});

        if(Config.focusMode == 0){
            //手动对焦
            focusBtn.setVisibility(View.VISIBLE);
            focusBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    CameraManager.get().requestAutoFocus();
                }
            });
        }

		
		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imgResult.setImageBitmap(null);
				camLayout.setVisibility(View.VISIBLE);
				previewLayout.setVisibility(View.INVISIBLE);
				
				CameraManager.get().startPreview();
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
			Toast.makeText(CaptureActivity.this,"调用摄像头权限被禁止，请在权限管理中授权！",Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/**
	 * surfaceChanged.
	 * @param holder
	 * @param format
	 * @param width
	 * @param height
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Log.e("surfaceChanged", "surfaceChanged  相机界面改变");
	}

	/**
	 * surfaceCreated.
	 * @param holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			initCamera(holder);
			hasSurface = true;
		}

	}

	/**
	 * surfaceDestroyed.
	 * @param holder
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}
	
	
	/**
	 * 暂停,将相机关闭.
	 */
	@Override
	protected void onPause() {
		CameraManager.get().stopPreview();
		CameraManager.get().closeDriver();
		Log.e("onPause", "onPause  相机界面暂停");
		super.onPause();
	}
	
	/**
	 * 恢复.
	 */
	@Override
	protected void onResume() {
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
     *   播放系统拍照声音 
     */  
    public void shootSound(){  

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

    public String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static final String insertImage(ContentResolver cr, Bitmap source,
                                           String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri url = null;
        String stringUrl = null;

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }
}