<%--
  SyncManager Web interface Main page
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Vector" %>

<%! Vector<Vector<String>> table; %>
<% table = (Vector<Vector<String>>)request.getAttribute("table"); %>

<p>

  <head>
    <title>Sync Manager v0.9</title>
  </head>

  <link href="TableStyle.css" rel="stylesheet" type="text/css">

    <div>Sync Manager Main</div>
    <br>
    ${date}
    <br>

<br>
<p><table>
  <caption><b>Current Task Status List</b></caption>
  <tr>
    <th>№</th>
    <th>Source directory</th>
    <th>Destination directory</th>
    <th>Delete from destination</th>
    <th>Last Sync Date</th>
    <th>Task Status</th>
    <th>Sync Type</th>
    <th>Sync Schedule</th>
    <th>Running Status</th>
  </tr>

    <%  for (int row = 0; row < table.size(); row ++) { %>

  <tr>
    <td><%=row + 1%></td>
    <td><%=table.get(row).get(0)%></td>
    <td><%=table.get(row).get(1)%></td>
    <td><%=table.get(row).get(2)%></td>
    <td><%=table.get(row).get(3)%></td>
    <td><%=table.get(row).get(4)%></td>
    <td><%=table.get(row).get(5)%></td>
    <td><%=table.get(row).get(6)%></td>
    <td><%=table.get(row).get(7)%></td>

    <td class="tdref"><form method="post" action="/SyncManager/run">
      <input type="hidden" name="taskNumber" value="<%=row%>"/>
      <button type="submit">Run</button>
    </form></td>

    <td class="tdref"><form method="post" action="/SyncManager/stop">
      <input type="hidden" name="taskNumber" value="<%=row%>"/>
      <button type="submit">Stop</button>
    </form></td>

    <td class="tdref"><form method="get" action="/SyncManager/edit">
      <input type="hidden" name="taskNumber" value="<%=row%>"/>
      <button type="submit">Edit</button>
    </form></td>

    <td class="tdref"><form method="post" action="/SyncManager/delete">
      <input type="hidden" name="taskNumber" value="<%=row%>"/>
      <button type="submit">Delete</button>
    </form></td>

    <td class="tdref"><form method="post" action="/SyncManager/history">
      <input type="hidden" name="taskNumber" value="<%=row%>"/>
      <button type="submit">History</button>
    </form></td>

  </tr>

  <%}%>

</table></p>

<br>

    <p><form method="post" action="/SyncManager/sync">
      <input type="submit" name="btn" value="Start All Tasks" />
      <input type="submit" name="btn" value="Stop All Tasks" />
      <input type="submit" name="btn" value="Sync All Now" />
      <input type="submit" name="btn" value="Add New Task" />
      <input type="submit" name="btn" value="Show log" />
      <input type="submit" name="btn" value="Refresh Page" />
    </form></p>

  </body>

</html>