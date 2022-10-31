package com.example.posturfiy.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.posturfiy.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private ImageAnalysis imageAnalysis;
    private ImageCapture imageCapture;
    private Button button;
    private List<String> picturesTaken = new ArrayList<>();
    private Context context;
    private String PATH;
    private OurClassifier classifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        startCamera();
        button = findViewById(R.id.buttonMap);
        context = this.getApplicationContext();
        PATH =  context.getExternalCacheDir() + "/";
        classifier = new OurClassifier(context, PATH);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Picture taken");
                takePhoto();
            }
        });
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

    private File createFileInstance() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";// mContext.getExternalCacheDir();

        File image = new File(PATH + imageFileName +".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        String ImagePath = image.getAbsolutePath();

        System.out.println(ImagePath);
        return image;
    }

    public void takePhoto(){
        ImageCapture copy = imageCapture;
        File file = null;
        try {
            file = createFileInstance();
            System.out.println("fileCreated");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(file!=null) {
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(file).build();
            System.out.println("builder ready");
            File finalFile = file;
            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                            // insert your code here.
                            System.out.println("Fuck this shit");
                            picturesTaken.add(finalFile.getName());
                            System.out.println("Trying to classify file:" + finalFile.getName());
                            classifier.classify(finalFile.getName());
                            System.out.println(outputFileResults.toString());

                        }

                        @Override
                        public void onError(ImageCaptureException error) {
                            // insert your code here.
                            System.out.println("really fucked up");
                            System.out.println(error);
                        }
                    }
            );
        }
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                image.close();
            }
        });
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                //   textView.setText(Integer.toString(orientation));    //shows the angle of orientation for the image
            }
        };
        orientationEventListener.enable();
        // Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
        // previewView.setScaleType(PreviewView.ScaleType.FILL_START);
        // preview.setSurfaceProvider(previewView.getSurfaceProvider());     //shows the preview of the camera for taking the image
        cameraProvider.bindToLifecycle(this, cameraSelector,
                imageCapture);
    }

}
