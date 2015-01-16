package com.tesis.restapp.restapp.activities.search;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tesis.restapp.restapp.R;
import com.tesis.restapp.restapp.activities.search.adapters.ItemsAdapter;

public class ItemsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ItemsAdapter itemsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsAdapter = new ItemsAdapter(getActivity(), R.layout.listview_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ListView itemsListView = (ListView) rootView.findViewById(R.id.search_listview);
        itemsListView.setAdapter(itemsAdapter);
        itemsListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("itemId", (int) id);
        getActivity().setResult(getActivity().RESULT_OK, returnIntent);
        getActivity().finish();
    }

    public void filterByName(CharSequence name){
        if (itemsAdapter != null) {
        itemsAdapter.getFilter().filter(name);
        }
    }
}
