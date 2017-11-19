package com.example.wiccansharing;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.util.Scanner;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toast.makeText(getApplicationContext(),
                "Input the IP address of the server and your username to access files.",
                Toast.LENGTH_SHORT).show(); // Ou long

        // check if a saved username & IP exist
        if(configsE(this)) {
            loadConfigs(this);
            TextView IP_TextBox = findViewById(R.id.IP);
            //IP_TextBox.setText("LOL");
        }

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onClickLogin(View view) {

    }

    private String IPAddr = "";
    private String UsrN = "";

    public boolean configsE(Context context) {
        return true;
    }

    public void loadConfigs(Context context) {
        FileInputStream inputStream;
        try {
            inputStream = openFileInput("settings");
            Scanner scanner = new Scanner(inputStream);
            IPAddr = scanner.nextLine();
            UsrN = scanner.nextLine();
            inputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, "Unknown error. Could not load login settings.", Toast.LENGTH_LONG).show();
        }
    }

    public void createFile(Context context, String contents) {
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput("settings", Context.MODE_PRIVATE);
            outputStream.write(contents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, "Unknown error. Could not save login settings.", Toast.LENGTH_LONG).show();
        }
    }
}

