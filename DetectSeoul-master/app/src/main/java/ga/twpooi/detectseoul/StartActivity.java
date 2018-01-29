package ga.twpooi.detectseoul;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.ppamorim.dragger.DraggerPosition;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;
import com.mingle.sweetpick.ViewPagerDelegate;
import com.squareup.picasso.Picasso;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ga.twpooi.detectseoul.activity.CameraActivity;
import ga.twpooi.detectseoul.activity.DetailActivity;
import ga.twpooi.detectseoul.activity.SearchListActivity;
import ga.twpooi.detectseoul.util.AdditionalFunc;
import ga.twpooi.detectseoul.util.Attraction;
import ga.twpooi.detectseoul.util.OnDetecterListener;
import ga.twpooi.detectseoul.util.ParsePHP;

public class StartActivity extends BaseActivity implements OnDetecterListener{

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SHOW_PROGRESS = 500;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 501;
    private final int MSG_MESSAGE_ERROR_DIALOG = 502;
    private final int MSG_MESSAGE_CHANGE_PROGRESS = 503;
    private final int MSG_MESSAGE_REFRESH_BG = 504;

    private Detecter detecter;
    private Executor executor = Executors.newSingleThreadExecutor();

    private RelativeLayout root;
    private KenBurnsView kenBurnsView;
//    private ImageView backgroundImg;
    private TextView tv_title;
    private TextView tv_sub_title;
    private TextView showDetailBtn;
    private Button searchBtn, detectBtn;

    private MaterialDialog progressDialog;
    private SweetSheet mSweetSheet2;

    private Attraction bgAttraction;
    private boolean needReload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();

