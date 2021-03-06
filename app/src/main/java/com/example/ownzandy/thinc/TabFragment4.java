package com.example.ownzandy.thinc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 9/20/15.
 */
public class TabFragment4 extends Fragment{
    private ListView procedures;
    private ArrayList<Map<String, String>> toAdd = new ArrayList<Map<String, String>>();
    private SimpleAdapter adapter;
    private String myIns;
    private ArrayList<String> myProcedures;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.procedures4, container, false);
        //get data from bundle

        Map<String,ArrayList<String>> myData = ((MainActivity) getActivity()).getDataMap();
        myProcedures = myData.get("procedure");

        //progammatically add insurance info at bottom.
//        RelativeLayout relativeLayout =
//                (RelativeLayout) rootView.findViewById(R.id.rootLayout);
//        TextView textView = new TextView (getActivity());
//        textView.setLayoutParams(new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.FILL_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT));
//        //get insurance from data
//        textView.setText("myIns");
//        relativeLayout.addView(textView);

        procedures = (ListView) rootView.findViewById(R.id.myListView);

        ArrayList<Map<String, String>> toAdd = builds();
        String[] from = {"desc", "date"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        adapter = new SimpleAdapter(getActivity(), toAdd, android.R.layout.simple_list_item_2, from, to);
        procedures.setAdapter(adapter);

        return rootView;
    }

    private ArrayList<Map<String, String>> builds() {
        //for statement iterating over info and adding each to listViewArray
        ArrayList<Map<String, String>> toAdd = new ArrayList<Map<String, String>>();
        //for loop here
        for (int i=0; i<myProcedures.size()-1; i+=2){
            toAdd.add(putData(myProcedures.get(i),myProcedures.get(i+1)));
        }
//        toAdd.add(putData("Atrial Fibrilation", "7/24/09"));
//        toAdd.add(putData("Heat Stroke, diagnosed", "7/24/09"));
//        toAdd.add(putData("PTSD", "11/30/11"));
        return toAdd;
    }

    private HashMap<String, String> putData(String desc, String date) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("desc", desc);
        item.put("date", date);
        return item;
    }
}

