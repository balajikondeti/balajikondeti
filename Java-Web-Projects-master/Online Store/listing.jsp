<?xml version="1.0" encoding="UTF-8"?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xml:lang="en" lang="en"
      xmlns="http://www.w3.org/1999/xhtml"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core">

<%--
	Copyright (c) 2012 Jonathan J. Halliday
	(csc3103dev@the-transcend.com)
	for the School of Computing Science, Newcastle University, UK.
	(http://www.cs.ncl.ac.uk)

 	.jsp page for the display of a number of items from the webstore.
--%>

<head><title>Webstore</title></head>

<body style="bgcolor: #ffffff; text-align: center">
<div>
    <h1><a href="<c:url value="/"/>">Webstore</a></h1>

    <h3>Please choose from our selection</h3>
    
    <%-- TODO - insert an HTML form here for searching the database --%>
    <form action="http://localhost:8080/" method="get">    
    	Search: <input type="text" name="q" value=""></input>
    	<input type="submit" value="submit"></input>    
    </form>


    <%-- TODO - insert some next and previous links here --%>
    <a href="/?first=${Backr1}&amp;last=${Backr2}${search}">Back</a>          <a href="/?first=${Upr1}&amp;last=${Upr2}${search}">Forward</a>

    <table style="margin-left: auto; margin-right: auto" id="listingTable">

        <c:forEach var="item" varStatus="status" items="${items}">

            <tr>
                <td align="left">
                    <c:url var="detailURL" value="/detail.html">
                        <c:param name="id" value="${item.id}"/>
                    </c:url>
                    <a href="<c:out value="${detailURL}"/>"><c:out value="${item.title}"/></a>
                </td>
                <td align="left">
                    <c:out value="${item.artist.name}"/>
                </td>

                <td align="right">
                    <fmt:formatNumber value="${item.price/100.0}" type="currency"/>
                </td>
            </tr>

        </c:forEach>

    </table>

    <c:if test="${!empty recentItems}">
        <h3>Recently Viewed Items</h3>
        <table style="margin-left: auto; margin-right: auto" id="recentItemsTable">

            <c:forEach var="item" varStatus="status" items="${recentItems}">
                <tr>
                    <td align="left">
                        <c:url var="detailURL" value="/detail.html">
                            <c:param name="id" value="${item.id}"/>
                        </c:url>
                        <a href="<c:out value="${detailURL}"/>"><c:out value="${item.title}"/></a>
                    </td>
                    <td align="left">
                        <c:out value="${item.artist.name}"/>
                    </td>

                    <td align="right">
                        <fmt:formatNumber value="${item.price/100.0}" type="currency"/>
                    </td>
                </tr>

            </c:forEach>

        </table>
    </c:if>

</div>
</body>
</html>