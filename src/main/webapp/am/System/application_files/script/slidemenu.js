//v6
//DOM and animation logic optimized

animateSpeed=33;

modifier=1.83;

wait_onmouseover=220;

if (typeof(cleanWhitespace)!="function")
{
	function cleanWhitespace(node)
	{
		notWhitespace = /\S/;
		for (var x = 0; x < node.childNodes.length; x++)
		{
			var childNode = node.childNodes[x];
			if ((childNode.nodeType == 3)&&(!notWhitespace.test(childNode.nodeValue)))
			{
				node.removeChild(node.childNodes[x]);
				x--;
			}
			if (childNode.nodeType == 1)
			{
				cleanWhitespace(childNode);
			}
		}
	}
}


function drawmenu(divname, setting)
{

	if (!document.getElementById("heightTest"))
	{
		document.write("<div style=\"position:absolute;left:-8000px;top:-8000px\" id=\"heightTest\"></div>");
	} else {
		var htest = document.getElementById("heightTest");
		htest.style.position = "absolute";
		htest.style.left = "-8000px";
		htest.style.top = "-8000px";
		
	}

	var e=document.getElementById(divname);
	if (e)
	{
		e.style.visibility = "visible";
		cleanWhitespace(e);
		// Force apply to all pages
		setting.getEvent="onclick";
		setting.arrowUse=true;
		setting.arrowCSS="";
		setting.arrowDn="http://www.asiamiles.com/am/application_images/misc/bullet_down.gif";
		setting.arrowUp="http://www.asiamiles.com/am/application_images/misc/bullet_up.gif";
		setting.arrowOp="http://www.asiamiles.com/am/application_images/misc/bullet_left.gif";
		e.setting={};
		for (var key in setting) {
			e.setting[key] = setting[key];
		}

		items=e.childNodes;

		if (items)
		{
			arrow=e.setting.arrowDn;
		
			var used_height=0;
			for (var i=0;i<items.length;i++)
			{
				if (isItem(items[i]))
				{
					var divs=items[i].childNodes[0];
				}else{
					var divs=items[i];
				}
				used_height+=parseInt(divs.offsetHeight);
			}
			e.setting.menuheight=used_height;
			e.setting.submenuOpened="";

			for (var i=0;i<items.length;i++)
			{

				if (isItem(items[i]))
				{

					var divs=items[i].childNodes[0];
					var divspan=items[i].childNodes[1];

					if (e.setting.maxheight==-1)
					{
						document.getElementById("heightTest").style.width="240px";
						document.getElementById("heightTest").style.padding="0px";
						document.getElementById("heightTest").className="mod";
						document.getElementById("heightTest").innerHTML="<div style=\"margin:0px;padding:10px\">"+divspan.innerHTML+"</div>";
						divspan.max_height=divspan.offsetHeight+15;

						for(j=0;j<i;j++)
						{
							if (isItem(items[j]))
							{
								pre_divspan=items[j].childNodes[1];
								pre_divspan.max_height=Math.max(pre_divspan.max_height,divspan.max_height);
								divspan.max_height=Math.max(pre_divspan.max_height,divspan.max_height);
							}
						}

					}else{
						divspan.max_height=e.setting.maxheight-used_height;
					}
				}
			}
			arrow="bullet_down";
			for (var i=0;i<items.length;i++)
			{
				if (isItem(items[i]))
				{

					var divs=items[i].childNodes[0];
					var divspan=items[i].childNodes[1];


					var title=divs.innerHTML;

					if (i<items.length-1)divspan.style.borderBottom="0px";

					if (i!=e.setting.opened_item)
					{
						divspan.style.height=0;
						var s = divs.parentNode.className;
						divs.parentNode.className = s.replace(/ on/gi,"");

						var child = divs.firstChild;
						while (child) {
							if ( child.nodeName == "SPAN" ) {
								child.className = arrow;
								break;
							}
							child = child.nextSibling;
						}
					}else{
						divs.parentNode.className += " on";
						var child = divs.firstChild;
						while (child) {
							if ( child.nodeName == "SPAN" ) {
								child.className = "bullet_left";
								break;
							}
							child = child.nextSibling;
						}
						arrow="bullet_up";
					}

					if (divs.id=="")
					{
						divs.id="divs_"+Math.round(Math.random()*100000000);
					}
					if (divspan.id=="")
					{
						divspan.id="divspan_"+Math.round(Math.random()*100000000);
					}

					if ((i!=e.setting.opened_item)||(e.setting.delayOpen!=-1))
					{
						divspan.style.height=0;

						if (i==e.setting.opened_item)
						{
							if (e.setting.allowMultiopen)
							{
								setTimeout("animateToggle(document.getElementById('"+divspan.id+"'))", e.setting.delayOpen);
							}else{
								setTimeout("animateInit('"+divname+"', '"+divs.id+"')", e.setting.delayOpen);
							}
						}
					}else{
						divspan.style.height=""+divspan.max_height+"px";
					}


					divspan.style.overflow = "hidden";
					divspan.ymotion="stop";


					if (e.setting.allowMultiopen)
					{
						eval("divs."+e.setting.getEvent+"=function(){animateToggle(this);};");
					}else{
						eval("divs."+e.setting.getEvent+"=function(){animateInit('"+divname+"','"+divs.id+"');};");
					}

					divs.onmouseover=function(){this.parentNode.pre_className=this.parentNode.className;this.parentNode.className+=" over";}
					divs.onmouseout=function(){if (this.parentNode.pre_className!=""){this.parentNode.className=this.parentNode.pre_className;}}

				}
			}
		}
		
		/* START CHANGE by AM-Dev cpphowc 20081014 */

		// the following line above duplicate right hand tools box's content into "heightTest" div so clear it after finish "drawing menu"
		// document.getElementById("heightTest").innerHTML="<div style=\"margin:0px;padding:10px\">"+divspan.innerHTML+"</div>";
		document.getElementById("heightTest").innerHTML='';
		
		/* END CHANGE by AM-Dev cpphowc 20081014 */
	}
	
}

