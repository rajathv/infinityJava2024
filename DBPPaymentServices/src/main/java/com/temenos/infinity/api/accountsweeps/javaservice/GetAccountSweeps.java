package com.temenos.infinity.api.accountsweeps.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.infinity.api.accountsweeps.resource.api.AccountSweepsResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetAccountSweeps implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetAccountSweeps.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {

        Result result= new Result();
        try {
            AccountSweepsResource accountSweepsResource =  DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AccountSweepsResource.class);
            Map<String, Object> inputParamsMap = (HashMap<String, Object>) inputArray[1];
            FilterDTO filterDTO = JSONUtils.parse(new JSONObject(inputParamsMap).toString(), FilterDTO.class);
            result=accountSweepsResource.getAccountSweeps(filterDTO,request);
        }
        catch (Exception e){
            LOG.error("Exception occured while getting account sweep"+e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }

        return result;
    }
}
