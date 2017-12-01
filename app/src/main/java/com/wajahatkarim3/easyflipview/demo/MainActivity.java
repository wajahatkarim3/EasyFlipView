package com.wajahatkarim3.easyflipview.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.easyFlipView);
        easyFlipView.setFlipDuration(1000);
        easyFlipView.setFlipEnabled(true);

        findViewById(R.id.imgFrontCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Front Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();

            }
        });

        findViewById(R.id.imgBackCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Back Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();
            }
        });

        easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView.FlipState newCurrentSide) {
                Toast.makeText(MainActivity.this, "Flip Completed! New Side is: " + newCurrentSide, Toast.LENGTH_LONG).show();
            }
        });

    }
}
