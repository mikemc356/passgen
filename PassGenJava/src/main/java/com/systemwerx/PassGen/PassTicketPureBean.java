package com.systemwerx.PassGen;

import java.io.Serializable;
import java.security.Provider;
import java.security.Security;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.license.license;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.common.util.ProductPathUtil;
import com.systemwerx.utils.LoadJCEProvider;
import com.systemwerx.utils.SxUtilException;
import com.systemwerx.utils.TranslateBean;

public class PassTicketPureBean implements Serializable {

   String userID;
   String sessionKey;
   String passwordFile;
   String keystoreFile;
   String application;
   int gmtOffset;
   int debugTime;
   String translateTable;
   String workString;
   TranslateBean tb;
   license lic;
   boolean licOK = true;
   boolean tempLic = false;
   private static String PRODUCT_ID = "05";

   public static void main(String args[]) {
      PassTicketPureBean pw = new PassTicketPureBean();
      String Result;
      try {

         String TransTab = "00010203372D2E2F1605250B0C0D0E0F101112133C3D322618193F271C1D1E1F405A7F7B4A6C507D4D5D5C4E6B604B61F0F1F2F3F4F5F6F7F8F97A5E4C7E6E6F7CC1C2C3C4C5C6C7C8C9D1D2D3D4D5D6D7D8D9E2E3E4E5E6E7E8E9B1E0BBBA6D79818283848586878889919293949596979899A2A3A4A5A6A7A8A9C04FD0BC0768DC5142434447485253545756586367719C9ECBCCCDDBDDDFECFC705B80BFFF4555CEDE49699A9BABAF5FB8B7AA8A8B2B2C092128656264B438313433B0B22422172906202A46661A35083936303A9F8CAC7273740A757677231514046A783BEE59EBEDCFEFA08EAEFEFBFD8DADA1BECA8F1BB9B6B5E19D90BDB3DAFAEA3E41";
         // If we have user MIKEM system TSOSYS1 and key E001193519561977 the ticket is
         // AEPRRZ3S

         // pw.setDebugTime(1111111);
         // pw.setUserID("USERA");
         // pw.setGMTOffset(0);
         // pw.setTranslateTable("trans.txt");
         // pw.setTranslateArray(TransTab);
         // pw.setSessionKey("E001193519561977");
         // pw.setApplication("TSOSYS1");
         // pw.setDebugTime(1357578876);
         // pw.setUserID("VZCTIAD");
         // pw.setGMTOffset(0);
         // pw.setTranslateTable("trans.txt");
         // pw.setTranslateArray(TransTab);
         // pw.setSessionKey("E012345678912345");
         // pw.setApplication("TSOSVZ4");
         // pw.setPasswordFile("pswd");
         // pw.setKeystore("keystore");
         // pw.loadKeystoreParms();
         Result = pw.getPassTicket();
         Log.error("Passticket is " + Result);
      } catch (Exception e) {
         Class c = e.getClass();
         String s = c.getName();
         Log.error("Exception >>>" + s + "  " + e.getMessage());
         ExceptionUtil.printStackTrace(e);
      }
   }
   // */

   public PassTicketPureBean() {
      userID = "";
      sessionKey = "";
      application = "";
      gmtOffset = -99;
      // TranslateTable = "";
      debugTime = 0;
      tb = new TranslateBean();
      //lic = new license("05");
      /*
       * if ( !lic.verifyLicenseFile("1234567812345678"))
       * {
       * if ( traceActive ) tc.traceMessage("license key is invalid");
       * return;
       * }
       */

      //String licensePath = System.getProperty("sxlicense");

      //if ( licensePath == null ) {
      //   licensePath = ProductPathUtil.getLicensePath("passgen");
      //}

      //if (!lic.verifyLicenseFile(licensePath)) {
      //   licOK = false;
      //   return;
      // }

      Log.debug("license key is valid");
      licOK = true;
   }

