package com.systemwerx.PassGen.obsolete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import com.systemwerx.utils.trace;

public class LicenseManagerCmd
{
	static boolean traceActive = false;
	static trace tc;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
        if ( args.length != 2)
        {
			displayErrorMessage("License command is invalid");
			return;
        }

        	
        if ( args[0].equalsIgnoreCase("-apply"))
        {
        	if ( storeLicense(args[1]))
				displayInfoMessage("License apply successful");
        	else
    			displayErrorMessage("License apply failed");
        		
        		
        }
	}

	static boolean storeLicense(String Key)
	{
		try
		{
			String licenseFile;
			if ((licenseFile = System.getProperty("sxlicense")) == null)
			{
				licenseFile = "license.dat";
			}
			else
			{
				if (traceActive)
					tc.traceMessage("License file selected : " + licenseFile);
			}
			license lic = new license();
			File out = new File(licenseFile);
			FileOutputStream fos = new FileOutputStream(licenseFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			if (Key.length() != 48)
			{
				displayErrorMessage("License is invalid - length is incorrect");
				return false;
			}

			if (lic.verifyLicense(Key.substring(0, 4), Key.substring(4, 8),
					"1234567812345678", Key.substring(8)))
			{
				displayInfoMessage("License is valid - expires "
						+ lic.getExpireDateString(Key.substring(0, 4)));
				oos.writeUTF(Key);
				oos.close();
				return true;
			}
			else
			{
				displayErrorMessage("License is not valid");
				return false;
			}
		}
		catch (Exception ex)
		{
			System.out.println("Exception detected - " + ex.getMessage());
			System.out.println("Exception detected - " + ex.getClass().getName());
			ex.printStackTrace();
		}
		return true;
	}

	static private void displayErrorMessage(String string)
	{
		System.out.println("ERROR " + string);
	}

	static private void displayInfoMessage(String string)
	{
		System.out.println(string);
	}

}
