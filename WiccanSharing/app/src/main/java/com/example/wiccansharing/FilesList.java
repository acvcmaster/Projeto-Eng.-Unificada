package com.example.wiccansharing;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class FilesList extends AppCompatActivity {
    public static FTPClient ftpClient = null;
    public static String ftpPath = "/";
    private static String ftpFileTmp = "";
    private static final int LOCAL_DOWNLOAD_PATH_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_fileslist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        ftpPath = "/";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCAL_DOWNLOAD_PATH_CODE:
                if (resultCode == RESULT_OK) {
                    String remoteFilePath = ftpFileTmp;
                    String localDownloadPath = FileUtil.getFullPathFromTreeUri(data.getData(), this);
                    FileDownloadTask fileDownloadTask = new FileDownloadTask(remoteFilePath, localDownloadPath);
                    fileDownloadTask.execute();
                    return;
                }
                break;
            default:
                break;
        }
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
        Intent intent = new Intent(this, Uploadview.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        // Mostrar diálogo
        if (ftpPath.equals("/")) {
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
        } else {
            // Navigate to parent
            navigateBackwards();
            FileFetchTask fileFetchTask = new FileFetchTask();
            fileFetchTask.execute();
        }
    }

    public void onClickNewFolder(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.create_folder_name));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewFolderTask newFolderTask = new NewFolderTask(input.getText().toString());
                newFolderTask.execute();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    public class FileFetchTask extends AsyncTask<Void, Void, FTPFile[]> {

        @Override
        protected FTPFile[] doInBackground(Void... params) {
            try {
                return ftpClient.listFiles(FilesList.ftpPath);
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
                String[] fileNames = new String[result.length];
                String[] fileDescriptions = new String[result.length];
                for (int i = 0; i < result.length; i++) {
                    fileNames[i] = result[i].getName();
                    fileDescriptions[i] = String.format("%s,%s", result[i].getSize(), result[i].isDirectory());
                }
                FileListAdapter fileListAdapter = new FileListAdapter(AppContext, fileNames, fileDescriptions);
                listView.setAdapter(fileListAdapter);
                TextView pathView = findViewById(R.id.textView7);
                pathView.setText(String.format(getString(R.string.file_path_format), ftpPath));
            }
        }
    }

    public class CloseConnectionTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ftpClient.logout();
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
            ftpPath = "/";
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.filelist_rowlayout, parent, false);
            TextView fileName = rowView.findViewById(R.id.fileName);
            TextView fileDescription = rowView.findViewById(R.id.fileDescription);
            ImageView fileTypeIcon = rowView.findViewById(R.id.file_type_icon);
            ImageView downloadButton = rowView.findViewById(R.id.download_button);
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileNames[position]);
            int resourceID = context.getResources()
                    .getIdentifier(extension, "drawable", context.getPackageName());
            fileName.setText(fileNames[position]);
            String[] fileData = fileDescriptions[position].split(",");
            int size = Integer.parseInt(fileData[0]);
            final boolean isDirectory = Boolean.parseBoolean(fileData[1]);
            fileDescription.setText(null);
            if (!isDirectory) {
                fileTypeIcon.setImageResource(resourceID != 0 ? resourceID : R.drawable._blank);
                fileDescription.setText(String.format(getString(R.string.file_size_format), (size / 1000) == 0 ? 1 : size / 1000));
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fileName = fileNames[position];
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        ftpFileTmp = ftpPath + fileName;
                        try {
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.directory_get_dialog)), LOCAL_DOWNLOAD_PATH_CODE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            // Potentially direct the user to the Market with a Dialog
                            Toast.makeText(context, getString(R.string.manager_not_found_err), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                fileTypeIcon.setImageResource(R.drawable.folder);
                downloadButton.setVisibility(View.INVISIBLE);
            }

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // delete file or folder
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(view.getContext());
                    dlgAlert.setMessage(getString(isDirectory ? R.string.message_delete_folder : R.string.message_delete_file));
                    dlgAlert.setTitle(getString(R.string.app_name));
                    dlgAlert.setPositiveButton(getString(R.string.dialog_yes), new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            FileDeleteTask fileDeleteTask = new FileDeleteTask(fileNames[position], isDirectory);
                            fileDeleteTask.execute();
                        }
                    });
                    dlgAlert.setNegativeButton(getString(R.string.dialog_no), null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    return true;
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Navigate inwards it's a folder / download if it's a file
                    String fileName = fileNames[position];
                    if (isDirectory) {
                        ftpPath += fileName + "/";
                        FileFetchTask fileFetchTask = new FileFetchTask();
                        fileFetchTask.execute();
                    } else {
                        // tmp download and open file
                        FileOpenTask fileOpenTask = new FileOpenTask(ftpPath + fileName);
                        fileOpenTask.execute();
                    }
                }
            });
            return rowView;
        }
    }

    public class FileDownloadTask extends AsyncTask<Void, Void, Boolean> {
        private final String remoteFilePath;
        private final String localFilePath;
        private volatile FileOutputStream downloadedFile;

        FileDownloadTask(String remoteFilePath, String localFilePath) {
            this.remoteFilePath = remoteFilePath;
            this.localFilePath = localFilePath;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String fileName = new File(remoteFilePath).getName();
                downloadedFile = new FileOutputStream(String.format("%s/%s", localFilePath, fileName));
                ftpClient.retrieveFile(remoteFilePath, downloadedFile);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {
            Context AppContext = getBaseContext();
            FileFetchTask fileFetchTask = new FileFetchTask();
            fileFetchTask.execute();
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.file_download_err), remoteFilePath, localFilePath), Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(AppContext, getString(R.string.file_download_success), Toast.LENGTH_SHORT).show();
        }
    }

    public class FileOpenTask extends AsyncTask<Void, Void, Boolean> {
        private final String remoteFilePath;
        private volatile FileOutputStream downloadedFile;
        private String filesDir;

        FileOpenTask(String remoteFilePath) {
            this.remoteFilePath = remoteFilePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            filesDir = getBaseContext().getFilesDir().toString();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String fileName = new File(remoteFilePath).getName();
                File tmpFolder = new File(String.format("%s/%s", filesDir, getString(R.string.tmp_folder)));
                if (!tmpFolder.exists())
                    tmpFolder.mkdir();
                else if (!tmpFolder.isDirectory()) {
                    tmpFolder.delete();
                    tmpFolder.mkdir();
                }
                downloadedFile = new FileOutputStream(String.format("%s/%s/%s", filesDir, getString(R.string.tmp_folder), fileName));
                ftpClient.retrieveFile(remoteFilePath, downloadedFile);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {

            Context AppContext = getBaseContext();
            FileFetchTask fileFetchTask = new FileFetchTask();
            fileFetchTask.execute();
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.file_open_err), remoteFilePath), Toast.LENGTH_LONG).show();
                return;
            }
            File tmpFile = new File(String.format("%s/%s/%s",
                    filesDir, getString(R.string.tmp_folder), new File(remoteFilePath).getName()));

            if (!tmpFile.exists()) {
                Toast.makeText(AppContext, getString(R.string.file_open_err_tmp), Toast.LENGTH_LONG).show();
                return;
            }

            MimeTypeMap fileMime = MimeTypeMap.getSingleton();
            Intent viewFile = new Intent(Intent.ACTION_VIEW);
            String Extension = MimeTypeMap.getFileExtensionFromUrl(tmpFile.getPath());
            String mimeType = fileMime.getMimeTypeFromExtension(Extension);
            // Use the FileProvider to get a content URI
            Uri fileUri = null;
            try {
                fileUri = FileProvider.getUriForFile(
                        AppContext,
                        "com.example.wiccansharing.fileprovider",
                        tmpFile);
            } catch (IllegalArgumentException e) {
                Toast.makeText(AppContext, getString(R.string.file_open_err_uri), Toast.LENGTH_LONG).show();
                return;
            }

            viewFile.setDataAndType(fileUri, mimeType);
            viewFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                AppContext.startActivity(viewFile);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(AppContext, getString(R.string.no_suitable_app), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class FileDeleteTask extends AsyncTask<Void, Void, Boolean> {
        String fileName;
        boolean isDirectory;

        public FileDeleteTask(String fileName, boolean isDirectory) {
            this.fileName = fileName;
            this.isDirectory = isDirectory;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (!isDirectory)
                    ftpClient.deleteFile(ftpPath + fileName);
                else
                    ftpClient.removeDirectory(ftpPath + fileName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {
            if (!exit_status) {
                Toast.makeText(getBaseContext(),
                        String.format(getString(isDirectory ? R.string.delete_folder_err : R.string.delete_file_err,
                                fileName)), Toast.LENGTH_LONG).show();
                CloseConnectionTask closeConnectionTask = new CloseConnectionTask();
                closeConnectionTask.execute();
                return;
            }
            FileFetchTask fileFetchTask = new FileFetchTask();
            fileFetchTask.execute();
        }
    }

    public class NewFolderTask extends AsyncTask<Void, Void, Boolean> {
        String folderName;

        public NewFolderTask(String folderName) {
            this.folderName = folderName;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ftpClient.makeDirectory(ftpPath + folderName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exit_status) {
            Context AppContext = getBaseContext();
            if (!exit_status) {
                Toast.makeText(AppContext, String.format(getString(R.string.create_folder_err), ftpPath + folderName), Toast.LENGTH_LONG).show();
                return;
            }
            FileFetchTask fileFetchTask = new FileFetchTask();
            fileFetchTask.execute();
        }
    }

    public static void navigateBackwards() {
        ftpPath = ftpPath.substring(0, ftpPath.lastIndexOf('/') - 1);
        ftpPath = ftpPath.substring(0, ftpPath.lastIndexOf('/') + 1);
    }
}

@SuppressLint("NewApi")
final class FileUtil {

    static String TAG = "TAG";
    private static final String PRIMARY_VOLUME_NAME = "primary";


    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    @NonNull
    public static String getSdCardPath() {
        String sdCardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

        try {
            sdCardDirectory = new File(sdCardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
            //Log.e(TAG, "Could not get SD directory", ioe);
        }
        return sdCardDirectory;
    }


    public static ArrayList<String> getExtSdCardPaths(Context con) {
        ArrayList<String> paths = new ArrayList<String>();
        File[] files = ContextCompat.getExternalFilesDirs(con, "external");
        File firstFile = files[0];
        for (File file : files) {
            if (file != null && !file.equals(firstFile)) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    //Log.w("", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    @Nullable
    public static String getFullPathFromTreeUri(@Nullable final Uri treeUri, Context con) {
        if (treeUri == null) {
            return null;
        }
        String volumePath = FileUtil.getVolumePath(FileUtil.getVolumeIdFromTreeUri(treeUri), con);
        if (volumePath == null) {
            return File.separator;
        }
        if (volumePath.endsWith(File.separator)) {
            volumePath = volumePath.substring(0, volumePath.length() - 1);
        }

        String documentPath = FileUtil.getDocumentPathFromTreeUri(treeUri);
        if (documentPath.endsWith(File.separator)) {
            documentPath = documentPath.substring(0, documentPath.length() - 1);
        }

        if (documentPath.length() > 0) {
            if (documentPath.startsWith(File.separator)) {
                return volumePath + documentPath;
            } else {
                return volumePath + File.separator + documentPath;
            }
        } else {
            return volumePath;
        }
    }


    private static String getVolumePath(final String volumeId, Context con) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null;
        }

        try {
            StorageManager mStorageManager =
                    (StorageManager) con.getSystemService(Context.STORAGE_SERVICE);

            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getUuid = storageVolumeClazz.getMethod("getUuid");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isPrimary = storageVolumeClazz.getMethod("isPrimary");
            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String uuid = (String) getUuid.invoke(storageVolumeElement);
                Boolean primary = (Boolean) isPrimary.invoke(storageVolumeElement);

                // primary volume?
                if (primary && PRIMARY_VOLUME_NAME.equals(volumeId)) {
                    return (String) getPath.invoke(storageVolumeElement);
                }

                // other volumes?
                if (uuid != null) {
                    if (uuid.equals(volumeId)) {
                        return (String) getPath.invoke(storageVolumeElement);
                    }
                }
            }

            // not found.
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getVolumeIdFromTreeUri(final Uri treeUri) {
        final String docId = DocumentsContract.getTreeDocumentId(treeUri);
        final String[] split = docId.split(":");

        if (split.length > 0) {
            return split[0];
        } else {
            return null;
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getDocumentPathFromTreeUri(final Uri treeUri) {
        final String docId = DocumentsContract.getTreeDocumentId(treeUri);
        final String[] split = docId.split(":");
        if ((split.length >= 2) && (split[1] != null)) {
            return split[1];
        } else {
            return File.separator;
        }
    }


}
