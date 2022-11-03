package com.example.posturfiy.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.posturfiy.ui.database.SQLiteManager;
import com.example.posturfiy.ui.database.place.Place;
import com.example.posturfiy.ui.database.record.Record;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class OurClassifier {
    private Classifier classifier;
    private Instances collectedData;
    private Attributes readings = new Attributes();
    private HashMap<String, Integer> recordedAcitivities = new HashMap<>();
    private Context mContext;
    private String PATH;

    public OurClassifier(Context context, String PATH){
        mContext = context;
        this.PATH = PATH;
        InputStream data = null;
        try {
            data = mContext.getAssets().open("v2LMT-90 (N=52).model");
            classifier = (Classifier) SerializationHelper.read(data);
            System.out.println("Model is trained");
            //checkInstance();
            collectedData = new Instances("TestInstances",readings.getAttributeList() , 1);
            collectedData.setClassIndex(collectedData.numAttributes()-1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void classify(String name) {
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);
        InputStream instr = null;
        try {
                File initialFile = new File(PATH + name);
                instr = new FileInputStream(initialFile);
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
                                                    float lShoulderX = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x;
                                                    float lShoulderY = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y;
                                                    float rShoulderX = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x;
                                                    float rShoulderY = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y;
                                                    float lHipX = pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x;
                                                    float rHipX = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x;

                                                    float leftKurtosisAvg = (lShoulderX - lHipX) / image.getWidth();
                                                    float rightKurtosisAvg = (rShoulderX - rHipX) / image.getWidth();
                                                    float topDiffAvg = (lShoulderY - rShoulderY) / image.getHeight();

                                                    collectedData.setClassIndex(collectedData.numAttributes() - 1);
                                                    DenseInstance instance = new DenseInstance(collectedData.numAttributes());
                                                    instance.setValue(readings.LEFT_DIFF, leftKurtosisAvg);
                                                    instance.setValue(readings.RIGHT_DIFF, rightKurtosisAvg);
                                                    instance.setValue(readings.VERT_DIFF, topDiffAvg);
                                                    collectedData.add(instance);
                                                    System.out.println(collectedData);
                                                    evaluateData();
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
                } else {
                    System.out.println("Image was not read");
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void evaluateData() {
        try {
            System.out.println("Starting evaluation");

                double result = classifier.classifyInstance(collectedData.instance(0));
                String activity = readings.getClasses().get(new Double (result).intValue());
                if (HomeFragment.placeChosen != null) {
                    int recordId = Place.getIdFromPlaceName(HomeFragment.placeChosen);
                    Record newRecord = new Record(
                            Record.recordsList.size(),
                            recordId,
                            activity,
                            new Timestamp(new Date().getTime()));
                    Record.recordsList.add(newRecord);
                    SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(mContext);
                    sqLiteManager.addRecordToDatabase(newRecord);
                }
                System.out.println("RECORD WAS SAVED================");
                System.out.println(activity);
                collectedData.remove(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getRecordedAcitivities() {
        return recordedAcitivities.toString();
    }
}
