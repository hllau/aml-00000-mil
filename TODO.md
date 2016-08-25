# TODO

1. Can the database be initialized on app first run automatically?

2. The scirpt file `scripts/add_mvn_lib.bat` only works on Windows. Can `clsUtil.jar`
   and `common-logger.jar` be uploaded to Artifactory so the script is not neccesary?

3. Version is not correct on one of the maven install script.

4. The code are bound to assume root path at "/demo", please make them relative
   to the page as the container will host the app on the root context:

       src/main/webapp/am/System/application_files/script/reactjs/SectorPanel.jsx:                buildAjaxRequest("/demo/rest/AsiaMileService/calMilelage/",
       src/main/webapp/am/System/application_files/script/reactjs/SectorSelector.jsx:                buildAjaxRequest2("/demo/rest/AsiaMileService/getToAirportList/"+event.value,
       src/main/webapp/am/System/application_files/script/reactjs/SectorSelector.jsx:                buildAjaxRequest2("/demo/rest/AsiaMileService/getCarrierList/"+newSector.from.value+"/"+newSector.to.value,
       src/main/webapp/index.jsp:    <title>Asia-mile calulator demo</title>
       src/main/webapp/index.jsp:    <meta http-equiv="refresh" content="0;URL='/demo/am/en/redeem/flightawardfinder.html'" />

5. Environment variables must start with a letter, not number. Please remove '00000_' prefix for all env
   variables.

