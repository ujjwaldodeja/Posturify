package com.example.posturfiy;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posturfiy.ui.Map.MapFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.posturfiy.databinding.ActivityMainBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private final String TAG = "CameraXApp";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_CAMERA = 100 ;
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private ClipData.Item settings;
    private Button mapButton;
    private Button statButton;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private TextView textView;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        //Checking for Camera Permissions
        if(allPermissionsGranted()){
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_CAMERA);
        }
        binding.buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        //intialise a preview for camera
        previewView = findViewById(R.id.previewView);
        //textView = findViewById(R.id.orientation);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        cameraExecutor = Executors.newSingleThreadExecutor();
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_statistics, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();

        //just disabled the navigation bar
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> provider = ProcessCameraProvider.getInstance(this);
        imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        provider.addListener(new Runnable() {
            @Override
            public void run() {
                ProcessCameraProvider cameraProvider = null;
                try {
                    cameraProvider = provider.get();
                    //Preview preview = new Preview.Builder().build();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                imageCapture = new ImageCapture.Builder().build();
                if(cameraProvider != null){
                    bindImageAnalysis(cameraProvider);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePhoto(){
        //ImageCapture imageCapture = new ImageCapture.Builder().build();
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        content.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            content.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");
        }


        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                System.out.println("IMAGE CAPTURED");
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                System.out.println("IMAGE ERROR");
            }
        });


        //Writing image into a file to save on android device           -- still have issues
//        //object which contains output file + metadata
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        File file = new File(uri + name + ".jpg");
//            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//        System.out.println("trying to capture image");
//            imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
//                @Override
//                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                    System.out.println("IMAGE CAPTURED");
//                    Log.e("Photo capture successful", outputFileOptions.toString());
//                }
//
//                @Override
//                public void onError(@NonNull ImageCaptureException exception) {
//                    System.out.println("NOT CAPTURED");
//                    Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
//                }
//            });
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(@NonNull ImageProxy image) {
//                image.close();
//            }
//        });
//        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                 // textView.setText(Integer.toString(orientation));    //shows the angle of orientation for the image
//            }
//        };
//        orientationEventListener.enable();
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        previewView.setScaleType(PreviewView.ScaleType.FILL_START);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());     //shows the preview of the camera for taking the image
        cameraProvider.bindToLifecycle(this, cameraSelector,
                imageCapture, preview);
    }
}