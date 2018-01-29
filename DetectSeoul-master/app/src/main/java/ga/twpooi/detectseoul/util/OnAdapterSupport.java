package ga.twpooi.detectseoul.util;

import android.content.Intent;

/**
 * Created by tw on 2017-01-24.
 */

public interface OnAdapterSupport {
    void showView();
    void hideView();

    void redirectActivityForResult(Intent intent);

    void redirectActivity(Intent intent);
}
