package org.irmacard.android.util.credentials;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.irmacard.credentials.info.FileReader;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;
import org.irmacard.credentials.info.VerificationDescription;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public class AndroidFileReader implements FileReader {
	private static final String assetPath = "irma_configuration";
	private static final String internalPath = "store";
	private Context context;
	private AssetManager assets;

	public AndroidFileReader(Context context) {
		this.context = context;
		this.assets = context.getAssets();
	}

	@Override
	public InputStream retrieveFile(String path) throws InfoException {
		if (path.length() > 0 && !path.startsWith("/"))
			path = "/" + path;

		Exception ex;
		try {
			return assets.open(assetPath + path);
		} catch (IOException e) { /** Ignore absence of file, try internal storage next */ }

		try {
			File file = new File(context.getDir(internalPath, Context.MODE_PRIVATE), path);
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new InfoException("Could not read file " + path + " from assets or storage");
		}
	}

	@Override
	public String[] list(String path) {
		if (path.length() > 0 && !path.startsWith("/"))
			path = "/" + path;

		ArrayList<String> files = new ArrayList<String>();

		try {
			files.addAll(Arrays.asList(assets.list(assetPath + path)));
		} catch (IOException e) {
			// assets.list() doesn't throw exceptions when queried on nonexisting paths,
			// (it just returns an empty array); so when it does, something must be wrong
			throw new RuntimeException("Could not list assets at " + path, e);
		}

		File dir = new File(context.getDir("store", Context.MODE_PRIVATE), path);
		String[] internalFiles = dir.list();
		if (internalFiles != null && internalFiles.length > 0)
			files.addAll(Arrays.asList(internalFiles));

		return files.toArray(new String[files.size()]);
	}

	@Override
	public boolean isEmpty(String path) {
		String[] files = list(path);
		return files == null || files.length == 0;
	}

	@Override
	public boolean containsFile(String path, String filename) {
		String[] files = list(path);
		return files != null && Arrays.asList(files).contains(filename);
	}

	public Bitmap getIssuerLogo(IssuerDescription issuer) {
		Bitmap logo = null;
		String issuerID = issuer.getID();

		try {
			logo = BitmapFactory.decodeStream(retrieveFile(issuerID + "/logo.png"));
		} catch (InfoException e) {
			e.printStackTrace();
		}

		return logo;
	}

	public Bitmap getVerifierLogo(VerificationDescription verification) {
		Bitmap logo = null;
		String verifierID = verification.getVerifierID();

		try {
			logo = BitmapFactory.decodeStream(retrieveFile(verifierID + "/logo.png"));
		} catch (InfoException e) {
			e.printStackTrace();
		}

		return logo;
	}
}
