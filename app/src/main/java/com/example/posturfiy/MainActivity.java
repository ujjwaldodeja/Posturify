package com.example.posturfiy;

import android.content.ClipData;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.posturfiy.recognition.PoseClassifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.posturfiy.databinding.ActivityMainBinding;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private ClipData.Item settings;
    private Button mapButton;
    private Button statButton;
    private static final String TAG = "PoseClassifier";
    private static final int MAX_DISTANCE_TOP_K = 30;
    private static final int MEAN_DISTANCE_TOP_K = 10;
    // Note Z has a lower weight as it is generally less accurate than X & Y.
    private static final PointF3D AXES_WEIGHTS = PointF3D.from(1, 1, 0.2f);
    private static final String POSE_SAMPLES_FILE = ".csv";
    private PoseClassifier poseClassifier;
    //private Classifier classifier;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_statistics, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        InputStream data = null;
//        try {
//            data = getAssets().open("jfoureight.model");
//            //classifier = (Classifier) SerializationHelper.read(data);
//            System.out.println("Model is trained");
//            //checkInstance();
////            collectedData = new Instances("TestInstances",readings.getAttributeList() , 1);
////            collectedData.setClassIndex(collectedData.numAttributes()-1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }


    public void start() {
//        Not accurate detector
//        PoseDetectorOptions options =
//                new PoseDetectorOptions.Builder()
//                        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
//                        .build();
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);
        InputStream instr = null;
        try {
            AssetManager assets = getAssets();
            instr = assets.open("DI.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(instr);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        if (image != null) {
            System.out.println("Image was read");
            Task<Pose> result =
                    poseDetector.process(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<Pose>() {
                                        @Override
                                        public void onSuccess(Pose pose) {
                                            // Task completed successfully
                                            System.out.println("Success");
                                            System.out.println(pose.getAllPoseLandmarks());
                                            // ...
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            System.out.println("Failure");
                                        }
                                    });
            Pose pose = result.getResult();
            //List<PoseLandmark> allPoseLandmarks = pose.getAllPoseLandmarks();

            PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
            PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
            PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
            PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);

        } else {
            System.out.println("Image was not read");
        }
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

}