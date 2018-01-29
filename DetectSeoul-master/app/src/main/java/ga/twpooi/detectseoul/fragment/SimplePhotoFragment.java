package ga.twpooi.detectseoul.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ga.twpooi.detectseoul.R;
import ga.twpooi.detectseoul.util.AdvancedImageView;

public class SimplePhotoFragment extends BaseFragment {

    // UI
    private View view;
    private Context context;

    private int pos;
    private ArrayList<String> imgList;
    private AdvancedImageView imageView;
    private String img;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        if(getArguments() != null) {
            pos = getArguments().getInt("position");
            img = getArguments().getString("img");
            imgList = (ArrayList<String>)getArguments().getSerializable("imgList");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_simple_photo, container, false);
        context = container.getContext();

        init();


        return view;
    }

    private void init(){

        imageView = (AdvancedImageView)view.findViewById(R.id.image);
        Picasso.with(context)
                .load(img)
                .into(imageView);
        imageView.setImageList(imgList, pos);

    }



}
