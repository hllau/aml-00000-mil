try {
	//nothing to do
	if (calLang) {}
} catch (e) {
	calLang = "en";
}

var en_month=new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
var zh_month=new Array("年1月","年2月","年3月","年4月","年5月","年6月","年7月","年8月","年9月","年10月","年11月","年12月");
var sc_month=new Array("年1月","年2月","年3月","年4月","年5月","年6月","年7月","年8月","年9月","年10月","年11月","年12月");
var ja_month=new Array("年1月","年2月","年3月","年4月","年5月","年6月","年7月","年8月","年9月","年10月","年11月","年12月");
var ko_month=new Array("년1월","년2월","년3월","년4월","년5월","년6월","년7월","년8월","년9월","년10월","년11월","년12월");

var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var isFromCal = false;
var isToSubmit = false;
var d=new Date();
var yrToday=d.getFullYear();
var monToday=d.getMonth();
var todaysDate=d.getDate();
var currYear = yrToday;
var currMonth = monToday;
var nextYear = yrToday;
var nextMonth;
var startYear = yrToday;
var startMonth = monToday;
var startDate = todaysDate;
var endYear = yrToday;
var endMonth = monToday;
var endDate = todaysDate;
var isCalendarClose = false;
var IE = checkBrowser();
var homeID;
var actionLock=false;

function toggleCalendar(selectID,event,isSubmit)
{
    homeID = selectID;  
    var e=document.getElementById(selectID);	
	
	currYear = yrToday;
	currMonth = monToday;	
	
	if(currMonth == "10")
	{
		nextMonth = "0";
		nextYear = parseInt(currYear) + 1;
	}
	else
	{
		nextMonth = parseInt(currMonth) + 2;
		nextYear = currYear;
	}
	if(isSubmit)
	{
		isToSubmit = true;
	}
	else
	{
		isToSubmit = false;
	}

	if(document.getElementById('calendar').style.display=="none")
	{
		ShowCalendar(event);
	}
	else
	{
		hideCal('close');
	}
}

function formatDates(lang, year, month)
{
		var formatStr="";
		var monAry=eval(lang+"_month");
		if (lang=="en") {
			formatStr=monAry[month]+" "+year;
		} else {
			formatStr=year+monAry[month];
		}
		return(formatStr);
}

function newCalendar()
{    
    var monthNext;
    var yearNext;
    
	if(currMonth == "11")
	{
		monthNext = "0";
		yearNext = parseInt(currYear) + 1;
	}
	else
	{
		monthNext = parseInt(currMonth) + 1;
		yearNext = currYear;
	}    
    
	isCalendarClose=false;
	isFromCal=true;
	if(currYear == endYear && currMonth == endMonth)
	{
		nextYear = currYear;
		nextMonth = currMonth;
		if(currMonth == "11")
		{
			currMonth = '0';
			currYear = parseInt(currYear) - 1;
		}
		else
		{
			currMonth = parseInt(currMonth) - 1;
		}
	}
	if(!checkValid(1, currMonth, currYear))
	{
		document.getElementById('prevLink').style.display = "none";
	}
	else
	{
		document.getElementById('prevLink').style.display = "inline";
	}
	if(!checkValid(31, currMonth+1, currYear))
	{
		document.getElementById('nextLink').style.display = "none";
	}
	else
	{
		document.getElementById('nextLink').style.display = "inline";
	}
	document.getElementById('calYearMon').innerHTML = formatDates(calLang, currYear, currMonth);
	document.getElementById('calYearMon1').innerHTML = formatDates(calLang, yearNext, monthNext);
	var newCal = new Date(currYear , currMonth, 1);
	var newCal1 = new Date(yearNext , monthNext, 1);
	var day = -1;
	var startDay = newCal.getDay();
	var startDay1 = newCal1.getDay();
	var daily = 0;
	today = new getToday(); // 1st call

	if ((today.year == newCal.getFullYear() ) &&   (today.month == newCal.getMonth()))
	   day = today.day;
	// Cache the calendar table's tBody section, dayList.
	var tableCal = document.getElementById('dayList');
	var tableCal1 = document.getElementById('dayList1');
	var intDaysInMonth = getDays(newCal.getMonth(), newCal.getFullYear() );
	var intDaysInMonth1 = getDays(newCal1.getMonth(), newCal1.getFullYear() );

	for (var intWeek = 0; intWeek < tableCal.rows.length;  intWeek++)
	{
		for (var intDay = 0; intDay < tableCal.rows[intWeek].cells.length;intDay++)
	 	{
			var cell = tableCal.rows[intWeek].cells[intDay];
			// Start counting days.
			if ((intDay == startDay) && (0 == daily))
			{
				daily = 1;
			}
			
			if ((daily > 0) && (daily <= intDaysInMonth) )
			{
				
				if (checkValid(daily,(parseInt(newCal.getMonth())+1),parseInt(newCal.getFullYear())))
			    {
 				    cell.innerHTML = "<div class=\"calDay\"><a href=\"javascript:returnDate("+daily+","+parseInt(newCal.getMonth()+1)+","+parseInt(newCal.getFullYear())+",'"+homeID+"')\">"+(daily++)+"</a></div>";
				}			
				else
				{
					cell.innerHTML = "<div class=\"calDay\">"+(daily++)+"</div>";
				}
			}
		  	else
		  	{
				cell.innerHTML = "";
			}
	   }
	}
	daily = 0;
	for (var intWeek = 0; intWeek < tableCal1.rows.length;  intWeek++)
	{
		for (var intDay = 0; intDay < tableCal1.rows[intWeek].cells.length;intDay++)
		{
			var cell = tableCal1.rows[intWeek].cells[intDay];
			// Start counting days.
			if ((intDay == startDay1) && (0 == daily))
			{
				daily = 1;
			}

			if ((daily > 0) && (daily <= intDaysInMonth1) )
			{		
				if (checkValid(daily,(parseInt(newCal1.getMonth())+1),parseInt(newCal1.getFullYear())))
				{
				  cell.innerHTML = "<div class=\"calDay\"><a href=\"javascript:returnDate("+daily+","+parseInt(newCal1.getMonth()+1)+","+parseInt(newCal1.getFullYear())+",'"+homeID+"')\">"+(daily++)+"</a></div>";
				}
				else
				{
				  cell.innerHTML = "<div class=\"calDay\">"+(daily++)+"</div>";
				}		
			}
			else
			{
				cell.innerHTML = "";
			}
	   }
	}
	return;
}

