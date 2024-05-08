/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public interface ReceivableSingleBillBackendDelegate extends BackendDelegate {
    ReceivableSingleBillDTO createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    ReceivableSingleBillDTO updateSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    List<ReceivableSingleBillDTO> getSingleBills(DataControllerRequest request);

    ReceivableSingleBillDTO getSingleBillById(String billReference, DataControllerRequest request);
}
