<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Email Confirmation (Host)</title>
</head>

<body>
<span style='font-family:Calibri,sans-serif'>
Dear ${hostName},<br>
<br>
This is to confirm that you have purchased a guest pass for a student and such pass has been issued to your guest already.<br>
<br>
Details of the pass are listed below.<br>
Room: ${roomNbr}, ${bldgCde}<br>
Guest: ${guestName}<br>
Period: 2pm, ${moveInDate} to 12noon, ${moveOutDate}<br>
Charge: HK$ ${chargeAmt}<br>
Payment: HK$ ${payAmt}<br>
<br>
Please be reminded that when hosting an overnight guest in your room,<br>
</span>
<ul>
	<li><span style='font-family:Calibri,sans-serif'>You are required to comply with the Comply with the Terms & Conditions of the Guest Pass Scheme</span></li>
	<li><span style='font-family:Calibri,sans-serif'>You are responsible for any misconduct of your guest</span></li>
</ul>
<span style='font-family:Calibri,sans-serif'>
For a summary of related guest passes, you may login to the Guest Pass Scheme online system at <a href="${enquiryUrl}">${enquiryUrl}</a>.<br>
<br>
For enquires, please contact the Student Housing and Residential Life Office at <a href="mailto:${gpsEmail}">${gpsEmail}</a>.<br>
<br>
Best Regards,<br>
Student Housing and Residential Life Office<br>
</span>

<br>
<br>
<table border="1" style="border-collapse: collapse;">
	<td style="font-family: Lucida Console; font-size: 10pt; padding: 10px; text-align: center; vertical-align: middle">
		This is a system-generated email, please DO NOT reply
	</td>
</table>

</body>
</html>
