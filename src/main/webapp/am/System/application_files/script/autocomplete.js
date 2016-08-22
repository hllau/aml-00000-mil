<!--//--><![CDATA[//><!--
/*
AutoSuggest: V1.6 - Written By Henry Wong
Status: Not Optimized. Need cross browser checking
Security: Added JSON string parser function
Known Bug / Limitation:
1) Problem with Opera (does not send keyUp event for DownArrow
see http://unixpapa.com/js/key.html (for more info)

2) IE - when run locally, the 2nd time will take 1 + minChar to show suggestPane (because html cache)
When run on remote server, make sure a random variable is added to avoid cache of the result

See CVS log for more info on previous versions.
*/

function autoComplete() {
	this.config = [];
	this.allACField = [];
	this.allACSuggest = []; // array of arrays
	this.allACSuggestDirty = [];
	this.allACScroll = [];
	this.suggestIdx = null;
	this.userKeyword = null;
	this.suggestPane = null;
	this.suggestHTML = "<div id='ac__{array_idx}' class='suggest_keyword' onmouseover='ACManager.highlightSelectedSuggest(this,{array_idx});'  onmousedown='ACManager.suggestClick(\"{element}\",\"{keyword}\");'>{keyword}</div>";
	this.viewAllLink = "javascript:ACManager.viewAllClick(\"{element}\");";
	this.AJAXRequest = AJAXManager();
	this.minChar = 3;
	this.pageNo =  0;
	this.enMinChar = 3;
	this.llMinChar = 2;
	this.pagingRow = 150;
	
	//CPPPEP ADD FOR AML.31473 20140526 START,add awardType to check
	this.mulAwardType = "";
	this.singleAwardType = "";
	this.airlineDefaultOption = "";
	this.prevMulAwardType="old";
	this.prevSingleAwardType="old";
	this.mulAirlines = [];
	this.singleAirlines = [];
	//CPPPEP ADD FOR AML.31473 20140526 END
	
	this.fullList = false;

	if ( window.attachEvent) {
		// wrapper trick which enable to trigger the event with the "this" object pass on,
		var caller = this;
		window.attachEvent("onresize",function(){caller.hideSuggest();});
	} else {
		// wrapper trick which enable to trigger the event with the "this" object pass on,
		var caller = this;
		window.addEventListener("resize",function(){caller.hideSuggest();},false);
	}
}

