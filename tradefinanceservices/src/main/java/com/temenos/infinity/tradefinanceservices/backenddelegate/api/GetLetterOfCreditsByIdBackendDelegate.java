package com.temenos.infinity.tradefinanceservices.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public interface GetLetterOfCreditsByIdBackendDelegate extends BackendDelegate{
	LetterOfCreditsDTO getSRMSID(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request)
            throws ApplicationException;
}
