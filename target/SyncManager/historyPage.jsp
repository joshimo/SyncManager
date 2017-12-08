<%@ page import="unitmodel.SyncUnit" %>
<%@ page import="java.util.Vector" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
    Map<String, Vector<SyncUnit>> deletedHistory;
    Map<String, Vector<SyncUnit>> modifiedHistory;
    Set<String> deletedKeys;
    Set<String> modifiedkeys;
%>
<%
    deletedHistory = (Map<String, Vector<SyncUnit>>) request.getAttribute("DeletedHistory");
    deletedKeys = deletedHistory.keySet();
    modifiedHistory = (Map<String, Vector<SyncUnit>>) request.getAttribute("ModifiedHistory");
    modifiedkeys = modifiedHistory.keySet();
%>

<html>

<head>
    <title>Sync Manager v0.9</title>
</head>

<link href="TableStyle.css" rel="stylesheet" type="text/css">

<body>

<div>Task History</div>

<br>
<p>
    <h2><b>Deleted files:</b></h2>
    <br>
<table>
    <tr>
        <th>Type</th>
        <th>Filename</th>
        <th>Path</th>
    </tr>
    <% for (String key : deletedKeys) { %>
    <tr>
        <tr><td class="tdmerges" colspan="3"><b><%=key%></b></td></tr>
    </tr>
        <% for (SyncUnit su : deletedHistory.get(key)) {%>
    <tr>
            <% if (su.isFile()) { %>
                <td>File</td>
                <td><%=su.getName()%></td>
                <td><%=su.getAbsolutePath()%></td>
                <td class="tdref"><a href="<%=su.getAbsolutePath()%>" download>Download</a></td>
            <% } else { %>
                <td>Directory</td>
                <td><%=su.getName()%></td>
                <td><%=su.getAbsolutePath()%></td>
            <% } %>
    </tr>
        <% } %>
    <% } %>
</table>

</p>
<br>
<p>
    <h2><b>Modified files:</b></h2>
    <br>
<table>
    <tr>
        <th>Type</th>
        <th>Filename</th>
        <th>Path</th>
    </tr>
    <% for (String key : modifiedkeys) { %>
    <tr>
    <tr><td class="tdmerges" colspan="3"><b><%=key%></b></td></tr>
    </tr>
    <% for (SyncUnit su : modifiedHistory.get(key)) {%>
    <tr>
        <% if (su.isFile()) { %>
        <td>File</td>
        <td><%=su.getName()%></td>
        <td><%=su.getAbsolutePath()%></td>
        <td class="tdref"><a href="<%=su.getAbsolutePath()%>" download>Download</a></td>
        <% } else { %>
        <td>Directory</td>
        <td><%=su.getName()%></td>
        <td><%=su.getAbsolutePath()%></td>
        <% } %>
    </tr>
    <% } %>
    <% } %>
</table>
</p>
<br>

</body>

</html>