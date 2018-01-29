package ga.twpooi.detectseoul;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ga.twpooi.detectseoul.util.OnDetecterListener;

/**
 * Created by tw on 2017. 8. 28..
 */

public class Detecter {

    private Context context;
    private OnDetecterListener listener;

    private static final int INPUT_SIZE = 299;
    private static final int IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 255.0f;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/rounded_graph.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/retrained_labels.txt";

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    public Detecter(Context context, OnDetecterListener listener){
        this.context = context;
        this.listener = listener;
        initTensorFlowAndLoadModel();
    }

    public void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            context.getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    // recognize bitmap and get results
    public void recognize_bitmap(Bitmap bm) {

        // create a bitmap scaled to INPUT_SIZE
        final Bitmap bitmap = Bitmap.createScaledBitmap(bm, INPUT_SIZE, INPUT_SIZE, false);

        // returned value stores in Classifier.Recognition format
        // which provides various methods to parse the result,
        // but I'm going to show raw result here.
        listener.onDetectFinish(classifier.recognizeImage(bitmap));

//        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
//        imgResult.setImageBitmap(bitmap);
//        txtResult.setText(results.toString());
    }


    public void onDestroy() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

}
