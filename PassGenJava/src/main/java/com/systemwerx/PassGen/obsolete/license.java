package com.systemwerx.PassGen.obsolete;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.utils.LoadJCEProvider;
import com.systemwerx.utils.trace;
import com.systemwerx.utils.TranslateBean;

public class license implements Serializable
{
   String ErrorMessage = "";
   boolean traceActive = false;
   trace tc;   

   public license()
   {
	  if ( System.getProperty("e3debug") != null )
	     {
	        traceActive = true;
	        tc = new trace();
         }	   
      if ( traceActive ) tc.traceMessage("In constructor");
   }
      	
   public boolean verifyLicenseFile(String ProdKey)
   {
	 String licenseFile;  
	 try
	  {  
	   if ( (licenseFile = System.getProperty("e3license")) == null )
       {
	       licenseFile = "license.dat";
       }
       else
       {
          if ( traceActive ) tc.traceMessage("License file selected : "+licenseFile);
       }
	   FileInputStream fis = new FileInputStream(licenseFile);
   	   ObjectInputStream ois = new ObjectInputStream(fis);

	   String Key = null;
	   try
	   {
	      Key = ois.readUTF();
       }
       
       catch ( java.io.FileNotFoundException ex )
       {
	       return false;
       }
	   
       if ( verifyLicense(Key.substring(0,4),Key.substring(4,8),ProdKey,Key.substring(8)) )
        {
//          System.out.println("License OK");
          return true;
        }
        else
        {
//          System.out.println("License invalid");
	      return false;
        }
      }
    
     catch ( java.io.FileNotFoundException ex )
     {
	       return false;
     }
     
     catch (java.io.EOFException ex)
     {
//        System.out.println("EOF detected"+ex.getMessage());
        return false;
     }
     catch (Exception ex)
     {
        Log.error("Exception detected - "+ex.getMessage());
        Log.error("Exception detected - "+ex.getClass().getName());
        ExceptionUtil.printStackTrace(ex);
        return false;
     }
   }
   
   public boolean verifyLicense(String Expire, String LicNo,String ProdKey,String Lic)
   { 
	  String License, Host = null, Firstchar = null; 
      Calendar expireDate;
	  try
      {
	     if ( isTrial(Expire))
	     {
		     Host = "Trial";
	     }
	     else
	     {  
	        Host = java.net.InetAddress.getLocalHost().getHostName();	    
         /* Is this a workstation license ???? */
         
            Firstchar = Expire.substring(0,1);
         
            if ( Firstchar.equals("4"))
            {
	            if (isServer())    
	            {
		            ErrorMessage = "This is a server with a workstation license";
		            return false;  // This is a server with a workstation license
	            }
            }
            
         }
         

         Host = Host.toUpperCase();
         
	     TranslateBean tb = new TranslateBean(); 
	     String CompleteKey = new String(); 
         //Provider sunJCE = new com.sun.crypto.provider.SunJCE();
         Provider sunJCE;
	     LoadJCEProvider jceProvider = new LoadJCEProvider();
	     sunJCE = jceProvider.load();
         Security.addProvider(sunJCE);
         Cipher c = Cipher.getInstance("DES");
         DESKeySpec Key = new DESKeySpec (tb.TransHexCharToBinaryData(ProdKey));
         SecretKeySpec DESKey = new SecretKeySpec (Key.getKey(), "DES");   
         MessageDigest md = MessageDigest.getInstance("SHA-1");
         byte [] datastream;
         CompleteKey = Expire+Host+LicNo;
         datastream = CompleteKey.getBytes();
         md.update(datastream);
         byte[] digest = md.digest();
         License = tb.TransToHexData(digest,Array.getLength(digest));
      }      
      catch ( Exception e)
      {
        Class c = e.getClass();
        String s = c.getName();
        Log.error("Exception >>>"+s+"  "+e.getMessage());
        ExceptionUtil.printStackTrace(e);
        return false;
      }
      
      if ( Lic.equals(License))
      {
	      /* check Expiry date  */
	      expireDate = getExpireCalendar(Expire);
	      if ( isAfterExpiry(expireDate))
	      {
		     ErrorMessage = "License Expired";
	         return false;
          }
	      return true;
      }
            
	  return false;
   }
   
   
   boolean isTrial(String Expire)
   {
	   String val = Expire.substring(0,1);
	   if ( val.equals("8") ||
	        val.equals("9") ||
	        val.equals("A") ||
	        val.equals("a") ||
	        val.equals("B") ||
	        val.equals("b") ||
	        val.equals("C") ||
	        val.equals("c") ||
	        val.equals("D") ||
	        val.equals("d") ||
	        val.equals("E") ||
	        val.equals("e") ||
	        val.equals("F") ||
	        val.equals("f")  )
	        return true;
	   return false;
   }
   
