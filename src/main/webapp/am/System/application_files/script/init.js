<!--//--><![CDATA[//><!--

//settings
document.menuDelayTime = 200; // dropdown layer menu hide timer - milliseconds

// attach event listener
if (document.addEventListener) {
    //mozilla / FF compatible
    window.addEventListener('load', initNavMenu, false);
    var onePxModifier = 0;
} else {
    //IE / PC
    window.attachEvent('onload', initNavMenu);
    var onePxModifier = 1;
}

checkCookies();

// initialize
function checkCookies() {
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        var a_temp_cookie = c.split('=');
        var cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');

        // if the extracted name matches passed check_name
        if (cookie_name == 'mlc_prelogin') {
            var cookie_value;
            // we need to handle case where cookie has no value but exists (no = sign, that is):
            if (a_temp_cookie.length > 1) {
                cookie_value = unescape(a_temp_cookie[1].replace(/^\s+|\s+$/g, ''));
            }
            // note that in cases where cookie is initialized but no value, null is returned
            if (cookie_value == 1) {
                var url = window.location.href.toString();
                var urlparts = url.split('://');
                var httpValue = urlparts[0];
                if (httpValue == ('http')) {
                    var newRequestURL = "https" + url.substring('http'.length, url.length);
                    window.location = newRequestURL;
                }
            }
            break;
        }
    }
    return null;
}

// initialize
function initNavMenu() {
    // assign layer menu interaction
    var e = document.getElementById("navMenu");
    cleanWhitespace(e);
    for (var i = 0; i < e.childNodes.length; i++) {
        tempvar = " " + e.childNodes[i].id;
        if (tempvar.indexOf("nav") > 0) {
            e.childNodes[i].onmouseover = function() {
                fnClearMenuHideTimer();
            };
            e.childNodes[i].onmouseout = function() {
                fnMenuHide();
            };
        }
    }
}
/* added 2014aug06 for menu fit on tablet/mobile  start */

 jQuery(document).ready(function(){    
      var currentmenu = "";
         jQuery('.menu li a').each(function() {
					var $menuItem = jQuery(this),
						$menuItemName = jQuery(this).attr('name')  ;
                    if ($menuItemName != "hdrNav1")                 
                    {
                        if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
                                    $menuItem.on('click',function(e){
                                         if ( currentmenu != $menuItemName)
                                        {
											if (!jQuery(this).hasClass("on"))	{
												e.preventDefault();
											}
                                            fnMenuShowHolder($menuItemName,jQuery(this)[0])  ;
                                            currentmenu =$menuItemName;
                                        }
                                        else
                                        {
                                            fnMenuHide();
                                            currentmenu = ""
                                        }
                                    });                         
                                }
                        else
                        {
                            $menuItem.mouseover(function(){
                                    fnMenuShowHolder($menuItemName,jQuery(this)[0])  
                                });
                            $menuItem.mouseout (function(){
                                    fnMenuHide();
                                });
                         }
                    }	
				});

     jQuery('html').bind("click touchstart",function(e){

    // Check if click was triggered on or within #menu_content
         if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
    if( jQuery(e.target).closest("#navMenu").length > 0 || jQuery(e.target).closest("#main_nav").length > 0 ) {
        console.log("menu");
        return ;
    }

    // Otherwise
     fnMenuHide();
       
     }
      });
 });
    
/* added 2014aug06 for menu fit on tablet/mobile  end */








/* added by JW for delay menu display */
function fnMenuShowHolder(divLayerID, divMenu) {
    document.pendingManuShow = divMenu.id;
    function tmp() {
        fnMenuShow(divLayerID, divMenu);
    }

    /* use closure to pass the divmenu object */
    clearTimeout(document.timerHideMenu);
    document.timerShowMenu = setTimeout(tmp, document.menuDelayTime);
}
/*end */

function fnMenuShow(divLayerID, divMenu) {
    if (document.pendingManuShow != divMenu.id) return;
    /* added by JW for delay menu display */
    var objLayer = document.getElementById(divLayerID);
    var objMenu = divMenu;
    fnMenuHideNow();

    document.activeLayer = objLayer;
    document.activeNav = objMenu
    if (objMenu.className) {
        document.activeNavClassName = objMenu.className;
    } else {
        document.activeNavClassName = "";
    }
    document.activeNav.className = "on";

    if (objLayer && objMenu) {
        var layerTop = objMenu.offsetTop + objMenu.offsetHeight;
        var layerLeft = objMenu.offsetLeft;
        var elm = objMenu.offsetParent;
        while (elm) {
            layerTop += elm.offsetTop;
            layerLeft += elm.offsetLeft;
            elm = elm.offsetParent;
        }
        objLayer.style.display = "block";
        objLayer.style.top = layerTop + "px";
        objLayer.style.left = layerLeft + onePxModifier + "px";
        fnIFrameHack(objLayer);
    } else {
        //debug - layer menu not found
        //alert("Invalid Layer ID or Menu Div - can't display menu");
    }
}

function fnIFrameHack(divObj) {
    if (document.all) {
        var f = document.getElementById("frameNavElement");
        f.style.width = divObj.offsetWidth;
        f.style.height = divObj.offsetHeight;
        f.style.top = divObj.style.top;
        f.style.left = divObj.style.left;
        f.style.display = "block";
		
		f.style.border="0"; /*2014aug06 added fix 1E9,10 bugs*/
    }
}

function fnMenuHide() {
    document.pendingManuShow = "";
    /* added by JW for delay menu display */
    fnClearMenuHideTimer();
    clearTimeout(document.timerShowMenu);
    document.timerHideMenu = setTimeout("fnMenuHideNow()", document.menuDelayTime);
}

function fnMenuHideNow() {
    clearTimeout(document.timerHideMenu);
    if (document.activeNav) document.activeNav.className = document.activeNavClassName;
    if (document.activeLayer) document.activeLayer.style.display = "none";

    var f = document.getElementById("frameNavElement");
    if (f) f.style.display = "none";
}

function fnClearMenuHideTimer() {
    clearTimeout(document.timerHideMenu);
}

function fnMenuShowChild(divID) {
    var objLayer = document.getElementById(divID);
    objLayer.style.display = "block";
    objLayer.style.left = "180px";
}

sfHover = function() {
    var sfEls = document.getElementById("navMenu").getElementsByTagName("li");
    for (var i = 0; i < sfEls.length; i++) {
        sfEls[i].onmouseover = function() {
            this.className += " sfhover";
        };
        sfEls[i].onmouseout = function() {
            this.className = this.className.replace(new RegExp(" sfhover\\b"), "");
        };
    }
};

if (window.attachEvent) window.attachEvent("onload", sfHover);

// Search box function //
document.param_original_searchText = "";
function fn_focusSearch(txtBox) {
    if (document.param_original_searchText == "") {
        document.param_original_searchText = txtBox.value;
    }
    if (txtBox.value == document.param_original_searchText) {
        txtBox.value = "";
        txtBox.className = "txt_search_on";
    }
}


function fn_blurSearh(txtBox) {
    if (txtBox.value == "") {
        txtBox.value = document.param_original_searchText;
        txtBox.className = "txt_search_off";
    }
}
//--><!]]>