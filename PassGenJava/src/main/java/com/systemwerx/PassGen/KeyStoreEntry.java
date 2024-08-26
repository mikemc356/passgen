package com.systemwerx.PassGen;

/* -------------------------------------------------------

   15 Chars - Application name 
   20 Chars - Userid
   20 Chars - Password
   3  Chars - Offset



   ------------------------------------------------------- */ 



public class KeyStoreEntry 
{
    String SessionKey = null;
    String ApplicationName = null;
    int GMTOffset = 0;
    String UserID = null;
    String Seed = null;
    int passwordType = 0;
    public static final int passwordType_Password = 1;
    public static final int passwordType_PassTicket = 2;
    public static final int passwordType_SKEY = 3;

    
	int getPasswordType()
	{
		return passwordType;
	}
	
	public String getSeed()
	{
		return Seed;
	}
	
	public String getSessionKey()
	{
		return SessionKey;
	}

	public String getUserID()
	{
		return UserID;
	}

	public int getGmtOffset()
	{
		return GMTOffset;
	}

	public String getApplicationName()
	{
		return ApplicationName;
	}
	
	public void setPasswordType(int i) throws KeyStoreEntryValueInvalid
	{
		switch ( i )
		{
			case passwordType_Password:
			     break;
			     
			case passwordType_PassTicket:
			     break;
			     
			case passwordType_SKEY:
			     break;
			     
			default:
		         throw new KeyStoreEntryValueInvalid("Invalid password type");
			     
		}
		
		passwordType = i;
	}
	
	public void setSessionKey(String k)  throws KeyStoreEntryValueInvalid
	{
		if ( k.length() > 20 )
		   throw new KeyStoreEntryValueInvalid("SessionKey is must be 20 or less characters");
		SessionKey = k;
	}
	
	public void setSeed(String k)  throws KeyStoreEntryValueInvalid
	{
		if ( k.length() > 20 )
		   throw new KeyStoreEntryValueInvalid("Seed must be 20 or less characters");
		Seed = k;
	}
	
	public void setUserID(String u) throws KeyStoreEntryValueInvalid
	{
		if ( u.length() > 20 )
		   throw new KeyStoreEntryValueInvalid("User must be 20 or less characters");
		UserID = u;
	}

	public void setGmtOffset(int o)
	{
		GMTOffset = o;
	}

	public void setApplicationName(String a) throws KeyStoreEntryValueInvalid
	{
		if ( a.length() > 20 )
		   throw new KeyStoreEntryValueInvalid("ApplicatioName must be 20 or less characters");
		ApplicationName = a;
	}
}
  