        printKeyHash();

    }

    private void init(){

        root = (RelativeLayout)findViewById(R.id.root);
        kenBurnsView = (KenBurnsView)findViewById(R.id.image);
//        backgroundImg = (ImageView)findViewById(R.id.background_img);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_sub_title = (TextView)findViewById(R.id.tv_sub_title);
        showDetailBtn = (TextView)findViewById(R.id.show_detail_btn);
        showDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, DetailActivity.class);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                intent.putExtra("data", new ArrayList<>());
                intent.putExtra("attraction", bgAttraction);
                startActivity(intent);
            }
        });
        searchBtn = (Button)findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchAttractionDialog();
            }
        });
        detectBtn = (Button)findViewById(R.id.detect_btn);
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent);
//                showDetectDialog();
                mSweetSheet2.toggle();
            }
        });

        detecter = new Detecter(getApplicationContext(), this);

        initProgressDialog();
        initSweetDialog();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        findViewById(R.id.tv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBackgroundImage();
            }
        });
        getBackgroundImage();

    }

    private void initSweetDialog(){

        mSweetSheet2 = new SweetSheet(root);
//        mSweetSheet2.setMenuList(R.menu.menu_select);
//        mSweetSheet2.setDelegate(new ViewPagerDelegate());
//        mSweetSheet2.setBackgroundEffect(new DimEffect(0.2f));
//        mSweetSheet2.setOnMenuItemClickListener(new SweetSheet.OnMenuItemClickListener() {
//            @Override
//            public boolean onItemClick(int position, MenuEntity menuEntity1) {
//
//                Toast.makeText(StartActivity.this, menuEntity1.title + "  " + position, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_select_method, null, false);
        customDelegate.setCustomView(view);
        mSweetSheet2.setDelegate(customDelegate);
        mSweetSheet2.setBackgroundEffect(new DimEffect(0.2f));
        view.findViewById(R.id.rl_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelectorActivity.start(StartActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false,false,false);
                mSweetSheet2.toggle();
            }
        });
        view.findViewById(R.id.rl_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImgFromURL();
                mSweetSheet2.toggle();
            }
        });
        view.findViewById(R.id.rl_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, CameraActivity.class);
                startActivity(intent);
                mSweetSheet2.toggle();
            }
        });

    }

    private void initProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(this)
                .content(getString(R.string.please_wait))
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
    }

    private void getBackgroundImage(){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "getRandomLocation");
        map.put("english", AdditionalFunc.needEnglishText());
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                bgAttraction = AdditionalFunc.getAttractionInfo(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_REFRESH_BG));

            }
        }.start();

    }

    private void setBackgroundImage(){

        tv_title.setText(bgAttraction.title);
        if(bgAttraction.address == null || "".equals(bgAttraction.address)){
            tv_sub_title.setVisibility(View.GONE);
        }else{
            tv_sub_title.setVisibility(View.VISIBLE);
            tv_sub_title.setText(bgAttraction.address);
        }
        showDetailBtn.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(bgAttraction.picture.get(1))
                .into(kenBurnsView);

    }

    public void showDetectDialog(){
        String[] list = {
                "Gallery",
                "URL",
                "Camera"
        };
        new MaterialDialog.Builder(this)
                .title("선택")
                .items(list)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which){
                            case 0: // Gallery
                                ImageSelectorActivity.start(StartActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false,false,false);
                                break;
                            case 1: // URL
                                loadImgFromURL();
                                break;
                            case 2: { // Camera
                                Intent intent = new Intent(StartActivity.this, CameraActivity.class);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                })
                .show();
    }

    public void loadImgFromURL(){

        new MaterialDialog.Builder(StartActivity.this)
                .title(R.string.input_srt)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .theme(Theme.LIGHT)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .input(getString(R.string.please_input_photo_url), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        final String url = input.toString();

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PROGRESS));

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    String url = editURL.getText().toString();
                                    InputStream input = new java.net.URL(url).openStream();
                                    //InputStream input = new java.net.URL(editURL.getText().toString()).openConnection().getInputStream();

                                    final Bitmap bitmap =  BitmapFactory.decodeStream(input);
                                    // recognize_bitmap needs to update the UI(imgResult, txtResult), so invoke it in runOnUiThread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //
                                            detecter.recognize_bitmap(bitmap);
                                        }
                                    });


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }
                })
                .show();

    }

    private void showSearchAttractionDialog(){

        new MaterialDialog.Builder(StartActivity.this)
                .title(R.string.input_srt)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .theme(Theme.LIGHT)
                .positiveText(R.string.search_srt)
                .negativeText(R.string.cancel)
                .input(getString(R.string.please_input_serach_text), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PROGRESS));
                        searchAttraction(input.toString());

                    }
                })
                .show();

    }

    private void searchAttraction(final String query){

        HashMap<String, String> map = new HashMap<>();
        map.put("service", "searchLocation");
        map.put("query", query);
        map.put("english", AdditionalFunc.needEnglishText());
        new ParsePHP(Information.MAIN_SERVER_ADDRESS, map) {

            @Override
            protected void afterThreadFinish(String data) {

                ArrayList<Attraction> list = AdditionalFunc.getAttractionInfoList(data);

                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_HIDE_PROGRESS));

                Intent intent = new Intent(StartActivity.this, SearchListActivity.class);
                intent.putExtra("search", query);
                intent.putExtra("list", list);
                startActivity(intent);

            }
        }.start();

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
                case MSG_MESSAGE_CHANGE_PROGRESS:
                    progressDialog.setTitle(R.string.loading_attraction_info);
                    break;
                case MSG_MESSAGE_ERROR_DIALOG:
                    new MaterialDialog.Builder(StartActivity.this)
                            .title(R.string.fail_srt)
                            .content(R.string.fail_detect)
                            .positiveText(R.string.ok)
                            .show();
                    break;
                case MSG_MESSAGE_REFRESH_BG:
                    setBackgroundImage();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // pass the selected image from image picker to tensorflow
        // image picker returns image(s) in arrayList

        if(resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE){

            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PROGRESS));

            new Thread() {
                @Override
                public void run() {

                    ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

                    // image decoded to bitmap, which can be recognized by tensorflow
                    Bitmap bitmap = BitmapFactory.decodeFile(images.get(0));

                    detecter.recognize_bitmap(bitmap);

                }
            }.start();

        }
    }

    @Override
    public void onDetectFinish(List<Classifier.Recognition> results) {
//        showSnackbar(results.toString());
//        handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_HIDE_PROGRESS));
        ArrayList<Classifier.Recognition> list = new ArrayList<>();
        list.addAll(results);
        if(list.size() > 0) {
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_CHANGE_PROGRESS));
            loadAttractionInfo(list);
//            Intent intent = new Intent(StartActivity.this, DetailActivity.class);
//            intent.putExtra("drag_position", DraggerPosition.TOP);
//            intent.putExtra("data", list);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
        }else{
            handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_ERROR_DIALOG));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(needReload){
            needReload = false;
            getBackgroundImage();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        needReload = true;
    }

    @Override
    public void onBackPressed(){
        if(mSweetSheet2.isShow()){
            mSweetSheet2.dismiss();
        }else{
            new MaterialDialog.Builder(this)
                    .title(R.string.ok)
                    .content(R.string.finish_app)
                    .positiveText(R.string.finish_srt)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            StartActivity.super.onBackPressed();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void loadAttractionInfo(final ArrayList<Classifier.Recognition> list){

        String attraction = list.get(0).getTitle();

        attraction = AdditionalFunc.convertAttraction(attraction);
//        switch (attraction){
//            case "heunginjimun":
//                attraction = "흥인지문(동대문)";
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

                Intent intent = new Intent(StartActivity.this, DetailActivity.class);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                intent.putExtra("data", list);
                intent.putExtra("attraction", att);
                startActivity(intent);

            }
        }.start();

    }
}
