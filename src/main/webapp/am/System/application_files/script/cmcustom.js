/**********************************************************************
    * File Name       :	cmcustom.js 
    * Created Date    :	15 Sep,2011
    * Description     :	Coremetrics-Related Variables / Functions
    *                   for AM.
***********************************************************************/

// Coremetrics variables
var coreOnFlag = 1; // 0 = off, 1 = on
var coreProductionFlag = 1; // 0 = Test, 1 = Prod
var clientManaged = true; // true = prod , false = Test
var collectionDomain = "data.coremetrics.com";//data.coremetrics.com = Prod, testdata.coremetrics.com = Test
var cookieDomain = "asiamiles.com";

// Switch off Coremetrics Tags when on management stage
if (window.location.hostname.indexOf("proam.") == 0) {
	coreOnFlag = 0;
} else if (window.location.hostname.indexOf("prestaam.") == 0) { 
	coreOnFlag = 0;
} 

// Map to retrieve Master Client ID
// usage: MasterClientMap[key]
var MasterClientIDMap = {"AM":90383563 };


