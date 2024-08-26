<!--
 Copyright (c) 2010 Systemwerx Ltd.

  All rights reserved.

  This is a sample JSP to generate a passticket
-->

<%@ page import = "com.systemwerx.PassGen.*" %>

<jsp:useBean id="pw" class="com.systemwerx.PassGen.PassTicketPureBean" scope="session"/>


<html>
<head><title>Generated PassTicket</title></head>
<body bgcolor="white">
<font size=4>

<%
 try 
 {
   String Value;
   int Error = 0;
   Value = request.getParameter("UserID");
   if ( Value.length() > 8 || Value.length() == 0 )
      {
         out.println("Please enter a valid userid<BR>"); 
         Error = 1;
      }

   pw.setUserID(Value);

   Value = request.getParameter("AppName");

   if ( Value.length() > 8 || Value.length() == 0 )
      {
         out.println("Please enter a valid application name<BR>"); 
         Error = 1;
      }

   pw.setApplication(Value);

   Value = request.getParameter("SessionKey");

   if ( Value.length() != 16 )
      {
         out.println("Please enter a valid Session Key<BR>"); 
         Error = 1;
      }

   pw.setSessionKey(Value);

// Set the following two values 

   pw.setTranslateTable("C:\\Work\\eclipse\\workspace\\PassGenJava\\trans.txt");
   pw.setGMTOffset(0);

   out.println(getServletContext().getRealPath("."));  
   if ( Error == 0)
      {
       out.println("<BR><BR><H2>PassTicket value is "+pw.getPassTicket());  
       out.println("</H2>");  
      }
   else
      {
       out.println("PassTicket generation failed - please select the browser back key and try again");  
      }

  

  }
  catch (Exception e)
  {
      Class c = e.getClass();
      String s = c.getName();
      out.println("Exception >>>"+s+"  "+e.getMessage());  
} 
 %>



</font>
</body>
</html>


