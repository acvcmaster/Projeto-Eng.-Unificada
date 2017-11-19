package com.example.wiccansharing;

import android.os.Handler;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class LogoDisplay extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_display);
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
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
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