package com.temenos.infinity.api.cards.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.infinity.api.cards.resource.api.CardServicesResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetStatementsOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(ActivateCardOperation.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			//Initializing of CardsResource through Abstract factory method
			CardServicesResource cardResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CardServicesResource.class);
            String card_id = request.getParameter("Card_id");
            String month= request.getParameter("month");
			String userId = HelperMethods.getUserIdFromSession(request);
			result  = cardResource.getStatements(card_id,month,userId);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of ActivateCardOperation: "+e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}


}
