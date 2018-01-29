package ga.twpooi.detectseoul.fragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import ga.twpooi.detectseoul.R;

/**
 * Created by tw on 2017-05-28.
 */

public class BaseFragment extends Fragment {

    public void showSnackbar(View g_view, Context context, String msg){
        Snackbar snackbar = Snackbar.make(g_view, msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(View g_view, Context context, int id){
        Snackbar snackbar = Snackbar.make(g_view, getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.snackbar_color));
        snackbar.show();
    }

    public int getColorId(Context context, int id){
        return ContextCompat.getColor(context, id);
    }

}