function ShowCalendar(event)
{
		var tempX = 0;
		var tempY = 0;
		

		tempX = event.clientX + document.documentElement.scrollLeft;
		tempY = event.clientY + document.documentElement.scrollTop;

		if(event.pageX || event.pageY)
		{
			tempX = event.pageX;
			tempY = event.pageY;
		}
		
		newCalendar();

	    document.getElementById('divCal').style.left = tempX + 10;
		document.getElementById('divCal').style.top = (tempY + 5);
		document.getElementById('calendar').style.display="inline";
		document.getElementById('divCal').style.display="inline";
		document.getElementById('frameCalNew').style.left = tempX + 10;		
		document.getElementById('frameCalNew').style.top = (tempY + 10);
		document.getElementById('frameCalNew').style.width = 260;
		document.getElementById('frameCalNew').style.filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';
		document.getElementById('calendar').style.width = 260 + 'px';
		document.getElementById('frameCalNew').style.display="inline";
}

function hideCal(fromWhere) {
  if (IE)
  {
	HideCalendar(fromWhere);
  }   
  else
  {
	hideCalendar();
  }  
}

function HideCalendar(fromWhere)
{   
    if (isFromCal)
    {
	    if (isCalendarClose)
	    {
	  	  //isCalendarClose = false;
	      document.getElementById('calendar').style.display="none";
		  document.getElementById('divCal').style.display="none";
		  document.getElementById('frameCalNew').style.display="none";
		}
	    if (fromWhere == "no")
	    {
	      isCalendarClose=false;
	    }
	    else if (fromWhere="close")
	    {
	      isCalendarClose=true;
	    }	
    }    
}

function getToday()
{
	var todaysYear = yrToday;
	var todaysMonth = monToday;
	this.now = new Date(parseInt(todaysYear), parseInt(todaysMonth), parseInt(todaysDate));
	this.year = this.now.getFullYear() ; // Returned year XXXX
	this.month = this.now.getMonth();
	this.day = this.now.getDate();
}

