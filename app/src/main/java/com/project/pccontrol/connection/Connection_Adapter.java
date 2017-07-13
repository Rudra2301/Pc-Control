package com.project.pccontrol.connection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.project.pccontrol.R;
import com.project.pccontrol.model.Connection;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class Connection_Adapter
        extends RealmBasedRecyclerViewAdapter<Connection, Connection_Adapter.ConHolder> {
    private int used;
    private Connection_Adapter.onConnection mOnConnection;


    public Connection_Adapter(
            Context context,
            RealmResults<Connection> realmResults,
            boolean automaticUpdate,
            boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ConHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.con_item, viewGroup, false);
        ConHolder vh = new ConHolder(v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ConHolder holder, final int position) {
        final Connection connection = realmResults.get(position);
        holder.use.setChecked(position == used);
        if(connection.getType() == Connection.ConType.bluetooth){
            holder.icon.setImageResource(R.drawable.bluetooth);
        }
        else {
            holder.icon.setImageResource(R.drawable.wifi);

        }
        holder.name.setText(connection.getName());
        holder.use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used = position;
                notifyDataSetChanged();
                mOnConnection.onConnectionClick(position);

            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used = position;
                notifyDataSetChanged();
                mOnConnection.onConnectionClick(position);


            }
        });
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used = position;
                notifyDataSetChanged();
                mOnConnection.onConnectionClick(position);


            }
        });


    }

    public class ConHolder extends RealmViewHolder{
        public RadioButton use;
        public ImageView icon;
        public TextView name;

        public ConHolder(View itemView) {
            super(itemView);
            use = (RadioButton) itemView.findViewById(R.id.con_use);
            icon = (ImageView) itemView.findViewById(R.id.con_icon);
            name = (TextView) itemView.findViewById(R.id.con_name);


        }
    }
    public void setListiner(Connection_Adapter.onConnection listiner){
        mOnConnection = listiner;
    }

    public interface onConnection{
        void onConnectionClick(int position);
    }

}
