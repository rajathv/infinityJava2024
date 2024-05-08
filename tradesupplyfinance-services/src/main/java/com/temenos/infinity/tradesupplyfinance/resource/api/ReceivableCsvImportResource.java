/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableCsvImportDTO;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;

/**
 * @author k.meiyazhagan
 */
public interface ReceivableCsvImportResource extends Resource {

    Result createBillsCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request);

    Result updateCsvImportBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);
    Result updateCsvImport(DataControllerRequest request);

    Result getCsvImportById(DataControllerRequest request);

    Result getCsvImports(TsfFilterDTO filterDto, DataControllerRequest request);

    Result deleteImportedBills(DataControllerRequest request);
}
