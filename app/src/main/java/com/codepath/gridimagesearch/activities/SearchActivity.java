package com.codepath.gridimagesearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.gridimagesearch.adapters.ImageResultAdapter;
import com.codepath.gridimagesearch.fragments.SearchFiltersDialog;
import com.codepath.gridimagesearch.helpers.EndlessScrollListener;
import com.codepath.gridimagesearch.models.FilterModel;
import com.codepath.gridimagesearch.models.ImageResultModel;
import com.codepath.gridimagesearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements SearchFiltersDialog.OnSearchFiltersActionListener {
    private static final int PAGE_SIZE = 8;
    private SearchView mSearchView;
    private GridView gvResults;
    private FilterModel mSearchFilters = new FilterModel();

    private ImageResultAdapter mImageResultsAdapter;
    private SearchFiltersDialog mFiltersDialog;

    public static final String IMAGE_RESULT_EXTRA = "image_result_extra";
    private String mCurrentQuery = "";

    private void setupViews() {
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

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        mFiltersDialog = SearchFiltersDialog.newInstance(mSearchFilters);
        mFiltersDialog.show(fm, "fragment_search_filters_dialog");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        setupAdapters();


        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                fetchQueryResults(page);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mCurrentQuery = mSearchView.getQuery().toString();
                fetchQueryResults(0);
                hideSoftKeyboard(mSearchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchQueryResults(final int page) {
        if (page == 0) {
            mImageResultsAdapter.clear(); // Clear in cases where this is a new search
        }

        AsyncHttpClient client = new AsyncHttpClient();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("ajax.googleapis.com")
                .appendPath("ajax")
                .appendPath("services")
                .appendPath("search")
                .appendPath("images")
                .appendQueryParameter("v", "1.0")
                .appendQueryParameter("q", mCurrentQuery)
                .appendQueryParameter("rsz", Integer.toString(PAGE_SIZE))
                .appendQueryParameter("safe", mSearchFilters.getSafetyLevel().toString())
                .appendQueryParameter("start", Integer.toString(page * PAGE_SIZE));

        if (mSearchFilters.getFileType() != FilterModel.FileType.NO_FILTER) {
            builder.appendQueryParameter("as_filetype", mSearchFilters.getFileType().toString());
        }

        if (mSearchFilters.getColorization() != FilterModel.Colorization.NO_FILTER) {
            builder.appendQueryParameter("imgc", mSearchFilters.getColorization().toString());
        }

        if (mSearchFilters.getDominantColor() != FilterModel.DominantColor.NO_FILTER) {
            builder.appendQueryParameter("imgcolor", mSearchFilters.getDominantColor().toString());
        }

        if (mSearchFilters.getSize() != FilterModel.ImageSize.NO_FILTER) {
            builder.appendQueryParameter("imgsz", mSearchFilters.getSize().toString());
        }

        if (mSearchFilters.getSite() != null && !mSearchFilters.getSite().equals("")) {
            builder.appendQueryParameter("as_sitesearch", mSearchFilters.getSite());
        }

        String url = builder.build().toString();

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    JSONObject responseData = response.optJSONObject("responseData");
                    if (responseData == null) {
                        return;
                    }

                    JSONArray imageResultsJSON = responseData.getJSONArray("results");
                    if (imageResultsJSON == null) {
                        return;
                    }

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

    public void onFiltersSave(FilterModel filters) {
        mSearchFilters = filters;
        mFiltersDialog.dismiss();
        fetchQueryResults(0);
    }
}
