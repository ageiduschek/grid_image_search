package com.codepath.gridimagesearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.models.ImageResultModel;
import com.squareup.picasso.Picasso;

import java.io.File;

public class LightboxActivity extends AppCompatActivity {

    private static final int HIDDEN_SYSTEM_UI_FLAGS = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

    private static final int HIDE_DELAY_MS = 3000;

    private final Handler mHandler = new Handler();
    private Runnable mEnterImmersiveModeRunnable = new Runnable() {
        @Override
        public void run() {
            enterImmersiveMode();
        }
    };

    View tvTitleFrame;

    public void setupImmersiveMode() {
        tvTitleFrame = findViewById(R.id.flImageTitleFrame);
        View view = getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & HIDDEN_SYSTEM_UI_FLAGS) == 0) {
                    // If this change didn't hide the soft buttons / status bar,
                    // then we show the title and share actions
                    exitImmersiveMode();
                }
            }
        });

        enterImmersiveMode();
    }

    private void enterImmersiveMode(){
        View view = getWindow().getDecorView();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        tvTitleFrame.setVisibility(View.GONE);

        view.setSystemUiVisibility(HIDDEN_SYSTEM_UI_FLAGS);
    }

    private void exitImmersiveMode() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            tvTitleFrame.setVisibility(View.VISIBLE);
        }

        enterImmersiveModeAfterDelay();
    }


    public void enterImmersiveModeAfterDelay() {
        // Cancel any pending posts on the runnable
        mHandler.removeCallbacks(mEnterImmersiveModeRunnable);
        mHandler.postDelayed(mEnterImmersiveModeRunnable, HIDE_DELAY_MS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbox);


        ImageResultModel imageInfo = getIntent().getParcelableExtra(SearchActivity.IMAGE_RESULT_EXTRA);

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        String htmlStrippedTitle = Html.fromHtml(imageInfo.getTitle()).toString();
        ivImage.setContentDescription(htmlStrippedTitle);

        TextView tvImageTitle = (TextView) findViewById(R.id.tvImageTitle);
        tvImageTitle.setText(htmlStrippedTitle);

        Picasso.with(this)
                .load(imageInfo.getImageURL())
                .fit()
                .centerInside()
                .into(ivImage);

        setupImmersiveMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lightbox, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_share:
                startShareActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startShareActivity() {
        ImageView siv = (ImageView) findViewById(R.id.ivImage);
        Drawable mDrawable = siv.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();

        if (fixMediaDir()) {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    mBitmap, "Image Description", null);

            Uri uri = Uri.parse(path);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(getContentResolver().getType(uri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share image using"));
        }
    }

    // Hacky fix that creates the DCIM folder when it doesn't exist (an emulator bug)
    public static boolean fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) {
            return false;
        }

        File dcim = new File(sdcard, "DCIM");
        File camera = new File(dcim, "Camera");

        return camera.exists() || camera.mkdir();
    }
}
