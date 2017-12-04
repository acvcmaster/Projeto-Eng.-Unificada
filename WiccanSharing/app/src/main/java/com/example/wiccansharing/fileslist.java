package com.example.wiccansharing;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.commons.net.ftp.FTPClient;

public class fileslist extends AppCompatActivity {
    public static FTPClient ftpClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileslist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ftpClient = null;
    }

    public void onBackPressed() {
        // Mostrar diálogo
    }
}
