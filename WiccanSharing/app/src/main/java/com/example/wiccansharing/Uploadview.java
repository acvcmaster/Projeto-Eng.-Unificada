package com.example.wiccansharing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Uploadview extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadview);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if(resultCode == RESULT_OK)
                {
                    EditText editText = findViewById(R.id.editText2);
                    editText.setText(data.getData().getPath());
                    return;
                }
                break;
            default:
                break;
        }
    }

    public void onClickBack(View view) {
        super.onBackPressed();
    }

    public void onClickEnter(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");      //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.file_get_dialog)), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, getString(R.string.manager_not_found_err), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickUpload(View view) {
        EditText editText = findViewById(R.id.editText2);
        UploadTask uploadTask = new UploadTask(editText.getText().toString());
        uploadTask.execute();
    }

    public class UploadTask extends AsyncTask<Void, Void, Boolean> {
        String file;

        public UploadTask(String file) {
            this.file = file;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {
            Context AppContext = getBaseContext();
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.upload_fail), file), Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(AppContext, String.format(getString(R.string.upload_succeeded), file), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
