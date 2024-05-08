package com.infinity.dbx.temenos.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbx.BasePostProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthorizedSignatoriesPostProcessor extends BasePostProcessor {
    private static final Logger logger = LogManager.getLogger(GetAuthorizedSignatoriesPostProcessor.class);

    @Override
    public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
            throws Exception {
        final String DATASET_NAME = "membershipowner";
        Dataset userDataset = result.getDatasetById(DATASET_NAME);
        String membershipId = result.getParamValueByName("membershipId");

        for (Record record : userDataset.getAllRecords()) {
            record.addParam(new Param("membershipId", membershipId, "String"));
        }

        return result;
    }

}
