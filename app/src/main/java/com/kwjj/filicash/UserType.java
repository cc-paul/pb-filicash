package com.kwjj.filicash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwjj.filicash.rv_history.transactionAdapter;
import com.kwjj.filicash.rv_history.transactionData;
import com.kwjj.filicash.rv_usertype.userAdapter;
import com.kwjj.filicash.rv_usertype.userData;

import java.util.ArrayList;

public class UserType extends AppCompatActivity {
    RecyclerView rvUserType;
    TextView tvRecords;
    LinearLayout lnBack;

    private RecyclerView.Adapter adapter;

    ArrayList<String> userList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        rvUserType = findViewById(R.id.rvUserType);
        tvRecords = findViewById(R.id.tvRecords);
        lnBack = findViewById(R.id.lnBack);

        lnBack.setOnClickListener(
                view -> {
                    super.onBackPressed();
                }
        );

        userList = getIntent().getStringArrayListExtra("userTypeList");
        loadUserType();
    }

    public void getBack() {
        lnBack.performClick();
    }

    private void loadUserType() {
        ArrayList<userData> list = new ArrayList<>();
        list.clear();

        for (int i = 0; i < userList.size(); i++) {
            String[] currentRow = userList.get(i).split("~");
            String userType = currentRow[0];
            String discount = currentRow[1];
            Boolean isDriverNeeded = Boolean.parseBoolean(currentRow[2]);
            String id = currentRow[1];

            list.add(new userData(userType,discount,isDriverNeeded,id));
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvUserType.setLayoutManager(mLayoutManager);
        tvRecords.setText("Total Records : " + userList.size());

        adapter = new userAdapter(list);
        rvUserType.setAdapter(adapter);
    }
}