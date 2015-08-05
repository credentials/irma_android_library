package org.irmacard.android.util.disclosuredialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentialdetails.AttributesRenderer;
import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.credentials.Attributes;
import org.irmacard.idemix.util.VerificationSetupData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DialogFragment for asking permission of a user to disclose specified attributes.
 */
public class DisclosureDialogFragment extends DialogFragment {
	private HashMap<CredentialPackage,Attributes> credentials;
	private DisclosureDialogListener listener;

	public interface DisclosureDialogListener {
		public void onDiscloseOK();
		public void onDiscloseCancel();
	}

	/**
	 * Constructs and returns a new DisclosureDialogFragment. Users must implement the DisclosureDialogListener
	 * interface.
	 *
	 * @param credentials A hashmap in which the keys are the credentials and the values the disclosed attributes.
	 * @return The new dialog.
	 */
	public static DisclosureDialogFragment newInstance(HashMap<CredentialPackage,Attributes> credentials) {
		DisclosureDialogFragment dialog = new DisclosureDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable("credentials", credentials);
		dialog.setArguments(args);

		return dialog;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		credentials = (HashMap<CredentialPackage, Attributes>) getArguments().getSerializable("credentials");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_disclosure, null);
		LinearLayout list = (LinearLayout) view.findViewById(R.id.attributes_container);
		AttributesRenderer renderer = new AttributesRenderer(getActivity(), inflater);

		for (CredentialPackage credential : credentials.keySet()) {
			Attributes disclosed = credentials.get(credential);
			renderer.render(credential, list, true, disclosed);
		}

		String question1 = getResources()
				.getQuantityString(R.plurals.disclose_question_1, credentials.size());
		((TextView) view.findViewById(R.id.disclosure_question_1)).setText(question1);

		return new AlertDialog.Builder(getActivity())
				.setTitle("Disclose attributes?")
				.setView(view)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onDiscloseOK();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onDiscloseCancel();
					}
				})
				.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Set our listener so we can send events to the host
			listener = (DisclosureDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement DisclosureDialogListener");
		}
	}
}
