package com.codepath.gridimagesearch.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.models.ImageResultModel;
import com.squareup.picasso.Picasso;

public class LightboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbox);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageResultModel imageInfo = getIntent().getParcelableExtra(SearchActivity.IMAGE_RESULT_EXTRA);

        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        Picasso.with(this).load(imageInfo.getImageURL()).into(ivImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
