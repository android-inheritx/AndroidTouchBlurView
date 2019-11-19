package site.iway.androidtouchblurview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;

import site.iway.androidhelpers.AssetsHelper;
import site.iway.androidhelpers.BitmapHelper;
import site.iway.androidhelpers.TouchBlurView;
import site.iway.javahelpers.Scale;

public class MainActivity extends Activity {

    Bitmap bmpImage;
    TouchBlurView touchBlurView;
    Button btnSave;
    int REQUEST_PREVIEW =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        touchBlurView = (TouchBlurView) findViewById(R.id.touchBlurView);
        touchBlurView.setDrawingCacheEnabled(true);
        touchBlurView.buildDrawingCache();

        touchBlurView.setSaveEnabled(true);
        bmpImage = AssetsHelper.readImageFile(MainActivity.this, "bike.jpg");
        touchBlurView.setBitmap(bmpImage, 50, 100);

        touchBlurView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (right - left > 0 && bottom - top > 0) {
                    bmpImage = AssetsHelper.readImageFile(MainActivity.this, "bike.jpg");
                    bmpImage = BitmapHelper.scale(bmpImage, Scale.CenterFit, right - left, bottom - top);
                    TouchBlurView touchBlurView = (TouchBlurView) v;
                    touchBlurView.setBitmap(bmpImage, 50, 100);
                }
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmpImage = touchBlurView.getDrawingCache();
                saveBitmap(bmpImage);
            }
        });

    }

    void saveBitmap(Bitmap mBitmap) {
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/BlurDemo";
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "Blur" + System.currentTimeMillis() + ".png");
            FileOutputStream fOut = null;

            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();


            Intent mIntent = new Intent(this,ImagePreview.class);
            mIntent.putExtra("filePath",file.getAbsolutePath());
            startActivityForResult(mIntent,REQUEST_PREVIEW);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PREVIEW && resultCode == Activity.RESULT_OK){
            touchBlurView.destroyDrawingCache();
        }
    }
}
