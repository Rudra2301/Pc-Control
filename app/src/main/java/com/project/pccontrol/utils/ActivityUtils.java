package com.project.pccontrol.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by MacBookPro on 5/28/17.
 */

public class ActivityUtils {
    public static void addFragmentToActivity(@Nullable FragmentManager manager
            , @Nullable Fragment fragment, int containerId) {
        checkNotNull(manager);
        checkNotNull(fragment);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.commit();
    }


}
