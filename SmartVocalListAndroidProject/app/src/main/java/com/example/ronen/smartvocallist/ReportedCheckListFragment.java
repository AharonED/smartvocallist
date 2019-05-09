package com.example.ronen.smartvocallist;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportedCheckListFragment extends Fragment {


    public ReportedCheckListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reported_check_list, container, false);
        String textToDisplay = ReportedCheckListFragmentArgs.fromBundle(getArguments()).getTextToDisplay();
        ((TextView)view.findViewById(R.id.reportedCheckListTextView)).setText(textToDisplay);
        return view;
    }

}
