package com.mm.privategallery.controler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mm.privategallery.R;
import com.mm.utility.SDHelper;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	protected static final String TAG = null;
    private Camera mCamera;
    private CameraPreview mPreview;

    private boolean mBFront = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		// Create an instance ofCamera
		mCamera = getCameraInstance();
		
		// Create our Preview viewand set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// get an imagefrom the camera
			mCamera.takePicture(null, null, mPicture);
		}
		});
		
		findViewById(R.id.button_switch).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBFront = !mBFront;
				int captureId = -1;
				if(mBFront){
					captureId = FindFrontCamera();
				}else{
					captureId = FindBackCamera();
				}
				reopenCamera(captureId);
			}
		});
	
	}
	
	private void reopenCamera(int id){
		if(id==-1){
			return;
		}
		mCamera.stopPreview();//停掉原来摄像头的预览
		mCamera.release();//释放资源
		mCamera = null;//取消原来摄像头
		mCamera = Camera.open(id);//打开当前选中的摄像头
        try {
        	mCamera.setPreviewDisplay(mPreview.getHolder());//通过surfaceview显示取景画面
        	mCamera.setDisplayOrientation(90);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.startPreview();//开始预览
	}

	/** A safe way to get an instance of theCamera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
		   c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
		// Camera is notavailable (in use or does not exist)
		}
		return c; // returns nullif camera is unavailable
	}

	/** A basic Camera preview class */
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;
		
		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;
			// Install aSurfaceHolder.Callback so we get notified when the
			// underlyingsurface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecatedsetting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	
		public void surfaceCreated(SurfaceHolder holder) {
		// The Surfacehas been created, now tell the camera where to draw
		// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.setDisplayOrientation(90);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG, "Errorsetting camera preview: " + e.getMessage());
			}
		}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Takecare of releasing the Camera preview in your
		// activity.
		mCamera.release();
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int w,int h) {
	// If yourpreview can change or rotate, take care of those events
	// here.
	// Make sure tostop the preview before resizing or reformatting it.
	
		if (mHolder.getSurface() == null) {
		// previewsurface does not exist
		return;
		}
		
		// stop previewbefore making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		// ignore: triedto stop a non-existent preview
		}
		
		// set previewsize and make any resize, rotate or
		// reformattingchanges here
		
		// start previewwith new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
			
		} catch (Exception e) {
			Log.d(TAG, "Errorstarting camera preview: " + e.getMessage());
		}
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
		
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				return;
			}
			
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				SDHelper.sendbroadcastScanSD(pictureFile.getAbsolutePath());
				mCamera.startPreview();
				Log.i("cameratest", "pictureFiledata=" + data.length);
			} catch (FileNotFoundException e) {
				Log.i("cameratest", "File notfound: " + e.getMessage());
			} catch (IOException e) {
				Log.i("cameratest", "Erroraccessing file: " + e.getMessage());
			}
		}
	};

	private static File getOutputMediaFile(int type) {
	// To be safe, you shouldcheck that the SDCard is mounted
	// usingEnvironment.getExternalStorageState() before doing this.
	
		File mediaStorageDir = new File(SDHelper.getDataPublicPath());
		// This location works bestif you want the created images to be shared
		// between applications andpersist after your app has been uninstalled.
		
		// Create the storagedirectory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed tocreate directory");
				return null;
			}
		}
	
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
			+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
			+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
	
		return mediaFile;
	}

	@Override
	protected void onStop() {
		Log.i("cameratest", "onStop");
		super.onStop(); // Always callthe superclass method first
		
	}

	@Override
	public void onPause() {
		super.onPause(); // Alwayscall the superclass method first
		Log.i("cameratest", "onPause");
		// Release the Camera becausewe don't need it when paused
		// and other activities mightneed to use it.
		if (mCamera != null) {
		mCamera.release();
		mCamera = null;
	}
}
	@TargetApi(9) 
    private int FindFrontCamera(){  
        int cameraCount = 0;  
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        cameraCount = Camera.getNumberOfCameras(); // get cameras number  
                
        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) {   
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
               return camIdx;  
            }  
        }  
        return -1;  
    }  
	
    @TargetApi(9)  
    private int FindBackCamera(){  
        int cameraCount = 0;  
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
        cameraCount = Camera.getNumberOfCameras(); // get cameras number  
                
        for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
            Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
            if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_BACK ) {   
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
               return camIdx;  
            }  
        }  
        return -1;  
    }  
}