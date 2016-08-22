var SectorSelector = React.createClass({
    getInitialState:function(){
      return {
          //sector:{from:null, to:null, airline:null},
          toAirportList:[],
          airlineList:[]
    }},
    handleFromChange(event){
        var newSector = jQuery.extend(true, {}, this.props.sector);
        if(event){
            newSector.from=event;
        }else{
            newSector.from=null;
        }
        newSector.to=null;
        newSector.airline=null;
        this.setState(this.getInitialState());
        if(event){
            jQuery.ajax(
                buildAjaxRequest2("/demo/rest/AsiaMileService/getToAirportList/"+event.value,
                    function (result) {
                        this.setState({toAirportList:
                            airportOptionList.filter(ap=>jQuery.inArray(ap.value,result) != -1)}
                            )
                        }
                    .bind(this)));
        }
        if(this.props.onChange)
            this.props.onChange(newSector);

    },
    handleAirlineChange(event){
        var newSector = jQuery.extend(true, {}, this.props.sector);
        newSector.airline = event;
        if(this.props.onChange)
            this.props.onChange(newSector);
    },
    handleToChange(event){
        var newState = jQuery.extend(true, {}, this.state);
        var newSector = jQuery.extend(true, {}, this.props.sector);
        if(event){
            newSector.to = event;
        }else{
            newSector.to = null;
        }
        newSector.airline=null;
        newState.airlineList=[];
        this.setState(newState);
        if(event){
            jQuery.ajax(
                buildAjaxRequest2("/demo/rest/AsiaMileService/getCarrierList/"+newSector.from.value+"/"+newSector.to.value,
                    function (result) {
                        this.setState({airlineList:
                            result.map(
                                function(opt){return {label: opt.name, value: opt.code}}
                            )
                        });
                    }.bind(this)));
        }
        if(this.props.onChange)
            this.props.onChange(newSector);
    },

    componentDidMount:function(){

    },
    render:function(){
        return (
            <table width="600" className="tbl_transparent">
                <colgroup>
                    <col width="28%" />
                    <col width="8%" />
                    <col width="28%" />
                    <col width="8%" />
                    <col width="28%" />
                </colgroup>
                <tbody>
                <tr>
                    <td colSpan="5"><hr/></td>
                </tr>
                <tr>
                    <td ><strong>Form *</strong>
                        <br/>
                        <SimpleSelect placeholder= "Select a city" options = {airportOptionList}
                                      renderValue={opt=>opt==null?"":opt.label.substring(0,17)}
                                      onValueChange={this.handleFromChange}
                                      value = {this.props.sector.from}
                        />

                    </td>
                    <td>&nbsp;</td>
                    <td ><strong>To *</strong>
                        <br/>
                        <SimpleSelect placeholder= "Select a city" options = {this.state.toAirportList}
                                      renderValue={opt=>opt==null?"":opt.label.substring(0,17)}
                                      onValueChange={this.handleToChange}
                                      value = {this.props.sector.to}
                        />

                    </td>
                    <td>&nbsp;</td>
                    <td ><strong>Airline *</strong>
                        <br/>
                        <SimpleSelect placeholder= "Select an airline"
                                      options = {this.state.airlineList}
                                      value = {this.props.sector.airline}
                                      onValueChange={this.handleAirlineChange}
                                      />
                    </td>
                </tr>
                <tr>
                    <td colspan="5">


                    </td>
                </tr>
                </tbody>
            </table>
        );
    }
})

window.SectorSelector = SectorSelector;


