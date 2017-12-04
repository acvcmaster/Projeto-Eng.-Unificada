package com.example.wiccansharing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.os.Handler;

import java.io.*;
import java.text.Format;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.leave_toast), Toast.LENGTH_SHORT).show();

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
            IP_TextBox.setError(getString(R.string.ip_invalid));
            error = true;
        }
        if (!isValidUser(IUser)) {
            user_TextBox.setError(getString(R.string.username_invalid));
            error = true;
        }
        if (error)
            return;

        if (checkBox.isChecked() &&
                !(IPAddr.equals(IIP) &&
                        UsrN.equals(user_TextBox.getText().toString()))) {

            saveConfigs(context, IIP
                    + "\n" + IUser);
            Toast.makeText(context, getString(R.string.login_info_saved), Toast.LENGTH_SHORT).show();
            IPAddr = IIP;
            UsrN = IUser;
        }
        if (!checkBox.isChecked()) {
            File settings = new File(context.getFilesDir() + "/" + getString(R.string.settings_file));
            try {
                settings.delete();
            } catch (Exception e) {
                Toast.makeText(context, getString(R.string.login_info_delete_err), Toast.LENGTH_LONG).show();
            }
        }
        // Lógica de login
        BackgroundConnectTask attemptConnection = new BackgroundConnectTask(this,
                IPAddr, UsrN, getString(R.string.default_ftp_password), 2121);
        attemptConnection.execute();
    }

    private boolean configsE(Context context) {
        File settings = new File(context.getFilesDir() + "/" + getString(R.string.settings_file));
        return settings.exists();
    }
    private void loadConfigs(Context context) {
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(getString(R.string.settings_file));
            Scanner scanner = new Scanner(inputStream);
            IPAddr = scanner.nextLine();
            UsrN = scanner.nextLine();
            inputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.login_info_load_err), Toast.LENGTH_LONG).show();
        }
    }
    private void saveConfigs(Context context, String contents) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(getString(R.string.settings_file), Context.MODE_PRIVATE);
            outputStream.write(contents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.login_info_save_err), Toast.LENGTH_LONG).show();
        }
    }
    public class BackgroundConnectTask extends AsyncTask<Void, Void, Boolean> {

        private final String Hostname;
        private final int Port;
        private final String Username;
        private final String Password;
        private final Context AppContext;

        BackgroundConnectTask(Context context, String host, String username, String password, int port) {
            AppContext = context;
            Hostname = host;
            Username = username;
            Password = password;
            Port = port;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            for(int i = 0; i < MAX_LOGIN_ATTEMPTS;)
            {
                try
                {
                    Thread.sleep(400);
                    ftpClient = new FTPClient();
                    ftpClient.connect(Hostname, Port);
                    return true;
                }
                catch (Exception e){
                    i++;
                }
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected void onPostExecute(final Boolean exit_status) {
            // mostrar toast ou continuar
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.ftp_socket_error), IPAddr), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
            else {
                BackgroundLoginTask attemptLogin = new BackgroundLoginTask(AppContext, Username, Password);
                attemptLogin.execute();
            }
        }
        protected void onCancelled() {
            showProgress(false);
        }
    }
    public class BackgroundLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String Username;
        private final String Password;
        private final Context AppContext;

        BackgroundLoginTask(Context context, String username, String password) {
            AppContext = context;
            Username = username;
            Password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return ftpClient.login(Username, Password);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean exit_status) {
            showProgress(false);
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.login_username_invalid), IPAddr), Toast.LENGTH_LONG).show();
            }
            else {
                // continuar (prox. activity)
                Intent intent = new Intent(AppContext, fileslist.class);
                fileslist.ftpClient = ftpClient;
                startActivity(intent);
            }
        }
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private FTPClient ftpClient;
    private static final String IP_Pattern =
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private static final String user_Pattern = "^[a-zA-Z0-9]+([a-zA-Z0-9](_|-| )[a-zA-Z0-9])*[a-zA-Z0-9]*$";
    private static Pattern pattern = null;
    private static Matcher matcher = null;
    private boolean doubleBackToExitPressedOnce = false;
    private final int MAX_LOGIN_ATTEMPTS = 5;
    private String IPAddr = "";
    private String UsrN = "";

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
    private static boolean isValidUser(String user) {
        pattern = Pattern.compile(user_Pattern);
        matcher = pattern.matcher(user);
        return matcher.matches() && user.length() <= 20;
    }
    private void showProgress(Boolean visible)
    {
        ProgressBar loginProgress = findViewById(R.id.loginProgress);
        loginProgress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}

