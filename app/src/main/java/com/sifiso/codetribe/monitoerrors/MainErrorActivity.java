package com.sifiso.codetribe.monitoerrors;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.com.boha.monitor.library.adapters.ErrorAndroidAdapter;
import com.com.boha.monitor.library.adapters.ErrorStatusAdapter;
import com.com.boha.monitor.library.dto.RequestDTO;
import com.com.boha.monitor.library.dto.ResponseDTO;
import com.com.boha.monitor.library.fragments.ErrorStoreListFragment;
import com.com.boha.monitor.library.fragments.PageFragment;
import com.com.boha.monitor.library.fragments.ProjectListErrorFragment;
import com.com.boha.monitor.library.toolbox.BaseVolley;
import com.com.boha.monitor.library.util.ErrorUtil;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.ToastUtil;
import com.com.boha.monitor.library.util.WebSocketUtil;
import com.sifiso.codetribe.monitoerrors.utiws.Websocket;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;


public class MainErrorActivity extends FragmentActivity implements ProjectListErrorFragment.ProjectErrorListListener, ErrorStoreListFragment.OnFragmentErrorStoreListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_error);
        ctx = getApplicationContext();
        mPager = (ViewPager) findViewById(R.id.pager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_error, menu);
        mMenu = menu;
        //setRefreshActionButtonState(true);
        getData();
        return true;
    }

    private void getData() {
        RequestDTO r = new RequestDTO();
        r.setRequestType(RequestDTO.GET_ANDROID_ERROR_DATA);
        r.setDay(8);
        Log.i(LOG, "Request And Response");

        setRefreshActionButtonState(true);
        Websocket.sendRequest(ctx, Statics.COMPANY_ENDPOINT, r, new Websocket.WebSocketListener() {
            @Override
            public void onMessage(final ResponseDTO resp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, resp)) {
                            return;
                        }
                        response = resp;
                       // Toast.makeText(getApplicationContext(), resp.getStatusCode() + "", Toast.LENGTH_SHORT).show();
                        buildPages();
//                        setTitle(response.getCompany().getCompanyName());
                        if (isRefresh) {
                            isRefresh = false;
                            mPager.setCurrentItem(currentPageIndex);
                        }
                        Log.i(LOG, "Request And Response");
                    }
                });
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void getVolleyData() {
        RequestDTO w = new RequestDTO();
        w.setRequestType(RequestDTO.GET_ANDROID_ERROR_DATA);
        w.setDay(6);

        if (!BaseVolley.checkNetworkOnDevice(ctx)) {
            return;
        }
        setRefreshActionButtonState(true);
        BaseVolley.getRequestQueue();
        BaseVolley.getRemoteData(Statics.COMPANY_URL, w, ctx, new BaseVolley.BohaVolleyListener() {
            @Override
            public void onResponseReceived(ResponseDTO r) {
                response = r;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        if (!ErrorUtil.checkServerError(ctx, response)) {
                            return;
                        }

                        buildPages();
                        //  setTitle(response.getCompany().getCompanyName());
                        if (isRefresh) {
                            isRefresh = false;
                            mPager.setCurrentItem(currentPageIndex);
                        }
                        Log.i(LOG, "Request And Response");
                       /* CacheUtil.cacheData(ctx,response,CacheUtil.CACHE_COMPANY,new CacheUtil.CacheUtilListener() {
                            @Override
                            public void onFileDataDeserialized(ResponseDTO response) {

                            }

                            @Override
                            public void onDataCached() {

                            }
                        });*/
                    }
                });
            }

            String message;

            @Override
            public void onVolleyError(VolleyError error) {
                message = error.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshActionButtonState(false);
                        ToastUtil.errorToast(ctx, message);
                    }
                });
            }
        });
    }

    private void buildPages() {

        pageFragmentList = new ArrayList<PageFragment>();
        projectListErrorFragment = new ProjectListErrorFragment();
        ResponseDTO r1 = new ResponseDTO();
        Bundle data1 = new Bundle();
        r1.setErrorStoreAndroidList(response.getErrorStoreAndroidList());
        r1.setErrorStoreList(response.getErrorStoreList());
        data1.putSerializable("response", r1);
        projectListErrorFragment.setArguments(data1);

        errorStoreListFragment = new ErrorStoreListFragment();
        errorStoreListFragment.setArguments(data1);

        pageFragmentList.add(projectListErrorFragment);
        pageFragmentList.add(errorStoreListFragment);

        initializeAdapter();

    }

    private void initializeAdapter() {
        adapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                currentPageIndex = arg0;
                pageFragmentList.get(arg0).animateCounts();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onErrorClicked(ErrorAndroidAdapter project) {

    }

    @Override
    public void onFragmentInteraction(ErrorStatusAdapter statusAdapter) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            return (Fragment) pageFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return pageFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "Title";

            switch (position) {
                case 0:
                    title = "Android Errors";
                    break;
                case 1:
                    title = "Error Store";
                    break;


                default:
                    break;
            }
            return title;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.refresh) {
            isRefresh = true;
            getData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (mMenu != null) {
            final MenuItem refreshItem = mMenu.findItem(R.id.refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.action_bar_progess);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //  Websocket.closeSession();
    }

    ViewPager mPager;
    Menu mMenu;
    ResponseDTO response;
    int currentPageIndex;
    PagerAdapter adapter;
    Context ctx;
    ProjectListErrorFragment projectListErrorFragment;
    ErrorStoreListFragment errorStoreListFragment;
    List<PageFragment> pageFragmentList;
    boolean isRefresh;

    static final String LOG = MainErrorActivity.class.getSimpleName();
}
