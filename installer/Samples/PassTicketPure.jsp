<!--
  Copyright (c) 2010 Systemwerx Ltd.

  All rights reserved.

  This is a sample JSP to generate a passticket

  This page prompts for input and calls 
  PassTicketPureGen.jsp to do the generation

-->



<html>
<head><title>Generate PassTicket</title></head>
<body bgcolor="white">
<font size=4>

<H2>Please enter the following information and press the "Submit" button</H2>
<br><br>
<form method=post action="PassTicketPureGen.jsp">
<table border="0">
<tr>
<td>UserID</td><td><input type=text name=UserID></td>
</tr>
<tr>
<td>SessionKey</td><td><input type=text name=SessionKey></td>
</tr>
<tr>
<td>Application</td><td><input type=text name=AppName></td>
</tr>
</table>
<br><br>
<input type=submit value="Submit">
</form>


 


</font>
</body>
</html>


