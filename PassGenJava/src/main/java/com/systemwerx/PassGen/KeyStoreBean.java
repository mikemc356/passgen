package com.systemwerx.PassGen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import com.systemwerx.utils.LoadJCEProvider;
import com.systemwerx.utils.TranslateBean;
import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;

public class KeyStoreBean  
{
    public static final int External_Keyfile = 1;
    public static final int Key_Supplied = 2;
    public static final int Internal_Key = 3;
    String Empty = new String("***Empty***    ");
    String keyStoreFileName;
    RandomAccessFile rafile = null;
    String TestKey = "12345678";
    Provider sunJCE;
    Cipher readCipher,writeCipher;
    DESedeKeySpec pbeKeySpec;
    PBEParameterSpec pbeParamSpec;
    SecretKeyFactory keyFac;
    SecretKey pbeKey;
    IvParameterSpec IV;
    byte[] keyin, emptybuf,temp;
    TranslateBean tb;
    
    // Salt
    byte[] salt = {
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };

    byte[] stashsalt = {
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99,
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99,
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };
    
    byte[] Key;

    public KeyStoreBean() throws Exception
    {
	    String password = new String("Password");
	    tb = new TranslateBean();
	    //sunJCE = new com.sun.crypto.provider.SunJCE();
	    //Provider sunJCE;
	    LoadJCEProvider jceProvider = new LoadJCEProvider();
	    sunJCE = jceProvider.load();
        Security.addProvider(sunJCE);
	    Empty = PadRight(Empty,88);
        keyFac = SecretKeyFactory.getInstance("DESede");
        readCipher = Cipher.getInstance("DESede/ECB/NoPadding");
        writeCipher = Cipher.getInstance("DESede/ECB/NoPadding");
		temp = Empty.getBytes();

    }
    
    boolean resetKey() throws Exception
    {
	    return true;
    }
    
    boolean setKey(String password) throws Exception
    {
        byte[] bytes = password.getBytes();
        bytes = MessageDigest.getInstance("MD5").digest(bytes);
        byte[] bytes1 = MessageDigest.getInstance("MD5").digest(bytes);
        keyin = new byte[32];
        System.arraycopy(bytes1,0,keyin,16,16);
        IV = new IvParameterSpec(salt);
        
        pbeKeySpec = new DESedeKeySpec (keyin);
        pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Initialize PBE Cipher with key and parameters
        writeCipher.init(Cipher.ENCRYPT_MODE, pbeKey);
        readCipher.init(Cipher.DECRYPT_MODE, pbeKey);
        emptybuf = writeCipher.doFinal(temp);
        return true;
    }
    
/*    public static void main(String args[]) 
    {
	    try 
	    {
		  File f = new File("keystore");
		  if ( f.exists() )
		  {
			  System.out.println("Deleting existing file");
		      f.delete();
		  }
		  
	      keyStoreBean kb = new keyStoreBean();
		  kb.saveEncryptionKey("stash","passworddata");
		  System.out.println("Key bad from stash "+kb.getEncryptionKey("stash"));
	      keyStoreEntry ke = new keyStoreEntry();
	      ke.setApplicationName("CICSA");
	      ke.setUserID("USERA");
	      ke.setSessionKey("0123456789abcdef");
	      ke.setGMTOffset(11);
	      kb.create(kb.Key_Supplied,"keystore","Password");
	      kb.open(kb.Key_Supplied,"keystore","Password");
		  System.out.println("Adding entry");
	      kb.addKey(ke);
	      ke.setApplicationName("CICSB");
		  System.out.println("Adding entry");
	      kb.addKey(ke);
		  System.out.println("Adding entry");
	      kb.addKey(ke);
	      kb.deleteKey(ke);
	      System.out.println("------------- Listing Apps ---------------");
	      
	      ke = kb.getFirstKey();
	      System.out.println("Application >"+ke.getApplicationName()+"<");
	      System.out.println("UserID      >"+ke.getUserID()+"<");
	      System.out.println("SessionKey  >"+ke.getSessionKey()+"<");
	      System.out.println("GMT Offset  >"+ke.getGMTOffset()+"<");
	      
	      while ( ( ke = kb.getNextKey()) != null )
	      {
	         System.out.println("Application >"+ke.getApplicationName()+"<");
	         System.out.println("UserID      >"+ke.getUserID()+"<");
	         System.out.println("SessionKey  >"+ke.getSessionKey()+"<");
	         System.out.println("GMT Offset  >"+ke.getGMTOffset()+"<");
	      }
        }
        catch ( Exception e )
        {
          Class c = e.getClass();
          String s = c.getName();
          System.out.println("Exception >>>"+s+"  "+e.getMessage());
          e.printStackTrace();
        }
    }*/

