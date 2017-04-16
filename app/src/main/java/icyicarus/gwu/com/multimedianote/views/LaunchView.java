package icyicarus.gwu.com.multimedianote.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import icyicarus.gwu.com.multimedianote.R;


/**
 * Created by IcarusXu on 4/2/2017.
 */

public class LaunchView extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(LaunchView.this, FragmentContainerView.class));
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_launch);

        ImageView ivLaunch = (ImageView) findViewById(R.id.imageViewLaunchBackground);
        WindowManager windowManager = this.getWindowManager();
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, windowManager.getDefaultDisplay().getWidth() / 2, windowManager.getDefaultDisplay().getHeight() / 2);
        scaleAnimation.setDuration(2500);
        scaleAnimation.setFillAfter(true);
        ivLaunch.setAnimation(scaleAnimation);
        scaleAnimation.startNow();
        handler.postDelayed(runnable, 2000);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}

