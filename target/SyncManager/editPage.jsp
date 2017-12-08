<%--
  SyncManager Web interface task editing page
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Sync Manager v0.9</title>
</head>

<link href="TableStyle.css" rel="stylesheet" type="text/css">

<body>
<div>Task edit</div>
<br>
<br>

<br>
<form name="edit" method="post" action="/SyncManager/edit">
    <p><b>Source folder:</b><br>
        <input type="text" name="sourceDirectory" value="${sourceDirectory}" size="40">
    </p>
    <p>
        <b>Destination folder:</b><br>
        <input type="text" name="destinationDirectory" value="${destinationDirectory}" size="40">
    </p>
    <br>
    <p>
        <input type="checkbox" name="deleteFromDestination" value=${deleteFromDestination}>Delete files from destination directory if still not present in source<Br>
    </p>
    <br>
    <p>
        <input type="checkbox" name="keepPrev" value="true" ${keepPrev}>Keep previous copies:<Br>
    </p>
    <p>
        Max. numbers:
        <input type="text" name="prevNum" size="2" value=${prevNum}>
        Days:
        <input type="text" name="prevDays" size="2" value=${prevDays}>
    </p>
    <br>
    <p>
        <input type="checkbox" name="syncBySchedule" value="true" ${syncBySchedule}>Use time schedule:<Br>
    </p>
    <p>
        Hours:
        <input type="text" name="scheduleHours" size="2" value=${scheduleHH}>
        Mins:
        <input type="text" name="scheduleMinutes" size="2" value=${scheduleMM}>
        Sec:
        <input type="text" name="scheduleSeconds" size="2" value=${scheduleSS}>
    </p>
    <br>
    <p>
        <input type="checkbox" name="syncByInterval" value="true" ${syncByInterval}>Use time interval<Br>
    </p>
    <p>
        Hours:
        <input type="text" name="intervalHours" size="2" value=${intervalHH}>
        Mins:
        <input type="text" name="intervalMinutes" size="2" value=${intervalMM}>
        Sec:
        <input type="text" name="intervalSeconds" size="2" value=${intervalSS}>
    </p>
    <br>
    <br>
    <p>
        <input type="submit" value="OK" action="/SyncManager/sync">
        <input type="reset" value="Cancel" action="/SyncManager/sync">
    </p>
</form>

<br>

</body>

</html>