function setMinMaxCal(pMinMMYYYY, pMaxMMYYYY) {
	startDate=pMinMMYYYY.substring(0,2);   
	startDate=startDate.substring(0,1).replace('0','') + startDate.substring(1,2);
	startMonth=pMinMMYYYY.substring(2,4);
	startMonth=startMonth.substring(0,1).replace('0','') + startMonth.substring(1,2);
	startYear=pMinMMYYYY.substring(4,8);
	endDate=pMaxMMYYYY.substring(0,2);   
	endDate=endDate.substring(0,1).replace('0','') + endDate.substring(1,2);
	endMonth=pMaxMMYYYY.substring(2,4);
	endMonth=endMonth.substring(0,1).replace('0','') + endMonth.substring(1,2);	
	endYear=pMaxMMYYYY.substring(4,8);	
}

function checkBrowser()
{
	var isIE = document.all?true:false;
	var agt=navigator.userAgent.toLowerCase();
	if (agt.indexOf("opera") != -1)
	{
		isIE = false;
	}
	return isIE;
}

function returnDate(dd,mm,yy,home)
{
	var e=document.getElementById(home);
	if (e)
	{
		elements=e.getElementsByTagName("select");
		for (i=0;i<elements[2].length;i++) {
			if (dd==elements[2].options[i].value) {
				elements[2].selectedIndex=i;
				break;
			}
		}
		for (i=0;i<elements[1].length;i++) {
			if (mm==elements[1].options[i].value) {
				elements[1].selectedIndex=i;
				break;
			}
		}
		for (i=0;i<elements[0].length;i++) {
			if (yy==elements[0].options[i].value) {
				elements[0].selectedIndex=i;
				break;
			}
		}
	}
	isCalendarClose=true;
	hideCal('close');
}

function getDays(month, year)
{
	// Test for leap year when February is selected.
	if (1 == month)
		return ((0 == year % 4) && (0 != (year % 100))) ||
			(0 == year % 400) ? 29 : 28;
	else
		return daysInMonth[month];
}

function fnChangeMonth(which)
{
	if(which == "prev")
	{
		if(currMonth == "0")
		{
			currMonth = '10';
			nextMonth = '0';
			nextYear = currYear;
			currYear = parseInt(currYear) - 1;
		}
		else if(currMonth == "1")
		{
			currMonth = '11';
			nextMonth = '0';
			nextYear = currYear;
			currYear = parseInt(currYear) - 1;
		}		
		else
		{
			nextMonth = currMonth;
			currMonth = parseInt(currMonth) - 2;
			nextYear = currYear;
		}
	}
	else
	{
		if(currMonth == "10")
		{
			currMonth = '0';
			nextMonth = parseInt(currMonth) + 2;
			currYear = parseInt(currYear) + 1;
			nextYear = currYear;
		}
		else if(currMonth == "11")
		{
			currMonth = '1';
			nextMonth = parseInt(currMonth) + 2;
			currYear = parseInt(currYear) + 1;
			nextYear = currYear;
		}
		else
		{
			currMonth = parseInt(currMonth) + 2;
			if(currMonth == "10")
			{
				nextMonth = '0';
				nextYear = parseInt(currYear) + 1;
			}
			else if(currMonth == "11")
			{
				nextMonth = '1';
				nextYear = parseInt(currYear) + 1;
			}			
			else
			{
				nextMonth = parseInt(currMonth) + 2;
				nextYear = currYear;
			}
		}
	}
	
	newCalendar();
}

///////////////////////////////////////////////////////////////////////////////////

function toggleCal(divname, homeid) {
	toggleCal2(divname, 0, homeid);
}

function toggleCal2(divname, offset, homeid) {
	if ( document.calendarOpened ) {
		hideCal();
	}
	document.calendarOpened = divname;
	var e=document.getElementById(divname);
	if (e) {		
		writeCal(divname, offset, homeid);
		e.style.display="inline";
		e.onclick=function (){actionLock=true;};
	}
}

function setSpecialMth(pSpecialInd) {
	isSpecial = pSpecialInd;
}

function writeCal(divname, monthoffset, homeid)
{
	var e=document.getElementById(divname);
	if (e) {
		e.innerHTML=writeMonth(divname, monthoffset, homeid)+writeMonth(divname, monthoffset+1, homeid);
	} else {
		alert("div not found");
	}
}

