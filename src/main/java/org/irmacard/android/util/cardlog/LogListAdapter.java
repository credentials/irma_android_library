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

package org.irmacard.android.util.cardlog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.paging.listview.PagingBaseAdapter;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentials.AndroidFileReader;
import org.irmacard.credentials.util.log.IssueLogEntry;
import org.irmacard.credentials.util.log.LogEntry;
import org.irmacard.credentials.util.log.RemoveLogEntry;
import org.irmacard.credentials.util.log.VerifyLogEntry;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class LogListAdapter extends PagingBaseAdapter<LogEntry> {
	private static LayoutInflater inflater = null;
	private AndroidFileReader fileReader;
	private Activity activity;

	public LogListAdapter(Activity activity) {
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.activity = activity;
		fileReader = new AndroidFileReader(activity);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
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

		LogEntry log = items.get(position);
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
				View item_view = inflater.inflate(R.layout.log_disclosure_item, null);

				TextView attribute = (TextView) item_view
						.findViewById(R.id.log_disclosure_attribute_name);
				TextView mode = (TextView) item_view
						.findViewById(R.id.log_disclosure_mode);

				attribute.setText(attr);
				String disclosure_text;
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
			if (log.getCredential().getIssuerDescription() != null) {
				actorLogo.setImageBitmap(
						fileReader.getIssuerLogo(log.getCredential().getIssuerDescription()));
			}
		}

		header_text += log.getCredential().getName();
		header.setText(header_text);
		datetime.setText(SimpleDateFormat.getDateTimeInstance().format(log.getTimestamp()));
		actionImage.setImageResource(actionImageResource);

		return view;
	}
}
