package com.systemwerx.utils;

import java.security.Provider;
import java.security.Security;

public class LoadJCEProvider {

	public  Provider load() {
		Provider sunJCE;

		try {
			sunJCE = Security.getProvider("SunJCE");
		} catch (Exception ex) {
			try {
				sunJCE = Security.getProvider("IBMJCE");
			} catch (Exception ex1) {
				return null;
			}
		}
		return sunJCE;
	}
}
