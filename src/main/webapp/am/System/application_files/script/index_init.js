 jQuery(document).ready(function(){
          //  bxslider for banner 2014jul25 updated start
 
      var slider;
	  if(jQuery('.bxslider').length > 0){
		  jQuery('.bxslider').bxSlider({
            mode: 'horizontal', //mode: 'fade',            
            speed: 500,
            auto: true,
            infiniteLoop: true,
            hideControlOnEnd: true,
            useCSS: false,
          controls:false,
          infiniteLoop : true,
              slideWidth : 675,
          pagerCustom: '#bx-pager',
          mode: 'fade'
        });
	  }
        jQuery('#bx-pager a').click(function(e){
			fnMenuHide(); /* 2014Aug06 Added */
            
            var i = jQuery(this).index();
            slider.goToSlide(i);
            slider.stopAuto();
            restart=setTimeout(function(){
               
                slider.startAuto();
                },500);

            return false;
        });
     
     if ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
        console.log("mobile/tablet");
     }
     else
     {
     jQuery('#bx-pager a').mouseover(function(){
        jQuery(this).addClass("active_over");    
    });
      jQuery('#bx-pager a').mouseout (function(){
        jQuery(this).removeClass("active_over");
         
    });
     }

 //  bxslider for banner 2014jul25 updated end
        // check no. of redeem and fix the margin
         var count = jQuery("#redeem_list ul").children().length;
         if (count == 3)
             jQuery( "#redeem_list ul li" ).addClass("item_3");
     
     // hide advertisting when no ad 2014jul25 updated
     
    
     if   (jQuery('#ad_area:has(img)').length <= 0)
        {  
             jQuery( "#ad_area" ).css("display","none");
        }
     
      // hide advertisting when no ad 2014jul25 end
	  
	  if( jQuery.isFunction(jQuery.fn.placeholder) ){ 
	    jQuery('input, textarea').placeholder();
	  }
         
    });
    