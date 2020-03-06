
package com.example.mandelbrot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void drawRed(View v){ toDrawActivity(0xffff0000); }

    public void drawGreen(View v){
        toDrawActivity (0xff00ff00);
    }

    public void drawBlue(View v){
        toDrawActivity (0xff0000ff);
    }

    public void drawYellow(View v){
        toDrawActivity(0xffffff00);
    }

    public void drawCyan(View v){
        toDrawActivity(0xff00ffff);
    }

    public void drawMagenta(View v){
        toDrawActivity(0xffff00ff);
    }

    public void drawWhite(View v){
        toDrawActivity(0xffffffff);
    }
    public void toDrawActivity(int color){
        Intent drawIntent = new Intent(this, DrawActivity.class);
        drawIntent.putExtra("color", color);
        startActivity(drawIntent);
    }
}