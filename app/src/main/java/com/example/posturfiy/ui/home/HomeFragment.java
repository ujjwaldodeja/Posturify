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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.posturfiy.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int CAPTURE_IMAGE = 0;
    private FragmentHomeBinding binding;
    private ImageView imageView;
    private Context mContext;
    private String PATH;
    private List<String> picturesTaken = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
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
}