package ga.twpooi.detectseoul.activity;

import android.animation.PropertyValuesHolder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.db.chart.animation.Animation;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.XRenderer;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.view.HorizontalBarChartView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ppamorim.dragger.DraggerCallback;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import ga.twpooi.detectseoul.BaseActivity;
import ga.twpooi.detectseoul.Classifier;
import ga.twpooi.detectseoul.Information;
import ga.twpooi.detectseoul.R;
import ga.twpooi.detectseoul.StartActivity;
import ga.twpooi.detectseoul.fragment.SimplePhotoFragment;
import ga.twpooi.detectseoul.util.AdditionalFunc;
import ga.twpooi.detectseoul.util.Attraction;
import ga.twpooi.detectseoul.util.CustomViewPager;
import ga.twpooi.detectseoul.util.ParsePHP;

public class DetailActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SHOW_PROGRESS = 500;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 501;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;

    private DraggerView draggerView;

    private ImageView img_main;
    private TextView tv_content;
    private LinearLayout li_detail;
    private RelativeLayout rl_picture;
    private CustomViewPager viewPager;
    private NavigationAdapter pagerAdapter;
    private DotIndicator dotIndicator;

    private RelativeLayout rl_mapView;

    private MaterialDialog progressDialog;

    // Chart
    private RelativeLayout rl_chart;
    private HorizontalBarChartView mChart;

    private Attraction attraction;
    private ArrayList<Classifier.Recognition> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        data = (ArrayList<Classifier.Recognition>)intent.getSerializableExtra("data");
        attraction = (Attraction)intent.getSerializableExtra("attraction");
        String photo = attraction.picture.get(0);
        attraction.picture.remove(0);
        attraction.picture.add(photo);

        init();


    }

    private void draggerInit(){
        draggerView = new DraggerView(getApplicationContext());
        clearSingleton(); // 프레임 중복 해결
        draggerView = (DraggerView)findViewById(R.id.dragger_view);
        draggerView.setDraggerPosition((DraggerPosition)getIntent().getSerializableExtra("drag_position"));
        draggerView.setDraggerCallback(new DraggerCallback() {
            @Override
            public void onProgress(double v) {

            }

            @Override
            public void notifyOpen() {

            }

            @Override
            public void notifyClose() {
                finish();
            }
        });
    }

    private void init(){

        draggerInit();
        initObserval();
        initProgressDialog();

        mTitleView.setText(attraction.title);
        img_main = (ImageView)findViewById(R.id.image);
        Picasso.with(getApplicationContext())
                .load(attraction.picture.get(1))
                .into(img_main);
        tv_content = (TextView)findViewById(R.id.tv_content);
        tv_content.setText(attraction.contents);

        li_detail = (LinearLayout)findViewById(R.id.li_detail);
        makeDetailInfo();

        rl_picture = (RelativeLayout)findViewById(R.id.rl_picture);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        dotIndicator = (DotIndicator) findViewById(R.id.main_indicator_ad);
        loadViewPager();

        initMapView();
        initChart();

    }

    private void initProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
    }

    private void initObserval(){

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = 0;//getActionBarSize();

        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(DetailActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(attraction.url));
                startActivity(intent);
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });

    }

    private void loadViewPager(){

        pagerAdapter = new NavigationAdapter(getSupportFragmentManager(), attraction.picture);
        viewPager.setOffscreenPageLimit(attraction.picture.size());
        viewPager.setAdapter(pagerAdapter);
//        viewPager.setPageMargin(0);
        viewPager.setClipChildren(false);

        dotIndicator.setSelectedDotColor(Color.parseColor("#FFFFFF"));
        dotIndicator.setUnselectedDotColor(Color.parseColor("#CFCFCF"));
        dotIndicator.setNumberOfItems(attraction.picture.size());
        dotIndicator.setSelectedItem(0, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotIndicator.setSelectedItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initMapView() throws UnsatisfiedLinkError{

        rl_mapView = (RelativeLayout)findViewById(R.id.rl_map_view);

        if(attraction.isHaveLatLng()){
            rl_mapView.setVisibility(View.VISIBLE);

            String arch = System.getProperty("os.arch");
            System.out.println(arch);
            if(arch.startsWith("arm")) {

                MapView mapView = new MapView(this);
                //            mapView.setOnTouchListener(new View.OnTouchListener() {
                //                @Override
                //                public boolean onTouch(View view, MotionEvent motionEvent) {
                //                    System.out.println(motionEvent);
                //                    switch (motionEvent.getAction()){
                //                        case MotionEvent.ACTION_DOWN:
                //                            break;
                //                        case MotionEvent.ACTION_CANCEL:
                //                            break;
                //                    }
                //                    return false;
                //                }
                //
                //            });

                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(attraction.title);
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(attraction.lat, attraction.lng));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                mapView.addPOIItem(marker);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(attraction.lat, attraction.lng), true);

                ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);

                findViewById(R.id.tv_show_kakao_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String lat = attraction.lat.toString();
                        String lng = attraction.lng.toString();

                        String uri = "daummaps://look?p=" + lat + "," + lng;

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);

                    }
                });

            }else{
                rl_mapView.setVisibility(View.GONE);
            }

        }else{
//            findViewById(R.id.map_view).setVisibility(View.GONE);
            rl_mapView.setVisibility(View.GONE);
        }

    }

    private void initChart(){

        rl_chart = (RelativeLayout)findViewById(R.id.rl_chart);
        mChart = (HorizontalBarChartView)findViewById(R.id.chart);
        if(data != null && data.size() > 0){
            rl_chart.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.VISIBLE);
            Tooltip tip = new Tooltip(getApplicationContext());
            tip.setBackgroundColor(Color.parseColor("#f39c12"));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1)).setDuration(150);
                tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0)).setDuration(150);
            }

            mChart.setTooltips(tip);

            mChart.setOnEntryClickListener(new OnEntryClickListener() {
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {

                    String msg = String.format("%.2f", data.get((data.size()-1) - entryIndex).getConfidence()*100);
                    showSnackbar(msg + "%");
//                    handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PROGRESS));
//                    showSnackbar(setIndex + ", " + entryIndex + ", " + rect.toString() + ", " + data.get((data.size()-1) - entryIndex).getTitle());
//                    loadAttractionInfo(data.get((data.size()-1) - entryIndex));

//                    mTextViewValue.setText(String.format(Locale.ENGLISH, "%d", (int) mValues[0][entryIndex]));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
//                        mTextViewValue.animate().alpha(1).setDuration(200);
//                    else mTextViewValue.setVisibility(View.VISIBLE);
                }
            });

            mChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
