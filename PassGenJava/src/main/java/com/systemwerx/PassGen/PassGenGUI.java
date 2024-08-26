package com.systemwerx.PassGen;

import java.io.File;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.ProductPathUtil;

public class PassGenGUI {
	KeyStoreBean keyStoreBean;
	PassGenGUILogon thelogon;
	boolean firstLogon = false;
	boolean loginPasswordOk = false;
	String loginPassword;
	File productData;
	File licenseFile;
	File keystore;
	
	static PassGenGUI passGenGUIstatic;

	PassGenGUI() {
		passGenGUIstatic = this;
	}

	public static void main(String args[]) {
		PassGenGUI theapp;
		theapp = new PassGenGUI();
		theapp.init();
		theapp.doMainDisplay();
	}

	static PassGenGUI getPassGenGUI() {
		return passGenGUIstatic;
	}

	/*
	 * 
	 */

	boolean init() {
		String OS = System.getProperty("os.name").toLowerCase();
		try {
			Log.debug("Started init");
			keyStoreBean = new KeyStoreBean();
			File f;
			keystore = new File(ProductPathUtil.getDataDirectory("passgenjava") + System.getProperty("file.separator") + "keystore");
			String productDataPath = ProductPathUtil.getDataDirectory("passgenjava");
			licenseFile = new File(ProductPathUtil.getLicensePath("passgenjava"));

			if (keystore.exists()) {
			} else {
				firstLogon = true;
			}

			return true;

		} catch (Exception ex) {
			Log.error("Exception " + ex.getClass().getName() + " " + ex.getMessage());
			ExceptionUtil.printStackTrace(ex);
		}
		return true;
	}

	boolean doMainDisplay() {
		try {
			Log.debug("Starting main thread");
			PassGenGUIMain main = new PassGenGUIMain();
			main.initApp(this, keyStoreBean, thelogon);
		}

		catch (Exception ex) {
			Log.error("Exception detected - " + ex.getMessage());
			Log.error("Exception detected - " + ex.getClass().getName());
			ExceptionUtil.printStackTrace(ex);
		}

		Log.debug("Exiting");
		return true;
	}

	public boolean isFirstLogon() {
		return firstLogon;
	}

	public void setFirstLogon(boolean firstLogon) {
		this.firstLogon = firstLogon;
	}

	public boolean isLoginPasswordOk() {
		return loginPasswordOk;
	}

	public void setLoginPasswordOk(boolean loginPasswordOk) {
		this.loginPasswordOk = loginPasswordOk;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public File getProductData() {
		return productData;
	}

	public void setProductData(File productData) {
		this.productData = productData;
	}

	public File getLicenseFile() {
		return licenseFile;
	}

	public void setLicenseFile(File licenseFile) {
		this.licenseFile = licenseFile;
	}

	public File getKeystore() {
		return keystore;
	}

	public void setKeystore(File keystore) {
		this.keystore = keystore;
	}

	public KeyStoreBean getKeyStoreBean() {
		return keyStoreBean;
	}

	public void setKeyStoreBean(KeyStoreBean keyStoreBean) {
		this.keyStoreBean = keyStoreBean;
	}	
}