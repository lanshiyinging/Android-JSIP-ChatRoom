
package com.rance.chatui.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rance.chatui.enity.PeerInfo;
import com.rance.chatui.util.Constants;

import java.util.ArrayList;
import java.util.List;

import bupt.jsip_demo.R;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class PeerListFragment extends ListFragment implements FragmentBackHandler{

    PeerListAdapter listAdapter = null;
    ArrayList<PeerInfo> peers = new ArrayList<>();
    boolean backHandled;

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.devices_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = new PeerListAdapter(this.getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1,
                peers);
        setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        ((PeerClickListener) getActivity()).chatP2p((PeerInfo) l.getItemAtPosition(position));
    }

    public void setPeers(ArrayList<PeerInfo> peerList){
        for(PeerInfo peer : peerList) peers.add(peer);
    }

    public class PeerListAdapter extends ArrayAdapter<PeerInfo> {

        private List<PeerInfo> items;

        public PeerListAdapter(Context context, int resource,
                                  int textViewResourceId, ArrayList<PeerInfo> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            PeerInfo peer = items.get(position);
            if (peer != null) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(peer.getUsername());
                }
                TextView statusText = (TextView) v
                        .findViewById(android.R.id.text2);
                statusText.setText(getPeerStatus(peer.getStatus()));
            }
            return v;
        }

    }

    public static String getPeerStatus(int statusCode) {
        switch (statusCode) {
            case Constants.ON_LINE:
                return "Online";
            case Constants.OFF_LINE:
                return "Offline";
            case Constants.BUSY:
                return "Busy";
            default:
                return "Unknown";

        }
    }

}
