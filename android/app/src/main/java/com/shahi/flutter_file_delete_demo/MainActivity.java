package com.shahi.flutter_file_delete_demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity implements MethodChannel.MethodCallHandler {

    private MethodChannel.Result resultReturn;

    private int x = 0;
    private List<String> imagePathList = new ArrayList<String>();

    private ArrayList<String> mimes = new ArrayList<>();

    private static final int PERMISSION_ALL = 402;
    private static String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private String fileType;
    private String extraFile;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startFilePick();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MethodChannel methodChannel = new MethodChannel(Objects.requireNonNull(getFlutterEngine())
                .getDartExecutor().getBinaryMessenger(), "test.file.path");
        methodChannel.setMethodCallHandler(this);

        mimes.add("pdf");
        mimes.add("doc");
        mimes.add("docx");
        mimes.add("txt");
    }
    
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        resultReturn = result;
        if (call.method.equals("getPath")) {
            fileType = call.argument("fileType").toString().toLowerCase();
            extraFile = call.argument("extraFile");

            if (!hasPermissions(getContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            } else {
                startFilePick();
            }
        }

    }

    @SuppressLint("InlinedApi")
    private void startFilePick() {
        Intent intent = new Intent();
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + File.separator);

        intent.setType(getFileType(fileType));
        intent.setDataAndType(uri, getFileType(fileType));

        if (extraFile != null) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimes);
        }

        if (fileType.equals("file")) {
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        }

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select"), 10);
    }

    private String getFileType(String accessType) {
        switch (accessType) {
            case "image":
                return "image/*";
            case "audio":
                return "audio/*";
            case "video":
                return "video/*";
            case "file":
                return "application/*";
            case "apk":
                return "application/apk";
            default:
                return "*/*";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            imagePathList.clear();
            if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
                if (data.getClipData() != null) {
                    for (x = 0; x < data.getClipData().getItemCount(); x++) {
                        imagePathList.add(FileManagement.getPath(MainActivity.this, data.getClipData().getItemAt(x).getUri()));
                        Toast.makeText(this, "" + FileManagement.getPath(MainActivity.this, data.getClipData().getItemAt(x).getUri()), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    imagePathList.add(FileManagement.getPath(MainActivity.this, data.getData()));
                }

                resultReturn.success("Total Item Select: " + imagePathList);
            }
        } catch (Exception e) {
            resultReturn.success("Error: " + e.getMessage());
        }
    }
}
