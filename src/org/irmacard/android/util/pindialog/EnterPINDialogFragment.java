package org.irmacard.android.util.pindialog;

import org.irmacard.android.util.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EnterPINDialogFragment extends DialogFragment {
	private static final String EXTRA_TRIES = "irma_library.EnterPINDialogFragment.tries";
	
	public interface PINDialogListener {
		public void onPINEntry(String pincode);
        public void onPINCancel();
	}

    PINDialogListener mListener;
    int tries;
	private AlertDialog dialog;

	public static EnterPINDialogFragment getInstance(int tries) {
        EnterPINDialogFragment f = new EnterPINDialogFragment();

        Bundle args = new Bundle();
        args.putInt(EXTRA_TRIES, tries);
        f.setArguments(args);

        return f;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// For backward compatibility
    	if(getArguments().containsKey(EXTRA_TRIES)) {
    		tries = getArguments().getInt(EXTRA_TRIES);
    	} else {
    		tries = -1;
    	}
    }

    private void okOnDialog(View dialogView) {
        EditText et = (EditText)dialogView.findViewById(R.id.pincode);
        String pincodeText = et.getText().toString();
        mListener.onPINEntry(pincodeText);
    }

    private void dismissDialog() {
        dialog.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_pinentry, null);
        builder.setView(dialogView)
        	.setTitle("Enter PIN")
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                  okOnDialog(dialogView);
	               }
	           })
	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   mListener.onPINCancel();
	               }
	           });
        // Create the AlertDialog object and return it
        dialog = builder.create();
        // Make sure that the keyboard is always shown and doesn't require an additional touch
        // to focus the TextEdit view.
        dialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        TextView error_field = (TextView) dialogView.findViewById(R.id.enterpin_error);
        EditText pin_field = (EditText) dialogView.findViewById(R.id.pincode);
        pin_field.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					okOnDialog(dialogView);
					dismissDialog();
				}
				return false;
            }
        });

        if(tries != -1) {
        	error_field.setVisibility(View.VISIBLE);
        	error_field.setText(getResources().getQuantityString(R.plurals.error_tries_left, tries, tries));
        }
        
        // prevent cancelling the dialog by pressing outside the bounds
        dialog.setCanceledOnTouchOutside(false);
        
        pin_field.requestFocus();

        return dialog;
    }
    
    // Override the Fragment.onAttach() method to instantiate the PINDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PINDialogListener so we can send events to the host
            mListener = (PINDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PINDialogListener");
        }
    }
}
