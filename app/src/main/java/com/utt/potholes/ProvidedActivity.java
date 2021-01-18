package com.utt.potholes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.utt.API.API_clients;
import com.utt.API_Interface.Pothole_Interface;
import com.utt.Database.SQLite;
import com.utt.model.DataPothole;
import com.utt.model.Pothole;
import com.utt.model.ResponseData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProvidedActivity extends AppCompatActivity {

    private ImageButton imbtnChooseFile, imbtnOpenCamera, imbtnClearImage;
    private EditText edName, edEmail, edFilePath, edNote;
    private TextView txtLatitude, txtLongitude;
    private ImageView imResult;
    private Button btnSend;
    private LinearLayout layoutImage;

    Double latitude, longitude;

    //upload
    Button btpic, btnup;
    private Uri fileUri;
    String picturePath, email, name;
    Uri selectedImage;
    Bitmap photo;
    String ba1;
    public static String URL = "Paste your URL here";
    private ArrayList<DataPothole> dataPotholeList = new ArrayList<>();
    private ResponseData pothole;
    private String TAG="ProviderActivity";
    private AlertDialog alertDialog;
    private String imagePath, note;
    private static int REQUEST_CODE_CAMERA = 100;
    private static int REQUEST_CODE_FOLDER = 200;
    private static final int MY_CAMERA_REQUEST_CODE = 10;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provided);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initLayout();

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        if (email != null && name!=null){
            edName.setText(name);
            edEmail.setText(email);
        }else{
            Log.d("email and name", "null");
        }
        latitude = intent.getDoubleExtra("latitude",0);
        longitude = intent.getDoubleExtra("longitude", 0);
        if (latitude!=null && longitude != 0){
            txtLatitude.setText(latitude.toString()+"");
            txtLongitude.setText(longitude.toString()+"");
        }else{
            Log.e("get locaton", "null");
        }

        imbtnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCheckImage();
            }
        });

        note = edNote.getText().toString();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        imbtnClearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imResult!=null){
                    imResult.setImageResource(0);
                    layoutImage.setVisibility(View.GONE);
                }
            }
        });
    }
    private void initLayout(){
        imbtnChooseFile = (ImageButton) findViewById(R.id.imgChooseFile);
        imbtnOpenCamera = (ImageButton) findViewById(R.id.imgOpenCamera);
        edName = (EditText) findViewById(R.id.editName);
        edEmail = (EditText) findViewById(R.id.editEmail);
        edFilePath = (EditText) findViewById(R.id.editFilePath);
        edNote = (EditText) findViewById(R.id.editNote);
        btnSend = (Button) findViewById(R.id.btnSend);
        imbtnClearImage = (ImageButton) findViewById(R.id.imClearImage);
        imResult = (ImageView) findViewById(R.id.imgResult);
        layoutImage = (LinearLayout) findViewById(R.id.layoutImage);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
    }

    private void openDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProvidedActivity.this);
        builder.setMessage("Do you want to push data?")
                .setIcon(R.drawable.ic_check)
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        note = edNote.getText().toString();
                        //new ProvidedActivity.uploadDataPothole().execute();
                        pushData(name, email, latitude, longitude, imagePath, note);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Cancel");
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void openCheckImage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProvidedActivity.this);
        builder.setMessage("Choose type")
                .setIcon(R.drawable.ic_check)
                .setCancelable(false)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    }
                })
                .setNegativeButton("Open folder", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        Intent result = Intent.createChooser(intent,"Choose Image");
                        startActivityForResult(result,REQUEST_CODE_FOLDER);
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    class uploadDataPothole extends AsyncTask<Void, Void, String> {
        // private ProgressDialog dialog;
        private ProgressDialog dialog = new ProgressDialog(ProvidedActivity.this);

        @Override
        protected void onPreExecute() {
            // This progressbar will load util tast in doInBackground method loads
            dialog = new ProgressDialog(ProvidedActivity.this);
            dialog.setMessage("Please wait ...");
            dialog.setCancelable(true);
            dialog.setTitle("Push data");
            dialog.setIcon(R.drawable.ic_warning);
            dialog.setMax(100);
            dialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            Log.e(TAG, ""+email+ name+ latitude+ longitude+ imagePath+ note);
            //Toast.makeText(getApplicationContext(), ""+email+ name+ latitude+ longitude+ imagePath+ note,Toast.LENGTH_SHORT).show();
            pushData(name, email, latitude, longitude, imagePath, note);
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }

    private void pushData(String name, String email, Double latitude, Double longitude, String imagePath, String note) {
        File file;
        String imgname = "";
        if (imagePath!=null){
            file = new File(imagePath);
            RequestBody photoContent = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            MultipartBody.Part photo = MultipartBody.Part.createFormData("image",file.getName(),photoContent);
            Log.e(TAG, ""+email+ name+ latitude+ longitude+ photo+ note);
            imgname = file.getName();
        }
        Log.e(TAG, ""+email+ name+ latitude+ longitude+ imgname+ note);
        Pothole_Interface service = API_clients.getClient().create(Pothole_Interface.class);
        Call<ResponseData> userCall = service.postAddPotholee(name, email, latitude, longitude, imgname, note);
        userCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                        //Log.e(TAG, ""+response.body().getCode());
                        //Toast.makeText(getApplicationContext(), "",Toast.LENGTH_SHORT).show();
                        pothole = response.body();

                        View view = findViewById(R.id.btnSend);
                        Snackbar.make(view, ""+ pothole.getMessage(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                }else {
                    Log.e(TAG, "Upload failure!");
                }

            }
            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(),contentUri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "camera permission granted");
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null){
            if (requestCode == REQUEST_CODE_CAMERA) {
//                Uri uri = data.getData();
//                imagePath = getRealPathFromURI(uri);
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imResult.setImageBitmap(bitmap);
                layoutImage.setVisibility(View.VISIBLE);
                edFilePath.setText("");
            }
            else if (requestCode == REQUEST_CODE_FOLDER){
                Uri uri = data.getData();
                imagePath = getRealPathFromURI(uri);
                imResult.setImageURI(uri);
                layoutImage.setVisibility(View.VISIBLE);
                edFilePath.setText(imagePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_provided, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(ProvidedActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }
}


