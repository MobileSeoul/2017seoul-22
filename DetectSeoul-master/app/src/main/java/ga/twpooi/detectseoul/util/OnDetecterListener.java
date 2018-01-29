package ga.twpooi.detectseoul.util;

import java.util.List;

import ga.twpooi.detectseoul.Classifier;

/**
 * Created by tw on 2017. 8. 28..
 */

public interface OnDetecterListener {
    void onDetectFinish(List<Classifier.Recognition> results);
}
