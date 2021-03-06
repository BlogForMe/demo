package com.chs.androiddailytext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chs.androiddailytext.base.BaseActivity;
import com.chs.androiddailytext.changeSkin.ChangeSkinActivity;
import com.chs.androiddailytext.chart.BarChartActivity;
import com.chs.androiddailytext.chart.PercentActivity;
import com.chs.androiddailytext.coordinatorlayout.CoordinatorlayoutActivity;
import com.chs.androiddailytext.custom_views.WorkLoadStatisticActivity;
import com.chs.androiddailytext.dagger.DaggerActivity;
import com.chs.androiddailytext.glide.GlideActivity;
import com.chs.androiddailytext.jetpack.LiveDataFirstActivity;
import com.chs.androiddailytext.kotlin.CoroutinesActivity;
import com.chs.androiddailytext.list.ListActivity;
import com.chs.androiddailytext.myanimator.AnimActivity;
import com.chs.androiddailytext.netease.NeteaseActivity;
import com.chs.androiddailytext.pattern.strategy.StrategyActivity;
import com.chs.androiddailytext.permission.PermissionActivity;
import com.chs.androiddailytext.piccanvas.BigViewActivity;
import com.chs.androiddailytext.piccanvas.PicCanvasActivity;
import com.chs.androiddailytext.popwindow.PopActivity;
import com.chs.androiddailytext.recyclerview.RecyclerActivity;
import com.chs.androiddailytext.retorfit.OkhttpTextActivity;
import com.chs.androiddailytext.retorfit.RetrofitTextActivity;
import com.chs.androiddailytext.slideview.SlideActivity;
import com.chs.androiddailytext.tabscroll.TabRecyclerActivity;
import com.chs.androiddailytext.tabscroll.TabStickyActivity;

import java.util.ArrayList;
import java.util.List;

import login.LoginActivity;
import login.RegisterActivity;

public class MainActivity extends BaseActivity {
    List<String> datas = new ArrayList<>();
    RecyclerView recyclerView;
    BaseQuickAdapter adapter;
    @Override
    public int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_main,datas) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.tv_name,item);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        startActivity(PermissionActivity.class);
                        break;
                    case 1:
                        startActivity(ViewActivity.class);
                        break;
                    case 2:
                        startActivity(AnimatorActivity.class);
                        break;
                    case 3:
                        startActivity(PullRefreshActivity.class);
                        break;
                    case 4:
                        startActivity(ChartActivity.class);
                        break;
                    case 5:
                        startActivity(StrategyActivity.class);
                        break;
                    case 6:
                        startActivity(ChangeSkinActivity.class);
                        break;
                    case 7:
                        startActivity(OkhttpTextActivity.class);
                        break;
                    case 8:
                        startActivity(RetrofitTextActivity.class);
                        break;
                    case 9:
                        startActivity(GlideActivity.class);
                        break;
                    case 10:
                        startActivity(WorkLoadStatisticActivity.class);
                        break;
                    case 11:
                        startActivity(BarChartActivity.class);
                        break;
                    case 12:
                        startActivity(DaggerActivity.class);
                        break;
                    case 13:
                        startActivity(ListActivity.class);
                        break;
                    case 14:
                        startActivity(PicCanvasActivity.class);
                        break;
                    case 15:
                        startActivity(PopActivity.class);
                        break;
                    case 16:
                        startActivity(LoginActivity.class);
                        break;
                    case 17:
                        startActivity(RegisterActivity.class);
                        break;
                    case 18:
                        startActivity(NeteaseActivity.class);
                        break;
                    case 19:
                        startActivity(LiveDataFirstActivity.class);
                        break;
                    case 20:
                        startActivity(AnimActivity.class);
                        break;
                    case 21:
                        startActivity(RecyclerActivity.class);
                        break;
                    case 22:
                        startActivity(SlideActivity.class);
                        break;
                    case 23:
                        startActivity(BigViewActivity.class);
                        break;
                    case 24:
                        startActivity(PercentActivity.class);
                        break;
                    case 25:
                        startActivity(TabRecyclerActivity.class);
                        break;
                    case 26:
                        startActivity(TabStickyActivity.class);
                        break;
                    case 27:
                        startActivity(CoordinatorlayoutActivity.class);
                        break;
                    case 28:
                        startActivity(CoroutinesActivity.class);
                        break;
                    case 29:
                        Bundle bundle = new Bundle();
                        bundle.putString("userName","??????");
                        PendingIntent pendingIntent = new NavDeepLinkBuilder(MainActivity.this)
                                .setGraph(R.navigation.nav_graph)
                                .setDestination(R.id.thirdFragment)
                                .setArguments(bundle)
                                .createPendingIntent();
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            notificationManager.createNotificationChannel(new NotificationChannel(
                                    "deepLink","deepLinkName", NotificationManager.IMPORTANCE_HIGH
                            ));
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "deepLink")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle("??????deepLink")
                                .setContentText("?????????")
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        notificationManager.notify(10,builder.build());
                        break;
                    default:
                }
            }
        });
    }
    @Override
    public void initData() {
        datas.add("??????");
        datas.add("View");
        datas.add("??????");
        datas.add("??????");
        datas.add("??????");
        datas.add("????????????");
        datas.add("??????");
        datas.add("okhttp");
        datas.add("retrofit");
        datas.add("glide");
        datas.add("??????");
        datas.add("?????????");
        datas.add("Dagger");
        datas.add("treelist");
        datas.add("PicCanvas");
        datas.add("toPop");
        datas.add("??????");
        datas.add("??????");
        datas.add("Netease");
        datas.add("Jetpack");
        datas.add("????????????");
        datas.add("??????recycleview");
        datas.add("Slide");
        datas.add("BigView");
        datas.add("percent");
        datas.add("tabRecycler");
        datas.add("tabSticky");
        datas.add("Coordinator");
        datas.add("??????");
        datas.add("deepLink");
    }

}