package com.qqcs.smartHouse.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * 基础的相机预览类
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {


    private Camera camera;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("wang", "surfaceCreated");
        camera = Camera.open();
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                Camera.Parameters parameters = camera.getParameters();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                } else {
                    camera.setDisplayOrientation(0);
                    parameters.setRotation(0);
                }
                //设置比例
                Camera.Size previewSize = getBestSize(parameters.getSupportedPreviewSizes());
                Camera.Size pictureSize = getBestSize(parameters.getSupportedPictureSizes());

                if (previewSize != null) {
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    parameters.setPictureSize(pictureSize.width, pictureSize.height);
                } else {
                    parameters.setPreviewSize(1920, 1080);
                    parameters.setPictureSize(1920, 1080);
                }

                //设置持续的对焦模式
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                } else {
                    //手动对焦
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }

                camera.setParameters(parameters);
                camera.startPreview();
            } catch (Exception e) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                        camera.setDisplayOrientation(90);
                        parameters.setRotation(90);
                    } else {
                        camera.setDisplayOrientation(0);
                        parameters.setRotation(0);
                    }
                    camera.setParameters(parameters);
                    camera.startPreview();
                } catch (Exception e1) {
                    e.printStackTrace();
                    camera = null;
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("wang", "surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * Android相机的预览尺寸都是4:3或者16:9，这里遍历所有支持的预览尺寸，得到16:9的最大尺寸，保证成像清晰度
     *
     * @param sizes
     * @return 最佳尺寸
     */
    private Camera.Size getBestSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (Camera.Size size : sizes) {
            if ((float) size.width / (float) size.height == 4.0f / 3.0f) {
                if (bestSize == null) {
                    bestSize = size;
                } else {
                    if (size.width > bestSize.width) {
                        bestSize = size;
                    }
                }
            }
        }

        if (bestSize == null) {
            return sizes.get(0);
        } else {
            return bestSize;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {//屏幕触摸事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦
                    }
                }
            });
        }

        return true;
    }

    /**
     * 拍摄照片
     *
     * @param pictureCallback 在pictureCallback处理拍照回调
     */
    public void takePhoto(Camera.PictureCallback pictureCallback) {
        if (camera != null) {
            try {
                camera.takePicture(null, null, pictureCallback);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 开始预览外部接口
     */
    public void startPreview() {
        if (camera != null) {
            try {
                camera.startPreview();
            } catch (Exception e) {
            }
        }
    }


}