    /** Create a keystore. 
    
    Parms :
    
    Type = Type of Password (  External_Keyfile = Password file  
                               Key_Supplied = Password supplied;
                               Internal_Key = Reserved for future use )
     
    FileName = Filename of KeyStore
    Password = Password or name of Password file, depending on Type                          
    
                                 */
	public boolean create(int Type,String FileName,String Password) throws Exception
	{
        File f = new File(FileName);
        if ( f.exists() ) throw new KeyStoreAlreadyExists("This KeyStore already exists - cannot execute create() method on this file");
        RandomAccessFile ra = new RandomAccessFile(FileName,"rw");
        ra.close();
		keyStoreFileName = FileName;

		setKey(Password);		
		return true;
	}
	
    /** Open a keystore. 
    
    Parms :
    
    Type = Type of Password (  External_Keyfile = Password file  
                               Key_Supplied = Password supplied;
                               Internal_Key = Reserved for future use )
     
    FileName = Filename of KeyStore
    Password = Password or name of Password file, depending on Type                          
    
                                 */
	public boolean open(int Type,String FileName,String Password) throws Exception
	{
		long pos;
		String in;
		byte data[] = new byte[88];
		keyStoreFileName = FileName;
        rafile = new RandomAccessFile(FileName,"rw");
        rafile.read(data,0,88);
        in = new String(data);
        rafile.seek(0);
        
         
        switch (Type)
        {
	        case External_Keyfile:
	             setKey(getEncryptionKey(Password));
	             break;
	        case Key_Supplied:
        		 setKey(Password);		
	             break;
	        case Internal_Key:
	             break;
	        
        }        

		return true;
	}
	
	boolean saveEncryptionKey(String FileName,String IKey) throws Exception
	{
		byte[] outdata;
		int number= 0;
		File f = new File(FileName);
		String Modstring = new String(IKey);
/*		if ( ( IKey.length() % 8 )!= 0)
		   {
			   number = Modstring.length() / 8;
			   number += 1;
			   number *= 8;
			   Modstring = PadRight(Modstring,number);
		       System.out.println("Padding ");
		   }
		  */ 
//		byte keydata[] = Modstring.getBytes();
		byte keydata[] = IKey.getBytes();
		FileOutputStream fos = new FileOutputStream(f);
        Cipher outCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        IV = new IvParameterSpec(salt);
        
        DESedeKeySpec spec = new DESedeKeySpec (stashsalt);
        SecretKey key = keyFac.generateSecret(spec);

        // Initialize PBE Cipher with key and parameters
//        outCipher.init(Cipher.ENCRYPT_MODE, key, IV);
        outCipher.init(Cipher.ENCRYPT_MODE, key);
        outdata = outCipher.doFinal(keydata);
        fos.write(outdata);
        fos.close();
		return true;
	}

	
	String getEncryptionKey(String FileName) throws Exception
	{
		File f = new File(FileName);
		String instring;
		int insize = 0;
		byte keydata[] = new byte[100];
		byte keydata1[]  = null;
		byte indata[];
		FileInputStream fis = new FileInputStream(f);
        Cipher outCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        IV = new IvParameterSpec(salt);
        
        DESedeKeySpec spec = new DESedeKeySpec (stashsalt);
        SecretKey key = keyFac.generateSecret(spec);

        // Initialize PBE Cipher with key and parameters
//        outCipher.init(Cipher.DECRYPT_MODE, key, IV);
        outCipher.init(Cipher.DECRYPT_MODE, key);
        insize = fis.read(keydata);
        indata = outCipher.doFinal(keydata,0,insize);
        fis.close();
        instring = new String(indata);
        instring = instring.trim();
        return instring;
	}

	
	/** Add a Key to the Keystore. */
	boolean addKey(KeyStoreEntry IEntry) throws Exception
	{
		byte out[], writebuf[] ;
		String outrec = new String();
		String aa;
		outrec = IEntry.getApplicationName();
		outrec = PadRight(outrec,15);
		outrec = outrec + IEntry.getUserID();
		outrec = PadRight(outrec,35);
		outrec = outrec + IEntry.getSessionKey();
		outrec = PadRight(outrec,55);
		outrec = outrec + IEntry.getSeed();
		outrec = PadRight(outrec,75);
		outrec = outrec + outrec.valueOf(IEntry.getGmtOffset());
		outrec = PadRight(outrec,78);
		outrec = outrec + outrec.valueOf(IEntry.getPasswordType());
		outrec = PadRight(outrec,88);
//        System.out.println("rec :"+outrec);
//        System.out.println("rec :0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
//        System.out.println("rec :          1         2         3         4         5         6         7         8         9");
		
		
		/* Does this application exist ? */
		if ( !findApplication(PadRight(IEntry.getApplicationName(),15)))
		{
		/* Now scan for a space in the file to replace */
		
		   if ( findApplication(Empty))
		   {  
			/* Overlay an existing entry */
			out = outrec.getBytes();
            writebuf = writeCipher.doFinal(out);
			rafile.write(writebuf);
		   }
		   else
		   {
			/* Append to file */
			out = outrec.getBytes();
            writebuf = writeCipher.doFinal(out);
			rafile.write(writebuf);
		   }
		}
		else
		{
		  out = outrec.getBytes();
          writebuf = writeCipher.doFinal(out);
		  rafile.write(writebuf);
		}
 
		
		return true;
	}

