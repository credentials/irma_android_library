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

package org.irmacard.android.util.credentialdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

/**
 * An activity representing a single Credential detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * CredentialListActivity.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link CredentialDetailFragment}.
 */
public class CredentialDetailActivity extends FragmentActivity implements
		CredentialDetailFragment.Callbacks {
	public static final String ARG_RESULT_DELETE = "deletedCred";
	public static final int RESULT_DELETE = RESULT_FIRST_USER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credential_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.

			Attributes attributes = (Attributes) getIntent().getSerializableExtra(CredentialDetailFragment.ATTRIBUTES);
			int hashCode = getIntent().getIntExtra(CredentialDetailFragment.HASHCODE, 0);

			Bundle arguments = new Bundle();
			arguments.putSerializable(CredentialDetailFragment.ATTRIBUTES, attributes);
			arguments.putInt(CredentialDetailFragment.HASHCODE, hashCode);

			CredentialDetailFragment fragment = new CredentialDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.credential_detail_container, fragment).commit();
			getActionBar().setTitle(attributes.getCredentialIdentifier().getCredentialName());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.i("CDA", "Up button pressed, returning;");
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDeleteCredential(int hashCode) {
		Intent data = new Intent(this, CredentialDetailActivity.class);
		data.putExtra(CredentialDetailActivity.ARG_RESULT_DELETE, hashCode);
		setResult(RESULT_DELETE, data);
		finish();
	}
}
