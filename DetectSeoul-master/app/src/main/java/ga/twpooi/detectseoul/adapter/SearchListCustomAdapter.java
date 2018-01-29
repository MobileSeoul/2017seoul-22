package ga.twpooi.detectseoul.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ppamorim.dragger.DraggerPosition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import ga.twpooi.detectseoul.R;
import ga.twpooi.detectseoul.StartActivity;
import ga.twpooi.detectseoul.activity.DetailActivity;
import ga.twpooi.detectseoul.activity.SearchListActivity;
import ga.twpooi.detectseoul.util.Attraction;
import ga.twpooi.detectseoul.util.OnAdapterSupport;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


/**
 * Created by tw on 2017-06-24.
 */
public class SearchListCustomAdapter extends RecyclerView.Adapter<SearchListCustomAdapter.ViewHolder> {

    // UI
    private Context context;
    private SearchListActivity activity;

    //    private MaterialNavigationDrawer activity;
    private OnAdapterSupport onAdapterSupport;

    public ArrayList<Attraction> list;

    // 무한 스크롤
//    private OnLoadMoreListener onLoadMoreListener;
//    private int visibleThreshold = 10;
//    private int lastVisibleItem, totalItemCount;
//    private boolean loading = false;

    // 생성자
    public SearchListCustomAdapter(Context context, ArrayList<Attraction> list, RecyclerView recyclerView, OnAdapterSupport listener, SearchListActivity activity) {
        this.context = context;
        this.list = list;
        this.onAdapterSupport = listener;
        this.activity = (SearchListActivity) activity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new ScrollListener() {
                @Override
                public void onHide() {
                    hideViews();
                }

                @Override
                public void onShow() {
                    showViews();
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_custom_item,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Attraction item = list.get(position);
        final int pos = position;

        holder.tv_title.setText(item.title);
        if(item.address == null || "".equals(item.address)){
            holder.tv_address.setVisibility(View.GONE);
        }else{
            holder.tv_address.setVisibility(View.VISIBLE);
            holder.tv_address.setText(item.address);
        }
        holder.tv_content.setText(item.sContents);

        Picasso.with(context)
                .load(item.picture.get(1))
                .into(holder.img);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("drag_position", DraggerPosition.TOP);
                intent.putExtra("data", new ArrayList<>());
                intent.putExtra("attraction", item);
                onAdapterSupport.redirectActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    private void hideViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.hideView();
        }
    }

    private void showViews() {
        if (onAdapterSupport != null) {
            onAdapterSupport.showView();
        }
    }

    // 무한 스크롤
//    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
//        this.onLoadMoreListener = onLoadMoreListener;
//    }
//    public void setLoaded() {
//        loading = false;
//    }

    public abstract class ScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 30;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

//            totalItemCount = linearLayoutManager.getItemCount();
//            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                // End has been reached
//                // Do something
//                loading = true;
//                if (onLoadMoreListener != null) {
//                    onLoadMoreListener.onLoadMore();
//                }
//            }
            // 여기까지 무한 스크롤

            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }

            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
            // 여기까지 툴바 숨기기
        }

        public abstract void onHide();
        public abstract void onShow();

    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView tv_title;
        TextView tv_address;
        TextView tv_content;
        ImageView img;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView)v.findViewById(R.id.cv);
            tv_title = (TextView)v.findViewById(R.id.tv_title);
            tv_address = (TextView)v.findViewById(R.id.tv_address);
            tv_content = (TextView)v.findViewById(R.id.tv_content);
            img = (ImageView)v.findViewById(R.id.img);
        }
    }

}