function animateInit(divname, expandDiv)
{

	var e=document.getElementById(divname);

	if (e.motionlock) return;

	if (e)
	{
		e.motionlock=true;
		cleanWhitespace(e);
		var items=e.childNodes;
		if (items)
		{
			arrow="bullet_down";
			for (var i=0;i<items.length;i++)
			{

				if (isItem(items[i]))
				{
					var divs=items[i].childNodes[0];
					var divspan=items[i].childNodes[1];
					var divspanimg=items[i].childNodes[0].childNodes[1];

					if (divs.id==expandDiv)
					{
						divs.parentNode.className += " on";
						divs.parentNode.pre_className="";
						var child = divs.firstChild;
						while (child) {
							if ( child.nodeName == "SPAN" ) {
								child.className = "bullet_left";
								break;
							}
							child = child.nextSibling;
						}
						arrow="bullet_up"

						divspan.ymotion="expand";
						divspan.style.display="block";
						divspan.style.overflow="hidden";

					}else{
						var s = divs.parentNode.className;
						divs.parentNode.className = s.replace(/ on/gi,"");
						var s = divs.parentNode.className;
						divs.parentNode.className = s.replace(/ over/gi,"");
						
						var child = divs.firstChild;
						while (child) {
							if ( child.nodeName == "SPAN" ) {
								child.className = arrow;
								break;
							}
							child = child.nextSibling;
						}

						divspan.ymotion="shrink";
						divspan.style.overflow="hidden";

					}
				}
			}


		}
	}

	animate(divname);

}


function animateToggle(divs)
{

	var e=divs.parentNode;
	if (e)
	{
		cleanWhitespace(e);

		var divspan=e.childNodes[1];
		var divspanimg=e.childNodes[0].childNodes[1];


		
		if ((divspan.ymotion=="shrink")||((divspan.ymotion=="stop")&&(divspan.offsetHeight<30)))
		{
			var child = divs.firstChild;
			while (child) {
				if ( child.nodeName == "SPAN" ) {
					child.className = "bullet_left";
					break;
				}
				child = child.nextSibling;
			}
			
			divspan.ymotion="expand";

		}else{
			var child = divs.firstChild;
			while (child) {
				if ( child.nodeName == "SPAN" ) {
					child.className = "bullet_left";
					break;
				}
				child = child.nextSibling;
			}

			divspan.ymotion="shrink";
			

		}

		animateSingle(divspan.id);

	}

}

