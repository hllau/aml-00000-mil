package com.cx.ng;

import com.asiamiles.ixClsMileageCal.beans.calculator.*;
import com.asiamiles.ixClsMileageCal.beans.common.McDAO;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RequestScoped
@Path("/AsiaMileService")
public class AsiaMileService
{
    @POST
    @Path("/calMilelage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AjaxResponse calMilelage(AsiaMileParam param)
    {
        MileageCalParam mcParam;
//        sectors.add(new MileageCalSector("HVB", "MBH", "QFA", null));
        List sectors = param.getSectorList().stream().map(s->new MileageCalSector(s.getOrigin(), s.getDestination(), s.getAirline(), null))
                .collect(Collectors.toList());
        mcParam = new MileageCalParam(1, param.getAwardType(), new ArrayList(sectors), "-1");
        MileageCalRedem mcr = new MileageCalRedem();
        MileageCalReturn mcReturn =  mcr.redemMileage(mcParam);
        AjaxResponse resp = new AjaxResponse();
        if(mcReturn.getErrorCode() == null){
            int award = mcReturn.getMileage().stream().mapToInt(m->((MileageCalAward)m).getAm_Mileage()).sum();
            resp.setData(award);
        }else{
            resp.addErrorMessage(mcReturn.getErrorCode(0));
        }
        return resp;
    }

    @POST
    @Path("/getCarrierList/{fromAirportCode}/{toAirportCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Carrier> getCarrierList(@PathParam("fromAirportCode") String fromAirportCode, @PathParam("toAirportCode") String toAirportCode){
        ResultSet rs;
        Connection conn = McDAO.getInstance().getConnection();
        String sql = "";
        List<Carrier> result = new ArrayList<>();
        try
        {
            sql = "select c.CARRIER_CODE, c.CARRIER_NAME from accrual_airport_pair map join carrier c on map.FKX1_CARRIERCODE = c.CARRIER_CODE\n" +
                    "where FKX_O_AIRPORT_CODE = ? and FKX_D_AIRPORT_CODE=?" ;

            PreparedStatement ps_query = conn.prepareStatement(sql);
            ps_query.setString(1, fromAirportCode);
            ps_query.setString(2, toAirportCode);
            rs = ps_query.executeQuery();

            while (rs.next()) {
                Carrier c = new Carrier();
                c.setCode(rs.getString("CARRIER_CODE"));
                c.setName(rs.getString("CARRIER_NAME"));
                result.add(c);
            }



            ps_query.close();
            rs.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            McDAO.getInstance().closeConnection(conn);
        }
        return result;
    }

    @POST
    @Path("/getToAirportList/{fromAirportCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getToAirportList(@PathParam("fromAirportCode") String fromAirportCode)
    {
        ResultSet rs;
        Connection conn = McDAO.getInstance().getConnection();
        String sql = "";
        List<String> result = new ArrayList<>();
        try
        {
            sql = "SELECT FKX_D_AIRPORT_CODE " +
                    "FROM ACCRUAL_AIRPORT_PAIR  " +
                    "WHERE FKX_O_AIRPORT_CODE = ? " ;

            PreparedStatement ps_query = conn.prepareStatement(sql);
            ps_query.setString(1, fromAirportCode);
            rs = ps_query.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("FKX_D_AIRPORT_CODE"));
            }



            ps_query.close();
            rs.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            McDAO.getInstance().closeConnection(conn);
        }
        return result;
    }
}