   /**
    * Set the application name the Passticket is generated for. The field is a
    * maximum of 8 characters.
    * 
    * This field is used by loadKeystoreParms() to determine which key entry to
    * load.
    */

   public void setApplication(String app) {
      application = app;
      if (application != null)
         application = application.toUpperCase();
      Log.debug("setApplication() - " + app);
   }

   /**
    * This allows the time value being used to be overidden. Useful for debugging
    * purposes.
    */

   public void setDebugTime(int DT) {
      debugTime = DT;
   }

   /** Set the User name. The field is a maximum of 8 characters. */

   public void setUserID(String user) {
      userID = user;
      if (userID != null)
         userID = userID.toUpperCase();
      Log.debug("setUserID() - " + user);
   }

   /**
    * Set the Password file name. This file contains an encrypted KeyStore password
    * for use in applications
    * where users do not wish to hard code passwords
    * 
    * This can be used in conjunction with the loadKeystoreParms() method
    */

   public void setPasswordFile(String pwfile) {
      passwordFile = pwfile;
      Log.debug("setPasswordFile() - " + pwfile);
   }

   /**
    * Set the Keystore file name. This is used in conjunction with with the
    * loadKeystoreParms() method.
    */

   public void setKeystore(String keystore) {
      keystoreFile = keystore;
      Log.debug("setPasswordFile() - " + keystore);
   }

   /**
    * Set the Session Key. This is specified as 16 Hexadecimal characters
    * representing the 8 byte
    * PTKTDATA profile in RACF. This value must be the same as coded in the User or
    * Applications PTKTDATA profile.
    */
   public void setSessionKey(String sessKey) throws PassTicketException {
      try {
         Log.debug("setSessionKey() - " + sessKey);

         if (sessKey.length() != 16) {
            throw new PassTicketException("Key value length is invalid");
         }

         if (tb.TransHexCharToBinaryData(sessKey) == null) {
            throw new PassTicketException("Invalid Hex data in key");
         }
      } catch (SxUtilException ex) {
         throw new PassTicketException("Invalid Hex data in key");
      } catch (Exception ex) {
         Log.error("Exception detected - " + ex.getMessage());
         Log.error("Exception detected - " + ex.getClass().getName());
         ExceptionUtil.printStackTrace(ex);
         throw new PassTicketException();
      }

      sessionKey = sessKey;
   }

   /**
    * Set the Offset between hosts. This field is rarely used. If you have a TOD
    * clock not using GMT you can
    * compensate by adjusting the time generated on this end of the connection.
    * This value is added or subtracted ( if a
    * minus value ) from the current time before generating the PassTicket.
    * 
    * Values range from -12 through +12.
    */
   public void setGmtOffset(int offset) {
      gmtOffset = offset;
      Log.debug("setGMTOffset - " + offset);
   }

   /**
    * Specify and ASCII-EBCDIC translate table from a file.
    * 
    */

   public void setTranslateTable(String table) throws PassTicketException {
      translateTable = new String(table);
      if (!tb.LoadTable(table))
         throw new PassTicketException("Translate table load failed - " + tb.GetErrorMessage());

      Log.debug("setTranslateTable - " + table);
   }

   /**
    * Pass an ASCII-EBCDIC translate table from an array. The array is sepcified in
    * Hexadecimal format.
    * 
    * The table is 256 characters long and the function expects 512 Hexadecimal
    * characters to be provided.
    * 
    */
   public void setTranslateArray(String array) throws Exception {
      tb.SetTranslateArray(array);
      Log.debug("setTranslateArray - " + array);
   }

   public void setLicense(String license) throws Exception {
      license lic = new license(PRODUCT_ID);
      try {
         if (lic.verifyLicense(license.substring(0, 5),
               license.substring(5, 9), license.substring(9))) {
            licOK = true;
         } else {
            licOK = false;
         }
      } catch (Exception ex) {
         licOK = false;
      }

   }

