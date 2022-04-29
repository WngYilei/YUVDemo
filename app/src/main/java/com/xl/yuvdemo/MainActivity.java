package com.xl.yuvdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xl.yuvdemo.bean.ImageBean;
import com.xl.yuvdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    static {
        System.loadLibrary("yuvdemo");
    }


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String path = getCacheDir().toString() + "/image.jpg";
        binding.iamgeOld.setImageBitmap(BitmapFactory.decodeFile(path));

        binding.btnCover.setOnClickListener(V -> {

            ImageBean imageBean = ImageUtils.imgToNV21(path);


            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());

            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.compressI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, imageBean.getWight(), imageBean.getHeight(), 0, true);


            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });

        binding.btnMirror.setOnClickListener(v -> {

            ImageBean imageBean = ImageUtils.imgToNV21(path);

            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.mirrorI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2);


            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });


        binding.btnRotate.setOnClickListener(v -> {

            ImageBean imageBean = ImageUtils.imgToNV21(path);

            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.rotateI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, 90);

            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });


        binding.btnScaled.setOnClickListener(v -> {
            ImageBean imageBean = ImageUtils.imgToNV21(path);

            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.scaledI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, imageBean.getWight(), imageBean.getWight());


            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });
    }

}