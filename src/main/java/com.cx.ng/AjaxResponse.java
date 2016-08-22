package com.cx.ng;

import java.util.ArrayList;
import java.util.List;

public class AjaxResponse {
    private List<String> errorMessageList;
    private List<String> warnMessageList;
    private Object data;

    public boolean getHasError(){
        return errorMessageList != null;
    }
    public boolean getHasWarn(){
        return warnMessageList != null;
    }
    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public List<String> getWarnMessageList() {
        return warnMessageList;
    }

    public void addErrorMessage(String errorMsg) {
        if(errorMessageList == null)
            errorMessageList = new ArrayList<>();
        errorMessageList.add(errorMsg);
    }
    public void addWarnMessage(String warnMsg) {
        if(warnMessageList == null)
            warnMessageList = new ArrayList<>();
        warnMessageList.add(warnMsg);
    }
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}