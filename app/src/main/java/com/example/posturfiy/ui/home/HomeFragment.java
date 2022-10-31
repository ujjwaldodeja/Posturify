package com.example.posturfiy.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.posturfiy.R;
import com.example.posturfiy.databinding.FragmentHomeBinding;
import com.example.posturfiy.ui.database.SQLiteManager;
import com.example.posturfiy.ui.database.place.Place;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final int CAPTURE_IMAGE = 0;
    private FragmentHomeBinding binding;
    private ImageView imageView;
    private Context mContext;
    private String PATH;
    private List<String> picturesTaken = new ArrayList<>();

    private Spinner spinner;
    public static String nameChosenByUser;

    private Classifier classifier;
    private Instances collectedData;
    private Attributes readings = new Attributes();
    private HashMap<String, Integer> recordedAcitivities = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(getContext());
        sqLiteManager.populatePlaceListArray();
        sqLiteManager.populateRecordListArray();

        ArrayAdapter<String> adapter = null;

        spinner = (Spinner) root.findViewById(R.id.spinner);
        if (Place.getPlacesNames().size() == 0) {
            adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1);
        } else {
            adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    Place.getPlacesNames());
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //imageView = (ImageView) root.findViewById(R.id.my_avatar_imageview);
        File storageDir = Environment.getExternalStorageDirectory();
        PATH = storageDir.getAbsolutePath() + "/DCIM/Camera/";
        binding.buttonStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Picture taken");
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        InputStream data = null;
        try {
            data = mContext.getAssets().open("bayesmodel92.model");
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

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    public void takePhoto(){
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, CAPTURE_IMAGE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                       // imageView.setImageBitmap(selectedImage);
                        FileOutputStream out = null;
                        File file = null;
                        try {
                        file = createFileInstance();
                            out = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        boolean saved = selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        if(saved == true){
                            picturesTaken.add(file.getName());
                            System.out.println(picturesTaken);
                        }
                        System.out.println("Image Compressed ==" + saved);
                        ListFiles(file.getPath());
                        start();
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    public void startCamera(){
    }

    public void ListFiles(String path){
        System.out.println(Arrays.toString(new File(path).listFiles()));
    }

    public void start() {
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                        .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);
        InputStream instr = null;
        try {
            for (int i = 0; i < picturesTaken.size(); i++) {
                File initialFile = new File(PATH + picturesTaken.get(i));
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void evaluateData() {
        try {
            System.out.println("Starting evaluation");
            for (int i = 0; i < collectedData.size(); i++) {
                double result = classifier.classifyInstance(collectedData.instance(i));
                String activity = readings.getClasses().get(new Double (result).intValue());

                System.out.println(activity);
//                if (recordedAcitivities.containsKey(activity)) {
//                    int count = recordedAcitivities.get(activity);
//                    recordedAcitivities.remove(activity);
//                    recordedAcitivities.put(activity, count + 1);
//                } else {
//                    recordedAcitivities.put(activity, 1);
//                }
            }
//            showResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void showResults() {
//        int max = 0;
//        String act = "";
//        for (String key : recordedAcitivities.keySet()) {
//            int value = recordedAcitivities.get(key);
//            if (value > max) {
//                max = value;
//                act = key;
//            }
//        }
//        int straight = 0, right = 0, left = 0;
//        if (recordedAcitivities.containsKey("straight")) {
//            straight = recordedAcitivities.get("straight");
//        }
//        if (recordedAcitivities.containsKey("right")) {
//            right = recordedAcitivities.get("right");
//        }
//        if (recordedAcitivities.containsKey("left")) {
//            left = recordedAcitivities.get("left");
//        }
//        System.out.println(straight);
//        System.out.println(recordedAcitivities.toString());
//
//    }

    public String getRecordedAcitivities() {
        return recordedAcitivities.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        nameChosenByUser = spinner.getSelectedItem().toString();
        System.out.println(nameChosenByUser + "====================================");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}