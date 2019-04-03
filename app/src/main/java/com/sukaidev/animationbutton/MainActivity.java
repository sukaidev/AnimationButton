package com.sukaidev.animationbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sukaidev.animationbutton.widget.AnimationButton;

public class MainActivity extends AppCompatActivity {

    private AnimationButton mAnimationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAnimationButton = findViewById(R.id.button);

        mAnimationButton.setAnimationButtonListener(new AnimationButton.AnimationButtonListener() {
            @Override
            public void onClick() {
                mAnimationButton.start();
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "动画播放完毕！", Toast.LENGTH_SHORT).show();
                mAnimationButton.reset();
            }
        });
    }
}
