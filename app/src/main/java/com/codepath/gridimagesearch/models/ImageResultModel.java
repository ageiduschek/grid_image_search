package com.codepath.gridimagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A model for an image result
 */
public class ImageResultModel implements Parcelable {
    private String thumbURL;
    private String imageURL;
    private String title;
    private int width;
    private int height;

    public String getThumbURL() {
        return thumbURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageResultModel(JSONObject json) {
        try {
            this.thumbURL = json.getString("tbUrl");
            this.imageURL = json.getString("url");
            this.title = json.getString("title");
            this.width = Integer.parseInt(json.getString("width"));
            this.height = Integer.parseInt(json.getString("height"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<ImageResultModel> fromJSONArray(JSONArray array) {
        ArrayList<ImageResultModel> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResultModel(array.getJSONObject(i)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbURL);
        dest.writeString(this.imageURL);
        dest.writeString(this.title);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    private ImageResultModel(Parcel in) {
        this.thumbURL = in.readString();
        this.imageURL = in.readString();
        this.title = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<ImageResultModel> CREATOR = new Creator<ImageResultModel>() {
        public ImageResultModel createFromParcel(Parcel source) {
            return new ImageResultModel(source);
        }

        public ImageResultModel[] newArray(int size) {
            return new ImageResultModel[size];
        }
    };
}
