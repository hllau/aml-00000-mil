// Detect IE7 standard mode and redirect
if((document.all)&&(navigator.appVersion.indexOf("MSIE 7.")!=-1)){
	if(navigator.userAgent.indexOf("Trident") ==-1) {
		window.location = 'http://www.asiamiles.com/am/System/application_files/IE_upgrade.html';
	}

}


// from v3 js
function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}

// from v3 js
function popupCurrencyConverter() {
	MM_openBrWindow('http://www.oanda.com/converter/classic?user=cathay&amp;lang=en','menuWin','scrollbars=no,width=630,height=400');
}

// from v3 js
function goURL(url) {
	document.location = url;
}

// from v3 js
function goSelectURL(obj, selectName) {
	var i=eval("document." + obj + "." + selectName + ".selectedIndex");
	if (eval("document." + obj + "." + selectName + ".options[" + i + "].value != '#'")) {
		document.location = eval("document." + obj + "." + selectName + ".options[" + i + "].value");
	}
}

// from v3 js
function autoTab(maxLength, elem, nextElem) {
	if (elem.value.length == maxLength) {
    	nextElem.focus();
	}
}



// from v3 js, modified
function newWindow(mypage, myname, w, h, scroll) {
	var winl = (screen.width - w) / 2;
	var wint = (screen.height - h) / 2;
	var winr = screen.width - w - (screen.width * 0.04);
	var winb = screen.height - h - (screen.height * 0.1);   
	winprops = 'height='+h+',width='+w+',top='+winb+',left='+winr+',scrollbars='+scroll+',resizable'
	win = window.open(mypage, myname, winprops)
	return win;
}

function popWindow(mypage, myname, w, h, scroll) {
	var win = newWindow(mypage, myname, w, h, scroll);
	if (parseInt(navigator.appVersion) >= 4) { win.window.focus(); }
}

function am_popup(mypage,myname){
	popWindow(mypage,myname, 620, 300, "yes");
}

function am_popupWithMemId(mypage,myname){
	memId = "";
	if(document.getElementById('txtMbrID')!=null){
		memId = document.getElementById('txtMbrID').value;
	}
	if(memId !=""){
		mypage = mypage + "?memberID="+memId;
	}
	am_popup(mypage,myname);
}

// from v3 js
function BrowserSupported() {
  var agt = navigator.userAgent.toLowerCase();

  // check for IE5.5+

  var is_ie = (agt.indexOf("msie") != -1);
  var is_opera = (agt.indexOf("opera") != -1);
  var is_mac = (agt.indexOf("mac") != -1);

  // !is_opera: Opera can include the MSIE string when it masquerades as IE
  // !is_mac: Omniweb includes MSIE string
  if (is_ie && !is_opera && !is_mac) {

    var version = GetFollowingFloat(agt, "msie ");
    if (version != null) {
      return (version >= 5.5);
    }
  }

  // check for Moz1.4+

  var is_gecko = (agt.indexOf("gecko") != -1);
  var is_safari = (agt.indexOf("safari") != -1);

  // !is_safari: Safari includes Gecko string
  if (is_gecko && !is_safari) {

    var version = GetFollowingFloat(agt, "rv:");
    if (version != null) {
      return (version >= 1.4);

    } else {
      // no rv: version; check for Galeon versions that did't include rv:
      var i = agt.indexOf("galeon");
      version = GetFollowingFloat(agt, "http://www.asiamiles.com/am/en/redeem/galeon/");

      if (version != null) {
        // Galeon 1.3+ can be used with Moz 1.3+
        // to really check for Gecko 1.4+, should parse the date string
        // following "Gecko/"
        return (version >= 1.3);
      }
    }
  }

  // check for Safarai 1.2.1+
  if (is_safari) {
    var version = GetFollowingFloat(agt, "http://www.asiamiles.com/am/en/redeem/applewebkit/");
    if (version != null) {
      return (version >= 124);
    }
  }

  return false;
}

// from v3 js
function getDomain() {
	var myDomain = document.domain;
}

