package com.temenos.infinity.api.cards.javaservice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.infinity.api.cards.resource.api.CardServicesResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2394
 * @version 1.0
 * Java Service end point to Apply For New Card
 */
public class ApplyForDebitCard implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(ActivateCardOperation.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			//Initializing of CardsResource through Abstract factory method
			CardServicesResource cardResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CardServicesResource.class);

			result  = cardResource.applyForDebitCard(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of ApplyForNewCard: "+e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}
}