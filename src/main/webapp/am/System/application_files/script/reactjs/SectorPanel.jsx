var SectorPanel = React.createClass({
    getInitialState:function(){
        return {
            sectorList:[
                {from:null, to:null, airline:null}
                ],
            ticketType:null,
            ticketClass:null,
            asiaMileRequired:"-",
            loading:false
        }},
    componentWillUpdate(nextProps, nextState){
//        console.log("componentWillUpdate")
    },

    componentDidUpdate(nextProps, nextState){
        console.log("componentDidUpdate:"+this.state.loading);
        if(!nextState.loading){
            this.calMilelage();
        }

    },
    calMilelage:function(){
        if(this.state.loading) return;
        var ready = (this.state.ticketClass != null)
            && this.state.sectorList.filter(s=>s.airline==null).length == 0;
        if(ready){
            this.setState({loading:true});
            jQuery.ajax(
                buildAjaxRequest(this.props.contextPath+"rest/AsiaMileService/calMilelage/",
                    {
                        awardType:this.state.ticketClass.value,
                        sectorList:this.state.sectorList.filter(s=>s.airline!=null).map(s=>{
                            return {
                                origin:s.from.value,
                                destination:s.to.value,
                                airline:s.airline.value
                            }
                        })
                    },
                    function (result) {
                        if(result.hasError){
                            this.setState({loading:false, asiaMileRequired:"specified itinerary is not eligible for Asia Miles awards."});
                        }else{
                            this.setState({loading:false, asiaMileRequired:formateNum(result.data)});
                        }
                        //this.setState({asiaMileRequired:result}

                    }.bind(this)));
        }else{
            if(this.state.asiaMileRequired != "-")
                this.setState({asiaMileRequired:"-"});
        }
    },
    handleRemove:function(){
        var newState = jQuery.extend(true, {}, this.state);
        newState.sectorList.splice( newState.sectorList.length-1, 1 );
        this.setState(newState);
    },
    handleAdd:function(){
        var newState = jQuery.extend(true, {}, this.state);
        newState.sectorList.push({from:null, to:null, airline:null});
        this.setState(newState);
    },
    handleClear:function(){
        this.setState({
            asiaMileRequired:"",
            sectorList:[
            {from:null, to:null, airline:null}]});
    },
    handleSectorChange:function(sector, modifiedSector, a){
        sector.from = modifiedSector.from;
        sector.to = modifiedSector.to;
        sector.airline = modifiedSector.airline;
        var newState = jQuery.extend(true, {}, this.state);
        this.setState(newState);
    },
    handleCalculate:function () {
        var url="";
        this.state.sectorList.forEach(s=>url+=s.from.value+":"+s.to.value+":"+s.airline.value+"\n");
        alert(url);
    },
    handleTicketTypeChange:function(event){
        this.setState({ticketType:event, ticketClass:null})
    },
    handleTicketClassChange:function(event){
        this.setState({ticketClass:event})
    },
    render:function(){
            return (
                <div>
                    <table>
                        <tr>
                            <td>
                                <strong><label >Ticket/Upgrade *</label></strong><br/>
                                <SimpleSelect placeholder= "Select Ticket Type"
                                              options = {ticketTypeList}
                                              onValueChange={this.handleTicketTypeChange}
                                              value = {this.state.ticketType}
                                />
                            </td>

                            <td>
                            <strong><label >Class *</label></strong><br/>
                                <SimpleSelect
                                    placeholder= "Select Ticket Class"
                                              options = {ticketClassList.filter(c=>this.state.ticketType== null?false:c.ticketType_Id == this.state.ticketType.value)}
                                              onValueChange={this.handleTicketClassChange}
                                              value = {this.state.ticketClass}
                                />
                        </td>
                        </tr>

                        </table>
                    {this.state.sectorList.map(s=><SectorSelector contextPath={this.props.contextPath} sector={s} onChange={this.handleSectorChange.bind(this, s)}/>)}

                    <table width="100%" className="tbl_transparent">
                        <colgroup>
                            <col width="34%"/>
                                <col width="33%"/>
                                    <col width="33%"/>
                        </colgroup>
                        <tbody>
                        <tr>
                            <td ><br/>
                                <div style={{ cursor: "pointer" }} className={this.state.sectorList.length>3?"divHide":"divShow"}>
                                    <a onClick={this.handleAdd} className="bulletLink">Add Sector</a>
                                    <br/>
                                </div>
                                <div style={{ cursor: "pointer" }} className={this.state.sectorList.length == 1 ?"divHide":"divShow"}>
                                    <a onClick={this.handleRemove} className="bulletLink">Remove Sector</a>
                                    <br/>
                                </div>
                                <a style={{ cursor: "pointer" }} onClick={this.handleClear} className="bulletLink">Start Again</a>
                            </td>
                            <th ><br/>
                                Asia Miles Required:
                            </th>
                            <td ><br/>
                                <strong>{this.state.asiaMileRequired}</strong>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>

            )
    }});
window.SectorPanel=SectorPanel