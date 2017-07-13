package com.project.pccontrol.shortcuts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.project.pccontrol.R;
import com.project.pccontrol.files.FilesFragment;

/**
 * Created by MacBookPro on 5/31/17.
 */

public class ShortcutsFragment extends Fragment implements ShortcutContractor.View{
    private static ShortcutsFragment Instance = null;
    private ShortcutContractor.Presenter mShortcutPresenter;

    private RelativeLayout cut,paste,copy,undo,select,search;
    public static ShortcutsFragment getInstance() {
        if (Instance == null) {
            Instance = new ShortcutsFragment();
        }
        return Instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shortcuts, container, false);

        copy = (RelativeLayout) rootView.findViewById(R.id.copy);
        cut = (RelativeLayout) rootView.findViewById(R.id.cut);
        search = (RelativeLayout) rootView.findViewById(R.id.search);
        undo = (RelativeLayout) rootView.findViewById(R.id.undo);
        select = (RelativeLayout) rootView.findViewById(R.id.select);
        paste = (RelativeLayout) rootView.findViewById(R.id.paste);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(36);
            }
        });
        cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(35);
            }
        });
          paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(37);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(39);
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(40);
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShortcutPresenter.sendAction(38);
            }
        });

        return rootView;
    }


    @Override
    public void setPresenter(ShortcutContractor.Presenter presenter) {
        mShortcutPresenter = presenter;
    }

    @Override
    public void showMessage(@Nullable String message) {
        if (isAdded())
            Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();

    }
}
