package com.wajahatkarim3.easyflipview.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

public class FlipOnceExampleActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_once_example);


        final EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.cardFlipView);
        easyFlipView.setFlipDuration(1000);
        easyFlipView.setFlipEnabled(true);

        findViewById(R.id.king_front_side).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FlipOnceExampleActivity.this, "Front Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();
            }
        });

        findViewById(R.id.king_back_side).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FlipOnceExampleActivity.this, "Back Card", Toast.LENGTH_SHORT).show();
                easyFlipView.flipTheView();
            }
        });

        easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView easyFlipView, EasyFlipView.FlipState newCurrentSide) {
                Toast.makeText(FlipOnceExampleActivity.this,
                        "Flipped once ! Ace revealed " + newCurrentSide, Toast.LENGTH_LONG).show();
            }
        });
    }
}



