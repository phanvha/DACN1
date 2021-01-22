package com.utt.potholes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.utt.Adapter.RecyclerViewAdapter;
import com.utt.model.Notify;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Notify> datalist = new ArrayList<>();
    private SharedPreferences pref;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        Double minDistance = intent.getDoubleExtra("minDistance",1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());


        datalist.add(new Notify(1 ,"Cảnh báo nguy hiểm", currentDateandTime,"Điểm đường xấu cách bạn khoảng "+minDistance+" m. Hãy cẩn thận"));
        datalist.add(new Notify(2 ,"Phản hồi ý kiến", currentDateandTime,"Gửi ý kiến đóng góp của bạn để chúng tôi có thể phát triển ứng dụng tốt hơn"));


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(datalist, NotificationActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));

//        initLayout();
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
//                Intent intent = new Intent(ProvidedActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


        }
//        if (item.getItemId() == android.R.id.home) {
//            finish(); // close this activity and return to preview activity (if there is any)
//        }

        return super.onOptionsItemSelected(item);
    }
}