   String getExpireDateString(String Expire)
   {
	  try 
	  { 
	   byte[] data;
	   int datai;
	   if ( Expire.equals("0999") || Expire.equals("4999"))
	   {
	        return new String("31/12/2099");
       }
       
	   Integer dataint = Integer.decode("0x0000"+Expire);
	   datai = dataint.intValue();	  
       TranslateBean tb = new TranslateBean();
       datai = datai & 0x00007FFF;
	   
	   int nyear = datai & 0x0000FF00;
	   nyear = nyear >> 8;
	   int nmonth = datai & 0x000000F0;
	   nmonth = nmonth >> 4;
	   int nweek  = datai & 0x0000000F;
	   nweek = nweek * 7;
	   
	   String syear = Integer.toString(nyear);
	   if ( syear.length() == 1 )
	       syear = "0" + syear;
	   
	   String smonth = Integer.toString(nmonth);
	   if ( smonth.length() == 1 )
	       smonth = "0" + smonth;
	       
	   String sweek = Integer.toString(nweek);
	   if ( sweek.length() == 1 )
	       sweek = "0" + sweek;
           if ( sweek.equals("00")) sweek = "01";
	   return new String(sweek+"/"+smonth+"/"+syear);
      } 
      catch ( Exception e)
      {
        Class c = e.getClass();
        String s = c.getName();
        Log.error("Exception >>>"+s+"  "+e.getMessage());
        ExceptionUtil.printStackTrace(e);
        return "";
      }      
   }

   
   Calendar getExpireCalendar(String Expire)
   {
	  Calendar cal; 
	  try 
	  { 	  
	   byte[] data;
	   int datai;
	   
	   if ( Expire.equals("0999") || Expire.equals("4999"))
	   {
          cal = Calendar.getInstance();
          cal.clear();
          cal.set(Calendar.YEAR,2099);
          cal.set(Calendar.MONTH,12);
          cal.set(Calendar.DAY_OF_MONTH,31);
          return cal;
       }
       
	   Integer dataint = Integer.decode("0x0000"+Expire);
	   datai = dataint.intValue();	  
       TranslateBean tb = new TranslateBean();
       datai = datai & 0x00007FFF;
	   
	   int nyear = datai & 0x0000FF00;
	   nyear = nyear >> 8;
	   int nmonth = datai & 0x000000F0;
	   nmonth = nmonth >> 4;
	   int nweek  = datai & 0x0000000F;
	   nweek = nweek * 7;
	   
       cal = Calendar.getInstance();
       cal.clear();
       cal.set(Calendar.YEAR,nyear+2000);
       cal.set(Calendar.MONTH,nmonth-1);
       cal.set(Calendar.DAY_OF_MONTH,nweek);

	   return cal;
      } 
      catch ( Exception e)
      {
        Class c = e.getClass();
        String s = c.getName();
        System.out.println("Exception >>>"+s+"  "+e.getMessage());
        e.printStackTrace();
        return null;
      }      
   }
      
   boolean isAfterExpiry(Calendar ICal)
   {
	   Calendar current = Calendar.getInstance();
	   if ( ICal.after(current))
	      return false;
	   return true;
   }
   
   boolean isServer()
   {
	   String name;
	   name = System.getProperty("os.name");
	   System.out.println(System.getProperty("os.name"));
	   
	   if (name.equals("Windows NT")) return false;
	   if (name.equals("Windows XP")) return false;
	   if (name.equals("Windows 2003")) return true;
	   if (name.equals("WindowsNT")) return false;
	   if (name.equals("Solaris")) return true;
	   if (name.equals("HP-UX")) return true;
	   if (name.equals("AIX")) return true;
	   if (name.equals("Linux")) return true;
	   return true;
	   
   }
   
/*   public static void main(String args[]) 
   {
    license pw=new license();
    String Result = null;
    Calendar licdate;
    try 
    {
       System.out.println("myServer: local host name is " +
                            java.net.InetAddress.getLocalHost( ).
                            getHostName());	    
//        Result = pw.generateLicense("Trial","8493","0001","1234567812345678");
        System.out.println("License is "+Result);
        if ( pw.verifyLicense("8493","0001","1234567812345678",Result) )
        {
          System.out.println("License is valid");
        }
        if ( pw.isTrial("8413") )
        {
          System.out.println("License is a trial");
        }
        
        System.out.println("-----Lic 1 start----- ");
        licdate = pw.getExpireCalendar("8413");
        System.out.println("Expiry 1 is "+licdate.toString());
        System.out.println("Expiry 1 is "+licdate.get(Calendar.DAY_OF_MONTH)+"/"+licdate.get(Calendar.MONTH)+"/"+licdate.get(Calendar.YEAR));
        System.out.println("Expiry 1 is "+licdate.get(Calendar.DAY_OF_MONTH)+"/"+licdate.get(Calendar.MONTH)+"/"+licdate.get(Calendar.YEAR));
        if ( pw.isAfterExpiry(licdate))
        {
           System.out.println("Expired");
        }
        else 
           System.out.println("License OK");
        
        licdate = pw.getExpireCalendar("8484");
        System.out.println("Expiry 2 is "+licdate.toString());
        if ( pw.isAfterExpiry(licdate))
        {
           System.out.println("Expired");
        }
        else 
           System.out.println("License OK");
        
        System.out.println("License is "+Result);
        System.out.println("Expire date is "+pw.getExpireDateString("8413"));
    }
    catch ( Exception e)
    {
      Class c = e.getClass();
      String s = c.getName();
      System.out.println("Exception >>>"+s+"  "+e.getMessage());
      e.printStackTrace();
    }
   }   */
}
