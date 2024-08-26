package com.systemwerx.PassGen.obsolete;

//import org.apache.axis.Service.*;
import java.net.*;
import java.applet.*;
import java.net.*;
import java.security.*; 
import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.File.*;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.lang.reflect.*;
import java.lang.Class;
import java.io.*;

import com.systemwerx.common.event.Log;
import com.systemwerx.common.util.ExceptionUtil;
import com.systemwerx.utils.*;


public class passGenGUILicenseManagerThread extends Thread 
{
	licenseManager b = null;
    boolean traceActive = false;
    trace tc;
    Object caller;
    boolean exitVM = false;

    passGenGUILicenseManagerThread(Object icaller,boolean iexit)
    {
	    super();
	    if ( System.getProperty("e3debug") != null )
	    {
	      traceActive = true;
	      tc = new trace();
        }
        caller = icaller;
        exitVM = iexit;
        if ( traceActive ) tc.traceMessage("In constructor");	    
    }
    
	 /**
	  * @wbp.parser.entryPoint
	  */
	 public void run() 
     {
         if ( traceActive ) tc.traceMessage("Starting run()");	    
	     try
         {
            if ( traceActive ) tc.traceMessage("Display license manager");	    
	         b = new licenseManager(this);
	         b.setTitle("License Manager");
//	         b.showDisplay();
	         synchronized (this) 
	         {
                 if ( traceActive ) tc.traceMessage("Waiting for license manager termination");	    
		         wait();
	         }
         }
         catch (Exception ex)
         {
           Log.error("Exception detected - "+ex.getMessage());
           Log.error("Exception detected - "+ex.getClass().getName());
           ExceptionUtil.printStackTrace(ex);
         }
	     
         if ( traceActive ) tc.traceMessage("license manager terminated - exiting");	    
         if ( caller != null )
         {
  		      synchronized ( caller )
		      {
		         caller.notify();
	          }
              if ( traceActive ) tc.traceMessage("license manager notified caller");	    
          }
          
          if (exitVM ) System.exit(0);
          
          return;
     }
     
 }