//                        mTextViewValue.animate().alpha(0).setDuration(100);
//                    else mTextViewValue.setVisibility(View.INVISIBLE);
                }
            });
            mChart.setLabelsColor(getColorId(R.color.white));

            ArrayList<Classifier.Recognition> tempList = (ArrayList<Classifier.Recognition>)data.clone();
            Collections.reverse(tempList);
            BarSet barSet = new BarSet();
            Bar bar;
            for(int i=0; i<tempList.size(); i++){
                Classifier.Recognition recognition = tempList.get(i);
                bar = new Bar(recognition.getTitle(), recognition.getConfidence());
                if(i==tempList.size()-1) {
                    bar.setColor(getColorId(R.color.colorPrimary));
                }else{
                    bar.setColor(getColorId(R.color.white));
                }
                barSet.addBar(bar);
            }
//            for (int i = 0; i < mLabels.length; i++) {
//                bar = new Bar(mLabels[i], mValues[0][i]);
//                bar.setColor(getColorId(R.color.white));
////                switch (i) {
////                    case 0:
////                        bar.setColor(Color.parseColor("#77c63d"));
////                        break;
////                    case 1:
////                        bar.setColor(Color.parseColor("#27ae60"));
////                        break;
////                    case 2:
////                        bar.setColor(Color.parseColor("#47bac1"));
////                        break;
////                    case 3:
////                        bar.setColor(Color.parseColor("#16a085"));
////                        break;
////                    case 4:
////                        bar.setColor(Color.parseColor("#3498db"));
////                        break;
////                    default:
////                        break;
////                }
//                barSet.addBar(bar);
//            }
            mChart.addData(barSet);

            int[] order = new int[data.size()];
            for(int i=0; i<data.size(); i++){
                order[i] = (data.size()-1) - i;
            }
