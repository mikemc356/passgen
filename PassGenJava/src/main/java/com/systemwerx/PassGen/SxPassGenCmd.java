package com.systemwerx.PassGen;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;

public class SxPassGenCmd {

	/**
	 * PassGen command line interface 
	 * 
	 * @param args
	 */
	
	KeyStoreBean kb;
	
	public SxPassGenCmd()
	{
		
	}
	
	public static void main(String[] args) 
	{
		SxPassGenCmd cmd = new SxPassGenCmd();
		
        if ( args.length == 1 && args[0].equals("test"))
        {
        	// Generate Passtickets
        	String tcmd = "/GENERATEPTKT /application:TSOS0W1 /OFFSET:0 /USERID:MIKEM /SESSKEY:E001193519561977";
        	tcmd = "/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwd:mike";
        	tcmd = "/SAVEPASSWORD /KEYSTORE:keystore /keystpwd:mike /KEYSTPWDF:stash";
        	tcmd = "/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwdf:stash";
        	tcmd = "/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwdf:stash";
        	tcmd = "/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwdf:stash /RUNCOMMAND:cmd.exe /c echo $userid $passticket > c:\\work\\test.txt";
        	tcmd = "/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwdf:stash /RUNCOMMAND:cmd.exe /c echo $userid $passticket > c:\\work\\test.txt";
        	tcmd = "/GENERATESKEY /application:TSOS0W1 /SEED:dog /PASSWORD:mike /SEQUENCE:21";
        	tcmd = "/GENERATESKEY /application:TSOS0W1 /SEED:dog /PASSWORD:mike /SEQUENCE:21 /RUNCOMMAND:cmd.exe /c echo $skey > c:\\work\\test.txt";
        	tcmd = "/GENERATESKEY /application:SKEY /KEYSTORE:keystore /keystpwd:mike /SEQUENCE:21 /RUNCOMMAND:cmd.exe /c echo $skey > c:\\work\\test.txt";
        	tcmd = "/GENERATESKEY /application:SKEY /KEYSTORE:keystore /keystpwdf:stash /SEQUENCE:21 /RUNCOMMAND:cmd.exe /c echo $skey > c:\\work\\test.txt";
        	tcmd = "/GETPASSWORD /application:PASSWORD /KEYSTORE:keystore /keystpwdf:stash /RUNCOMMAND:cmd.exe /c echo $password > c:\\work\\test.txt";
        	tcmd = "/CREATEapplication /application:PTKT /TYPE:PTKT /KEYSTORE:keystore /keystpwdf:stash /SESSKEY:E001193519561977 /OFFSET:0 /USERID:MIKEM";
        	String[] arr=tcmd.split(" ");
        	cmd.processCommandLine(arr);
        	tcmd = "/MODIFYapplication /application:PTKT /TYPE:PTKT /KEYSTORE:keystore /keystpwdf:stash /SESSKEY:E001193519561977 /OFFSET:0 /USERID:MIKEA";
        	tcmd = "/DELETEapplication /application:PTKT /KEYSTORE:keystore /keystpwdf:stash";
        		
        	//String[] 
        	arr=tcmd.split(" ");
        	cmd.processCommandLine(arr);
        	return;
        }
        
        int rc = cmd.processCommandLine(args);
        System.exit(rc);
        
	}