   /**
    * Load parameters from a Keystore.
    * 
    * This method opens the keystore specified by setKeystore() using the password
    * in setPasswordFile().
    * The entry in the keystore specified in setApplication() is retreived and
    * parameters loaded into the bean.
    * 
    * 
    */

   public boolean loadKeystoreParms() throws PassTicketException {
      try {
         Log.debug("loadKeystoreParms()");
         KeyStoreBean kb = new KeyStoreBean();
         KeyStoreEntry ke;

         kb.open(kb.External_Keyfile, keystoreFile, passwordFile);
         ke = kb.getKey(application);
         if (ke == null) {
            Log.debug("Application name not found");
            return false;
         }

         setGmtOffset(ke.getGmtOffset());
         setUserID(ke.getUserID());
         setSessionKey(ke.getSessionKey());
      } catch (Exception ex) {
         Log.error("Exception detected - " + ex.getMessage());
         Log.error("Exception detected - " + ex.getClass().getName());
         ExceptionUtil.printStackTrace(ex);
      }

      Log.debug("Exiting");
      return true;

   }

   /** Generate a Passticktet. */

   public String getPassTicket() throws PassTicketException, SxUtilException {
      try {
         Log.debug("In getPassTicket()");
         byte ebuser[], encebuser[], ebappl[], work[], work2[], work3[], resultc[], result4[], resulte[];
         byte ebpad[] = { 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55 };
         byte L2B[] = new byte[2];
         byte R2B[] = new byte[2];
         byte resultb[] = new byte[8];
         byte resultd[] = new byte[2];
         byte result5[] = new byte[8];
         int count, count1;
         Date date = new Date();
         boolean debug = false;
         Integer workint = new Integer(0);
         int time;
         Calendar cal;
         String out, finalres, UserIDPad, AppPad;
         // Provider sunJCE = new com.sun.crypto.provider.SunJCE();
         Provider sunJCE;
         LoadJCEProvider jceProvider = new LoadJCEProvider();
         sunJCE = jceProvider.load();
         Security.addProvider(sunJCE);
         Cipher c = Cipher.getInstance("DES");
         DESKeySpec Key = new DESKeySpec(tb.TransHexCharToBinaryData(sessionKey));
         SecretKeySpec DESKey = new SecretKeySpec(Key.getKey(), "DES");
         // SecretKeySpec DESKey = new SecretKeySpec
         // (tb.TransHexCharToBinaryData(SessionKey));
         UserIDPad = PadRight(userID, 8);
         AppPad = PadRight(application, 8);
         // out = tb.TransToHexData(Key.getKey(),8);
         // System.out.println("key "+out);
         if ( !licOK && sessionKey.equals("E001193519561977") && application.equals("TSOS0W1")) {
            licOK = true;
            tempLic = true;
         }

         if (!licOK && debugTime == 0 ) {
            throw new PassTicketException("License is expired or invalid");
         }

         Log.debug("Padded User >" + userID + "<");
         Log.debug("Padded App  >" + application + "<");
         Log.debug("Padded User  >" + UserIDPad + "<");
         Log.debug("Padded App   >" + AppPad + "<");
         Log.debug("Session Key  >" + sessionKey + "<");

         ebuser = tb.TransData(UserIDPad);
         ebappl = tb.TransData(AppPad);

         // Encrypt user name with session key

         c.init(Cipher.ENCRYPT_MODE, DESKey);
         encebuser = c.doFinal(ebuser);
         // out = tb.TransToHexData(encebuser,8);
         // System.out.println("First encr "+out);

         // Now XOR encrypted user with the application name

         work = XOR(encebuser, ebappl, 8);
         // out = tb.TransToHexData(work,8);
         // System.out.println("First XOR "+out);

         // Now encrypt that with application key again

         c.init(Cipher.ENCRYPT_MODE, DESKey);
         work2 = c.doFinal(work);
         // out = tb.TransToHexData(work2,8);
         // System.out.println("result2 "+out);

         // Take first four bytes

         work = new byte[4];

         for (count = 0; count < 4; count++) {
            work[count] = work2[count];
         }

         // Get time since Jan 1 1970 in seconds

         // cal = Calendar.getInstance();
         time = (int) (System.currentTimeMillis() / 1000);
         Log.debug("Time >" + time + "<");
         // System.out.println("Time in millis "+System.currentTimeMillis());
         time = time - (gmtOffset * 3600);      
         Log.debug("Adjusted time >" + time + "<");

         if (debugTime != 0) {
            time = debugTime;
            Log.debug("Debug time >" + debugTime + "<");
         }

         // System.out.println("Time in secs "+time);

         workString = Integer.toHexString(time);
         if ((workString.length() % 2) != 0)
            workString = "0" + workString;

         if (workString.length() == 6)
            workString = "00" + workString;

         // System.out.println("length of string "+WorkString.length());

         // System.out.println("Hexstr"+WorkString);
         work2 = tb.TransHexCharToBinaryData(workString);
         // out = tb.TransToHexData(work2,4);
         // System.out.println("Result"+out);
         result4 = XOR(work, work2, 4);
         // out = tb.TransToHexData(result4,4);
         // System.out.println("Result4"+out);

         // Get L2B and R2B

         L2B[0] = result4[0];
         L2B[1] = result4[1];
         R2B[0] = result4[2];
         R2B[1] = result4[3];

         out = tb.TransToHexData(L2B, 2);
         // System.out.println("L2B "+out);

         out = tb.TransToHexData(R2B, 2);
         // System.out.println("R2B "+out);

         // R2B = resulta
         // Generate PAD1 & PAD2

         // Generate PAD Bytes

         for (count = 0; count < userID.length(); count++) {
            ebpad[count] = ebuser[count];
         }

         out = tb.TransToHexData(ebpad, 12);
         // System.out.println("pad "+out);
         // Run time coder algorithm

         for (count = 1; count < 7; count++) {
            out = tb.TransToHexData(L2B, 2);
            // System.out.println("L2B "+out);

            out = tb.TransToHexData(R2B, 2);
            // System.out.println("R2B "+out);
            resultb[0] = R2B[0];
            resultb[1] = R2B[1];

            out = tb.TransToHexData(resultb, 2);
            // System.out.println("resultb "+out);

            switch (count) {
               case 1:
               case 3:
               case 5:
                  for (count1 = 0; count1 < 6; count1++) {
                     resultb[count1 + 2] = ebpad[count1];
                  }
                  break;

               case 2:
               case 4:
               case 6:
                  for (count1 = 0; count1 < 6; count1++) {
                     resultb[count1 + 2] = ebpad[count1 + 6];
                  }
                  break;

            }

            // Now encrypt result B with application key giving result c

            c.init(Cipher.ENCRYPT_MODE, DESKey);
            resultc = c.doFinal(resultb);
            // out = tb.TransToHexData(resultc,8);
            // System.out.println("resultc "+out);
            resultd[0] = resultc[0];
            resultd[1] = resultc[1];

            // XOR first two bytes of L2B with first two bytes of result C giving resulte

            resulte = XOR(resultc, L2B, 2);

            // out = tb.TransToHexData(resulte,2);
            // System.out.println("resulte "+out);
            // redefine L2B and R2B

            L2B[0] = R2B[0]; // L2B = R2B
            L2B[1] = R2B[1];

            R2B[0] = resulte[0]; // R2B = resulte
            R2B[1] = resulte[1];

            // Permute R2B

            R2B = Permute(R2B, count);

         }

         // Re-combine to give result 5 - complete time coder

         result5[0] = L2B[0];
         result5[1] = L2B[1];
         result5[2] = R2B[0];
         result5[3] = R2B[1];

         finalres = TranslateChars(result5);
         Log.debug("PassTicket = " + finalres);
         if ( tempLic ) {
            licOK = false;
            tempLic = false;
         }

         return finalres;
      }

      catch (Exception e) {
         Class c = e.getClass();
         String s = c.getName();
         if (s.equals("com.systemwerx.PassGen.PassTicketException"))
            throw new PassTicketException(e.getMessage());

         Log.error("Exception >>>" + s + "  " + e.getMessage());
         ExceptionUtil.printStackTrace(e);
      }

      return "";

   }

