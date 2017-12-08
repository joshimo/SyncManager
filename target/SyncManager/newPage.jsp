<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>Sync Manager v0.9</title>
</head>

<link href="TableStyle.css" rel="stylesheet" type="text/css">

<body>

<div>New Task</div>

<br>
    <form name="edit" method="post" action="/SyncManager/new">
        <p><b>Source folder:</b><br>
            <input type="text" name="sourceDirectory" value="c:\syncTest\!source0\" size="40">
        </p>
        <p>
            <b>Destination folder:</b><br>
            <input type="text" name="destinationDirectory" value="d:\syncTest\!destination0\" size="40">
        </p>
        <br>
        <p>
            <input type="checkbox" name="deleteFromDestination" value="true">Delete files from destination directory if still not present in source<Br>
        </p>
        <br>
        <p>
            <input type="checkbox" name="keepPrev" value="true">Keep previous copies:<Br>
        </p>
        <p>
            Max. numbers:
            <input type="text" name="prevNum" size="2">
            Days:
            <input type="text" name="prevDays" size="2">
        </p>
        <br>
        <p>
            <input type="checkbox" name="syncBySchedule" value="true">Use time schedule:<Br>
        </p>
        <br>
        <p>
            Hours:
            <input type="text" name="scheduleHours" size="2">
            Mins:
            <input type="text" name="scheduleMinutes" size="2">
            Sec:
            <input type="text" name="scheduleSeconds" size="2">
        </p>
        <br>
        <p>
            <input type="checkbox" name="syncByInterval" value="true">Use time interval<Br>
        </p>
        <p>
            Hours:
            <input type="text" name="intervalHours" size="2">
            Mins:
            <input type="text" name="intervalMinutes" size="2">
            Sec:
            <input type="text" name="intervalSeconds" size="2">
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