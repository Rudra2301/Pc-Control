package com.project.pccontrol.files;

import android.support.annotation.Nullable;

import com.project.pccontrol.BasePresenter;
import com.project.pccontrol.BaseView;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
/**
 * Created by MacBookPro on 5/30/17.
 */

public interface FilesContractor {

    interface View extends BaseView<Presenter>{

        void showMessage(@Nullable String message);

        void returnedFiles(List<String> files, String dir);


    }
    interface Presenter extends BasePresenter{

        void sendFileExploreRequest(String fileString);


    }

}