	/** Get a keyStoreEntry from the keystore. */
	
	public KeyStoreEntry getKey(String Value) throws Exception
	{
        try
        {
		   if ( findApplication(Value))
		   {  
			   return getKey();
		   }
		   else
		   {
			   return null;
		   }
        }
        catch ( Exception e )
        {
          Class c = e.getClass();
          String s = c.getName();
          Log.error("Exception >>>"+s+"  "+e.getMessage());
		  ExceptionUtil.printStackTrace(e);
          return null;
        }
	}

	boolean findApplication(String Name) throws Exception
	{
		String in;
		String AppName = null;
		boolean forever = true;
		long fptr = 0;
		byte data[] = new byte[88];
		byte clearin[];
		Name = PadRight(Name,15);
		
        try
        {
           rafile.seek(0);
           while (forever)
           {
	         fptr = rafile.getFilePointer();
             rafile.read(data,0,88);
             clearin = readCipher.doFinal(data);
             
             if ( rafile.getFilePointer() == fptr)
             {
               rafile.seek(rafile.length());
               return false;
             }
             
             in = new String(clearin);
             AppName = in.substring(0,15);
             if ( AppName.equals(Name))
             {
	             rafile.seek(fptr);
	             return true;
             }
           }
        }
        
        catch ( java.io.EOFException e )
        {
          rafile.seek(rafile.length());
          rafile.setLength(rafile.length()+88);
          return false;
        }

        catch ( Exception e )
        {
          Class c = e.getClass();
          String s = c.getName();
          Log.error("Exception >>>"+s+"  "+e.getMessage());
		  ExceptionUtil.printStackTrace(e);
          return false;
        }
        
        return false;
	}
	
	/** Replace a keyStoreEntry in the keystore. */
	public boolean replaceKey(KeyStoreEntry IEntry) throws Exception
	{
		return addKey(IEntry);
	}
	
	KeyStoreEntry getKey() throws Exception
	{
		byte data[] = new byte[88];
		byte cleardata[];
		KeyStoreEntry ke = new KeyStoreEntry();
		String Temp;
		String in = null;
		long fptr = 0;
		boolean forever = true;

        while ( forever )
        {
            fptr = rafile.getFilePointer();
        
            if ( fptr == rafile.length() )
            {
	          return null; /* EOF */
            }
            
           rafile.read(data,0,88);
           cleardata = readCipher.doFinal(data);
           in = new String(cleardata);
           /* Is this an empty record ?? */
           if ( !in.equals(Empty))
           {
	           break;
           }
        }
        
        /* Load data into class */
        Temp = in.substring(0,14);
        Temp = Temp.trim();
//        System.out.println("App :"+Temp);
        ke.setApplicationName(Temp);
        Temp = in.substring(15,34);
        Temp = Temp.trim();
//        System.out.println("User:"+Temp);
        ke.setUserID(Temp);
        Temp = in.substring(35,54);
        Temp = Temp.trim();
//        System.out.println("key :"+Temp);
        ke.setSessionKey(Temp);
        Temp = in.substring(55,74);
        Temp = Temp.trim();
//        System.out.println("seed:"+Temp);
        ke.setSeed(Temp);
        Temp = in.substring(75,77);
        Temp = Temp.trim();
//        System.out.println("gmt :"+Temp);
        try
        {
           ke.setGmtOffset(Integer.parseInt(Temp));
        }
        catch (java.lang.NumberFormatException exd)
        {
           ke.setGmtOffset(0);
        }
//        System.out.println("GMT coming back "+Temp);
        Temp = in.substring(78,79);
        Temp = Temp.trim();
//        System.out.println("type:"+Temp);
        ke.setPasswordType(Integer.parseInt(Temp));
        
        
		return ke;
	}

