package com.temenos.infinity.tradefinanceservices.businessdelegate.impl;

import java.util.List;
import com.temenos.infinity.tradefinanceservices.utils.ExcelBusinessDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetLetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

/**
 *
 */
public class GetLetterOfCreditsBusinessDelegateImpl implements GetLetterOfCreditsBusinessDelegate, ExcelBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsBusinessDelegateImpl.class);
    GetLetterOfCreditsBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
            .getBackendDelegate(GetLetterOfCreditsBackendDelegate.class);
    @Override
    public List<LetterOfCreditsDTO> getLetterOfCredits(LetterOfCreditsDTO letterOfCreditsDTO, DataControllerRequest request)
            throws ApplicationException {

        List<LetterOfCreditsDTO> letterOfCredits = orderBackendDelegate.getLetterOfCreditsFromSRMS(letterOfCreditsDTO, request); 
        return letterOfCredits;  
    }

    @Override
    public List<LetterOfCreditsDTO> getList(DataControllerRequest request) throws ApplicationException {
        return  orderBackendDelegate.getLetterOfCreditsFromSRMS(null, request);
    }
}
