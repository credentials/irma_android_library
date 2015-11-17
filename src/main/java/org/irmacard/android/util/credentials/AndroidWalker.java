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
