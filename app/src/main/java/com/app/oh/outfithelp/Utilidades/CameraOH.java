package com.app.oh.outfithelp.Utilidades;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.oh.outfithelp.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.app.oh.outfithelp.Utilidades.CameraHelper.*;
import static com.app.oh.outfithelp.Vistas.MiArmario.CATEGORIA;

/**
 * Created by Leonardo on 28/05/2018.
 */

public class CameraOH extends AppCompatActivity implements Camera.PictureCallback {
    public static final String EXTRA_OUTPUT = "DireccionDeSalida";
    private Camera camera;
    private CameraPreview cameraPreview;
    ImageButton IBTomarFoto;
    ImageView IVMascara;
    Intent thisIntent;
    Bundle extras;
    String categoria;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_oh);
        setResult(RESULT_CANCELED);
        thisIntent = getIntent();
        extras = thisIntent.getExtras();
        camera = getCameraInstance();
        IVMascara = findViewById(R.id.IVMascara);
        categoria = extras.getString(CATEGORIA);
        if (categoria.equals("Camisas")) IVMascara.setImageResource(R.drawable.mascaratop);
        else if (categoria.equals("Pantalones")) IVMascara.setImageResource(R.drawable.mascarapantalon);
        else if (categoria.equals("Zapatos")) IVMascara.setImageResource(R.drawable.mascarazapato);
        else if (categoria.equals("Vestidos")) IVMascara.setImageResource(R.drawable.mascaravestido);
        else {
            Toast.makeText(this, ""+categoria, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Oops! Error obteniendo mascara.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            this.finish();
        }
        IBTomarFoto = findViewById(R.id.TomarFoto);
        IBTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        if (cameraAvailable(camera))
        {
            initCameraPreview();
        } else Toast.makeText(this, "Oops! La camara no esta disponible o no existe", Toast.LENGTH_SHORT).show();
    }
    public void takePicture()
    {
        camera.takePicture(null,null, this);
    }


    private void initCameraPreview() {
        cameraPreview = findViewById(R.id.cameraPreview);
        cameraPreview.init(camera);
        Camera.Parameters params = camera.getParameters();
        params.setRotation(90);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(params);
        camera.setDisplayOrientation(90);
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        String Direccion = extras.getString(EXTRA_OUTPUT);
        savePictureToFileSystem(bytes, Direccion);
        setResult(RESULT_OK, thisIntent);
        finish();
    }

    private void savePictureToFileSystem(byte[] bytes, String Direccion) {
        try {
            FileOutputStream fos = new FileOutputStream(Direccion);
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Oops! Error obteniendo direccion del archivo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Oops! Error Guardando foto", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    private void releaseCamera() {
        if(camera != null){
            camera.release();
            camera = null;
        }
    }
}

