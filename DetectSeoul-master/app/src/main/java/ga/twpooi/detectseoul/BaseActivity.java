package ga.twpooi.detectseoul;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tw on 2017-05-28.
 */

public class BaseActivity extends AppCompatActivity {

    public void setFadeInAnimation(View view){
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void setFadeOutAnimation(View view){
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void redirectStartPage(){
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void showSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(int id){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }



    public void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    public void setStatusColorDark(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
    }

    public void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ga.twpooi.detectseoul",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                showSnackbar(Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

//    Observable ScrollView
    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

}