// from v3 js
function displayPullDownMenu(objectArray) {
	if (objectArray != null) {
	for (var j=0; j < objectArray.length; j++) { 
		document.write('<option value="' + objectArray[j][1] + '">' + objectArray[j][0] + '</option>');
    }
    }
}

function displayPullDownMenuById(objectArray, selectBox){
	selectBox.length = 0;
	if(selectBox!=null){
		if (objectArray != null) {
		for( i = 0; i < objectArray.length; i++){
				selectBox.options[i] = new Option(objectArray[i][0], objectArray[i][1]);
			}
		}
	} 
}

function displayEarnPartnerCategory(){
	displayPullDownMenu(partnercArr);
}

var earn_partner_search_missing_country = '';
function submitEarnPartnerSearch(form) {
	var errMsg = '';

	var ddlCategory = form.elements['category'];
	var ddlPartnerName = form.elements['partnerName'];
	var ddlCountry = form.elements['country'];
	var txtKeyword = form.elements['keyword'];

	var category = ddlCategory.options[ddlCategory.selectedIndex].value;
	var partnerName = ddlPartnerName.options[ddlPartnerName.selectedIndex].value;
	var country = ddlCountry.options[ddlCountry.selectedIndex].value;
	var keyword = txtKeyword.value;


	// validate input
	if (ddlCountry.disabled == false) {
		if (country == '--') {
			errMsg = errMsg + earn_partner_search_missing_country + '\n';		
		}
	}

	
	// validate partner
	var wrongPartner = false;
	var partnerNames = partnerNameArr[ddlCategory.selectedIndex];
	if (ddlPartnerName.selectedIndex >= partnerNames.length) {
		wrongPartner = true;
	} else {
		if (partnerNames[ddlPartnerName.selectedIndex][1] != partnerName) {
			wrongPartner = true;
		}
	}
	if (wrongPartner) {
		earnPartnerCategoryOnchange(form, ddlCategory.selectedIndex);
		errMsg = errMsg + earn_partner_search_wrong_partner_list + '\n';
	}

	// validate wrongCountryDisable
	var wrongCountryDisable = false;
	if (partnerCatCountryDisabledArr[category] != null) {
		if ( ! ddlCountry.disabled) {
			ddlCountry.disabled = true;
		}
	} else {
		if (ddlCountry.disabled) {
			ddlCountry.disabled = false;
			errMsg = errMsg + earn_partner_search_country_should_enable + '\n';
			wrongCountryDisable = true;
		}
	}
	
	//if (wrongCountryDisable || wrongPartner) {
	//	return;
	//}


	if (errMsg == '') {
		form.submit();
	} else {
		window.alert(errMsg);
	}
}

function diningPartnerCityOnchange(form, idx) {
	var districtSelectBox = form.district;
	
	displayPullDownMenuById(diningCityDistrictOptionArr[idx], districtSelectBox);
}

function setRHTDiningDefaultCity(form) {
	var diningCityDDL = form.city;

	if (typeof earn_dining_search_default_city == 'undefined' ||
		earn_dining_search_default_city == null ||
		earn_dining_search_default_city == '') {
		
	} else {
		for (var i = 0; i < diningCityDDL.options.length; i++) {
			if (diningCityDDL.options[i].value == earn_dining_search_default_city) {
				diningCityDDL.options[i].selected = true;
				break;
			}
		}		
	}
}

function resetRHTDiningPartnerForm(form) {
	var diningCityDDL = form.city;
	var diningCuisineDDL = form.cuisine;
	var diningDistrictDDL = form.district;
	var diningKeywordTxt = form.keyword;

	// reload DDL & reset default
	displayPullDownMenuById(diningCityOptionArr, diningCityDDL);
	setRHTDiningDefaultCity(form);
					
	displayPullDownMenuById(diningCuisineOptionArr, diningCuisineDDL);
	diningCuisineDDL.selectedIndex = 0;
					
	diningPartnerCityOnchange(diningDistrictDDL.form, diningCityDDL.selectedIndex);
	diningDistrictDDL.selectedIndex = 0;
					
	diningKeywordTxt.value = '';						
}

