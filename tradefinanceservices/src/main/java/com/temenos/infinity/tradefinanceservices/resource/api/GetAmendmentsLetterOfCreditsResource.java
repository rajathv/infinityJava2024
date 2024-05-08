/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public interface GetAmendmentsLetterOfCreditsResource extends Resource {

	Result getAmendLetterOfCredits(Object[] inputArray, LetterOfCreditsDTO letterOfCredits,
			DataControllerRequest request);

	Result getAmendmentsById(Object[] inputArray, DataControllerRequest request);

}
