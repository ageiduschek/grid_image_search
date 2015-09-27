package com.codepath.gridimagesearch.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.models.FilterModel;

/**
 * A {@link DialogFragment} for selecting filters.
 * Activities that contain this fragment must implement the
 * {@link OnSearchFiltersActionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFiltersDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFiltersDialog extends DialogFragment {
    private static final String FILTERS_ARG_PARAM = "filters_arg_param";

    private FilterModel mFilters;

    private OnSearchFiltersActionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param filters search filters
     * @return A new instance of fragment SearchFiltersDialog.
     */
    public static SearchFiltersDialog newInstance(FilterModel filters) {
        SearchFiltersDialog fragment = new SearchFiltersDialog();
        Bundle args = new Bundle();
        args.putParcelable(FILTERS_ARG_PARAM, filters);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFiltersDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilters = getArguments().getParcelable(FILTERS_ARG_PARAM);
        }
    }

    private class SimpleSpinnerAdapter extends ArrayAdapter<Enum> {

        public SimpleSpinnerAdapter(Context context, Enum[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View dialog =  inflater.inflate(R.layout.fragment_search_filters_dialog, container, false);

        getDialog().setTitle(R.string.filter_title);

        final Spinner spinnerFileType = initializeSpinnerWithEnum(dialog, R.id.spinnerFileType,
                FilterModel.FileType.class, mFilters.getFileType());

        final Spinner spinnerColorization = initializeSpinnerWithEnum(dialog, R.id.spinnerColorization,
                FilterModel.Colorization.class, mFilters.getColorization());

        final Spinner spinnerImageSize = initializeSpinnerWithEnum(dialog, R.id.spinnerImageSize,
                FilterModel.ImageSize.class, mFilters.getSize());

        final Spinner spinnerSafetyLevel = initializeSpinnerWithEnum(dialog, R.id.spinnerSafetyLevel,
                FilterModel.SafetyLevel.class, mFilters.getSafetyLevel());

        final EditText etSite = (EditText)dialog.findViewById(R.id.etSite);
        etSite.setText(mFilters.getSite());

        Button button = (Button)dialog.findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFilters.setFileType((FilterModel.FileType) spinnerFileType.getSelectedItem());
                mFilters.setColorization((FilterModel.Colorization) spinnerColorization.getSelectedItem());
                mFilters.setSize((FilterModel.ImageSize) spinnerImageSize.getSelectedItem());
                mFilters.setSafetyLevel((FilterModel.SafetyLevel) spinnerSafetyLevel.getSelectedItem());
                mFilters.setSite(etSite.getText().toString());
                mListener.onFiltersSave(mFilters);
            }
        });

        return dialog;
    }

    public <E extends Enum<E>> Spinner initializeSpinnerWithEnum(View dialog,int spinnerResId, Class<E> enumClass, Enum defaultValue){
        final Spinner spinner = (Spinner)dialog.findViewById(spinnerResId);
        ArrayAdapter<Enum> arrayAdapter = new SimpleSpinnerAdapter(getActivity(), enumClass.getEnumConstants());
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(arrayAdapter.getPosition(defaultValue));
        return spinner;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSearchFiltersActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSearchFiltersActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSearchFiltersActionListener {
        public void onFiltersSave(FilterModel filters);
    }

}
