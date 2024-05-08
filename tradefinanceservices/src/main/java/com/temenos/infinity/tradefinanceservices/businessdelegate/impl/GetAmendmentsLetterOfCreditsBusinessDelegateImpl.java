/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.impl;

import java.util.List;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetAmendmentsLetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetAmendmentsLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.utils.ExcelBusinessDelegate;

public class GetAmendmentsLetterOfCreditsBusinessDelegateImpl implements GetAmendmentsLetterOfCreditsBusinessDelegate, ExcelBusinessDelegate {

	@Override
	public List<LetterOfCreditsDTO> getAmendLetterOfCredits(LetterOfCreditsDTO letterOfCreditsDTO,
			DataControllerRequest request) throws ApplicationException {

		GetAmendmentsLetterOfCreditsBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(GetAmendmentsLetterOfCreditsBackendDelegate.class);
		List<LetterOfCreditsDTO> letterOfCredits = orderBackendDelegate
				.getamendLetterOfCreditsFromSRMS(letterOfCreditsDTO, request);
		return letterOfCredits;
	}

	public LetterOfCreditsDTO getAmendmentsById(String amendmentReference, DataControllerRequest request) {

		GetAmendmentsLetterOfCreditsBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(GetAmendmentsLetterOfCreditsBackendDelegate.class);

		return backendDelegate.getAmendmentsById(amendmentReference, request);
	}

	@Override
	public List<LetterOfCreditsDTO> getList(DataControllerRequest request) throws ApplicationException {
		GetAmendmentsLetterOfCreditsBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(GetAmendmentsLetterOfCreditsBackendDelegate.class);
		return orderBackendDelegate
				.getamendLetterOfCreditsFromSRMS(null, request);
	}
}