autoComplete.prototype = {

	// set the minimum char required to trigger the ajax request
	setMinChar : function(nchar) {
		//console.log("setMinChar");
		this.minChar = nchar;
	},


	// setFields is the constructor of the autoComplete object. this attach event to the text elements and init the variables
	setFields : function(config) {
		//console.log("setFields");
		this.config = config;
		for ( var i = 0 ; i < config.length; i++) {
			var mainEle = document.getElementById(config[i].id);
			if ( mainEle ) {
				if ( mainEle.attachEvent) {
					// wrapper trick which enable to trigger the event with the "this" object pass on,
					var caller = this;
					mainEle.attachEvent("onkeyup",function(event){var e = event||window.event;caller.keyEvent(e);});
					mainEle.attachEvent("onkeydown",function(event){var e = event||window.event;caller.keyDownEvent(e);});
					//mainEle.attachEvent("onblur",function(event){var e = event||window.event;caller.onBlur();});
					mainEle.attachEvent("onfocus",function(event){var e = event||window.event;caller.onFocus(e);});
				} else {
					// wrapper trick which enable to trigger the event with the "this" object pass on,
					var caller = this;
					mainEle.addEventListener("keyup",function(event){var e = event||window.event;caller.keyEvent(e);},false);
					mainEle.addEventListener("keydown",function(event){var e = event||window.event;caller.keyDownEvent(e);},false);
					//mainEle.addEventListener("blur",function(event){var e = event||window.event;caller.onBlur();},false);
					mainEle.addEventListener("focus",function(event){var e = event||window.event;caller.onFocus(e);}, false);
				}
				this.allACField.push(mainEle);
				this.allACSuggestDirty.push(1);
			} //end if
		} //end for
	},

	// this compare the text element with the internal Array allACField to get the offset
	lookupFieldOffset : function(ele) {
		//console.log("lookupFieldOffset");
		var offset = null;
		for (var i = 0 ; i < this.allACField.length ; i++ ) {
			if ( this.allACField[i] === ele ) {
				offset = i;
				break;
			}
		}
		return offset;
	},

	// create the div when it is not created yet
	createSuggestPane : function() {
		//console.log("createSuggestPane");
		this.suggestPane = document.createElement("div");
		this.suggestPane.id = "am_suggestPane";
		if (this.suggestPane.attachEvent) {
			var caller = this;
			this.suggestPane.attachEvent("onblur",function(event){caller.onBlur();});
		} else {
			var caller = this;
			this.suggestPane.addEventListener("blur",function(event){caller.onBlur();},false);
		}
		document.body.appendChild(this.suggestPane);
		fnIFrameHack(this.suggestPane);

	},


	showSuggest : function(suggestList,callerTextEle) {
		//console.log("showSuggest");
		// get the offset for lookup the current suggest list
		var offset = this.lookupFieldOffset(callerTextEle);
		// two case, suggestList is "string" (which is json text), this means new retrieved list
		// else suggestList is null, so use existing suggestList
		if ( typeof(suggestList) === "string") {
			//since we are only getting a list of strings but no json functions required
			//this.allACSuggest[offset] = eval(suggestList);
			jsonObject = JSON.parse(String(suggestList));
			//airportDesc is the name of the list in JSON result
			if(jsonObject.airportDesc==null || jsonObject.airportDesc=="" || jsonObject.airportDesc.length==0 ){
				//For SiteSearch Auto Complete
 			    this.allACSuggest[offset] = jsonObject.classes;
			} else {
				//For Airport Description
				this.allACSuggest[offset] = jsonObject.airportDesc;
			}
			//this.allACSuggest[offset] = String(suggestList).parseJSON();
		}

		if (!this.suggestPane) {
			this.createSuggestPane();
		}

		// get the filtered result.
		//Jason :
		if(	this.fullList){
			this.userKeyword = null;
		}
		var filteredResult = this.filterSuggestList(this.allACSuggest[offset],this.userKeyword);
		//this.userKeyword = callerTextEle.value;

		//var filteredResult = this.filterSuggestList(this.allACSuggest[offset],callerTextEle.value);
		this.suggestPane.innerHTML = "";

		//alert ("Filtered " + filteredResult);
		//render the suggestions
		var hideSuggestbln = false;
		if ( !filteredResult) {
			//this.suggestPane.innerHTML = "No result";
			//this.hideSuggest();
			hideSuggestbln = true;
		} else {
			if ( filteredResult.length === 0 ) {
			//this.suggestPane.innerHTML = "No result";
			//this.hideSuggest();
			hideSuggestbln = true;
			}
		}



		if(!hideSuggestbln){
			//show the pane
			//this.suggestPane.style.top = callerTextEle.offsetTop +callerTextEle.offsetHeight + "px";
			//this.suggestPane.style.left = callerTextEle.offsetLeft + "px";
			this.suggestPane.style.top = this.getTop(callerTextEle) + callerTextEle.offsetHeight + "px";
			this.suggestPane.style.left  = this.getLeft(callerTextEle) + "px";
			this.suggestPane.style.backgroundColor = "#FFFFFF";
			this.suggestPane.style.height = "0px";

			//fnIFrameHack( document.getElementById("am_suggestPane"));

			//this.suggestPane.style.height = "auto";
			//alert("total length:"+ filteredResult.length);
			//alert("Date  " + new Date() + " " + filteredResult.length);
			var y = 0;

			if(	this.fullList){
				var caller = this;

				//alert(this.config[offset].id);
				this.suggestPane.innerHTML += "<select size=20 name='fullListSelect' id='fullListSelect' onClick='ACManager.fullListOnchange(\""+this.config[offset].id+"\",this.value)'></select>";
				var selectFullList = document.getElementById('fullListSelect');
				if(filteredResult.length){
					for ( var i = 0 ; i < filteredResult.length ; i++) {
						if(selectFullList){
							selectFullList.options[selectFullList.length] = new Option(filteredResult[i],filteredResult[i],false);
						}
					}
					selectFullList.focus();
					selectFullList.options[0].selected;
					var textboxid = this.config[offset].id;
					if (selectFullList.attachEvent) {
						var caller = this;
						selectFullList.attachEvent("onblur",function(event){caller.onBlur();});
						selectFullList.attachEvent("onkeyup",function(event){var e = event||window.event;caller.fullListKeyevent(e, textboxid);});
					} else {
						var caller = this;
						selectFullList.addEventListener("blur",function(event){caller.onBlur();},false);
						selectFullList.addEventListener("keyup",function(event){var e = event||window.event;caller.fullListKeyevent(e, textboxid);},false);
					}
				}
			} else {
					if(filteredResult.length > 1000){
						this.showErrorHelp(callerTextEle);
					} else {
						for ( var i = 0 ; i < filteredResult.length ; i++) {
							if ((i === this.config[offset].maxRow) && (!this.config[offset].hasScroll) ) break; // only add up to max rows
			//				alert(callerTextEle.id);
			//				alert(filteredResult[i]);
							this.suggestPane.innerHTML += this.suggestHTML.supplant({array_idx:String(i),element:callerTextEle.id,keyword:filteredResult[i]});
						}
					}
					if (this.suggestPane.attachEvent) {
						var caller = this;
						this.suggestPane.attachEvent("onblur",function(event){caller.onBlur();});
					} else {
						var caller = this;
						this.suggestPane.addEventListener("blur",function(event){caller.onBlur();},false);
					}
			}

			//alert("Date  " + new Date() + " " + filteredResult.length);

			if (this.config[offset].hasScroll) {
				this.suggestPane.style.overflow = "auto";
				this.suggestPane.style.overflowX = "hidden";
				this.suggestPane.style.overflowY = "scroll";
			} else {
				this.suggestPane.style.overflow = "hidden";
			}

			if ( (this.suggestPane.style.visibility !== "visible") ) {
				this.suggestPane.style.display = "block";
				this.suggestPane.style.visibility = "visible";
				this.suggestPane.style.width = (callerTextEle.offsetWidth+50) + "px";
			}



			if(	this.fullList){
				var selectFullList = document.getElementById('fullListSelect');
				if(callerTextEle.offsetWidth < 100) {
					selectFullList.style.width =  (callerTextEle.offsetWidth+150) + "px";
					this.suggestPane.style.width = (callerTextEle.offsetWidth+150) + "px";
				} else {
					this.suggestPane.style.width = (callerTextEle.offsetWidth+50) + "px";
					selectFullList.style.width =  (callerTextEle.offsetWidth+50) + "px";
				}

				this.suggestPane.style.overflowY = "hidden";
				this.suggestPane.style.height = (this.suggestPane.firstChild.offsetHeight) + "px";
				fnIFrameHack( document.getElementById("am_suggestPane"));
			} else {
				if (filteredResult.length < this.config[offset].maxRow) {
					this.suggestPane.style.overflow = "hidden";
					this.suggestPane.style.overflowX = "hidden";
					this.suggestPane.style.overflowY = "hidden";
					this.suggestPane.style.height = (this.suggestPane.firstChild.offsetHeight * filteredResult.length) + "px";
					fnIFrameHack( document.getElementById("am_suggestPane"));
				} else {
					this.suggestPane.style.height = (this.suggestPane.firstChild.offsetHeight * this.config[offset].maxRow) + "px";
					fnIFrameHack( document.getElementById("am_suggestPane"));
				}
			}
		} else {
			//alert('hi');
			this.showErrorHelp(callerTextEle);
		}

	},

	// filter the suggestList against the userText.
	// This function Assume all content in suggestList is in lower case
	filterSuggestList : function(suggests,userText) {
		//console.log("filterSuggestList");
		//alert("filterSuggestList " + userText);
		//alert("suggests.length " + suggests.length);
		var userTextL = (userText)?userText.toLowerCase():"";
		var filteredResult = [];
		this.suggestPane.innerHTML = "";
		if(userText && userText.length>0){
			//alert(suggests);
			// filter work by the servlet
			if(suggests){
				for ( var i = 0 ; i < suggests.length; i++) {
					if ( (suggests[i].toLowerCase()).indexOf(userTextL) !== -1 ) {
						filteredResult.push(suggests[i]);
					}
				}
			}
		} else {
			//Handling for help text full port list (No keyword)
			//alert(suggests);
			filteredResult = suggests;
		}
		return filteredResult;
//		alert(suggests);
//		return suggests;
	},

	onBlur : function() {
		//console.log("onBlur");
		this.suggestIdx = null;
		this.userKeyword = null;

		//onblur
		var selectFullList = document.getElementById('fullListSelect');
		if(selectFullList){
			//do not hide if full list exist
		} else {
			setTimeout("hideSuggestPane();",200);
		}
		this.fullList = false;
		//this.hideSuggest()
	},

	onFocus : function(e) {
		if (this.suggestPane) {
				hideSuggestPane();
//				this.createSuggestPane();
		}
		var target = e.target || e.srcElement;
		var offset = this.lookupFieldOffset(target);
		//console.log("onBlur");
		if(target.value==""){
			this.showHelp(target);
		}
	},

	showHelp : function(callerTextEle) {
			if (!this.suggestPane) {
				this.createSuggestPane();
			}

			var viewallLabel = "";
			if(document.getElementById('portSuggestViewAllUrl')){
				viewallLabel = document.getElementById('portSuggestViewAllUrl').innerHTML;
			}

			if(callerTextEle.id.indexOf("search") >= 0){
			} else {
			this.fullList = false;
			this.suggestPane.style.top = this.getTop(callerTextEle) + callerTextEle.offsetHeight + "px";
			this.suggestPane.style.left  = this.getLeft(callerTextEle) + "px";
			this.suggestPane.style.height = "230px";
			this.suggestPane.style.display = "block";
			this.suggestPane.style.visibility = "visible";
			if(callerTextEle.offsetWidth < 100) {
			this.suggestPane.style.width = (callerTextEle.offsetWidth+150) + "px";
			} else {
			this.suggestPane.style.width = (callerTextEle.offsetWidth+50) + "px";
			}
			this.suggestPane.style.overflowX = "auto";
			this.suggestPane.style.backgroundColor = "#FFFFFF";
			fnIFrameHack( document.getElementById("am_suggestPane"));
			var portSuggestHelpTxt = document.getElementById('portSuggestHelpTxt').innerHTML + "<br>&nbsp;&nbsp;<a href='"+ this.viewAllLink.supplant({element:callerTextEle.id}) + "'>"+viewallLabel+"</a>";
			this.suggestPane.innerHTML = portSuggestHelpTxt;
			}
	},

	showErrorHelp : function(callerTextEle) {
			if (!this.suggestPane) {
				this.createSuggestPane();
			}

			var viewallLabel = "";
			if(document.getElementById('portSuggestViewAllUrl')){
				viewallLabel = document.getElementById('portSuggestViewAllUrl').innerHTML;
			}


			if(callerTextEle.id.indexOf("search") >= 0) {
			} else {
			this.fullList = false;
			this.suggestPane.style.top = this.getTop(callerTextEle) + callerTextEle.offsetHeight + "px";
			this.suggestPane.style.left  = this.getLeft(callerTextEle) + "px";
			this.suggestPane.style.height = "230px";
			this.suggestPane.style.display = "block";
			this.suggestPane.style.visibility = "visible";
			if(callerTextEle.offsetWidth < 100) {
			this.suggestPane.style.width = (callerTextEle.offsetWidth+150) + "px";
			} else {
			this.suggestPane.style.width = (callerTextEle.offsetWidth+50) + "px";
			}
			this.suggestPane.style.overflowX = "auto";
			this.suggestPane.style.backgroundColor = "#FFDDDD";
			fnIFrameHack( document.getElementById("am_suggestPane"));
			var portSuggestHelpTxt = document.getElementById('portSuggestHelpTxt').innerHTML + document.getElementById('portSuggestErrorTxt').innerHTML  + "<br>&nbsp;&nbsp;<a href='"+ this.viewAllLink.supplant({element:callerTextEle.id}) + "'>"+viewallLabel+"</a>";
			this.suggestPane.innerHTML = portSuggestHelpTxt;
			}
	},


	// hide the suggestPane
	hideSuggest : function() {
		//console.log("hideSuggest");
		var suggestPane = this.suggestPane;
		//find id - because setTimeout has no reference for this.suggestPane.
		if (!suggestPane) suggestPane = document.getElementById("am_suggestPane");
		if (suggestPane) {
			suggestPane.innerHTML = "";
			suggestPane.style.visibility = "hidden";
			suggestPane.style.top = "-1000px";
			suggestPane.style.left = "-1000px";
		}
		// hide iframe
		var f = document.getElementById("frameNavElement");
		if (f) f.style.display="none";
	},


	fullListKeyevent : function(e,textboxid){
		var target = e.target || e.srcElement; //SELECT box
		var offset = this.lookupFieldOffset(document.getElementById(textboxid));
		var divSuggest = null;
		// text length in the text field shorter than 2, suggestList invalidated (mark as dirty)
		var kc = e.keyCode;
		if (( kc === 37 ) || ( kc === 39 )) {
			return;
		} else if ( kc=== 27) {
			// escape
			hideSuggestPane();
		} else if ( kc === 13 ) {
			// enter
			this.fullListOnchange(textboxid,target.value.toLowerCase());
			hideSuggestPane();
		}
	},



	keyDownEvent : function(e) {
		var target = e.target || e.srcElement;
		var offset = this.lookupFieldOffset(target);
		var kc = e.keyCode;
		if ( kc=== 9) {
			// tab
			//this.onBlur();
			if(this.suggestIdx==null){
				divSuggest = document.getElementById("ac__0");
			} else {
				divSuggest = document.getElementById("ac__"+this.suggestIdx);
			}
			if(divSuggest){
				this.highlightSelectedSuggest(divSuggest);
				this.setSuggestKeyword(target,divSuggest.innerHTML);
			}
		}
	},

	//Logic for keypress of recorded text Field in setFields function
	keyEvent : function(e) {
		//console.log("keyUp kc=" + e.keyCode);
		var target = e.target || e.srcElement;
		var offset = this.lookupFieldOffset(target);
		var divSuggest = null;

		// text length in the text field shorter than 2, suggestList invalidated (mark as dirty)
		var kc = e.keyCode;

		if (( kc === 37 ) || ( kc === 39 ) || ( kc === 35 ) || ( kc === 36 ) ) {
			return;
		} else if  ( kc === 38 )  {
			// case arrow up
			if (this.suggestIdx == null) {
				this.suggestIdx = (this.suggestPane.childNodes.length -1);
			} else {
				this.suggestIdx--;
				if (this.suggestIdx < 0) this.suggestIdx = (this.suggestPane.childNodes.length -1);
			}
			divSuggest = document.getElementById("ac__" +this.suggestIdx);

		} else if ( kc === 40 ) {
			// case arrow down
			if (this.suggestIdx == null) {
				this.suggestIdx =0;
			} else {
				this.suggestIdx++;
				if (this.suggestIdx >= this.suggestPane.childNodes.length) this.suggestIdx=0;
			}
			divSuggest = document.getElementById("ac__" +this.suggestIdx);

		} else if ( kc=== 9) {
			// tab
			//this.onBlur();
		} else if ( kc=== 27) {
			// escape
			this.onBlur();

		} else if ( kc === 13 ) {
			// case enter
			//this.setSuggestKeyword(target,this.selectedSuggest.innerHTML);
			divSuggest = document.getElementById("ac__" +this.suggestIdx);

		} else {
			//store user keyword
			this.userKeyword = target.value;
			if( this.userKeyword){
				if( passAjax(this.userKeyword.charAt(0))){
					this.minChar = this.enMinChar;
				} else {
					this.minChar = this.llMinChar;
				}
			}
			//check is keyword is dirty
			if ( target.value.length < this.minChar ) {	// The current suggests for OFFSET is being dirtied and need to request again
				this.allACSuggestDirty[offset] = 1;
			}

			if ( this.allACSuggestDirty[offset] === 1 && target.value.length >= this.minChar ) {
                //target.value = target.value.toLowerCase();
//				alert(escape(target.value));
				var apiUrl = eval(this.config[offset].targetAPI);
                //alert(apiUrl);
				//suggestList is dirty, and text length longer than or equal minChar
				//this.AJAXRequest.sendRequest("dummy.asp?r="+(new Date()),"q="+escape(target.value),"get",this.showSuggest,this,target);
				//alert(apiUrl()+"&"+this.config[offset].targetField+"="+encodeURI(target.value));
				this.suggestIdx = null;
				this.AJAXRequest.sendRequest(apiUrl()+"&"+this.config[offset].targetField+"="+encodeURI(target.value.toLowerCase()), this.config[offset].targetField+"="+encodeURI(target.value.toLowerCase()),"get",this.showSuggest,this,target);
                //Jason

				//use the html file to run locally
				//this.AJAXRequest.sendRequest("dummy.html","q="+escape(target.value),"get",this.showSuggest,this,target);

				this.allACSuggestDirty[offset] = 0;
			} else if ( target.value.length >= this.minChar ) {
				//no need to query new suggestList and text length longer than or equal minChar
				this.showSuggest(null,target);
			} else if ( target.value.length < this.minChar ) {
				// text too short
//				this.hideSuggest();
				this.showHelp(target);
			}

		}

		// if arrow up / down or enter pressed
		if (divSuggest) {
			this.highlightSelectedSuggest(divSuggest);
			this.setSuggestKeyword(target,divSuggest.innerHTML);
			if (kc===13) this.onBlur();
		}

	},

	highlightSelectedSuggest : function(suggestDiv, suggestIdx) {
		//console.log("highlightSelectedSuggest");
		if(suggestIdx) this.suggestIdx = suggestIdx;
		this.resetBgc();
		this.suggestSelected = suggestDiv;
		suggestDiv.className +=" suggest_selected";
		this.setScroll(suggestDiv);
	},

	setSuggestKeyword : function(ele,key) {
		//console.log("setSuggestKeyword");
		var element = (typeof(ele) === "string")?document.getElementById(ele):ele;
		element.value = key;
		var offset = this.lookupFieldOffset(element);
//		alert("offset" + offset + " "+ element);
//		alert("postTrigger" + this.config[offset].postTrigger);
		if(this.config[offset].postTrigger!=""){
					var postFunction = this.config[offset].postTrigger.split("|");
					postJsFunction = eval(postFunction[0]);
//					alert("postFunction length"+ postFunction.length);
					if(postFunction.length==2){
//						alert (document.getElementById(postFunction[1]));
						postJsFunction(document.getElementById(postFunction[1]));
					}
		}
	},

	fullListOnchange: function(vid, newValue){
		var element = (typeof(vid) === "string")?document.getElementById(vid):vid;
		element.value=newValue;
		if(typeof(element.onchange) === 'function'){
		   element.onchange();
		}
		element.focus();
		var offset = this.lookupFieldOffset(element);
		if(this.config[offset].postTrigger!=""){
					var postFunction = this.config[offset].postTrigger.split("|");
					postJsFunction = eval(postFunction[0]);
//					alert("postFunction length"+ postFunction.length);
					if(postFunction.length==2){
//						alert (document.getElementById(postFunction[1]));
						postJsFunction(document.getElementById(postFunction[1]));
					}
		}
		hideSuggestPane();
	},
	suggestClick : function(ele,key) {
		//console.log("suggestClick");
		this.setSuggestKeyword(ele,key);
		//this.hideSuggest();
		this.onBlur();

	},
	
	//CPPPEP ADD FOR AML.31473 20140526 START
	//airline need be string
	awardTypeOnchangeHandler : function(airline){	
		//if true when airline id endwith number ,means airline should be multiple;
//		if(airline.match(/^[^\d]+\d{1,2}$/)){
//			var temp = this.mulAwardType.match(/^.*pt[12]$/i)?"new":"old";	//if awardtype endwith pt1 or pt2 ,means new tier award
//			//change award type have two kind,one need do filter,another one do not need,named them with "new" and "old",
//			//if current award type equals to prev one, no need do filter.
//			if(this.prevMulAwardType==temp){
//				return;
//			}
//			var idtemplate = airline.substring(0,airline.length-1);
//			if(!isNaN(idtemplate.substring(idtemplate.length-1))){
//				idtemplate = idtemplate.substring(0,idtemplate.length-1);
//			}
//			//max sector'number is 10,get airline1,2,3...10;
//			for(var i=0;i<10;i++){
//				var element = document.getElementById(idtemplate+(i+1));
//				if(element && element.options){
//					this.filterSingleAirline(element,this.mulAirlines[i],this.mulAwardType,true);
//				}
//			}
//			this.prevMulAwardType=temp;
//		}else{
//			var temp = this.singleAwardType.match(/^.*pt[12]$/i)?"new":"old";
//			if(this.prevSingleAwardType==temp){
//				return;
//			}
//			var element = document.getElementById(airline);
//			if(element && element.options){
//				this.filterSingleAirline(element,this.singleAirlines,this.singleAwardType,false);
//			}
//			this.prevSingleAwardType=temp;
//		}
	},
	filterSingleAirline : function(ele,arr,award,isMul){
		if(ele.disabled){
			return;
		}
		//ele.options.length=0;		//new requirement 20140714 cpppep
		if(award.match(/^.*pt[12]$/i)){
			ele.options.length=0;
			for(var i=0;arr && i<arr.length;i++){
				if(arr[i][1]=="HDA" ||arr[i][1]=="CPA" || arr[i][1]==""){
					ele.options[ele.options.length] = new Option(arr[i][0],arr[i][1],false,false);
				}
			}
		}else{
			//new requirement 20140714 cpppep
//			for(var i=0;arr && i<arr.length;i++){
//				ele.options[ele.options.length] = new Option(arr[i][0],arr[i][1],false,false);
//			}
		}
		if(ele.options.length==0 && !isMul){
			ele.options[0] = new Option(this.airlineDefaultOption,"",false,false);
		}
	},
	//init airlines array when airline options inited;
	//airline need be string
	initAirlinesArr : function(airline){
		var element = document.getElementById(airline);
		if(element.options){ //need update to (element && element.options)
			var options = element.options;
			//if airline end with number,airline input should be multiple;
			if(airline.match(/^[^\d]+\d{1,2}$/)){
				var ind = parseInt(airline.substring(airline.length-1));
				if(ind==0){
					ind = parseInt(airline.substring(airline.length-2));
				}
				ind -= 1;
				this.mulAirlines[ind]=[];
				for(var i=0;i<options.length;i++){
					this.mulAirlines[ind][i] = [options[i].text,options[i].value];
				}
				this.filterSingleAirline(element,this.mulAirlines[ind],this.mulAwardType,true);
			}else{
				this.singleAirlines=[];
				for(var i=0;i<options.length;i++){
					this.singleAirlines[i] = [options[i].text,options[i].value];
				}
				this.filterSingleAirline(element,this.singleAirlines,this.singleAwardType,false);
			}
		}
	},
	//CPPPEP ADD FOR AML.31473 20140620 END

	viewAllClick : function(ele) {
		var element = (typeof(ele) === "string")?document.getElementById(ele):ele;
		element.value="";
		var offset = this.lookupFieldOffset(element);
		this.fullList = true;
		var apiUrl = eval(this.config[offset].targetAPI);
		this.AJAXRequest.sendRequest(apiUrl()+"&"+this.config[offset].targetField+"=%", this.config[offset].targetField+"=%","get",this.showSuggest,this,element);
	},

	resetBgc : function () {
		//console.log("resetBgc");
		for (var d=0; d <this.suggestPane.childNodes.length; d++) {
			if(document.getElementById("ac__" + d)){
				sdiv=document.getElementById("ac__" + d);
				sdiv.className = "suggest_keyword";
			}
		}
	},

	setScroll : function (ele) {
		var rowMinVisible = this.suggestPane.scrollTop;
		var rowMaxVisible = rowMinVisible + this.suggestPane.offsetHeight-5;
		if ((ele.offsetTop > rowMinVisible) && (ele.offsetTop < rowMinVisible)) return;
		if (ele.offsetTop < rowMinVisible) {
			this.suggestPane.scrollTop = ele.offsetTop;
		} else if (ele.offsetTop >= rowMaxVisible) {
			this.suggestPane.scrollTop = ele.offsetTop - this.suggestPane.offsetHeight + ele.offsetHeight + 3;
		}

	},

	getLeft : function (txtBox) {
		var oNode = txtBox;
		var iLeft = 0;
		while(oNode) {
		  iLeft += oNode.offsetLeft;
		  oNode = oNode.offsetParent;
		}
		return iLeft;
	},

	getTop : function (txtBox) {
		 var oNode = txtBox;
		 var iTop = 0;
		 while(oNode) {
			  iTop += oNode.offsetTop;
			  oNode = oNode.offsetParent;
		 }
		 return iTop;
	}
}

String.prototype.supplant = function (o) {
    return this.replace(/{([^{}]*)}/g,
        function (a, b) {
            var r = o[b];
				/*ie6 bug - single quote problem */
				if (r.indexOf("'") >=0) r = String(r).replace(/'/g, "&#39");
            return typeof r === 'string'? r : a;
        }
    );
};


function hideSuggestPane(){
		suggestPane = document.getElementById("am_suggestPane");
		if (suggestPane) {
			fnIFrameHack( document.getElementById("am_suggestPane"));
			suggestPane.innerHTML = "";
			suggestPane.style.visibility = "hidden";
			suggestPane.style.top = "-1000px";
			suggestPane.style.left = "-1000px";
		}
		// hide iframe
		var f = document.getElementById("frameNavElement");
		if (f) f.style.display="none";
}

//--><!]]>