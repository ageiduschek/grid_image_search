package com.codepath.gridimagesearch.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.gridimagesearch.R;


import com.codepath.gridimagesearch.models.ImageResultModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * An adapter for displaying a single image result
 */
public class ImageResultAdapter extends ArrayAdapter<ImageResultModel> {

    private class ImageResultViews {
        ImageView ivImage;
        TextView tvImageTitle;
    }

    public ImageResultAdapter(Context context, List<ImageResultModel> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageResultModel imageInfo = getItem(position);

        ImageResultViews subviews;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);

            subviews = new ImageResultViews();

            subviews.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);

            convertView.setTag(subviews);
        } else {
            subviews = (ImageResultViews) convertView.getTag();
        }

        // Clear out old image
        subviews.ivImage.setImageResource(0);
        subviews.ivImage.setContentDescription(imageInfo.getTitle());

        Picasso.with(getContext()).load(imageInfo.getThumbURL()).into(subviews.ivImage);

        return convertView;
    }


}
