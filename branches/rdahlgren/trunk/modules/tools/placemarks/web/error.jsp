<%-- 
    Document   : norepo
    Created on : Dec 14, 2008, 12:47:04 PM
    Author     : jkaplan
--%>

<%@page contentType="text/html" pageEncoding="MacRoman"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=MacRoman">
        <title>Error</title>
    </head>
    <body>
        <h2>Placemarks Error</h2>
        ${requestScope['message']}
        <br><br>
        <a href="/placemarks/wonderland-placemarks/browse">Ok</a>
    </body>
</html>
