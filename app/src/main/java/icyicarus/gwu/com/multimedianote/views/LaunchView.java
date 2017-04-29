package icyicarus.gwu.com.multimedianote.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import icyicarus.gwu.com.multimedianote.R;

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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.e("launch new intent");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}