	public int processCommandLine(String[] args )
	{
		try
		{
		  kb = new KeyStoreBean();
		  // Get command and pass to function 
		  if ( args.length < 2 )
		  {
			log("Invalid command.");
			help();
		  }
		
		  if (args[0].toUpperCase().equals("/GENERATEPTKT"))
		  {
			return generatePTKT(args);
		  }
		  else if (args[0].toUpperCase().equals("/GENERATESKEY"))
		  {
			return generateSKEY(args);
		  }
		  else if (args[0].toUpperCase().equals("/GETPASSWORD"))
		  {
			return getPassword(args);
		  }
		  else if (args[0].toUpperCase().equals("/SAVEPASSWORD"))
		  {
			return savePassword(args);
		  }
		  else if (args[0].toUpperCase().equals("/CREATEAPPLICATION"))
		  {
			return createApplication(args);
		  }
		  else if (args[0].toUpperCase().equals("/DELETEAPPLICATION"))
		  {
			return deleteApplication(args);
		  }
		  else if (args[0].toUpperCase().equals("/MODIFYAPPLICATION"))
		  {
			return modifyApplication(args);
		  }
		  else if (args[0].toUpperCase().equals("/DISPLAYAPPLICATION"))
		  {
			return displayApplication(args);
		  }
		  else
		  {
			log("Invalid command.");
			help();
			return 8;
		  }
	   }
	   catch (Exception ex)
	   {
	       Log.error("Exception detected - "+ex.getMessage());
	       Log.error("Exception detected - "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
	       return 16;
	   }
	}
	
	int generatePTKT(String[] args )
	{
		try
		{
		  String application = null;
		  String transtable = null;
		  String userid = null;
		  String sessionKey = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  int offset = -999;
		  String runCommand  = null;
		  boolean setEnvVariables = false;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/APPLICATION:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if ( args[count].length() > 11 && args[count].toUpperCase().substring(0,12).equals("/TRANSTABLE:"))
			 {
				transtable = args[count].substring(12); 
			 } 
			 else if ( args[count].length() > 7 && args[count].toUpperCase().substring(0,8).equals("/USERID:"))
			 {
				userid = args[count].substring(8); 
			 } 
			 else if (  args[count].length() > 8 && args[count].toUpperCase().substring(0,9).equals("/SESSKEY:"))
			 {
				sessionKey = args[count].substring(9); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 7 && args[count].toUpperCase().substring(0,8).equals("/OFFSET:"))
			 {
				try
				{
				  offset = Integer.parseInt(args[count].substring(8));
				}
				catch (Exception ex)
				{
					log("Invalid Offset format.");
					return 8;
				}
			 } 
			 else if ( args[count].length() > 11 && args[count].toUpperCase().substring(0,12).equals("/RUNCOMMAND:"))
			 {
				runCommand = args[count].substring(12);
				// Now get the rest of the string ....
				  for (count++; count < args.length; count++)
				  {
					  runCommand = runCommand +" "+args[count];
				  }
				
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		 }
		
		 /* Now validate paremeters */
		
		 if ( application == null )
		 {
			log("application is a required parameter");
			return 8;
		 }
		
		 if ( keyStore == null )
		 {
			// There is no keystore - we are getting all parms from command line 
			
			if ( sessionKey == null )
			{
				log("SESSKEY is a required parameter");
				return 8;
			}
			
			if ( userid == null )
			{
				log("USERID is a required parameter");
				return 8;
			}
			
			if ( offset == -999 )
			{
				log("OFFSET is a required parameter");
				return 8;
			}
			
            pw=new PassTicketPureBean();
            
            pw.setUserID(userid);
            pw.setGmtOffset(offset);
            if ( transtable == null) 
            {
                pw.setTranslateTable("trans.txt");
            }
            else
            {
                pw.setTranslateTable(transtable);
            }
            pw.setSessionKey(sessionKey);
            pw.setApplication(application);
            result = pw.getPassTicket();
            log(result);
   		    // Run command if requested 
   		    if ( runCommand != null)
   		    {
   			 return runCommand(runCommand, "\\$passticket",result,userid);
   		    }
            return 0;
		 }
		 else
		 {
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
				log("KEYSTPWD or KEYSTPWDF is a required parameter");
				return 8;
			}
			
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
	          kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			  // Password file
	          kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			
            pw=new PassTicketPureBean();
            
            try
            {
               ke = kb.getKey(application);
			   if ( ke == null ) {
			      log("Application not found");
				  return 8;
				}
            }
            catch (Exception ex)
            {
			   Log.error("Exception detected - "+ex.getMessage());
			   Log.error("Exception detected - "+ex.getClass().getName());
               ex.printStackTrace();
               return 16;
            }
            
            pw.setUserID(ke.getUserID());
            pw.setGmtOffset(ke.getGmtOffset());
            if ( transtable == null) 
            {
                pw.setTranslateTable("trans.txt");
            }
            else
            {
                pw.setTranslateTable(transtable);
            }
            pw.setSessionKey(ke.getSessionKey());
            pw.setApplication(ke.getApplicationName());
            result = pw.getPassTicket();
            log(result);
            
   		    // Run command if requested 
   		    if ( runCommand != null)
   		    {
   			 return runCommand(runCommand, "\\$passticket",result,ke.getUserID());
   		    }
            return 0;
			
		 }
	    }
	    catch (Exception ex)
	    {
		  Log.error("Exception detected - "+ex.getMessage());
		  Log.error("Exception detected - "+ex.getClass().getName());
		  ex.printStackTrace();
		  return 16;
	    }
	}
	