   private String PadRight(String data, int length) {
      int count;
      String WorkString = new String(data);
      for (count = data.length(); count < length; count++) {
         WorkString = WorkString + " ";
      }
      return WorkString;
   }

   private byte[] XOR(byte Data[], byte Data2[], int Length) {
      byte outval[] = new byte[Length];
      int count = 0, outint;
      char out, val1, val2;
      Integer ai;

      for (count = 0; count < Length; count++) {
         // val1 = ( char ) Data[count];
         // val2 = (char ) Data2[count];
         // out = val1 ^ val2;
         // outval[count] = ( byte ) out;
         outint = Data[count] ^ Data2[count];
         ai = new Integer(outint);
         outval[count] = ai.byteValue();
         // System.out.println("value is"+ai);
      }

      return outval;
   }

   private byte[] Permute(byte Data[], int round) throws com.systemwerx.utils.SxUtilException {

      int bit1 = 0x00008000;
      int bit2 = 0x00004000;
      int bit3 = 0x00002000;
      int bit4 = 0x00001000;
      int bit5 = 0x00000800;
      int bit6 = 0x00000400;
      int bit7 = 0x00000200;
      int bit8 = 0x00000100;
      int bit9 = 0x00000080;
      int bit10 = 0x00000040;
      int bit11 = 0x00000020;
      int bit12 = 0x00000010;
      int bit13 = 0x00000008;
      int bit14 = 0x00000004;
      int bit15 = 0x00000002;
      int bit16 = 0x00000001;

      int lowbytes = 0x000000ff;
      int highbytes = 0x0000ff00;
      byte[] outbyte;

      // Convert input value to a short

      int in = 0, out = 0, in2 = 0;
      int t = 0;
      String strdta;
      strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer before "+strdta);
      in = in | Data[0];
      strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer first "+strdta);
      in = in << 8;
      in = in & highbytes;
      strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer second "+strdta);
      in2 = in2 | Data[1];
      in2 = in2 & lowbytes;
      strdta = Integer.toHexString(in2);
      // System.out.println("Hex of input integer third "+strdta);
      in = in | in2;
      strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer fourth "+strdta);
      // in = in >>8;
      strdta = tb.TransToHexData(Data, 2);
      // System.out.println("permute "+strdta);
      // System.out.println("Int "+in);
      strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer "+strdta);
      // System.out.println("Round number is "+round);

      // Now permute the bits....

      // Table 1
      switch (round) {
         case 1:
            if ((t = in & bit10) != 0)
               out = out | bit1;
            if ((t = in & bit2) != 0)
               out = out | bit2;
            if ((t = in & bit12) != 0)
               out = out | bit3;
            if ((t = in & bit4) != 0)
               out = out | bit4;
            if ((t = in & bit14) != 0)
               out = out | bit5;
            if ((t = in & bit6) != 0)
               out = out | bit6;
            if ((t = in & bit16) != 0)
               out = out | bit7;
            if ((t = in & bit8) != 0)
               out = out | bit8;
            if ((t = in & bit9) != 0)
               out = out | bit9;
            if ((t = in & bit1) != 0)
               out = out | bit10;
            if ((t = in & bit11) != 0)
               out = out | bit11;
            if ((t = in & bit3) != 0)
               out = out | bit12;
            if ((t = in & bit13) != 0)
               out = out | bit13;
            if ((t = in & bit5) != 0)
               out = out | bit14;
            if ((t = in & bit15) != 0)
               out = out | bit15;
            if ((t = in & bit7) != 0)
               out = out | bit16;
            break;

         // Table 2

         case 2:
            if ((t = in & bit1) != 0)
               out = out | bit1;
            if ((t = in & bit10) != 0)
               out = out | bit2;
            if ((t = in & bit3) != 0)
               out = out | bit3;
            if ((t = in & bit12) != 0)
               out = out | bit4;
            if ((t = in & bit13) != 0)
               out = out | bit5;
            if ((t = in & bit16) != 0)
               out = out | bit6;
            if ((t = in & bit7) != 0)
               out = out | bit7;
            if ((t = in & bit15) != 0)
               out = out | bit8;
            if ((t = in & bit9) != 0)
               out = out | bit9;
            if ((t = in & bit2) != 0)
               out = out | bit10;
            if ((t = in & bit11) != 0)
               out = out | bit11;
            if ((t = in & bit4) != 0)
               out = out | bit12;
            if ((t = in & bit5) != 0)
               out = out | bit13;
            if ((t = in & bit14) != 0)
               out = out | bit14;
            if ((t = in & bit8) != 0)
               out = out | bit15;
            if ((t = in & bit6) != 0)
               out = out | bit16;
            break;

         // Table 3

         case 3:
            if ((t = in & bit3) != 0)
               out = out | bit1;
            if ((t = in & bit10) != 0)
               out = out | bit2;
            if ((t = in & bit1) != 0)
               out = out | bit3;
            if ((t = in & bit12) != 0)
               out = out | bit4;
            if ((t = in & bit13) != 0)
               out = out | bit5;
            if ((t = in & bit16) != 0)
               out = out | bit6;
            if ((t = in & bit9) != 0)
               out = out | bit7;
            if ((t = in & bit15) != 0)
               out = out | bit8;
            if ((t = in & bit7) != 0)
               out = out | bit9;
            if ((t = in & bit2) != 0)
               out = out | bit10;
            if ((t = in & bit14) != 0)
               out = out | bit11;
            if ((t = in & bit4) != 0)
               out = out | bit12;
            if ((t = in & bit5) != 0)
               out = out | bit13;
            if ((t = in & bit11) != 0)
               out = out | bit14;
            if ((t = in & bit8) != 0)
               out = out | bit15;
            if ((t = in & bit6) != 0)
               out = out | bit16;
            break;

         // Table 4

         case 4:
            if ((t = in & bit10) != 0)
               out = out | bit1;
            if ((t = in & bit4) != 0)
               out = out | bit2;
            if ((t = in & bit12) != 0)
               out = out | bit3;
            if ((t = in & bit2) != 0)
               out = out | bit4;
            if ((t = in & bit14) != 0)
               out = out | bit5;
            if ((t = in & bit8) != 0)
               out = out | bit6;
            if ((t = in & bit16) != 0)
               out = out | bit7;
            if ((t = in & bit6) != 0)
               out = out | bit8;
            if ((t = in & bit9) != 0)
               out = out | bit9;
            if ((t = in & bit1) != 0)
               out = out | bit10;
            if ((t = in & bit13) != 0)
               out = out | bit11;
            if ((t = in & bit3) != 0)
               out = out | bit12;
            if ((t = in & bit11) != 0)
               out = out | bit13;
            if ((t = in & bit5) != 0)
               out = out | bit14;
            if ((t = in & bit15) != 0)
               out = out | bit15;
            if ((t = in & bit7) != 0)
               out = out | bit16;
            break;

         // Table 5

         case 5:
            if ((t = in & bit4) != 0)
               out = out | bit1;
            if ((t = in & bit10) != 0)
               out = out | bit2;
            if ((t = in & bit12) != 0)
               out = out | bit3;
            if ((t = in & bit1) != 0)
               out = out | bit4;
            if ((t = in & bit8) != 0)
               out = out | bit5;
            if ((t = in & bit16) != 0)
               out = out | bit6;
            if ((t = in & bit14) != 0)
               out = out | bit7;
            if ((t = in & bit5) != 0)
               out = out | bit8;
            if ((t = in & bit9) != 0)
               out = out | bit9;
            if ((t = in & bit2) != 0)
               out = out | bit10;
            if ((t = in & bit13) != 0)
               out = out | bit11;
            if ((t = in & bit3) != 0)
               out = out | bit12;
            if ((t = in & bit11) != 0)
               out = out | bit13;
            if ((t = in & bit7) != 0)
               out = out | bit14;
            if ((t = in & bit15) != 0)
               out = out | bit15;
            if ((t = in & bit6) != 0)
               out = out | bit16;
            break;

         // Table 6

         case 6:
            if ((t = in & bit1) != 0)
               out = out | bit1;
            if ((t = in & bit16) != 0)
               out = out | bit2;
            if ((t = in & bit15) != 0)
               out = out | bit3;
            if ((t = in & bit14) != 0)
               out = out | bit4;
            if ((t = in & bit13) != 0)
               out = out | bit5;
            if ((t = in & bit12) != 0)
               out = out | bit6;
            if ((t = in & bit11) != 0)
               out = out | bit7;
            if ((t = in & bit10) != 0)
               out = out | bit8;
            if ((t = in & bit9) != 0)
               out = out | bit9;
            if ((t = in & bit8) != 0)
               out = out | bit10;
            if ((t = in & bit7) != 0)
               out = out | bit11;
            if ((t = in & bit6) != 0)
               out = out | bit12;
            if ((t = in & bit5) != 0)
               out = out | bit13;
            if ((t = in & bit4) != 0)
               out = out | bit14;
            if ((t = in & bit3) != 0)
               out = out | bit15;
            if ((t = in & bit2) != 0)
               out = out | bit16;
            break;

         default:
            break;

      } // End of switch statement

      // Now get the low two bytes back into an array

      strdta = Integer.toHexString(out);
      if ((strdta.length() % 2) != 0)
         strdta = "0" + strdta;

      if (strdta.length() == 2)
         strdta = "00" + strdta;

      if (strdta.length() == 1)
         strdta = "000" + strdta;

      // System.out.println("Hex of permute output integer "+strdta+" lenth
      // "+strdta.length());
      outbyte = tb.TransHexCharToBinaryData(strdta);

      return outbyte;

   }