function writeMonth(divname, offset, homeid)
{
	var da=new Date();
	da.setMonth(monToday+offset);
	var month=da.getMonth();	
	var year=da.getFullYear();

	var lastda=new Date();
	lastda.setMonth(month+1+offset);
	lastda.setDate(-1);
	var lastdate=lastda.getDate()+1;
	var curDate = ((month+1).toString()).concat(year.toString());
	
	pre="";
	
	if ((checkValid(1, month+1, year)) && ((offset%2==0) || (offset==0 && isSpecial)))
//	if ((offset>0)&&(offset%2==0))
	{
		pre="<a href=\"javascript:writeCal('"+divname+"', "+(offset-2)+", '"+homeid+"')\">&lt;&lt;</a>";
	}

	nex="";
	if ((checkValid(lastdate, month+1, year)) && (Math.abs(offset%2)==1))
//	if (offset%2==1)
	{
		nex="<a href=\"javascript:writeCal('"+divname+"', "+(offset+1)+", '"+homeid+"')\">&gt;&gt;</a>";
	}

	txt="<div class=\"calBox\">";
	txt+="<div class=\"calDayNoBorder\">"+pre+"</div>";
	txt+="<div class=\"calDayNoBorder calYear\"><b>"+formatDates(calLang, year, month)+"</b></div>";
	txt+="<div class=\"calDayNoBorder\">"+nex+"</div>";
	txt+="<div class=\"colDayOfWeek\">";
	txt+="<div class=\"calDayNoBorder\">S</div>";
	txt+="<div class=\"calDayNoBorder\">M</div>";
	txt+="<div class=\"calDayNoBorder\">T</div>";
	txt+="<div class=\"calDayNoBorder\">W</div>";
	txt+="<div class=\"calDayNoBorder\">T</div>";
	txt+="<div class=\"calDayNoBorder\">F</div>";
	txt+="<div class=\"calDayNoBorder\">S</div>";
	txt+="</div>"
	txt+="<div>"+buildCal(offset, homeid)+"</div>";
	txt+="</div>";

	return (txt);
}

function buildCal(offset, homeid)
{
	var da=new Date();
	da.setMonth(monToday+1+offset);
	da.setDate(-1);
	var lastdate=da.getDate()+1;
	var lastmonth=da.getMonth()+1;
	var lastyear=da.getFullYear();

	da.setDate(1);
	var firstday=(da.getDay());

	empty="<div class=\"calDayNoBorder\"></div>";
	head="";
	for (i=0;i<firstday%7;i++) {
		head+=empty;
	}

	body="";
	for (i=1;i<=lastdate;i++) {
		sunday="";
		/*
		if ((firstday+i)%7==1)
		{
			sunday=" style=\"color:red\"";
		}
		*/
		if (checkValid(i, lastmonth, lastyear))
			body+="<div class=\"calDay\"><a href=\"javascript:returnDate("+i+","+lastmonth+","+lastyear+",'"+homeid+"')\""+sunday+">"+i+"</a></div>";
		else
			body+="<div class=\"calDay\">"+i+"</div>";
	}

	return(head+body);
}

function checkValid(pCurDD, pCurMM, pCurYY)
{
	// is valid if current date is >= min date and <= max date
	var isBefore = true;
	var isAfter = true;
	
	thisMinYr = startYear;
	thisMinMth = startMonth;
	thisMinDay = startDate;

	thisMaxYr = endYear;
	thisMaxMth = endMonth;
	thisMaxDay = endDate;

	if (pCurYY - thisMinYr > 0) {
		isBefore = false;
	} else if (pCurYY - thisMinYr == 0) {
		if (pCurMM - thisMinMth > 0) {
			isBefore = false;
		} else if (pCurMM - thisMinMth == 0) {
			if (pCurDD - thisMinDay >= 0) {
				isBefore = false;
			}
		}
	}

	if (thisMaxYr - pCurYY > 0) {
		isAfter = false;
	} else if (thisMaxYr - pCurYY == 0) {
		if (thisMaxMth - pCurMM > 0) {
			isAfter = false;
		} else if (thisMaxMth - pCurMM == 0) {
			if (thisMaxDay - pCurDD >= 0) {
				isAfter = false;
			}
		}
	}

	if (!isBefore && !isAfter)
		return true;
	else
		return false;
}

function hideCalendar() {
	if (!actionLock)
	{
		/* START CHANGE by AM-Dev 20080214 */
			/*
			var e = document.getElementById(document.calendarOpened);
			if (e) {
				e.style.display = "none";
			}
			*/ /* original code by Kitchen */
		if (document.calendarOpened)
		{
			var e = document.getElementById(document.calendarOpened);
			if (e) {
				e.style.display = "none";
			}
		}
		/* END CHANGE by AM-Dev 20080214 */
		document.calendarOpened = "";
	}
	actionLock=false;
}

document.onclick=hideCal;