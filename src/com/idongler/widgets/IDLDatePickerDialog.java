package com.idongler.widgets;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by fangjilue on 14-7-4.
 */
public class IDLDatePickerDialog extends DatePickerDialog {

    public IDLDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
        //super.onStop();
    }
}
