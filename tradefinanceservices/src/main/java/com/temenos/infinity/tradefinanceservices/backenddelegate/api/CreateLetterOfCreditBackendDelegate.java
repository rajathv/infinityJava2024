package com.temenos.infinity.tradefinanceservices.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

/**
 * The Interface CreateLetterOfCreditBackendDelegate.
 */
public interface CreateLetterOfCreditBackendDelegate extends BackendDelegate {

    /**
     * Creates the letter of credits order.
     *
     * @param letterOfCredit the letter of credit
     * @param request        the request
     * @return the letter of credits DTO
     * @throws ApplicationException the application exception
     */
    LetterOfCreditsDTO createLetterOfCreditsOrder(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request)
            throws ApplicationException;

    LetterOfCreditsDTO amendLetterOfCredit(LetterOfCreditsDTO letterOfCredits, DataControllerRequest request)
            throws ApplicationException;

    LetterOfCreditsDTO updateAmendLC(LetterOfCreditsDTO updateLCDTObyid, DataControllerRequest request);

}