	/** Get first Key entry in KeyStore  */
	public KeyStoreEntry getFirstKey() throws Exception
	{
		rafile.seek(0);
		resetKey();
		return getKey();
	}

	/** Get next entry in KeyStore. Use this with getFirstKey().  */
	public KeyStoreEntry getNextKey() throws Exception
	{
		return getKey();
	}

	/** Delete entry KeyStore.  */

	public boolean deleteKey(KeyStoreEntry IEntry) throws Exception
	{
		if ( findApplication(PadRight(IEntry.getApplicationName(),15)))
		{
		   rafile.write(emptybuf);
		   return true;
		}
		return false;
	}

	/** Delete entry KeyStore.  */
	public boolean deleteKey(String App) throws Exception
	{
		KeyStoreEntry ke = getKey(App);
		if ( ke == null ) return false;
		deleteKey(ke);
		return true;
	}
	
	/** Close KeyStore.  */
	public boolean close() throws Exception
	{
		rafile.close();
		return true;
	}
	
	/** Change key of currently open keystore. IKey is new key     */
	
	public boolean changeEncryptionKey(String IKey)
	{
		try
		{
	      KeyStoreBean tkb = new KeyStoreBean();
	      KeyStoreEntry tke;
	      /* Now cycle through the keystore and write a new one */
//	      System.out.println("------------- Listing Apps ---------------");
	      
	      tke = getFirstKey();
	      
	      if ( tke == null ) // Empty keystore - just set new key
	      {
		      setKey(IKey);
		      return true;
	      }

		  // Create new keystore in same dir as current
		  File keyStoreFile = new File(keyStoreFileName);
		  Path keyStorePath = keyStoreFile.toPath();
		  Path newPath = keyStorePath.resolveSibling("newkeystore");
		  String newName = newPath.toString();

	      tkb.create(Key_Supplied,newName,IKey);
	      tkb.open(Key_Supplied,newName,IKey);

//	      System.out.println("Application >"+tke.getApplicationName()+"<");
//	      System.out.println("UserID      >"+tke.getUserID()+"<");
//	      System.out.println("SessionKey  >"+tke.getSessionKey()+"<");
//	      System.out.println("GMT Offset  >"+tke.getGMTOffset()+"<");
	      tkb.addKey(tke);
	      
	      while ( ( tke = getNextKey()) != null )
	      {
//	         System.out.println("Application >"+tke.getApplicationName()+"<");
//	         System.out.println("UserID      >"+tke.getUserID()+"<");
//	         System.out.println("SessionKey  >"+tke.getSessionKey()+"<");
//	         System.out.println("GMT Offset  >"+tke.getGMTOffset()+"<");
	         tkb.addKey(tke);
	      }
	      
	      tkb.close();
	      close();
	      File kf = new File(keyStoreFileName);
	      kf.delete();
	      File kf1 = new File(newName);
	      kf1.renameTo(kf);
	      open(Key_Supplied,keyStoreFileName,IKey);
	      
        }
        catch ( Exception e )
        {
          Class c = e.getClass();
          String s = c.getName();
          Log.error("Exception >>>"+s+"  "+e.getMessage());
		  ExceptionUtil.printStackTrace(e);
          return false;
        }
		return true;
    }
	
	String PadRight(String In,int Length)
	{
		if ( In.length() < Length )
		   {
			   while ( In.length() < Length )
			   {
				   In = In + " ";
			   }
		   }
		   
		return In;
	}

	public String getKeyStoreFileName() {
		return keyStoreFileName;
	}

	public void setKeyStoreFileName(String keyStoreFileName) {
		keyStoreFileName = keyStoreFileName;
	}
	
}
  