function animateSingle(divname)
{

	var divspan=document.getElementById(divname);

	if (divspan.ymotion=="shrink")
	{

		divspan.style.height=parseInt(divspan.style.height)/modifier+"px";
		if (parseInt(divspan.style.height)<1)
		{
			divspan.style.height="0px";
			divspan.ymotion="stop";
		}
	}

	if (divspan.ymotion=="expand")
	{

		divspan.style.height=(parseInt(divspan.style.height)+divspan.max_height)/+"px";
		if (divspan.max_height-parseInt(divspan.style.height)<1)
		{
			divspan.style.height=divspan.max_height+"px";
			divspan.ymotion="stop";
		}
	}
	if ((divspan.ymotion=="shrink")||(divspan.ymotion=="expand"))
	{
		setTimeout("animateSingle('"+divname+"')", animateSpeed);
	}
}

function animate(divname)
{
	used_height=0;
	min_height=9999999;

	var e=document.getElementById(divname);
	if (e)
	{

		cleanWhitespace(e);
		var items=e.childNodes;
		if (items)
		{

			for (var i=0;i<items.length;i++)
			{

				if (isItem(items[i]))
				{
					var divspan=items[i].childNodes[1];


					if (divspan.ymotion=="expand")
					{
						var target=divspan;
					}

					if (divspan.ymotion=="shrink")
					{

						divspan.next_height=Math.floor(parseInt(divspan.style.height)/modifier)+"px";
						if (parseInt(divspan.next_height)<1)
						{
							divspan.next_height="0px";
							divspan.ymotion="stop";
							divspan.style.display="none";

						}

						used_height+=parseInt(divspan.next_height);

					}
				}
			}
		}



		if ((parseInt(e.offsetHeight)+1)<e.setting.maxheight)
		{
			e.style.height=Math.ceil((e.setting.maxheight+parseInt(e.offsetHeight))/modifier)+"px";
			min_height=parseInt(e.style.height)-e.setting.menuheight;
		}



		if (e.setting.delayOpen>-1)
		{

			target.next_height=Math.ceil((target.max_height+parseInt(target.offsetHeight))/2)+"px";
			if (target.max_height-parseInt(target.next_height)>0)
			{
				used_height=1;
			}

		}else{
			target.next_height=Math.ceil(Math.min(min_height, Math.max(target.max_height-used_height,0)))+"px";
		}




		if (items)
		{

			for (var i=0;i<items.length;i++)
			{

				if (isItem(items[i]))
				{
					var divspan=items[i].childNodes[1];


					divspan.style.height=divspan.next_height;

				}
			}
		}

	}



	if ((used_height>0)||(parseInt(e.offsetHeight)<e.setting.maxheight))
	{
		setTimeout("animate('"+divname+"')", animateSpeed);
	}else{
		e.setting.delayOpen=-1;
		e.motionlock=false;
		if (e.setting.submenuOpened!="")
		{
			var submenu=document.getElementById(e.setting.submenuOpened);
			submenu.style.display="none";
		}
		//target.style.overflow = "auto";
	}

}

/* START CHANGE by AM-Dev cpphowc 20080214 */
	/*
	function isItem(e)
	{
		if (e.className.indexOf("list_item") != -1)
		{
			return(true);
		}else{
			return(false);
		}
	}
	*/ /* Original version by Kitchen-Digital */
function isItem(e)
{
	if (e.className) {
		if (e.className.indexOf("list_item") != -1)
		{
			return(true);
		}else{
			return(false);
		}
	} else {
		return(false);
	}
}
/* END CHANGE by AM-Dev cpphowc 20080214 */



function showSublayer(div)
{

	var e=div.nextSibling;
	if (e)
	{
		e.style.display="block";

		if (div.parentNode.parentNode.parentNode.setting.submenuOpened!=e.id)
		{
			div.parentNode.parentNode.parentNode.setting.submenuOpened=e.id;
			t=div.parentNode;
			t.style.height=(parseInt(e.offsetHeight)+parseInt(t.offsetHeight))+"px";
		}
	}
}

