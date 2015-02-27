package studio.idle.emicalculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by ujain on 2/21/15.
 */
public class ErrorDialogFragment extends DialogFragment {
    Context mContext;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Please provide following inputs");
        alertDialogBuilder.setMessage(getMessage());
        return alertDialogBuilder.create();
    }
}
