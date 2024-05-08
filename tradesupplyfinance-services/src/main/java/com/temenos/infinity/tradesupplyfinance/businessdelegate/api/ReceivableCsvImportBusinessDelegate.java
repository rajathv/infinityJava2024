/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableCsvImportDTO;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public interface ReceivableCsvImportBusinessDelegate extends BusinessDelegate {

    ReceivableCsvImportDTO createCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request);

    ReceivableCsvImportDTO updateCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request);

    List<ReceivableCsvImportDTO> getCsvImports(DataControllerRequest request);

    ReceivableCsvImportDTO getCsvImportById(String fileReference, DataControllerRequest request);

}
