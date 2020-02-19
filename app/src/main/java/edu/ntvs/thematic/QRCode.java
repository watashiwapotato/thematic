package edu.ntvs.thematic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QRCode extends AppCompatActivity {
    private SurfaceView surfaceView;
    private Button button;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);
        surfaceView = (SurfaceView) findViewById(R.id.qrcode);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if (qrcode.size() != 0) {
                    Global.url = qrcode.valueAt(0).displayValue;
                    Global.pref = getSharedPreferences("Url", MODE_PRIVATE);
                    Global.pref.edit()
                            .putString("url", Global.url)
                            .commit();
                    Intent intent = new Intent(QRCode.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Handler handler = new Handler(Looper.myLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.release();
                        }
                    });
                }

            }
        });

        button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRCode.this, MainActivity.class);
                startActivity(intent);
                finish();
                Handler handler = new Handler(Looper.myLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraSource.release();
                    }
                });
            }
        });

    }
}

