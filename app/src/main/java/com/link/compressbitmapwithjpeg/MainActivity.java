package com.link.compressbitmapwithjpeg;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button takePhoto;
    ImageView originalImg;
    TextView originalSize;
    ImageView compressImg;
    TextView compressSize;

    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_FACING_BACK = 0;
    private int cameraFacingType = CAMERA_FACING_BACK;

    private static final String FRONT_EXTRAS_STR = "camerasensortype";
    public static final String TAKE_FILE_PROVIDER = "com.link.compressbitmapwithjpeg.FileProvider";

    private File takeImageFile;
    private File compressImgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takePhoto = findViewById(R.id.take_photo_btn);
        originalImg = findViewById(R.id.original_img);
        originalSize = findViewById(R.id.original_size);
        compressImg = findViewById(R.id.compress_img);
        compressSize = findViewById(R.id.compress_size);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    private void takePhoto(){
        if (!PermissionUtil.checkPermissions(this, this, PermissionUtil.CAMERA_PERMISSIONS
                , PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE)) {
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (cameraFacingType == CAMERA_FACING_FRONT) { //前置摄像头
            takePictureIntent.putExtra(FRONT_EXTRAS_STR, 2);
        } else {
            takePictureIntent.putExtra(FRONT_EXTRAS_STR, 1);
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (existSDCard()) {
                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            }else {
                takeImageFile = Environment.getDataDirectory();
            }
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, TAKE_FILE_PROVIDER, takeImageFile);
                } else {
                    uri = Uri.fromFile(takeImageFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        startActivityForResult(takePictureIntent, 1001);
    }

    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory())
            folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_CAMERA_PERMISSION_CODE:
                takePhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case 1001:
                Bitmap bitmap = JpegUtils.getCompressBitmap(takeImageFile.getPath());
                originalImg.setImageBitmap(bitmap);
                originalSize.setText(FileUtil.getFileOrFilesSize(takeImageFile.getPath(),FileUtil.SIZETYPE_MB)+"MB");
                compressImgFile = new File(JpegUtils.compressBitmapPath(bitmap,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        JpegUtils.generateFilePath(this,
                                new File(takeImageFile.getPath())), 20));
                //这里因为需求就是要得到压缩图片的File上传给服务器  为了方便演示就直接加载Bitmap了
                compressImg.setImageBitmap(JpegUtils.getCompressBitmap(compressImgFile.getPath()));
                compressSize.setText(FileUtil.getFileOrFilesSize(compressImgFile.getPath(),FileUtil.SIZETYPE_MB)+"MB");
                break;
        }
    }

}
