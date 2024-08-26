package com.systemwerx.PassGen.obsolete;
import java.io.*;

import java.lang.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import com.systemwerx.utils.LoadJCEProvider;
import com.systemwerx.utils.TranslateBean;


// Sample parms : 8E20 Trial 0017

public class licenseGen implements Serializable
{
   String ErrorMessage = "";
   	
   
   String generateLicense(String Host, String Expire, String LicNo,String ProdKey)
   {
	  String License; 
	  try
      { 
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
         Host = Host.toUpperCase();
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
        System.out.println("Exception >>>"+s+"  "+e.getMessage());
        e.printStackTrace();
        return "";
      }      
      
   return License;   

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
	   if ( Expire.equals("0999") )
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
	   return new String(sweek+"/"+smonth+"/"+syear);
      } 
      catch ( Exception e)
      {
        Class c = e.getClass();
        String s = c.getName();
        System.out.println("Exception >>>"+s+"  "+e.getMessage());
        e.printStackTrace();
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
	   
	   if ( Expire.equals("0999") )
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
   
   /* 
     Parameters 
     
     Expire host licenseno 
     
     8BB0 Trial 0017
                                                          */
   public static void main(String args[]) 
   {
    licenseGen pw=new licenseGen();
    license lic=new license();
    String Result = null;
    Calendar licdate;
    try 
    {
        if ( pw.isTrial(args[0] ))
        {
          System.out.println("License is a trial");
          Result = pw.generateLicense("Trial",args[0],args[2],"1234567812345678");
        }
        else
        {
          Result = pw.generateLicense(args[1],args[0],args[2],"1234567812345678");
        }
        
        System.out.println("License is "+args[0]+args[2]+Result);
        
    }
    catch ( Exception e)
    {
      Class c = e.getClass();
      String s = c.getName();
      System.out.println("Exception >>>"+s+"  "+e.getMessage());
      e.printStackTrace();
    }
   }   
}
