package com.example.ownzandy.thinc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TabFragment2 extends Fragment {
    private ListView medList;
    private ArrayList<String> toAdd = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.allergies1, container, false);

        medList = (ListView) rootView.findViewById(R.id.myListView);

        addItems();

        return rootView;
    }

    public void addItems(){
        //for statement iterating over info and adding each to listViewArray
//      for (int i=0;i<myArray.size();i++){
//            toAdd.add(toAdd.get(i));
//        }
        toAdd.add("Redinozone 5mL tablet.");
        toAdd.add("Insulin half dose with bonus 10mg antinflammatory agents");
        toAdd.add("20mL shot weekly antibiotics");

        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, toAdd);
        medList.setAdapter(adapter);
    }
}

