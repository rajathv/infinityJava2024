/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.api;

import java.util.List;

import com.dbp.core.api.BackendDelegate;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public interface GetAmendmentsLetterOfCreditsBackendDelegate extends BackendDelegate {

	List<LetterOfCreditsDTO> getamendLetterOfCreditsFromSRMS(LetterOfCreditsDTO letterOfCreditsDTO,
			DataControllerRequest request) throws ApplicationException;

	public LetterOfCreditsDTO getAmendmentsById(String amendmentReference, DataControllerRequest request);

}
