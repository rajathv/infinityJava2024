/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;

public interface DownloadGuaranteesSwiftPdfBusinessDelegate extends BusinessDelegate {
    byte[] generateGuaranteesSwiftPdf(GuranteesDTO guaranteesDTO, DataControllerRequest request);

}
