package com.project.pccontrol.files;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.pccontrol.R;
import com.project.pccontrol.mouse.MouseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class FilesFragment extends Fragment implements FilesContractor.View{

    private ListView mFilesRecycler;
    private TextView mDirectory;
    private ArrayList<String> files;
    private ArrayAdapter<String> adapter;

    private  FilesContractor.Presenter mFilesPresenter;

    private static FilesFragment Instance = null;
    public static FilesFragment getInstance(){
        if (Instance == null){
            Instance = new FilesFragment();
        }
        return Instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_files,container,false);

        files = new ArrayList<>();
        mDirectory = (TextView) rootView.findViewById(R.id.directory);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.file_row, this.files);
        mFilesRecycler = (ListView) rootView.findViewById(R.id.files_recycler);
        mFilesRecycler.setAdapter(adapter);
        mFilesRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFilesPresenter.sendFileExploreRequest(files.get(position));
            }
        });


        return rootView;
    }

    @Override
    public void setPresenter(FilesContractor.Presenter presenter) {
        mFilesPresenter = presenter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFilesPresenter.start();
    }




    @Override
    public void showMessage(@Nullable String message) {
        if (isAdded())
            Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();

    }

    @Override
    public void returnedFiles(List<String> mfile, final String directory) {
        files.clear();
        files.addAll(mfile);
        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                mDirectory.setText(""+directory);
                adapter.notifyDataSetChanged();
                mFilesRecycler.setSelectionAfterHeaderView();
            }
        });
    }
}


