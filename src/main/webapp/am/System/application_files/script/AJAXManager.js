var AJAXManager = function() {
	var xRequest=null;
	var READY_STATE_UNINITIALIZED=0;
	var READY_STATE_LOADING=1;
	var READY_STATE_LOADED=2;
	var READY_STATE_INTERACTIVE=3;
	var READY_STATE_COMPLETE=4;
	var callerEle = null;
	var customFunction = null;
	var callObj = null;

	var getXMLHTTPRequest = function() {
		if (window.XMLHttpRequest) {
			xRequest = new XMLHttpRequest();
		} else if (typeof ActiveXObject != "undefined"){
			xRequest = new ActiveXObject("Microsoft.XMLHTTP");
		}
	}

	var onReadyStateChange = function(){
		var ready=xRequest.readyState;
		var data=null;
		if (ready==READY_STATE_COMPLETE){
			customFunction.call(callObj,xRequest.responseText,callerEle);
		}else{
			data="loading...["+ready+"]";
		}
	}

	var sendRequest = function(url,params,HttpMethod,fn,scope,callerTextEle) {
		customFunction = fn;
		callObj = scope;
		callerEle = callerTextEle;
		if (!HttpMethod){
			HttpMethod="GET";
		}
		
		getXMLHTTPRequest();
		
		if (xRequest){
			xRequest.onreadystatechange=onReadyStateChange;
			xRequest.open(HttpMethod,url,true);
			xRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			xRequest.send(params);
		}
	}

	return {
		sendRequest:sendRequest
	}
}