/*CPPPEP ADD FOR AML.31473 20140625 START ,  highly customized for Award Charts*/
function award_charts_drawmenu(divid){
	var e = document.getElementById(divid);
	cleanWhitespace(e);
	var items = e.childNodes;
	var item_num = -1;
	if(items){
		for(var i=0;i<items.length;i++){
			if(items[i].nodeType==1 && items[i].className.indexOf("mod") != -1){
				item_num++;
				if(item_num==0){
					continue;		//ignore first block
				}
				var hd = items[i].childNodes[0];
				var bd = items[i].childNodes[1];
				
				items[i].className += " list_item";
				bd.style.overflow="hidden";
				bd.style.padding = "10px";
				bd.firstChild.style.display = "block";	bd.firstChild.style.height = "1px";		//adjust to ie7
				items[i].animating = false;
				items[i].bdheight = bd.offsetHeight-20;		//deduct 20 padding to get real height
				items[i].es_state = "shrink";
				bd.style.height = "0px";
				bd.style.display = "none";		//for anchor link display bug
				hd.ind = item_num-1;
				hd.innerHTML += "<span></span>";		//create span tag to show arrow
				hd.style.cursor = "pointer";
				cleanWhitespace(hd);
				
				award_charts_listItems.push(items[i]);
				
				hd.onclick = function(){		//create function on hd
					for(var i=0;i<award_charts_listItems.length;i++){		//check
						if(award_charts_listItems[i].animating){
							return;
						}
					}
					var temp = this.ind;
					for(var i=0;i<award_charts_listItems.length;i++){		//change all of class on span
						var child = award_charts_listItems[i].childNodes[0].childNodes[1];
						if(award_charts_listItems[temp].es_state == "shrink"){
							if(i == temp){
								child.className = "bullet_left";
							}else{
								if(i<temp){
									child.className = "bullet_down";
								}
								if(i>temp){
									child.className = "bullet_up";
								}
							}
						}else{
							child.className = "bullet_down";
						}
					}
					award_charts_animate(temp,false,divid);
				};
				
				hd.onmouseover = function(){
					var bd = award_charts_listItems[this.ind];
					if(bd.className.indexOf("over")==-1){
						bd.className += " over";
					}
				};
				
				hd.onmouseout = function(){
					var bd = award_charts_listItems[this.ind];
					bd.className = bd.className.replace(/ over/gi,"");
				};
			}
		}
	}
}

function award_charts_animate(ind,islink,divid){	//animate control
	var e = document.getElementById(divid);
	cleanWhitespace(e);
	for(var i=0;i<award_charts_listItems.length;i++){
		award_charts_listItems[i].animating = true;
		var bd = award_charts_listItems[ind].childNodes[1];
		if(i==ind){
			if(award_charts_listItems[i].es_state == "shrink"){
				award_charts_listItems[i].es_state = "expand";
				award_charts_listItems[i].childNodes[1].style.display = "block";
				award_charts_listItems[i].className += " on";
				award_charts_expand(i,islink);
			}else{
				award_charts_listItems[i].className = award_charts_listItems[i].className.replace(/ on/gi,"");
				bd.style.paddingTop = "0px";
				bd.style.paddingBottom = "0px";
				award_charts_shrink(i);
			}
		}else{
			award_charts_listItems[i].className = award_charts_listItems[i].className.replace(/ on/gi,"");
			bd.style.paddingTop = "0px";
			bd.style.paddingBottom = "0px";
			award_charts_shrink(i);
		}
	}
}

function award_charts_expand(ind,islink){		//expand action
	var bd = award_charts_listItems[ind].childNodes[1];
	var oheight = parseInt(bd.style.height);
	var maxheight = award_charts_listItems[ind].bdheight;
	if(oheight>=maxheight) {
		award_charts_listItems[ind].animating = false;
		//if(islink){		//for hyperlink
		//	var location =  window.location.href.replace(/#\d+$/,"");
		//	window.location.href = location+"#"+(ind+1);
		//}
		return;
	}
	bd.style.height = Math.ceil((maxheight-oheight)/2)+oheight+"px";
	window.setTimeout("award_charts_expand("+ind+","+islink+")",animateSpeed);
}

function award_charts_shrink(ind){		//shrink action
	var bd = award_charts_listItems[ind].childNodes[1];
	var oheight = parseInt(bd.style.height);
	var maxheight = award_charts_listItems[ind].bdheight;
	if(oheight<=0){
		award_charts_listItems[ind].animating = false;
		award_charts_listItems[ind].es_state = "shrink";
		award_charts_listItems[ind].childNodes[1].style.display = "none";		//for anchor link display bug
		bd.style.paddingTop = "10px";
		bd.style.paddingBottom = "10px";
		return;
	}
	bd.style.height = Math.floor(oheight/2)+"px";
	window.setTimeout("award_charts_shrink("+ind+")",animateSpeed);
}
/*CPPPEP ADD FOR AML.31473 20140715 END*/