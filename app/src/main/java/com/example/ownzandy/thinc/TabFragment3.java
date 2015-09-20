package com.example.ownzandy.thinc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabFragment3 extends Fragment {
    private ListView diagnosis;
    private ArrayList<Map<String, String>> toAdd = new ArrayList<Map<String, String>>();
    private SimpleAdapter adapter;
    private ArrayList<String> myDiag;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.medication2, container, false);

        Map<String,ArrayList<String>> myData = ((MainActivity) getActivity()).getDataMap();
        myDiag = myData.get("condition");

        diagnosis = (ListView) rootView.findViewById(R.id.myListView);

        ArrayList<Map<String, String>> toAdd = builds();
        String[] from = {"desc", "date"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        adapter = new SimpleAdapter(getActivity(), toAdd, android.R.layout.simple_list_item_2, from, to);
        diagnosis.setAdapter(adapter);

        return rootView;
    }

    private ArrayList<Map<String, String>> builds() {
        //for statement iterating over info and adding each to listViewArray
        ArrayList<Map<String, String>> toAdd = new ArrayList<Map<String, String>>();
        //for loop here
        for (int i=0; i<myDiag.size()-1; i+=2){
            toAdd.add(putData(myDiag.get(i),myDiag.get(i+1)));
        }
//        toAdd.add(putData("Atrial Fibrilation", "6/15"));
//        toAdd.add(putData("Heat Stroke, diagnosed", "7/12"));
//        toAdd.add(putData("PTSD", "10/10"));
        return toAdd;
    }

    private HashMap<String, String> putData(String desc, String date) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("desc", desc);
        item.put("date", date);
        return item;
    }
}