package com.temenos.infinity.api.savingspot.PreAndPostProcessors;

import java.util.HashMap;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.savingspot.constants.TemenosConstants;

public class createSavingsPotPreProcessor implements DataPreProcessor2{

	@Override
    @SuppressWarnings("unchecked")
    public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
        Result result) throws Exception {
        if(request.getParameter(TemenosConstants.STATUS).equalsIgnoreCase(TemenosConstants.SUCCESS))
         {
        	if(request.getParameter(TemenosConstants.POTCONTRACTID) != null){
        		inputMap.put(TemenosConstants.POTACCOUNTID,request.getParameter(TemenosConstants.POTCONTRACTID));
        	    return true;
        	}
        	else 
        	{
        		return false;
        	}	
         }
        else
        {
        	 return false;
        }
       
    }
}