//            int[] order = {4, 3, 2, 1, 0};
            mChart.setXLabels(XRenderer.LabelPosition.NONE).show(new Animation().inSequence(.5f, order));
//                    .show(new Animation().inSequence(.5f, order).withEndAction(action));
        }else{
            rl_chart.setVisibility(View.GONE);
            mChart.setVisibility(View.GONE);
        }

    }

    private void makeDetailInfo(){

        li_detail.removeAllViews();

        if(attraction.address != null && !"".equals(attraction.address)){
            View v = getLayoutInflater().inflate(R.layout.detail_information_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.address);
            tv_content.setText(attraction.address);

            li_detail.addView(v);
        }

        if(attraction.web != null && !"".equals(attraction.web)){

            View v = getLayoutInflater().inflate(R.layout.detail_information_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.web_page);
            tv_content.setText(attraction.web);
            tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(DetailActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(attraction.web));
                    startActivity(intent);
                }
            });

            li_detail.addView(v);

        }

        if(attraction.telephone != null && !"".equals(attraction.telephone)){

            View v = getLayoutInflater().inflate(R.layout.detail_information_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(R.string.telephone);
            tv_content.setText(attraction.telephone);
            tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(attraction.web));
//                    startActivity(intent);
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + attraction.telephone)));
                }
            });

            li_detail.addView(v);

        }


        for(String[] strs : attraction.detail){

            View v = getLayoutInflater().inflate(R.layout.detail_information_custom_item, null, false);
            TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
            TextView tv_content = (TextView)v.findViewById(R.id.tv_content);

            tv_title.setText(strs[0]);
            tv_content.setText(strs[1]);

            final String content = strs[1];

            tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", content);
                    clipboard.setPrimaryClip(clip);
                    showSnackbar(R.string.saved_clipboard);
                }
            });

            li_detail.addView(v);

        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SHOW_PROGRESS:
                    progressDialog.show();
                    break;
                case MSG_MESSAGE_HIDE_PROGRESS:
                    initProgressDialog();
                    break;
                default:
                    break;
            }
        }
    }

    private void loadAttractionInfo(Classifier.Recognition recognition){

        final ArrayList<Classifier.Recognition> list = new ArrayList<>();
        String attraction = recognition.getTitle();
        list.add(recognition);

        attraction = AdditionalFunc.convertAttraction(attraction);
//        switch (attraction){
//            case "roses":
//                attraction = "1898 명동성당";
//                break;
//            case "daisy":
//                attraction = "DDP(동대문디자인플라자)";
//                break;
//            case "dandelion":
//                attraction = "경복궁";
//                break;
//            case "sunflowers":
//                attraction = "경희궁";
//                break;
//            default:
//                attraction = "N서울타워";
//                break;
//        }

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getLocationInfo");
        map.put("query", attraction);
        map.put("english", AdditionalFunc.needEnglishText());
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                Attraction att = AdditionalFunc.getAttractionInfo(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_HIDE_PROGRESS));

                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                intent.putExtra("data", list);
                intent.putExtra("attraction", att);
                startActivity(intent);

            }
        }.start();

    }

    private static class NavigationAdapter extends FragmentPagerAdapter {

        private Fragment[] fragments;
        private ArrayList<String> list;

        public NavigationAdapter(FragmentManager fm, ArrayList<String> list){
            super(fm);
            this.list = list;
            fragments = new Fragment[list.size()];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            final int pattern = position % list.size();

            if(fragments[pattern] == null) {
                f = new SimplePhotoFragment();
                Bundle bdl = new Bundle(1);
                bdl.putInt("position", pattern);
                bdl.putSerializable("img", list.get(pattern));
                bdl.putSerializable("imgList", list);
                f.setArguments(bdl);
                return f;
            }else{
                return fragments[pattern];
            }
//            f = new ArticleItemFragment();

//            return f;
        }

        @Override
        public int getCount(){
            return list.size();
        }


    }

    @Override public void onBackPressed() {
        draggerView.closeActivity();
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        draggerView.setSlideEnabled(scrollY <= 0);
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));

        // Change alpha of overlay
//        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void clearSingleton(){
        try{
            Field field = DraggerView.class.getDeclaredField("singleton");
            field.setAccessible(true);
            field.set(this, null);
        }catch(NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
