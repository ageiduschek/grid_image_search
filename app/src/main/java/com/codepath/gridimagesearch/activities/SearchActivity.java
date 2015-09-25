package com.codepath.gridimagesearch.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.gridimagesearch.adapters.ImageResultAdapter;
import com.codepath.gridimagesearch.models.ImageResultModel;
import com.codepath.gridimagesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private EditText etQuery;
    private GridView gvResults;

    private ImageResultAdapter mImageResultsAdapter;

    public static final String IMAGE_RESULT_EXTRA = "image_result_extra";

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchActivity.this, LightboxActivity.class);
                intent.putExtra(IMAGE_RESULT_EXTRA, mImageResultsAdapter.getItem(i));
                startActivity(intent);
            }
        });
    }

    private void setupAdapters() {
        mImageResultsAdapter = new ImageResultAdapter(this, new ArrayList<ImageResultModel>());
        gvResults.setAdapter(mImageResultsAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        setupAdapters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Fired whenever the button is pressed
    public void onSearch(View view) {
        fetchQueryResults(etQuery.getText().toString());
    }

    private void fetchQueryResults(final String queryString) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+ queryString +"&rsz=8";

        Log.d("ASDF", url);
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    JSONArray imageResultsJSON = response.getJSONObject("responseData").getJSONArray("results");
                    mImageResultsAdapter.clear(); // Clear in cases where this is a new search
                    mImageResultsAdapter.addAll(ImageResultModel.fromJSONArray(imageResultsJSON));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 200) {
                    throw new RuntimeException("onFailure called with 200 status");
                }

                // TODO: Handle failure here
                Toast.makeText(getApplicationContext(), "Image retreival failure: " + statusCode, Toast.LENGTH_SHORT).show();
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
