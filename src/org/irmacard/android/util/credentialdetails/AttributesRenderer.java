package org.irmacard.android.util.credentialdetails;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.irmacard.android.util.R;
import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;

import java.util.List;

/**
 * Contains a method that dynamically adds attributes to a LinearLayout.
 *
 * Preferably we would do this using Fragments, but it seems to be completely impossible to add a fragment to alert
 * dialogs using AlertDialog with yes/no buttons.
 */
public class AttributesRenderer {
	Context context;
	LayoutInflater inflater;

	public AttributesRenderer(Context context, LayoutInflater inflater) {
		this.context = context;
		this.inflater = inflater;
	}

	/**
	 * Render the specified attributes, adding them to the specified list.
	 *
	 * @param credential The credential.
	 * @param list The list to add the rendered attributes to.
	 * @param includeTitle If true, the title of the credential will also be rendered and the attributes get a bullet
	 *                     in front of them (creating an unordered list).
	 * @param disclosedAttributes If specified, those attributes found in the credential but not here will be shown in
	 *                            light grey to indicate they are not being disclosed.
	 */
	public void render(CredentialPackage credential, LinearLayout list, boolean includeTitle, Attributes
			disclosedAttributes) {
		if (includeTitle) {
			TextView attrName = new TextView(context);
			attrName.setTextAppearance(context, R.style.DetailHeading);
			attrName.setText(credential.getCredentialDescription().getName());
			attrName.setTextColor(Color.BLACK);
			list.addView(attrName);
		}

		List<AttributeDescription> attr_desc = credential.getCredentialDescription().getAttributes();
		Attributes attr_vals = credential.getAttributes();
		for (AttributeDescription desc : attr_desc) {
			View attributeView = inflater.inflate(R.layout.row_attribute, null);

			TextView name = (TextView) attributeView.findViewById(R.id.detail_attribute_name);
			TextView value = (TextView) attributeView.findViewById(R.id.detail_attribute_value);

			if (disclosedAttributes != null) {
				if (disclosedAttributes.get(desc.getName()) != null) {
					value.setTextColor(Color.BLACK);
					value.setTextAppearance(context, R.style.DetailHeading);
				} else {
					name.setTextColor(context.getResources().getColor(R.color.irmagrey));
					value.setTextColor(context.getResources().getColor(R.color.irmagrey));
				}
			}

			if (includeTitle) {
				name.setText(" \u2022 " + desc.getName() + ":");
			} else {
				name.setText(desc.getName() + ":");
			}
			value.setText(new String(attr_vals.get(desc.getName())));

			list.addView(attributeView);
		}
	}

}
