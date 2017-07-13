package com.project.pccontrol.shortcuts;

import com.project.pccontrol.BasePresenter;
import com.project.pccontrol.BaseView;

/**
 * Created by MacBookPro on 5/31/17.
 */

public interface ShortcutContractor {

    interface View extends BaseView<Presenter>{
        void showMessage(String message);

    }


    interface Presenter extends BasePresenter{
        void sendAction(int actionNum);
    }
}
