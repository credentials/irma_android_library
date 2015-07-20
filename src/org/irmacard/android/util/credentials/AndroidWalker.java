/**
 * AndroidWalker.java
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

package org.irmacard.android.util.credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;
import org.irmacard.credentials.info.TreeWalkerI;
import org.irmacard.credentials.info.VerificationDescription;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AndroidWalker implements TreeWalkerI {
	static final String IRMA_CORE = "irma_configuration/";
	static final String TAG = "AWalker";
	
	DescriptionStore descriptionStore;
	AssetManager assetManager;
	
	public AndroidWalker(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public void parseConfiguration(DescriptionStore descriptionStore)
			throws InfoException {
		this.descriptionStore = descriptionStore;
		Log.i("parseConfiguration", "Android Walker parsing started!");
		
		InputStream s;
		try {
			List<String> issuers = new ArrayList<String>();
			List<String> verifiers = new ArrayList<String>();
			Set<String> allEntities = new HashSet<String>();
			String line = null;

			s = assetManager.open(IRMA_CORE + "android/issuers.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(s));
			while ((line = in.readLine()) != null)
				issuers.add(line);

			s = assetManager.open(IRMA_CORE + "android/verifiers.txt");
			in = new BufferedReader(new InputStreamReader(s));
			while ((line = in.readLine()) != null)
				verifiers.add(line);

			allEntities.addAll(issuers);
			allEntities.addAll(verifiers); // allEntities is a Set which ignore duplicates

			for (String i: allEntities) {
				String issuerDesc = IRMA_CORE + i + "/description.xml";
				IssuerDescription id = new IssuerDescription(assetManager.open(issuerDesc));
				descriptionStore.addIssuerDescription(id);
			}

			for (String issuer: issuers) {
				tryProcessCredentials(issuer);
			}

			for (String verifier: verifiers) {
				tryProcessVerifications(verifier);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new InfoException(e,
					"Failed to read (from) android/issuers.txt or android/verifiers.txt file");
		}
	}
	
	private void tryProcessCredentials(String issuer) throws InfoException {
		String path = IRMA_CORE + issuer + "/Issues";
		
		try {
			for(String cred : assetManager.list(path)) {
				String credentialSpec = path + "/" + cred + "/description.xml";
				CredentialDescription cd = new CredentialDescription(assetManager.open(credentialSpec));
				descriptionStore.addCredentialDescription(cd);
			}
		} catch (IOException e) {
			throw new InfoException(e,
					"Failed to read credentials issued by " + issuer + ".");
		}
	}
	
	private void tryProcessVerifications(String verifier) throws InfoException {
		String path = IRMA_CORE + verifier + "/Verifies";

		try {
			for(String cred : assetManager.list(path)) {
				String proofSpec = path + "/" + cred + "/description.xml";
				VerificationDescription vd = new VerificationDescription(assetManager.open(proofSpec));
				descriptionStore.addVerificationDescription(vd);
			}
		} catch (IOException e) {
			throw new InfoException(e,
					"Failed to read verifications used by " + verifier + ".");
		}
	}

	@Override
	public InputStream retrieveFile(URI path) throws InfoException {
		try {
			return assetManager.open(IRMA_CORE + path.toString());
		} catch (IOException e) {
			throw new InfoException(e, "reading file " + path);
		}
	}

	public Bitmap getIssuerLogo(IssuerDescription issuer) {
		Bitmap logo = null;
		String issuerID = issuer.getID();

		try {
			logo = BitmapFactory.decodeStream(retrieveFile(new URI(issuerID + "/logo.png")));
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logo;
	}
	
	public Bitmap getVerifierLogo(VerificationDescription verification) {
		Bitmap logo = null;

		String verifierID = verification.getVerifierID();

		try {
			logo = BitmapFactory.decodeStream(retrieveFile(new URI(verifierID + "/logo.png")));
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return logo;
	}
}
