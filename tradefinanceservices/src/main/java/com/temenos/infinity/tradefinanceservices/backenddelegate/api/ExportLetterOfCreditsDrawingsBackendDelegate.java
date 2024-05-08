/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.api;

import java.util.List;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.ExportLCDrawingsDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public interface ExportLetterOfCreditsDrawingsBackendDelegate extends BackendDelegate {
    public ExportLCDrawingsDTO createExportDrawing(ExportLCDrawingsDTO inputArray, DataControllerRequest request);

    public boolean matchSRMSId(LetterOfCreditsDTO letterOfCredit, String customerId, DataControllerRequest request);

    public ExportLCDrawingsDTO updateExportLetterOfCreditDrawing(ExportLCDrawingsDTO exportPayloadDTO,
                                                                 DataControllerRequest request);

    public ExportLCDrawingsDTO getExportLetterOfCreditDrawingById(DataControllerRequest request,
                                                                  String drawingSRMSRequestId);

    public List<ExportLCDrawingsDTO> getExportLetterOfCreditDrawings(DataControllerRequest request);

}
