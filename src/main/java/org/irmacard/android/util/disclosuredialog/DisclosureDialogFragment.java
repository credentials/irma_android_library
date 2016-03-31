/*
 * Copyright (c) 2015, the IRMA Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the IRMA project nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.irmacard.android.util.disclosuredialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentialdetails.AttributesRenderer;
import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.credentials.Attributes;

import java.util.HashMap;

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
