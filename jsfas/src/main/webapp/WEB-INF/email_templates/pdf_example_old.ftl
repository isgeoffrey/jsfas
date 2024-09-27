<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>
<title>Email Confirmation (Host)</title>
<style>
	.tableContent{
		width: 125px;
		padding: 5px
	}
	.tableTitle{
		width: 125px;
		padding: 5px
	}
	.descrCell{
		padding: 5px;
	}

</style>
</head>

<body>
<span style='font-family:Calibri,sans-serif'/>
<p style="text-align: left;"><img src="${url}"/></p>
<p style="text-align: right;">${date}</p>
<p>Dear ${APPL_USER_NAME!"Sir/Madam"},</p>
<p>I have much pleasure in writing to offer you below extra load activities.</p>
<table border="1">
<tbody>
<tr>
<td class="tableTitle">
<p>Extra Load Activities</p>
</td>
<td class="tableTitle">
<p>Program</p>
</td>
<td class="tableTitle">
<p>Period</p>
</td>
<td class="tableTitle">
<p>Total Payment</p>
</td>
<td class="tableTitle">
<p>Payment Method</p>
</td>
</tr>
<tr >
<td class="tableContent">
<p>${FAS_TYPE_NAM!""}</p>
</td>
<td class="tableContent">
<span>${ACAD_PLAN_DESCR!""}</span>
</td>
<td class="tableContent">
<p>${APPL_START_DT!""} &ndash; ${APPL_END_DT!""}</p>
</td>
<td class="tableContent">
<p>${PYMT_AMT!""}</p>
</td>
<td class="tableContent">
<p>${PYMT_TYPE_CDE!""}</p>
</td>
</tr>
<tr>
<td colspan="5" class="descrCell">
<span><b><u>Details: </u></b></span><br/>
${FAS_TYPE_DESCR!""}
<#-- <ul>
<li>Administering and coordinating academic programs, such as designing curricula and teaching classes</li>
<li>Reviewing student records to ensure compliance with school policies. procedures. and standards</li>
<li>Working with students individually or in groups to help them succeed academically</li>
</ul>-->
</td>
</tr>
</tbody>
</table>
<p>The School is appreciative of your contributions and wish you every success for many years to come.</p>

<p>&nbsp;</p>
<p>This is a computer-generated document. No signature is required.</p>

</body>
</html>
