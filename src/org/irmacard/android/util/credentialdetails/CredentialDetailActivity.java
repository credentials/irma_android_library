/**
 * CredentialDetailActivity.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
 */

package org.irmacard.android.util.credentialdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentials.CredentialPackage;
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
	CredentialPackage credential;

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
			credential = (CredentialPackage) getIntent().getSerializableExtra(
					CredentialDetailFragment.ARG_ITEM);
			Bundle arguments = new Bundle();
			arguments.putSerializable(CredentialDetailFragment.ARG_ITEM,
					credential);
			CredentialDetailFragment fragment = new CredentialDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.credential_detail_container, fragment).commit();
			getActionBar().setTitle(credential.getCredentialDescription().getName());
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
	public void onDeleteCredential(CredentialDescription cd) {
		Intent data = new Intent(this, CredentialDetailActivity.class);
		data.putExtra(CredentialDetailActivity.ARG_RESULT_DELETE, cd);
		setResult(RESULT_DELETE, data);
		finish();
	}
}