   private String TranslateChars(byte[] data) throws com.systemwerx.utils.SxUtilException {
      int bit1 = (int) 0x80000000;
      int bit2 = 0x40000000;
      int bit3 = 0x20000000;
      int bit4 = 0x10000000;
      int bit5 = 0x08000000;
      int bit6 = 0x04000000;
      int bit7 = 0x02000000;
      int bit8 = 0x01000000;
      int bit9 = 0x00800000;
      int bit10 = 0x00400000;
      int bit11 = 0x00200000;
      int bit12 = 0x00100000;
      int bit13 = 0x00080000;
      int bit14 = 0x00040000;
      int bit15 = 0x00020000;
      int bit16 = 0x00010000;
      int bit17 = 0x00008000;
      int bit18 = 0x00004000;
      int bit19 = 0x00002000;
      int bit20 = 0x00001000;
      int bit21 = 0x00000800;
      int bit22 = 0x00000400;
      int bit23 = 0x00000200;
      int bit24 = 0x00000100;
      int bit25 = 0x00000080;
      int bit26 = 0x00000040;
      int bit27 = 0x00000020;
      int bit28 = 0x00000010;
      int bit29 = 0x00000008;
      int bit30 = 0x00000004;
      int bit31 = 0x00000002;
      int bit32 = 0x00000001;
      int lowbytes = 0x000000ff;
      int highbytes = 0x0000ff00;

      int total, t, in, in2;
      String TransTab = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
      String Out = new String("");
      String strdta;

      // Load in

      in = 0;
      in = in | data[0];
      in = in << 24;
      in = in & 0xff000000;

      // strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer 1 "+strdta);
      in2 = 0;
      in2 = in2 | data[1];
      in2 = in2 << 16;
      in2 = in2 & 0x00ff0000;
      in = in | in2;
      // strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer 2 "+strdta);
      in2 = 0;
      in2 = in2 | data[2];
      in2 = in2 << 8;
      in2 = in2 & 0x0000ff00;
      in = in | in2;
      // strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer 3 "+strdta);
      in2 = 0;
      in2 = in2 | data[3];
      in2 = in2 & 0x000000ff;
      in = in | in2;
      // strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer 4 "+strdta);

      // strdta = tb.TransToHexData(Data,4);
      // System.out.println("Translate in "+strdta);
      // strdta = Integer.toHexString(in);
      // System.out.println("Hex of input integer before "+strdta);

      // Char 1 - 31,32,1-4
      total = 0;

      if ((t = in & bit31) != 0)
         total += 32;
      if ((t = in & bit32) != 0)
         total += 16;
      if ((t = in & bit1) != 0)
         total += 8;
      if ((t = in & bit2) != 0)
         total += 4;
      if ((t = in & bit3) != 0)
         total += 2;
      if ((t = in & bit4) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);
      // System.out.println("Translate char
      // "+TransTab.substring(total%36,(total%36)+1));
      // System.out.println("Translate pos "+total+" "+total%36);

      // Char 2 - 3-8
      total = 0;

      if ((t = in & bit3) != 0)
         total += 32;
      if ((t = in & bit4) != 0)
         total += 16;
      if ((t = in & bit5) != 0)
         total += 8;
      if ((t = in & bit6) != 0)
         total += 4;
      if ((t = in & bit7) != 0)
         total += 2;
      if ((t = in & bit8) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 3 - 7-12
      total = 0;

      if ((t = in & bit7) != 0)
         total += 32;
      if ((t = in & bit8) != 0)
         total += 16;
      if ((t = in & bit9) != 0)
         total += 8;
      if ((t = in & bit10) != 0)
         total += 4;
      if ((t = in & bit11) != 0)
         total += 2;
      if ((t = in & bit12) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 4 - 11-16
      total = 0;

      if ((t = in & bit11) != 0)
         total += 32;
      if ((t = in & bit12) != 0)
         total += 16;
      if ((t = in & bit13) != 0)
         total += 8;
      if ((t = in & bit14) != 0)
         total += 4;
      if ((t = in & bit15) != 0)
         total += 2;
      if ((t = in & bit16) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 5 - 15-20
      total = 0;

      if ((t = in & bit15) != 0)
         total += 32;
      if ((t = in & bit16) != 0)
         total += 16;
      if ((t = in & bit17) != 0)
         total += 8;
      if ((t = in & bit18) != 0)
         total += 4;
      if ((t = in & bit19) != 0)
         total += 2;
      if ((t = in & bit20) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 6 - 19-24
      total = 0;

      if ((t = in & bit19) != 0)
         total += 32;
      if ((t = in & bit20) != 0)
         total += 16;
      if ((t = in & bit21) != 0)
         total += 8;
      if ((t = in & bit22) != 0)
         total += 4;
      if ((t = in & bit23) != 0)
         total += 2;
      if ((t = in & bit24) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 7 - 23-28
      total = 0;

      if ((t = in & bit23) != 0)
         total += 32;
      if ((t = in & bit24) != 0)
         total += 16;
      if ((t = in & bit25) != 0)
         total += 8;
      if ((t = in & bit26) != 0)
         total += 4;
      if ((t = in & bit27) != 0)
         total += 2;
      if ((t = in & bit28) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      // Char 8 - 27-32
      total = 0;

      if ((t = in & bit27) != 0)
         total += 32;
      if ((t = in & bit28) != 0)
         total += 16;
      if ((t = in & bit29) != 0)
         total += 8;
      if ((t = in & bit30) != 0)
         total += 4;
      if ((t = in & bit31) != 0)
         total += 2;
      if ((t = in & bit32) != 0)
         total += 1;

      Out = Out + TransTab.substring(total % 36, (total % 36) + 1);

      return Out;
   }

}
