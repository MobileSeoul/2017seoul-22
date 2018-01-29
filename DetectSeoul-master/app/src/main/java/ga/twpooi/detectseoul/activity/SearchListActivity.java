package ga.twpooi.detectseoul.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;

import ga.twpooi.detectseoul.BaseActivity;
import ga.twpooi.detectseoul.R;
import ga.twpooi.detectseoul.adapter.SearchListCustomAdapter;
import ga.twpooi.detectseoul.util.Attraction;
import ga.twpooi.detectseoul.util.DividerItemDecoration;
import ga.twpooi.detectseoul.util.OnAdapterSupport;

public class SearchListActivity extends BaseActivity implements OnAdapterSupport {

    private Toolbar toolbar;
    private TextView toolbarTitle;

    // Recycle View
    private RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchListCustomAdapter adapter;

    private String searchText;
    private ArrayList<Attraction> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        searchText = getIntent().getStringExtra("search");
        list = (ArrayList<Attraction>)getIntent().getSerializableExtra("list");

        init();

        makeList();

    }

    private void init(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        toolbarTitle.setText(searchText);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLinearLayoutManager);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));

    }


    private void makeList(){

        adapter = new SearchListCustomAdapter(getApplicationContext(), list, rv, this, this);

        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void showView() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideView() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void redirectActivityForResult(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void redirectActivity(Intent intent) {
        startActivity(intent);
    }
}
