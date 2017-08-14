package com.example.nguyentrung.docbao.Dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class DialogLoading extends ProgressDialog {
    ProgressDialog dialog;
    public DialogLoading(Context context, String message, boolean indeterminate, boolean cancel) {
        super(context);
        setMessage(message);
        setIndeterminate(indeterminate);
        setCancelable(cancel);
    }
}
