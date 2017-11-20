package com.example.wiccansharing;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.os.Handler;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
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
        String IIP = IP_TextBox.getText().toString();
        String IUser = user_TextBox.getText().toString();

        IP_TextBox.setError(null);
        user_TextBox.setError(null);

        boolean error = false;
        if (!isValidIP(IIP)) {
            IP_TextBox.setError("Invalid IP address.");
            error = true;
        }
        if (!isValidUser(IUser)) {
            user_TextBox.setError("Invalid username.");
            error = true;
        }
        if(error)
            return;

        if (checkBox.isChecked() &&
                !(IPAddr.equals(IIP) &&
                        UsrN.equals(user_TextBox.getText().toString()))) {

            saveConfigs(context, IIP
                    + "\n" + IUser);
            Toast.makeText(context, "Login configuration saved.", Toast.LENGTH_SHORT).show();
            IPAddr = IIP;
            UsrN = IUser;
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

    private boolean configsE(Context context) {
        File settings = new File(context.getFilesDir() + "/settings");
        return settings.exists();
    }

    private void loadConfigs(Context context) {
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

    private void saveConfigs(Context context, String contents) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("settings", Context.MODE_PRIVATE);
            outputStream.write(contents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, "Unknown error. Could not save login settings.", Toast.LENGTH_LONG).show();
        }
    }


    private static final String IP_Pattern =
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private static final String user_Pattern = "^[a-zA-Z0-9]+([a-zA-Z0-9](_|-| )[a-zA-Z0-9])*[a-zA-Z0-9]*$";
    private static Pattern pattern = null;
    private static Matcher matcher = null;

    private static boolean isValidIP(String IP) {
        pattern = Pattern.compile(IP_Pattern);
        matcher = pattern.matcher(IP);
        return matcher.matches();
    }
    /*
        User names can consist of lowercase and capitals
        User names can consist of alphanumeric characters
        User names can consist of underscore and hyphens and spaces
        Cannot be two underscores, two hyphens or two spaces in a row
        Cannot have a underscore, hyphens or space at the start or end
        Must consist of exactly or less than 20 characters
    */
    private boolean isValidUser(String user){
        pattern = Pattern.compile(user_Pattern);
        matcher = pattern.matcher(user);
        return matcher.matches() && user.length() <= 20;
    }
}

