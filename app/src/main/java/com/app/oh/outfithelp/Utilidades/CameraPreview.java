package com.app.oh.outfithelp.Utilidades;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Created by Leonardo on 28/05/2018.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private Camera camera;
    private SurfaceHolder holder;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void init (Camera camera)
    {
        this.camera = camera;
        initSurfaceHolder();
    }
    @SuppressWarnings("deprecation")
    private void initSurfaceHolder()
    {
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void initCamera(SurfaceHolder holder)
    {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (Exception e){
            Toast.makeText(CameraPreview.this.getContext(), "Oops! Error al generar CameraPreview", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initCamera(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