	int generateSKEY(String[] args )
	{
		try
		{
		  String application = null;
		  String seed = null;
		  String password = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  int sequence = -1;
		  String runCommand  = null;
		  boolean setEnvVariables = false;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/APPLICATION:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if ( args[count].length() > 7 && args[count].toUpperCase().substring(0,6).equals("/SEED:"))
			 {
				seed = args[count].substring(6); 
			 } 
			 else if (  args[count].length() > 8 && args[count].toUpperCase().substring(0,10).equals("/PASSWORD:"))
			 {
				password = args[count].substring(10); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 8 && args[count].toUpperCase().substring(0,10).equals("/SEQUENCE:"))
			 {
				try
				{
				  sequence = Integer.parseInt(args[count].substring(10));
				}
				catch (Exception ex)
				{
					log("Invalid Sequence format.");
					return 8;
				}
			 } 
			 else if ( args[count].length() > 11 && args[count].toUpperCase().substring(0,12).equals("/RUNCOMMAND:"))
			 {
				runCommand = args[count].substring(12); 
				// Now get the rest of the string ....
				  for (count++; count < args.length; count++)
				  {
					  runCommand = runCommand +" "+args[count];
				  }
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		 }
		
		 /* Now validate parameters */
		
		 if ( application == null )
		 {
			log("application is a required parameter");
			return 8;
		 }
		
		 if ( keyStore == null )
		 {
			// There is no keystore - we are getting all parms from command line 
			
			if ( seed == null )
			{
				log("SEED is a required parameter");
				return 8;
			}
			
			if ( password == null )
			{
				log("PASSWORD is a required parameter");
				return 8;
			}
			
			if ( sequence == -1 )
			{
				log("SEQUENCE is a required parameter");
				return 8;
			}
			
	        SKeyBean s = new SKeyBean();
	        s.setSeed(seed);
	        s.setPassword(password);
	        s.setHashAlgorithm(s.MD4);
	        result = s.generatePassword(sequence);
            
   		    // Run command if requested 
   		    if ( runCommand != null)
   		    {
   			 return runCommand(runCommand, "\\$skey",result,"");
   		    }
   		    
            log(result);
            return 0;
		 }
		 else
		 {
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
				log("KEYSTPWD or KEYSTPWDF is a required parameter");
				return 8;
			}
		
			if ( sequence == -1 )
			{
				log("SEQUENCE is a required parameter");
				return 8;
			}
			
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
	          kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			  // Password file
	          kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			
            try
            {
               ke = kb.getKey(application);
            }
            catch (Exception ex)
            {
			   Log.error("Exception detected - "+ex.getMessage());
               Log.error("Exception detected - "+ex.getClass().getName());
               ex.printStackTrace();
               return 16;
            }
            
	        SKeyBean s = new SKeyBean();
	        s.setSeed(ke.getSeed());
	        s.setPassword(ke.getSessionKey());
	        s.setHashAlgorithm(s.MD4);
	         
		    result = s.generatePassword(sequence);
	            
	        log(result);
   		    // Run command if requested 
   		    if ( runCommand != null)
   		    {
   			 return runCommand(runCommand, "\\$skey",result,ke.getUserID());
   		    }
            return 0;
			
		 }
	    }
	    catch (Exception ex)
	    {
		  Log.error("Exception detected - "+ex.getMessage());
		  Log.error("Exception detected - "+ex.getClass().getName());
		  ExceptionUtil.printStackTrace(ex);
		  return 16;
	    }
	}
	
	int getPassword(String[] args )
	{
		try
		{
		  String application = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  String runCommand  = null;
		  KeyStoreEntry ke;
		  String result;
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/application:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 11 && args[count].toUpperCase().substring(0,12).equals("/RUNCOMMAND:"))
			 {
				runCommand = args[count].substring(12);
				// Now get the rest of the string ....
				  for (count++; count < args.length; count++)
				  {
					  runCommand = runCommand +" "+args[count];
				  }
				
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		 }
		
		 /* Now validate paremeters */
		
		 if ( application == null )
		 {
			log("application is a required parameter");
			return 8;
		 }
		
		 // We are using Keystore - get parameters from there 
		 if ( keyStorePassword == null && keyStorePasswordFile == null  )
		 {
				log("KEYSTPWD or KEYSTPWDF is a required parameter");
				return 8;
		 }
		 	
		 // Now get values from keystore
		 if ( keyStorePassword != null)
		 {
	       kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
		 }
		 else
		 {
		  // Password file
	       kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
		 }
			
            
         try
         {
           ke = kb.getKey(application);
         }
         catch (Exception ex)
         {
           Log.error("Exception detected - "+ex.getMessage());
           Log.error("Exception detected - "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
           return 16;
         }
            
         log(ke.getSessionKey());
            
   		 // Run command if requested 
   		 if ( runCommand != null)
   		 {
   			 return runCommand(runCommand, "\\$password",ke.getSessionKey(),ke.getUserID());
   		 }
         return 0;
			
	    }
	    catch (Exception ex)
	    {
		  Log.error("Exception detected - "+ex.getMessage());
		  Log.error("Exception detected - "+ex.getClass().getName());
		  ExceptionUtil.printStackTrace(ex);
		  return 16;
	    }
	}
	
	int savePassword(String[] args )
	{
		try
		{
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  KeyStoreEntry ke;
		  String result;
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
		   }
		
		 /* Now validate paremeters */
		
		   if ( keyStorePassword == null )
		   {
			 log("KEYSTPWD is a required parameter");
			 return 8;
		   }
		
		   if ( keyStorePasswordFile == null )
		   {
			 log("KEYSTPWDF is a required parameter");
			 return 8;
		   }
		   
		   if ( keyStore == null )
		   {
			 log("KEYSTORE is a required parameter");
			 return 8;
		   }
		   
		   if (!kb.saveEncryptionKey(keyStorePasswordFile, keyStorePassword))
		   {
			   log("SAVEPASSWORD failed");
			   return 16;
		   }
			
		   log("SAVEPASSWORD completed successfully");
           return 0;
			
	    }
	    catch (Exception ex)
	    {
		  Log.error("Exception detected - "+ex.getMessage());
		  Log.error("Exception detected - "+ex.getClass().getName());
		  ExceptionUtil.printStackTrace(ex);
		  return 16;
	    }
	}
	
	int createApplication(String[] args )
	{
		  String application = null;
		  String transtable = null;
		  String userid = null;
		  String sessionKey = null;
		  String seed = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  String type = null;
		  int offset = -1;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
          
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/APPLICATION:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if ( args[count].length() > 7 && args[count].toUpperCase().substring(0,8).equals("/USERID:"))
			 {
				userid = args[count].substring(8); 
			 } 
			 else if (  args[count].length() > 8 && args[count].toUpperCase().substring(0,9).equals("/SESSKEY:"))
			 {
				sessionKey = args[count].substring(9); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/PASSWORD:"))
			 {
				sessionKey = args[count].substring(10); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 5 && args[count].toUpperCase().substring(0,6).equals("/TYPE:"))
			 {
				type = args[count].substring(6); 
			 } 
			 else if ( args[count].length() > 8 && args[count].toUpperCase().substring(0,8).equals("/OFFSET:"))
			 {
				try
				{
				  offset = Integer.parseInt(args[count].substring(8));
				}
				catch (Exception ex)
				{
					log("Invalid Offset format.");
					return 8;
				}
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		  }
		  try
		  {
			/* Now validate paremeters */
				
			if ( application == null )
			{
			  log("application is a required parameter");
			  return 8;
			}
			
			if ( type == null )
			{
			  log("TYPE is a required parameter");
			  return 8;
			}
			
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
			  log("KEYSTPWD or KEYSTPWDF is a required parameter");
			  return 8;
		    }
			 
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
		       kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			   // Password file
		       kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			   
			if ( type.length() == 4 && type.toUpperCase().equals("SKEY"))
			{
			  ke = new KeyStoreEntry();
			  ke.setPasswordType(ke.passwordType_SKEY);
			  ke.setApplicationName(application);
			  if ( seed == null )
			  {
			    log("Error - SEED is a required parameter");
			    kb.close();
			    return 12;
			  }
			  ke.setSeed(seed);
			  
			  if ( sessionKey == null )
			  {
 			    log("Error - PASSWORD is a required parameter");
				kb.close();
				return 12;
			  }
			  
			  ke.setSessionKey(sessionKey);
			  if ( userid == null )
			  {
			    log("Error - USERID is a required parameter");
			    kb.close();
			    return 12;
			  }
			  ke.setUserID(userid);
			  kb.addKey(ke);
			}
			else if ( type.length() == 4 && type.toUpperCase().equals("PTKT"))
			{
			   ke = new KeyStoreEntry();
			   ke.setApplicationName(application);
			   ke.setPasswordType(ke.passwordType_PassTicket);
			   if ( sessionKey == null )
			   {
				   log("Error - SESSKEY is a required parameter");
				   kb.close();
				   return 12;
			   }
			   ke.setSessionKey(sessionKey);
			   if ( userid == null )
			   {
				   log("Error - USERID is a required parameter");
				   kb.close();
				   return 12;
			   }
			   ke.setUserID(userid);
			   
			   if ( offset == -1 )
			   {
				   log("Error - OFFSET is a required parameter");
				   kb.close();
				   return 12;
			   }
			   ke.setGmtOffset(offset);
			   
			   kb.addKey(ke);
			}
			else if ( type.length() == 3 && type.toUpperCase().equals("PWD"))
			{
			   ke = new KeyStoreEntry();
			   ke.setApplicationName(application);
			   ke.setPasswordType(ke.passwordType_Password);
			   if ( sessionKey == null )
			   {
				   log("Error - PASSWORD is a required parameter");
				   kb.close();
				   return 12;
			   }
			   ke.setSessionKey(sessionKey);
			   if ( userid == null )
			   {
				   log("Error - USERID is a required parameter");
				   kb.close();
				   return 12;
			   }
			   ke.setUserID(userid);
			   
			   kb.addKey(ke);
			}
			else
			{
			   log("Invalid TYPE - expecting SKEY, PTKT or PWD.");
			   return 12;
			}
			kb.close();
			return 0;
		 }
		 catch (Exception ex)
		 {
		   Log.error("Exception detected "+ex.getMessage());
		   Log.error("Exception detected "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
		   return -1;
		 }
			 
	}
	
	int deleteApplication(String[] args )
	{
		  String application = null;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/application:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		  }
		  try
		  {
			/* Now validate paremeters */
				
			if ( application == null )
			{
			  log("application is a required parameter");
			  return 8;
			}
			
			if ( keyStore == null )
			{
			  log("KEYSTORE is a required parameter");
			  return 8;
			}
			
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
			  log("KEYSTPWD or KEYSTPWDF is a required parameter");
			  return 8;
		    }
			 
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
		       kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			   // Password file
		       kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			 
			// Now get application details 
	         try
	         {
	        	 if ( !kb.deleteKey(application))
	        	 {
	        		 log("Error deleting application");
	        		 return 16;
	        	 }
	        	 
	 			 kb.close();
	             return 0;
	         }
	         catch (Exception ex)
	         {
	           Log.error("Exception detected - "+ex.getMessage());
	           Log.error("Exception detected - "+ex.getClass().getName());
			   ExceptionUtil.printStackTrace(ex);
	           return 16;
	         }
			
		 }
		 catch (Exception ex)
		 {
		   Log.error("Exception detected "+ex.getMessage());
		   Log.error("Exception detected "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
		   return -1;
		 }
			 
	}
	
	int modifyApplication(String[] args )
	{
		  String application = null;
		  String transtable = null;
		  String userid = null;
		  String sessionKey = null;
		  String seed = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  String type = null;
		  int offset = -1;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
          
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/application:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if ( args[count].length() > 7 && args[count].toUpperCase().substring(0,8).equals("/USERID:"))
			 {
				userid = args[count].substring(8); 
			 } 
			 else if (  args[count].length() > 8 && args[count].toUpperCase().substring(0,9).equals("/SESSKEY:"))
			 {
				sessionKey = args[count].substring(9); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/PASSWORD:"))
			 {
				sessionKey = args[count].substring(10); 
			 } 
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 5 && args[count].toUpperCase().substring(0,6).equals("/TYPE:"))
			 {
				type = args[count].substring(6); 
			 } 
			 else if ( args[count].length() > 8 && args[count].toUpperCase().substring(0,8).equals("/OFFSET:"))
			 {
				try
				{
				  offset = Integer.parseInt(args[count].substring(8));
				}
				catch (Exception ex)
				{
					log("Invalid Offset format.");
					return 8;
				}
			 } 
			 else
			 {
				 log("Invalid parameter : "+args[count]);
				 return 16;
			 }
		  }
		  try
		  {
			/* Now validate paremeters */
				
			if ( application == null )
			{
			  log("application is a required parameter");
			  return 8;
			}
			
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
			  log("KEYSTPWD or KEYSTPWDF is a required parameter");
			  return 8;
		    }
			 
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
		       kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			   // Password file
		       kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			 
			// Now get application details 
	         try
	         {
	           ke = kb.getKey(application);
	         }
	         catch (Exception ex)
	         {
			   Log.error("Exception detected - "+ex.getMessage());
	           Log.error("Exception detected - "+ex.getClass().getName());
			   ExceptionUtil.printStackTrace(ex);
	           return 16;
	         }
			
			if ( type.length() == 4 && type.toUpperCase().equals("SKEY"))
			{
			  ke.setPasswordType(ke.passwordType_SKEY);
			  ke.setApplicationName(application);
			  if ( seed != null )  ke.setSeed(seed);
			  
			  if ( sessionKey != null ) ke.setSessionKey(sessionKey);
			  if ( userid != null ) ke.setUserID(userid);
			  kb.addKey(ke);
			}
			else if ( type.length() == 4 && type.toUpperCase().equals("PTKT"))
			{
			   ke = new KeyStoreEntry();
			   ke.setApplicationName(application);
			   ke.setPasswordType(ke.passwordType_PassTicket);
			   if ( sessionKey != null ) ke.setSessionKey(sessionKey);
			   if ( userid != null ) ke.setUserID(userid);
			   
			   if ( offset != -1 ) ke.setGmtOffset(offset);
			   
			   kb.addKey(ke);
			}
			else if ( type.length() == 3 && type.toUpperCase().equals("PWD"))
			{
			   ke = new KeyStoreEntry();
			   ke.setApplicationName(application);
			   ke.setPasswordType(ke.passwordType_Password);
			   if ( sessionKey != null ) ke.setSessionKey(sessionKey);
			   if ( userid != null )ke.setUserID(userid);
			   
			   kb.addKey(ke);
			}
			else
			{
			   log("Invalid TYPE - expecting SKEY, PTKT or PWD.");
			   return 12;
			}
			kb.close();
			return 0;
		 }
		 catch (Exception ex)
		 {
		   Log.error("Exception detected "+ex.getMessage());
		   Log.error("Exception detected "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
		   return -1;
		 }
			 
	}

	int displayApplication(String[] args )
	{
		  String application = null;
		  String transtable = null;
		  String userid = null;
		  String sessionKey = null;
		  String seed = null;
		  String keyStore = null;
		  String keyStorePassword = null;
		  String keyStorePasswordFile = null;
		  String type = null;
		  int offset = -1;
		  KeyStoreEntry ke;
		  String result;
          PassTicketPureBean pw;
          
          
		  // Parse values into variables 
		  for ( int count = 1; count < args.length; count++)
		  {
			 if ( args[count].length() > 12 && args[count].toUpperCase().substring(0,13).equals("/application:"))
			 {
			 	application = args[count].substring(13); 
			 }
			 else if (  args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTORE:"))
			 {
				keyStore = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 9 && args[count].toUpperCase().substring(0,10).equals("/KEYSTPWD:"))
			 {
				keyStorePassword = args[count].substring(10); 
			 } 
			 else if ( args[count].length() > 10 && args[count].toUpperCase().substring(0,11).equals("/KEYSTPWDF:"))
			 {
				keyStorePasswordFile = args[count].substring(11); 
			 } 
			 else if ( args[count].length() > 5 && args[count].toUpperCase().substring(0,6).equals("/TYPE:"))
			 {
				type = args[count].substring(6); 
			 } 
		  }
		  try
		  {
			/* Now validate paremeters */
				
			/*if ( application == null )
			{
			  log("application is a required parameter");
			  return 8;
			}*/
			
			// We are using Keystore - get parameters from there 
			if ( keyStorePassword == null && keyStorePasswordFile == null  )
			{
			  log("KEYSTPWD or KEYSTPWDF is a required parameter");
			  return 8;
		    }
			 
			// Now get values from keystore
			if ( keyStorePassword != null)
			{
		       kb.open(kb.Key_Supplied,keyStore,keyStorePassword);
			}
			else
			{
			   // Password file
		       kb.open(kb.External_Keyfile,keyStore,keyStorePasswordFile);
			}
			 
			// Now get application details 
	         try
	         {
	      	   while ( ( ke = kb.getNextKey()) != null )
	      	   {
	      		   
	   	        log("application : "+ke.getApplicationName());
			    switch (ke.getPasswordType())
			    {
			       case KeyStoreEntry.passwordType_SKEY:
			    	   log("TYPE : SKEY");
			    	   log("SEED :"+ke.getSeed());
			    	   break;
			       case KeyStoreEntry.passwordType_PassTicket:
			    	   log("TYPE : PASSTICKET");
			    	   log("USERID :"+ke.getUserID());
			    	   log("OFFSET :"+ke.getGmtOffset());
			    	   break;
			       case KeyStoreEntry.passwordType_Password:
			    	   log("TYPE : PASSWORD");
			    	   log("USERID :"+ke.getUserID());
			    	   break;
			       default:
			    	   log("Invalid password type");
			    	   return 8;
			    }
			    
	      	   }
			  kb.close();
	         }
	         catch (Exception ex)
	         {
			   Log.error("Exception detected - "+ex.getMessage());
	           Log.error("Exception detected - "+ex.getClass().getName());
			   ExceptionUtil.printStackTrace(ex);
	           return 16;
	         }

			return 0;
		 }
		 catch (Exception ex)
		 {
		   Log.error("Exception detected "+ex.getMessage());
		   Log.error("Exception detected "+ex.getClass().getName());
		   ExceptionUtil.printStackTrace(ex);
		   return -1;
		 }
			 
	}
	
	
	void help()
	{
		log("PassGen command line interface - Copyright (c) 2011 Systemwerx Ltd.");
		log("");
		log("Command format :");
		log("");
		log("  SxPassGenCmd /GENERATEPTKT /application:{applicationname}");
	}
	
	void log(String in)
    {
		System.out.println(in);
	}
	
	int runCommand(String Command, String PwVarName, String Password, String Userid)
	{
		try
		{
   	      String exec = Command.replaceAll(PwVarName, Password);
		  exec = exec.replaceAll("\\$userid", Userid);
		  log("Executing command : "+exec);
		  Process child = Runtime.getRuntime().exec(exec);
		  child.waitFor();
		  int rc = child.exitValue();
		  log("Command returned "+rc+" return code");
		  return rc;
		}
	    catch (Exception ex)
	    {
		  Log.error("Exception detected "+ex.getMessage());
		  Log.error("Exception detected "+ex.getClass().getName());
		  ExceptionUtil.printStackTrace(ex);
		  return -1;
	    }
	}
}
