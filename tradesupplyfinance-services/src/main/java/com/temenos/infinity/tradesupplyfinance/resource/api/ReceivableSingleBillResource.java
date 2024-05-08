/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;

/**
 * @author k.meiyazhagan
 */
public interface ReceivableSingleBillResource extends Resource {

    Result saveSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result deleteSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result reviseSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result updateSingleBillByBank(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result requestSingleBillCancellation(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    Result getSingleBillById(DataControllerRequest request);

    Result getSingleBills(TsfFilterDTO filterDto, DataControllerRequest request);
}
