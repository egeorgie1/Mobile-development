
package com.example.mandelbrot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DrawActivity extends AppCompatActivity {

    public final static String _PATH_SD_CARD = "/mandelbrot";

    private ImageView imgV;
    private ZoomControls imagezoom;

    private Uri fileUrl;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        Intent intent = getIntent();
        color = intent.getIntExtra("color",0xffff0000);

        imagezoom=findViewById(R.id.zoomimage);
        imagezoom.hide();
    }

    // can we write?
    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;
        return false;

    }


    public void send(View v) {

        String[] _recipients = {"Elena Georgieva <elenageorgieva10@yahoo.com>"};

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_EMAIL, _recipients);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "A picture ...");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I'm sending. See it as attachment.");
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUrl);
        //apps to send our email?
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }

    }

    public void generate(View v){

        boolean _storage = isExternalStorageWritable();

        if (!_storage) return;

        imgV = (ImageView) findViewById(R.id.imgV);
        Bitmap b = Bitmap.createBitmap(imgV.getWidth(), imgV.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        int w = imgV.getWidth();
        int h = imgV.getHeight();

        int black = Color.BLACK;
        int maxIter = 1000;

        Paint p = new Paint();

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                double c_re = (col - w/2)*4.0/w;
                double c_im = (row - h/2)*4.0/h;
                double x = 0, y = 0;
                int iterations = 0;
                while (x*x+y*y < 4 && iterations < maxIter) {
                    double x_new = x*x-y*y+c_re;
                    y = 2*x*y+c_im;
                    x = x_new;
                    iterations++;
                }
                if (iterations < maxIter) {
                    p.setColor(color);
                    c.drawPoint(col, row, p);
                }
                else {
                    p.setColor(black);
                    c.drawPoint(col, row, p);
                }
            }
        }

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + _PATH_SD_CARD;

        String _ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {

            File dir = new File(fullPath);

            if (!dir.exists())
                dir.mkdirs();

            OutputStream fOut;

            File file = new File(fullPath, "g_img_" + _ts + ".png");

            fileUrl = Uri.fromFile(file);

            file.createNewFile();
            fOut = new FileOutputStream(file);

            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // toast notification:
        Toast.makeText(this, "Generated image stored to: " + fileUrl.getPath(), Toast.LENGTH_LONG).show();


        imgV.setImageDrawable(Drawable.createFromPath(fileUrl.getPath()));
        //Enable zooming:
        imagezoom.hide();

        imgV.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                imagezoom.show();
                return false;
            }
        });
        imagezoom.setOnZoomInClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                float x=imgV.getScaleX();
                float y=imgV.getScaleY();

                imgV.setScaleX(x+1);
                imgV.setScaleY(y+1);
                imagezoom.hide();
            }
        });

        imagezoom.setOnZoomOutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                float x=imgV.getScaleX();
                float y=imgV.getScaleY();

                imgV.setScaleX(x-1);
                imgV.setScaleY(y-1);
                imagezoom.hide();
            }
        });
    }

}
