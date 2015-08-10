/**
 * LogFragment.java
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.irmacard.android.util.R;
import org.irmacard.credentials.util.log.LogEntry;

import java.util.ArrayList;
import java.util.List;

public class LogFragment extends Fragment {	
	private LogListAdapter listAdapter;
	private List<LogEntry> logs = null;
	private ListView listView;

	public static final String ARG_LOG = "log";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LogFragment() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey(ARG_LOG)) {
			logs = (List<LogEntry>) getArguments().getSerializable(ARG_LOG);
		}

		if (logs == null) {
			Log.i("blaat", "No credentials available yet");
		} else {
			for (LogEntry cp : logs) {
				Log.i("blaat", cp.toString());
			}
		}

	    listAdapter = new LogListAdapter(getActivity(), logs);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_log,
				container, false);

		ListView list = (ListView) rootView.findViewById(R.id.log_list);
		TextView no_items = (TextView) rootView.findViewById(R.id.log_no_log);

		if(logs == null || logs.isEmpty()) {
			list.setVisibility(View.INVISIBLE);
			no_items.setVisibility(View.VISIBLE);
			if(logs == null) {
				no_items.setText(R.string.error_logs_not_read);
			}
		} else {
			list.setVisibility(View.VISIBLE);
			no_items.setVisibility(View.INVISIBLE);
		}
		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		listView = (ListView) view
				.findViewById(R.id.log_list);
		listView.setAdapter(listAdapter);
	}
}
