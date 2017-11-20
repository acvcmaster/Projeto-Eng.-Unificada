package com.example.wiccansharing;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.os.Handler;

import java.io.*;
import java.util.Scanner;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Toast.makeText(getApplicationContext(),
        //        "Input the IP address of the server and your username to access files.",
        //        Toast.LENGTH_SHORT).show(); // Ou long
        // check if a saved username & IP exist
        if (configsE(this)) {
            loadConfigs(this);
            TextView IP_TextBox = findViewById(R.id.IP);
            TextView user_TextBox = findViewById(R.id.editText);
            CheckBox checkBox = findViewById(R.id.SaveUsernameBOX);
            IP_TextBox.setText(IPAddr);
            user_TextBox.setText(UsrN);
            checkBox.setChecked(true);
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
        Context context = view.getContext();
        CheckBox checkBox = findViewById(R.id.SaveUsernameBOX);
        TextView IP_TextBox = findViewById(R.id.IP);
        TextView user_TextBox = findViewById(R.id.editText);

        // checar por número e ip e usuário válido
        if (checkBox.isChecked() &&
                !(IPAddr.equals(IP_TextBox.getText().toString()) &&
                        UsrN.equals(user_TextBox.getText().toString()))) {

            saveConfigs(context, IP_TextBox.getText()
                    + "\n" + user_TextBox.getText());
            Toast.makeText(context, "Login configuration saved.", Toast.LENGTH_SHORT).show();
            IPAddr = IP_TextBox.getText().toString();
            UsrN = user_TextBox.getText().toString();
        }
        if (!checkBox.isChecked()) {
            File settings = new File(context.getFilesDir() + "/settings");
            try {
                settings.delete();
            } catch (Exception e) {
                Toast.makeText(context, "Error. Failed to delete login settings data.", Toast.LENGTH_LONG).show();
            }
        }
        // Lógica de login
    }

    private String IPAddr = "";
    private String UsrN = "";

    public boolean configsE(Context context) {
        File settings = new File(context.getFilesDir() + "/settings");
        return settings.exists();
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

    public void saveConfigs(Context context, String contents) {
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

