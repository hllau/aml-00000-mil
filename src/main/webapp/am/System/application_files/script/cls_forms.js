<!--
var isNS = navigator.appName.indexOf("Netscape") != -1 && navigator.appVersion.charAt(0) >= 3;
var isIE = navigator.appName.indexOf("Microsoft Internet Explorer") != -1 && navigator.appVersion.charAt(0) >= 4;

function convert(formelement) {
  formelement.value = formelement.value.toUpperCase();	
}

function trim(str) {
	var strlen = str.length;
    var temp;
	for (var i=strlen-1; i>=0; i--) {
		 if ((str.substring(i,i+1) <= " ")) {
		 	str = str.substring(0,i); 
		 } else {
		 	break;
		 }
	}        
   	return str;
}    

function checkdate(objForm)
{
	dayIdx = objForm.birthDay.selectedIndex;
	monthIdx = objForm.birthMonth.selectedIndex;
	yearIdx = objForm.birthYear.selectedIndex;
	
	if (dayIdx>0 && monthIdx>0 && yearIdx>0) {
		dd = parseInt(objForm.birthDay.options[dayIdx].value);	
		mm = objForm.birthMonth.options[monthIdx].value;
		yyyy = parseInt(objForm.birthYear.options[yearIdx].value);
		
       	if (checkInvalidDate(dd, mm, yyyy))
			objForm.birthDay.options[0].selected = true;
	}
}

function checkInvalidDate(dd, mm, yyyy)
{
	falseDay = false; 

	// check the months with 30 days
	if (mm=="04" || mm=="06" || mm=="09" || mm=="11") {
		if (dd == 31) 
        	falseDay = true;
	}

	// check February and leap years
	if (mm=="02") {
		if (dd > 29) falseDay = true;
		if (dd == 29 && !((yyyy%4==0 && yyyy%100!=0) || yyyy%400==0)) 
			falseDay = true;
	}

	return falseDay
}

function formatNumber(inStr) {
	return formatNumber2DemcialPlaces(inStr, 0);
}

function formatMile(inStr) {
	return formatNumber2DemcialPlaces(inStr, 0);
}

function formatNumber2DemcialPlaces(inStr, dp) { // add comma and make number become n demcial place if the its dp is shorter
	var outStr = "";
	var dotindex;

	inStr = String(inStr);
	dotindex = inStr.indexOf(".");
	
	if (dotindex < 0) {
		if (dp > 0) {
			outStr = ".";
			dotindex = inStr.length;
			for (i = 0; i < dp; i++) {
				outStr = outStr + "0";
			}
		}
	} else {
		outStr = inStr.substring(inStr.indexOf("."), inStr.length);
		for (i = inStr.length - dotindex - 1; i < dp; i++) {
			outStr = outStr + "0";
		}
		inStr = inStr.substring(0, inStr.indexOf("."));
	}

	for (i=1;i<=inStr.length;i++) {
		if (i!= 1 && i%3 == 1) {
			outStr = "," + outStr;
		}
		outStr = inStr.substring(inStr.length-i, inStr.length-i+1) + outStr;			
	}
	return outStr;
}

function popupCurrencyConverter() {
	popThisUp('http://www.oanda.com/converter/classic?user=cathay&amp;lang=en','menuWin','scrollbars=no,width=630,height=400');
}

function updateRedemptionGroup(lang) {
	updateRedemptionGroup(lang, '');
}

function updateRedemptionGroup(lang, params) {
	var url = getUrl("/account/profile/nomination");
	if (params != null) {
		target = window.location.pathname + escape('?'+params);
	} else {
		target = window.location.pathname;
	}
	goURL(url + '?redirectSource=' + target);
}

function popThisUp(url, popupName, width, height) {
	window.open (url, popupName, 'scrollbars=yes,status=yes,resizable=yes,width='+width+',height='+height)
}

function popUpUrl(pUrl, popupName, width, height) {
	var url = getUrl(pUrl);
	window.open (url, popupName, 'scrollbars=yes,status=yes,resizable=yes,width='+width+',height='+height)
}

function getUrl(pUrl) {
	var winUrl = new String(window.location);
	if (winUrl!=null) {
		idx = winUrl.indexOf(contextRoot);
		if (idx>-1) {
			newString = winUrl.substring(idx+contextRoot.length);
			idx2 = newString.indexOf("http://www.asiamiles.com/");
			if (idx>-1) {
				localeVal = newString.substring(0, idx2);				
			}
		}
	}
	return contextRoot + localeVal + pUrl;
}

function gotoUrl(pUrl) {
	window.location = getUrl(pUrl);
}

function gotoUrlWithParam(pUrl, param) {
	window.location = getUrl(pUrl) + "?" + param;
}
//-->