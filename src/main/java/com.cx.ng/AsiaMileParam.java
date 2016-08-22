package com.cx.ng;

import java.util.List;

/**
 * Created by cheng on 8/21/2016.
 */
public class AsiaMileParam {
    private String awardType;
    private List<Sector> sectorList;

    public String getAwardType() {
        return awardType;
    }

    public void setAwardType(String awardType) {
        this.awardType = awardType;
    }

    public List<Sector> getSectorList() {
        return sectorList;
    }

    public void setSectorList(List<Sector> sectorList) {
        this.sectorList = sectorList;
    }
}
