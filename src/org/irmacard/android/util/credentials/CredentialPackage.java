/**
 * CredentialPackage.java
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

import java.io.Serializable;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

public class CredentialPackage implements Serializable{
	private static final long serialVersionUID = 6538230152019005462L;
	private CredentialDescription credDesc;
	private Attributes attributes;
	
	public CredentialPackage(CredentialDescription credDesc, Attributes attributes) {
		this.credDesc = credDesc;
		this.attributes = attributes;
	}

	public CredentialDescription getCredentialDescription() {
		return credDesc;
	}

	public Attributes getAttributes() {
		return attributes;
	}
	
	public String toString() {
		return "###" + credDesc.toString() + " ## " + attributes.toString() + "###";
	}
}
