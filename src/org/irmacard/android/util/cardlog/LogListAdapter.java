/**
 * LogListAdapter.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, March 2013.
 */

package org.irmacard.android.util.cardlog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.irmacard.android.util.credentials.AndroidWalker;
import org.irmacard.android.util.R;
import org.irmacard.credentials.util.log.IssueLogEntry;
import org.irmacard.credentials.util.log.LogEntry;
import org.irmacard.credentials.util.log.RemoveLogEntry;
import org.irmacard.credentials.util.log.VerifyLogEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private AndroidWalker aw;

	private List<LogEntry> logs;
	private Activity activity;

	public LogListAdapter(Activity activity,
			List<LogEntry> credentials) {
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (credentials != null) {
			this.logs = credentials;
		} else {
			this.logs = new ArrayList<LogEntry>();
		}

		this.activity = activity;
		aw = new AndroidWalker(activity.getResources().getAssets());
	}

	@Override
	public int getCount() {
		return logs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO: is this ok to do it like this?
		return position;
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if (view == null) {
			view = inflater.inflate(R.layout.log_item, null);
		}

		TextView header = (TextView) view.findViewById(R.id.log_item_header);
		TextView datetime = (TextView) view.findViewById(R.id.log_item_datetime);

		ImageView actionImage = (ImageView) view
				.findViewById(R.id.log_item_action_image);
		ImageView actorLogo = (ImageView) view
				.findViewById(R.id.log_item_actor_logo);
		LinearLayout attributesList = (LinearLayout) view
				.findViewById(R.id.log_item_list_disclosure);

		attributesList.removeAllViews();

		LogEntry log = logs.get(position);
		String header_text = "";
		int actionImageResource = R.drawable.irma_icon_warning_064px;
		HashMap<String, Boolean> attributesDisclosed = new HashMap<String, Boolean>();

		if(log instanceof VerifyLogEntry) {
			header_text = "Verified: ";
			actionImageResource = R.drawable.irma_icon_ok_064px;
			actorLogo.setImageResource(R.drawable.irma_logo_150);

			VerifyLogEntry vlog = (VerifyLogEntry) log;
			attributesDisclosed = vlog.getAttributesDisclosed();

			// This is not so nice, rather used a Listview here, but it is not possible
			// to easily make it not scrollable and show all the items.
			for (String attr : attributesDisclosed.keySet()) {
				View item_view = inflater.inflate(R.layout.log_disclosure_item,
						null);

				TextView attribute = (TextView) item_view
						.findViewById(R.id.log_disclosure_attribute_name);
				TextView mode = (TextView) item_view
						.findViewById(R.id.log_disclosure_mode);

				attribute.setText(attr);
				String disclosure_text = "";
				if (attributesDisclosed.get(attr)) {
					disclosure_text = "disclosed";
					mode.setTypeface(null, Typeface.BOLD);
				} else {
					disclosure_text = "hidden";
					attribute.setTypeface(null, Typeface.NORMAL);
					int color = activity.getResources().getColor(
							R.color.irmagrey);
					attribute.setTextColor(color);
					mode.setTextColor(color);
				}
				mode.setText(disclosure_text);

				attributesList.addView(item_view);
			}
		} else if(log instanceof RemoveLogEntry) {
			header_text = "Removed: ";
			actionImageResource = R.drawable.irma_icon_missing_064px;
			actorLogo.setImageResource(R.drawable.irma_logo_150);
		} else if(log instanceof IssueLogEntry) {
			header_text = "Issued: ";
			actionImageResource = R.drawable.irma_icon_warning_064px;
			actorLogo.setImageBitmap(aw.getIssuerLogo(log.getCredential()
					.getIssuerDescription()));
		}

		header_text += log.getCredential().getName();
		header.setText(header_text);
		datetime.setText(SimpleDateFormat.getDateTimeInstance().format(log.getTimestamp()));
		actionImage.setImageResource(actionImageResource);

		return view;
	}
}
