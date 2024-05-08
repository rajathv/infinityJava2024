/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.api;

import java.util.List;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.ExportLCDrawingsDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public interface ExportLetterOfCreditsDrawingsBusinessDelegate extends BusinessDelegate {
    ExportLCDrawingsDTO createExportDrawing(ExportLCDrawingsDTO inputArray, DataControllerRequest request);

    boolean matchSRMSId(LetterOfCreditsDTO letterOfCredit, String customerId, DataControllerRequest request);

    ExportLCDrawingsDTO updateExportLetterOfCreditDrawing(ExportLCDrawingsDTO exportPayloadDTO,
                                                                 DataControllerRequest request);

    List<ExportLCDrawingsDTO> getExportLetterOfCreditDrawings(DataControllerRequest request);

    ExportLCDrawingsDTO getExportLetterOfCreditDrawingById(DataControllerRequest request,
                                                                  String drawingSRMSRequestId);

    byte[] generateExportDrawingPdf(ExportLCDrawingsDTO drawingDetails, DataControllerRequest request);

}