function earnPartnerCategoryOnchange(form, idx){
	var partnerSelectBox = form.partnerName;
	var countrySelectBox = form.country;
	var keywordTextBox = form.keyword;
	if (partnerSelectBox !=null){
		displayPullDownMenuById(partnerNameArr[idx], partnerSelectBox);

		var selectCatCode = form.category.options[form.category.selectedIndex].value;
		if (partnerCatCountryDisabledArr[selectCatCode] != null) {
			countrySelectBox.disabled = true;
		} else {
			countrySelectBox.disabled = false; // enable it
		}

		countrySelectBox.selectedIndex = 0; // reset option
		partnerSelectBox.selectedIndex = 0; // reset option
		keywordTextBox.value = ''; // reset text field
	} else {
		countrySelectBox.disabled = false; // enable it		
	}
}

function displayEarnPartnerCountry(){
	displayPullDownMenu(partnerCountryArr);
}

function displayRangeMenu() {
	displayPullDownMenu(mArr);
}
function displayCategoryMenu() {
	displayPullDownMenu(cArr);
}
function displayRedeemOptionMenu(){
	displayPullDownMenu(rOpArr);
}
function displayCountryMenu(){
	displayPullDownMenu(cRArr);
}

function displayEarnMileageCalCarrier(){
	displayPullDownMenu(earnMCCarrier);
}

function pagaInit() {
	// you can customize the sb_setting (right-hand-tool setting object) 
	// and config (AJAX auto-select manager config) here
	// instead of using default value set by /component/am_html_header*.jsp

	// you can put ur own code in pagaInit() method

	drawmenu("ctnSidebar", sb_setting);
	ACManager.setFields(config);
}

/* add by cpphowc 19th Mar 2008 */
function formatCurrency(num) {
	num = num.toString().replace(/\$|\,/g,'');
	if(isNaN(num))
		num = "0";
		
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num*100+0.50000000001);
	cents = num%100;
	num = Math.floor(num/100).toString();
	if(cents<10)
		cents = "0" + cents;
		
	for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
		num = num.substring(0,num.length-(4*i+3))+','+ num.substring(num.length-(4*i+3));
		
	return (((sign)?'':'-') + '$' + num + '.' + cents);
}

function trim(stringToTrim) { 
	return stringToTrim.replace(/^\s+|\s+$/g,'');
}

function disableLogout(){
	if(document.getElementById('common_logout_btn')!=null){
		document.getElementById('common_logout_btn').disabled = true;
	}
}

//For PORT code auto suggest
function getIATAPortCode(fullname){
	if(fullname){
		if(fullname.lastIndexOf("(")>0 && fullname.lastIndexOf(")")>0){
			var result = fullname.substring(fullname.lastIndexOf("(")+1, fullname.lastIndexOf("(")+4);
			if(typeof(result)=='undefined'){
				return "";
			} else {
				return  fullname.substring(fullname.lastIndexOf("(")+1, fullname.lastIndexOf("(")+4);
			}
	}
	}
	return "";
}


function dropDownSuggest(list, target){
	if ( typeof(list) === "string") {
		//Jason
		//since we are only getting a list of strings but no json functions required
		//this.allACSuggest[offset] = eval(suggestList);
		jsonObject = JSON.parse(String(list));
		//airportDesc is the name of the list in JSON result
		this.suggestList = jsonObject.airline;
		//this.allACSuggest[offset] = String(suggestList).parseJSON();
			target.options.length=0;
		if (suggestList) {
			for (x =0 ; x < suggestList.length; x++){
				target.options[x]=new Option(suggestList[x].carrierName, suggestList[x].carrierCode, false, false);
			}
		}
		
		//CPPPEP ADD FOR AML.31473 20140529 START
		if(ACManager){
			ACManager.initAirlinesArr(target.id);
		}
		//CPPPEP ADD FOR AML.31473 20140529 END
	}
}

