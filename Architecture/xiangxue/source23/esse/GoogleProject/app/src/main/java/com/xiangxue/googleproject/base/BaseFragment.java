package com.xiangxue.googleproject.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.xiangxue.googleproject.ProjectApplication;
import com.xiangxue.googleproject.callback.SharedViewModel;

import timber.log.Timber;

/**
 * 所有Fragment的父类
 */
public class BaseFragment extends Fragment {

    protected AppCompatActivity mActivity;
    public SharedViewModel sharedViewModel;  // 使用共享

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = getAppViewModelProvider().get(SharedViewModel.class);

        Timber.tag(getClass().getSuperclass().getSimpleName()).i("BaseFragment onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    // 同学们，测试用的，暂无用
//    public boolean isDebug() {
//        return mActivity.getApplicationContext().getApplicationInfo() != null &&
//                (mActivity.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
//    }

    // 同学们，只是提示而已
    public void showLongToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    // 同学们，只是提示而已
    public void showShortToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    // 同学们，只是提示而已
    public void showLongToast(int stringRes) {
        showLongToast(mActivity.getApplicationContext().getString(stringRes));
    }

    // 同学们，只是提示而已
    public void showShortToast(int stringRes) {
        showShortToast(mActivity.getApplicationContext().getString(stringRes));
    }

    // 给当前BaseFragment用的
    protected ViewModelProvider getAppViewModelProvider() {
        return ((ProjectApplication) mActivity.getApplicationContext()).getAppViewModelProvider(mActivity);
    }

    // 给所有的fragment提供的函数，可以顺利的拿到 ViewModel
    protected ViewModelProvider getFragmentViewModelProvider(Fragment fragment) {
        return new ViewModelProvider(fragment, fragment.getDefaultViewModelProviderFactory());
    }

    // 给所有的fragment提供的函数，可以顺利的拿到 ViewModel
    protected ViewModelProvider getActivityViewModelProvider(AppCompatActivity activity) {
        return new ViewModelProvider(activity, activity.getDefaultViewModelProviderFactory());
    }

    /**
     * 暴露给自己的孩子 隐藏ActionBar
     */
    public void hideActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    /**
     * 暴露给自己的孩子 显示ActionBar
     */
    public void showActionBar() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if(actionBar!=null){
            actionBar.show();
        }
    }


     public void onPause() {
        super.onPause();
        Timber.tag(getClass().getSuperclass().getSimpleName()).i("BaseFragment onPause");
    }

    public void  onStop() {
        super.onStop();
        Timber.tag(getClass().getSuperclass().getSimpleName()).i("BaseFragment onStop");
    }

    public void onDestroy() {
        super.onDestroy();
        Timber.tag(getClass().getSuperclass().getSimpleName()).i("BaseFragment onDestroy");
    }
}
