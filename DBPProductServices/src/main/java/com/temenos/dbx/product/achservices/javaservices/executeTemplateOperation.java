package com.temenos.dbx.product.achservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * operation to execute template for the given template_id
 * @author: KH2624
 * @version 1.0
 * implements {@link JavaService2}
 */
public class executeTemplateOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(executeTemplateOperation.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {

        Result result = new Result();

        try {
            //Initializing of TransactionResource through Abstract factory method
            ACHTemplateResource AchServicesResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(ACHTemplateResource.class);

            result  = AchServicesResource.executeTemplate(methodID, inputArray, request, response);
        }
        catch(Exception e) {
            LOG.error("Error occured while invoking executeTemplateOperation: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }

        return result;
    }
}
