package org.irmacard.android.util.credentials;

import android.content.Context;
import android.os.AsyncTask;
import org.irmacard.api.common.SessionRequest;
import org.irmacard.credentials.idemix.info.IdemixKeyStore;
import org.irmacard.credentials.idemix.info.IdemixKeyStoreSerializer;
import org.irmacard.credentials.info.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manager for {@link DescriptionStore} and {@link IdemixKeyStore}: handles downloading new items
 * asynchroniously, and serializing them to storage.
 */
@SuppressWarnings("unused")
public class StoreManager implements DescriptionStoreSerializer, IdemixKeyStoreSerializer {
	/**
	 * Callbacks for when downloading new store items.
	 */
	public interface DownloadHandler {
		void onSuccess();
		void onError(Exception e);
	}

	private Context context;

	public StoreManager(Context context) {
		this.context = context;
	}

	@Override
	public void saveCredentialDescription(CredentialDescription cd, String xml) {
		String issuer = cd.getIssuerID();

		File issuesDir = new File(getIssuerPath(issuer), "Issues/" + cd.getCredentialID());
		if (!issuesDir.mkdirs() && !issuesDir.isDirectory())
			throw new RuntimeException("Could not create issuing path");

		try {
			FileOutputStream fos = new FileOutputStream(new File(issuesDir, "description.xml"));
			fos.write(xml.getBytes());
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void saveIssuerDescription(IssuerDescription issuer, String xml, InputStream logo) {
		File issuerDir = getIssuerPath(issuer.getID());
		writeString(issuerDir, "description.xml", xml);
		writeStream(issuerDir, "logo.png", logo);
	}

	@Override
	public void saveIdemixKey(IssuerDescription issuer,
	                          String key, String groupParameters, String systemParameters) {
		File issuerDir = getIssuerPath(issuer.getID());
		writeString(issuerDir, IdemixKeyStore.PUBLIC_KEY_FILE, key);
		writeString(issuerDir, IdemixKeyStore.GROUP_PARAMS_FILE, groupParameters);
		writeString(issuerDir, IdemixKeyStore.SYSTEM_PARAMS_FILE, systemParameters);
	}

	private void writeString(File path, String filename, String contents) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path, filename));
			fos.write(contents.getBytes());
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeStream(File path, String filename, InputStream stream) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path, filename));

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = stream.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}

			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getIssuerPath(String issuer) {
		File store = context.getDir("store", Context.MODE_PRIVATE);
		File verifier = new File(store, issuer);

		if (!verifier.mkdir() && !verifier.isDirectory())
			throw new RuntimeException("Could not create issuer path for '" + issuer + "' in storage");

		return verifier;
	}


	private static void downloadSync(Iterable<String> issuers, Iterable<String> credentials)
			throws InfoException, IOException {
		// This also downloads the issuer description
		for (String issuer: issuers) {
			if (DescriptionStore.getInstance().getIssuerDescription(issuer) == null)
				DescriptionStore.getInstance().downloadIssuerDescription(issuer);
			if (!IdemixKeyStore.getInstance().containsPublicKey(issuer))
				IdemixKeyStore.getInstance().downloadIssuer(issuer);
		}

		for (String credential : credentials)
			if (DescriptionStore.getInstance().getCredentialDescription(credential) == null)
				DescriptionStore.getInstance().downloadCredentialDescription(credential);
	}

	/**
	 * Asynchroniously attempt to download issuer descriptions, public keys and credential descriptions
	 * from the scheme managers.
	 * @param issuers Issuers to download
	 * @param credentials Credentials to download
	 * @param handler Handler to communicate results to
	 */
	public static void download(final Iterable<String> issuers,
	                            final Iterable<String> credentials,
	                            final DownloadHandler handler) {
		new AsyncTask<Void,Void,Exception>() {
			@Override protected Exception doInBackground(Void... params) {
				try {
					downloadSync(issuers, credentials);
					return null;
				} catch (Exception e) {
					return e;
				}
			}

			@Override protected void onPostExecute(Exception e) {
				if (e == null)
					handler.onSuccess();
				else
					handler.onError(e);
			}
		}.execute();
	}

	/**
	 * Asynchroniously attempt to download issuer descriptions, public keys and credential descriptions
	 * from the scheme managers.
	 * @param request A session request containing unknown issuers or credentials
	 * @param handler Handler to communicate results to
	 */
	public static void download(final SessionRequest request, final DownloadHandler handler) {
		download(request.getIssuerList(), request.getCredentialList(), handler);
	}
}