//NEW
function dropDownSuggestWithRetainValue(list, target){
 if ( typeof(list) === "string") {
  //Jason
  //since we are only getting a list of strings but no json functions required
  //this.allACSuggest[offset] = eval(suggestList);
  jsonObject = JSON.parse(String(list));
  //airportDesc is the name of the list in JSON result
  this.suggestList = jsonObject.airline;
  //this.allACSuggest[offset] = String(suggestList).parseJSON();
  if (suggestList) {
   for (x =0 ; x < suggestList.length; x++){
    target.options[target.options.length]=new Option(suggestList[x].carrierName, suggestList[x].carrierCode, false, false);
   }
  }
  	//CPPPEP ADD FOR AML.31473 20140529 START
	if(ACManager){
		ACManager.initAirlinesArr(target.id);
	}
	//CPPPEP ADD FOR AML.31473 20140529 END
 }
}



function mc_flightCalTicketOnChange(obj,idPrefix){
	var classOptions = mcTicketClass[obj.selectedIndex];
	var classValues = mcAwardClass[obj.selectedIndex];
//	alert[classOptions];
	
	var classDropDownObj = document.getElementById(idPrefix+"_class");
	if(classOptions.length > 0 ){
		classDropDownObj.options.length = 0;
		for (x = 0; x < classOptions.length; x++){
		classDropDownObj.options[classDropDownObj.length] = new Option(classOptions[x],classValues[x]);
		}
	}
	//CPPPEP ADD FOR AML.31473 20140527 START
	if(ACManager){
		ACManager.singleAwardType = obj.value;
		ACManager.awardTypeOnchangeHandler("faf_airline");
	}
	//CPPPEP ADD FOR AML.31473 20140527 END
}


var aec_returnFunction = function(responseStr, selectbox){
		jsonObject = JSON.parse(String(responseStr));
		defaultstr = selectbox.options[0];
		selectbox.length = 0;
		selectbox.options[0] = defaultstr;
		if(jsonObject.classes!=null && jsonObject.classes.length != 0 ){
			for (x = 0; x < jsonObject.classes.length; x++){
					selectbox.options[selectbox.options.length] = new Option(jsonObject.classes[x].classDesc,jsonObject.classes[x].classCode);
			}
		}
	}
function trimLength(textarea, maxChars) {
	if(textarea.value.length <= maxChars) return;	
	textarea.value = textarea.value.substr(0, maxChars);
}
function limitText(textarea, maxChars) {
	if(typeof(textarea.onkeypress.arguments[0]) != 'undefined')
		var keyCode = textarea.onkeypress.arguments[0].keyCode;
	else {
		if(document.selection.createRange().text.length != 0) return true;
		var keyCode = event.keyCode;
	}
	//Backspace, delete and arrow keys
	var allowedChars = new Array(8, 37, 38, 39, 40, 46);
	for(var x=0; x<allowedChars.length; x++) if(allowedChars[x] == keyCode) return true;	
	if(textarea.value.length < maxChars) return true;	
	return false;
}

//  ---------------------------------------
//  Retrieve the parameter from the URL
function queryString(paraName) {
	var qs = window.location.search.substring(1);
	var qsList = qs.split("&");
	for (i=0;i<qsList.length;i++) {
		paraList = qsList[i].split("=");
		if (paraList[0] == paraName) {
			return paraList[1];
		}
	}
}

// from v3 js
function popUpWinIBE(url) {
  var str = "left=0,screenX=0,top=0,screenY=0";

  if (window.screen) {
    var ah = screen.availHeight - 50;
    var aw = screen.availWidth - 10;
    str += ",height=" + ah;
    str += ",innerHeight=" + ah;
    str += ",width=" + aw;
    str += ",innerWidth=" + aw;
  }
  str += ",status=yes,menubar=no,scrollbars=yes,resizable=yes,toolbar=no"
  newWin = window.open(url, "", str);
  newWin.focus();
}

