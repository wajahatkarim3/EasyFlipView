package com.wajahatkarim3.easyflipview.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.wajahatkarim3.easyflipview.EasyFlipView;

/**
 * Created by Sachin Varma on 18-12-2017.
 */

public class SimpleViewFlipActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_view);

    final EasyFlipView easyFlipView = (EasyFlipView) findViewById(R.id.easyFlipView);
    easyFlipView.setFlipDuration(400);
    easyFlipView.setFlipEnabled(true);

    findViewById(R.id.imgFrontCard).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(SimpleViewFlipActivity.this, "Front Card", Toast.LENGTH_SHORT).show();
        easyFlipView.flipTheView();
      }
    });

    findViewById(R.id.imgBackCard).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(SimpleViewFlipActivity.this, "Back Card", Toast.LENGTH_SHORT).show();
        easyFlipView.flipTheView();
      }
    });

    easyFlipView.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
      @Override
      public void onViewFlipCompleted(EasyFlipView easyFlipView, EasyFlipView.FlipState newCurrentSide) {
        Toast.makeText(SimpleViewFlipActivity.this,
                "Flip Completed! New Side is: " + newCurrentSide, Toast.LENGTH_LONG).show();
      }
    });



    final EasyFlipView easyFlipView2 = (EasyFlipView) findViewById(R.id.easyFlipView2);
    easyFlipView2.setFlipDuration(400);
    easyFlipView2.setToHorizontalType();
    easyFlipView2.setFlipTypeFromLeft();


  }
}