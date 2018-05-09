package com.example.shizhuan.chelaile_ui;

import android.support.v4.app.Fragment;

import com.kcode.lib.dialog.UpdateActivity;

/**
 * Created by ShiZhuan on 2018/5/8.
 */

public class CustomsUpdateActivity extends UpdateActivity
{
    protected Fragment getUpdateDialogFragment()
    {
        return CustomsUpdateFragment.newInstance(this.mModel,"",false);
//        return super.getUpdateDialogFragment();
    }
}
