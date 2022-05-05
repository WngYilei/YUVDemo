package com.xl.yuvdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.xl.yuvdemo.bean.ImageBean;
import com.xl.yuvdemo.databinding.ActivityMainBinding;

public class ImageActivity extends AppCompatActivity {


    static {
        System.loadLibrary("yuvdemo");
    }


    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.image2);

//        Bitmap bitmapImage = BitmapFactory.decodeFile(getCacheDir().toString() + "/image.jpg");

        binding.iamgeOld.setImageBitmap(bitmapImage.copy(Bitmap.Config.ARGB_8888, true));

        ImageBean imageBean = ImageUtils.imgToNV21(bitmapImage);

        binding.btnCover.setOnClickListener(V -> {
            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());

            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.compressI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, imageBean.getWight(), imageBean.getHeight(), 90, true);

            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });

        binding.btnMirror.setOnClickListener(v -> {


            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.mirrorI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2);


            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });


        binding.btnRotate.setOnClickListener(v -> {

            byte[] dst = new byte[imageBean.getWight() * imageBean.getHeight() * 3 / 2];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getWight() * imageBean.getHeight() * 3 / 2];
            YuvUtils.rotateI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, 90);

            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight(), imageBean.getHeight());

            binding.iamgeNew.setImageBitmap(bitmap);
        });


        binding.btnScaled.setOnClickListener(v -> {

            byte[] dst = new byte[imageBean.getData().length];

            YuvUtils.convertNV21ToI420(imageBean.getData(), dst, imageBean.getWight(), imageBean.getHeight());


            byte[] dst2 = new byte[imageBean.getData().length];
            YuvUtils.scaledI420(dst, imageBean.getWight(), imageBean.getHeight(), dst2, imageBean.getWight() / 3, imageBean.getWight() / 3);


            Bitmap bitmap = Bitmap.createBitmap(imageBean.getWight(), imageBean.getHeight(), Bitmap.Config.ARGB_8888);
            YuvUtils.convertI420ToBitmap(dst2, bitmap, imageBean.getWight() / 3, imageBean.getHeight() / 3);

            binding.iamgeNew.setImageBitmap(bitmap);
        });
    }


}