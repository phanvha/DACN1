package com.utt.potholes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class CameraActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "mobilenet_v1_1.0_224_quant.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels_mobilenet_quant_v1_224.txt";
    private static final int INPUT_SIZE = 224;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_CODE_CAMERA = 123;
    int REQUEST_CODE_FOLDER = 456;


    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnCapture, btnToggleCamera;
    private ImageView imgBottomSheet, imgBack, imgChooseImage;
    private CameraView cameraView;
    LinearLayout layout;
    String stringURL;


    private String imagePath;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Uri urii;
    private ImageView imgScanner;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initLayout();


        final Intent intent = getIntent();
        stringURL = intent.getStringExtra("url");


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                bitmap = cameraKitImage.getBitmap();
//                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

//                imgBottomSheet.setImageBitmap(bitmap);
                if (bitmap!=null){
                    openBottomDialog(null,bitmap);
                }


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                Intent result = Intent.createChooser(intent,"Choose Image");
                startActivityForResult(result,REQUEST_CODE_FOLDER);
            }
        });
    }

    private void initLayout() {
        cameraView = findViewById(R.id.cameraView);
        imgBottomSheet = findViewById(R.id.imgBottomSheet);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnCapture = findViewById(R.id.btnCapture);
        imgChooseImage = (ImageView)findViewById(R.id.imgChooseImage);

    }

    //open bottom dialog
    private void openBottomDialog(final Uri uri, final Bitmap bitmapppp){
        bottomSheetDialog  = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(CameraActivity.this)
                .inflate(
                        R.layout.layout_bottom_sheets,
                        (RelativeLayout) findViewById(R.id.bottomSheetLayout)
                );

        ImageView imageView = bottomSheetView.findViewById(R.id.imgBottomSheet);
        if (uri!=null){
            urii = uri;
            imageView.setImageURI(uri);
        }
        if (bitmapppp != null){
            imageView.setImageBitmap(bitmapppp);
        }


        bottomSheetView.findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapppp!=null){
                    Log.e("bitmap","my check");
                    Intent intent = new Intent(CameraActivity.this, ProvidedActivity.class);
                    intent.putExtra("Image", bitmapppp);
                    startActivity(intent);
                    finish();

                }
                if (urii!=null){
                    String path = getRealPathFromURI(urii);
                    Bitmap bitmapp = decodeUriToBitmap(getApplicationContext(), urii);

                    if (bitmapp!=null){
                        Intent intent = new Intent(CameraActivity.this, ProvidedActivity.class);
                        intent.putExtra("Image", bitmapp);
                        intent.putExtra("FilePath", path);
                        startActivity(intent);
                    }else{
                        Log.e("bitmapp","null");
                    }
//                    detectImageColor(loadBitmap(imagePath));
                }else{
                    Log.e("uri","xyz");

                }
            }
        });
        bottomSheetView.findViewById(R.id.imgBackBottomSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            try {
                final Uri uri = data.getData();
                openBottomDialog(uri,null);
            }catch (Exception e){
                e.printStackTrace();
            }
//                imageViewResult.setImageURI(uri);

        }

    }
    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this,contentUri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                cameraView.start();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getApplicationContext(), "Camera Permission is required",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }

        }).check();

    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Bitmap loadBitmap(String url){
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

}
