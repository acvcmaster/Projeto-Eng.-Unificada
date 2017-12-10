package com.example.wiccansharing;

import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;


public class LogoDisplay extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_logo_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(new DelayedTask(this), 1200);
    }
    protected void onHandlerDone()
    {
        // clear tmp files
        File tmpDir = new File(getCacheDir() + "/" + getString(R.string.tmp_folder));
        if(tmpDir.exists())
            tmpDir.delete();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
    public void onBackPressed() {
    }
}

class DelayedTask implements Runnable
{
    LogoDisplay context;
    DelayedTask(LogoDisplay _context)
    {
        this.context = _context;
    }
    public void run()
    {
        context.onHandlerDone();
    }
}