package com.example.wiccansharing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.util.Locale;

public class FilesList extends AppCompatActivity {
    public static FTPClient ftpClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_fileslist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FileFetchTask fileFetchTask = new FileFetchTask();
        fileFetchTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileFetchTask fileFetchTask = new FileFetchTask();
        fileFetchTask.execute();
    }

    public void onClickPowerOff(View view) {
        // Mostrar diálogo
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(getString(R.string.leave_confirmation));
        dlgAlert.setTitle(getString(R.string.app_name));
        dlgAlert.setPositiveButton(getString(R.string.dialog_yes), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Voltar
                CloseConnectionTask closeConnectionTask = new CloseConnectionTask();
                closeConnectionTask.execute();
            }
        });
        dlgAlert.setNegativeButton(getString(R.string.dialog_no), null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void onClickUpload(View view) {
        showFileChooser();
    }

    public void onBackPressed() {
        // Mostrar diálogo
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(getString(R.string.leave_warning));
        dlgAlert.setTitle(getString(R.string.app_name));
        dlgAlert.setPositiveButton(getString(R.string.dialog_yes), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Voltar
                CloseConnectionTask closeConnectionTask = new CloseConnectionTask();
                closeConnectionTask.execute();
            }
        });
        dlgAlert.setNegativeButton(getString(R.string.dialog_no), null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");      //all files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 20); // FILE_SELECT_CODE ???
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public class FileFetchTask extends AsyncTask<Void, Void, FTPFile[]> {

        @Override
        protected FTPFile[] doInBackground(Void... params) {
            try {
                return ftpClient.listFiles();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(final FTPFile[] result) {
            Context AppContext = getBaseContext();
            if (result == null) {
                Toast.makeText(AppContext, getString(R.string.file_worker_fail), Toast.LENGTH_LONG).show();
                CloseConnectionTask closeConnectionTask = new CloseConnectionTask();
                closeConnectionTask.execute();
            } else {

                ListView listView = findViewById(R.id.sampleListView);
                /*
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(getApplicationContext(),
                                "Click ListItem Number " + position, Toast.LENGTH_LONG)
                                .show();
                    }
                });
                */
                String[] fileNames = new String[result.length];
                String[] fileDescriptions = new String[result.length];
                for (int i = 0; i < result.length; i++) {
                    fileNames[i] = result[i].getName();
                    fileDescriptions[i] = String.format("%s,%s", result[i].getSize(), result[i].isDirectory());
                }
                FileListAdapter fileListAdapter = new FileListAdapter(AppContext, fileNames, fileDescriptions);
                listView.setAdapter(fileListAdapter);
            }
        }
    }

    public class CloseConnectionTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ftpClient.disconnect();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {
            Context AppContext = getBaseContext();
            ftpClient = null;
            if (!exit_status)
                Toast.makeText(AppContext, getString(R.string.disconnect_fail), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AppContext, Login.class);
            startActivity(intent);
            finish();
        }
    }

    public class FileListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] fileNames;
        private final String[] fileDescriptions;

        public FileListAdapter(Context context, String[] fileNames, String[] fileDescriptions) {
            super(context, -1, fileNames);
            this.context = context;
            this.fileNames = fileNames;
            this.fileDescriptions = fileDescriptions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.filelist_rowlayout, parent, false);
            TextView fileName = rowView.findViewById(R.id.fileName);
            TextView fileDescription = rowView.findViewById(R.id.fileDescription);
            ImageView fileTypeIcon = rowView.findViewById(R.id.file_type_icon);
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileNames[position]);
            int resourceID = context.getResources()
                    .getIdentifier(extension, "drawable", context.getPackageName());
            fileName.setText(fileNames[position]);
            String[] fileData = fileDescriptions[position].split(",");
            int size = Integer.parseInt(fileData[0]);
            boolean isDirectory = Boolean.parseBoolean(fileData[1]);
            fileDescription.setText(null);
            if (!isDirectory) {
                fileTypeIcon.setImageResource(resourceID != 0 ? resourceID : R.drawable._blank);
                fileDescription.setText(String.format(getString(R.string.file_size_format), size / 1000));
            } else
                fileTypeIcon.setImageResource(R.drawable.folder);
            return rowView;
        }
    }
}
