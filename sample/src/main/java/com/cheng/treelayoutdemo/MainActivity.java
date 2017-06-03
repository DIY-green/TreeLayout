package com.cheng.treelayoutdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_directory_tree:
                overlay(DirectoryTreeActivity.class);
                break;
            case R.id.btn_custom_attribute:
                overlay(CustomAttributeActivity.class);
                break;
        }
    }

    private void overlay(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