function getCookie(c_name)
{
if (document.cookie.length>0)
  {
  c_start=document.cookie.indexOf(c_name + "=");
  if (c_start!=-1)
    { 
    c_start=c_start + c_name.length+1; 
    c_end=document.cookie.indexOf(";",c_start);
    if (c_end==-1) c_end=document.cookie.length;
    return unescape(document.cookie.substring(c_start,c_end));
    } 
  }
return "";
} 

function setCookie(c_name,value,path)
{
	var exdate=new Date();
	document.cookie=c_name+ "=" +escape(value)+
((path==null) ? "" : ";path="+path);
}

function isInAcceptSymbol(c){
	var acceptSym = " -().,/'";
	if(acceptSym.indexOf(c)>=0){
		return true;
	}
}
function getSiteAddress() {
	var url = location.protocol + '//' + location.host
	return url;
}
function getLangFromURL() {
	var path = document.location.pathname;
	var urlPrefix = '/am';
	var langStart = urlPrefix.length + 1;
	var langEnd = (urlPrefix + '/' + 'xx').length;	
	
	if (path.length >= langEnd) {
		lang = path.substring(langStart, langEnd);
	}
	
	//window.alert('path = ' + path + ', lang = ' + lang);
	
	return lang;
}

// from v3 js
function popupTnC(section) {
	var lang = getLangFromURL();
	popUpTC(lang, section);
}
// from v3 js
function popUpTC(lang, section) {
	var url = '/am/en/site/terms?format=popup';
	url = url.replace("en",lang);
	url = getSiteAddress() + url;
	
	var newArchor = '';
	if (typeof t_n_c_mapping != 'undefined' && t_n_c_mapping != null) {
		if (typeof t_n_c_mapping[lang] != 'undefined' && t_n_c_mapping[lang] != null) {
			newArchor = t_n_c_mapping[lang][section];
			if (typeof newArchor == 'undefined' || newArchor == null) {
				newArchor = '';
			}		
		} else {
			newArchor = '';			
		}
	}

	popWindow(url + '#' + newArchor,'_blank','710','600','yes');
	//popWindow(url + '#' + section,'_blank','710','600','yes');
}
// from v3 js
function popUpPrivacy(lang, anchor) {
	var url = '/am/en/about/privacy?format=popup';
	url = url.replace("en",lang);
	popWindow(url + '#' + section,'_blank','710','600','yes');
}
// from v3 js
function joinNow() {
	var url = '/am/en/about/join';
	var lang = getLangFromURL();	
	url = url.replace("en",lang);
	goURL(url);
}
// from v3 js
function enewsSubscribe() {
	var url = '/am/en/account/profile/esubscriptions';
	var lang = getLangFromURL();
	url = url.replace("en",lang);
	goURL(url);
}

function isLetter (c){return ( ((c >= "a") && (c <= "z")) || ((c >=
"A") && (c <= "Z")) ) }

function passAjax(c){
	return isInAcceptSymbol(c) || isLetter(c);
}

function updateSwitchingLangHiddenFormParam(name, value){
	if(document.getElementById('lang_switch_en_'+name)!=null){
		document.getElementById('lang_switch_en_'+name).value = value;
	}
	if(document.getElementById('lang_switch_zh_'+name)!=null){
		document.getElementById('lang_switch_zh_'+name).value = value;
	}
	if(document.getElementById('lang_switch_sc_'+name)!=null){
		document.getElementById('lang_switch_sc_'+name).value = value;
	}
	if(document.getElementById('lang_switch_ja_'+name)!=null){
		document.getElementById('lang_switch_ja_'+name).value = value;
	}
	if(document.getElementById('lang_switch_ko_'+name)!=null){
		document.getElementById('lang_switch_ko_'+name).value = value;
	}
}

function rememberMe() {
	var amRemMe = getCookie('cookie.amRememberMe');
	var txtMbrID = document.getElementById('txtMbrID');
	var chkRbrMe = document.getElementById('chkRbrMe');
	
	if (amRemMe != null && amRemMe != '') {
		txtMbrID.value = amRemMe;
		chkRbrMe.checked = true;
	} else {
		txtMbrID.value = '';
		chkRbrMe.checked = false;
	}
}
