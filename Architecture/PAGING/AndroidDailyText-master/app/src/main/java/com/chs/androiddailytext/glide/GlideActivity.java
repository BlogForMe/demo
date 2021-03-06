package com.chs.androiddailytext.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chs.androiddailytext.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class GlideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        final ImageView imageView = findViewById(R.id.imageview);
        final String url = "http://p3.pstatp.com/origin/pgc-image/15220293999066398493c24";
        Glide.with(this)
                .load(url)
                .into(imageView);

        new Thread(){
            @Override
            public void run() {
                FutureTarget<File> futureTarget =
                        Glide.with(getApplicationContext())
                                .asFile()
                                .load(url)
                                .submit();
                try {
                    File bitmap = futureTarget.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext()).clear(futureTarget);
            }
        };

        new Thread(){
            @Override
            public void run() {
                FutureTarget<Bitmap> futureTarget =
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(url)
                                .submit();

                try {
                    Bitmap bitmap = futureTarget.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext()).clear(futureTarget);
            }
        };
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)//???????????????????????????
                .error(R.mipmap.ic_launcher)//??????????????????????????????
                .override(400,400)//?????????????????????
                .fitCenter()//??????????????????????????????fitCenter ????????????????????????????????????????????????ImageView????????????????????????
                .centerCrop()//??????????????????????????????centerCrop ???????????????????????????????????????????????????????????????ImageView?????????????????????????????????????????????
                .circleCrop()//??????????????????????????????centerCrop ????????????
                .skipMemoryCache(true)//??????????????????
                .diskCacheStrategy(DiskCacheStrategy.ALL)//???????????????????????????
                .diskCacheStrategy(DiskCacheStrategy.NONE)//??????????????????
                .diskCacheStrategy(DiskCacheStrategy.DATA)//?????????????????????????????????
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//????????????????????????
                ;
        Glide.with(this)
                .load(url)
                .apply(options)
                .transition(withCrossFade())//???????????????????????????????????????????????????(??????????????????)
                .into(imageView);
        GlideApp.with(this)
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .fitCenter()
                .circleCrop()
                .override(400,400)
                .into(imageView);
        Glide.with(this)
                .load(url)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }
                });
//        Glide.with(this).asGif()
//        Glide.with(this).asFile()
//        Glide.with(this).asBitmap()
//        Glide.with(this).asDrawable()
//        int count = 1; for (int i = 1; i <= 5; i++) { count += i; }
//        Log.i("count",count+"");
//        int a=0; int c=0;
//        do{ --c; a=a-1; }while(a>0);
//
//        int x = (11+3*8)/4%3;
//        Log.i("countx",x+"");
//
//        int[] score = new int[]{1,2,3,4,5};
//        int temp = score[0];
//        for (int index = 1;index < 5;index++) {
//            if (score[index] < temp) {
//                temp = score[index];
//            } }

        int a = 0; while ( a < 5 )
        { switch(a){ case 0 : case 3 : a = a + 2; case 1 : case 2 : a = a + 3; default : a = a + 5; } }
        Log.i("count","a----"+a);

        int i=0,s=0; do{ if ( i%2 == 0 ){ i++; continue; } i++; s = s + i; } while (i<7);
        Log.i("count","i----"+s);

        int total = 0; for ( int j = 0; j < 4; j++ ){
            if ( j == 1) continue;
            if ( j == 2) break;
            total += j; }

        Log.i("count","total----"+total);

        int x;
        x = 6;
        Log.i("count","aaa----"+x+"x++"+(x++)+"xxx"+x);
        int y = 1+2/3-4*5;

        Log.i("count","y----"+y);
    }

}
