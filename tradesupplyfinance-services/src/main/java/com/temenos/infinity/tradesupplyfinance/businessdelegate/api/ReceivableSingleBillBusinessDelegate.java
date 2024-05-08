/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public interface ReceivableSingleBillBusinessDelegate extends BusinessDelegate {

    /**
     * Used to create a receivable single bill.
     *
     * @param inputDto
     * @param request
     * @return
     */
    ReceivableSingleBillDTO createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    /**
     * Used to update the receivable single bill.
     *
     * @param inputDto
     * @param request
     * @return
     */
    ReceivableSingleBillDTO updateSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request);

    /**
     * Used to get the list of receivable bills.
     *
     * @param request
     * @return
     */
    List<ReceivableSingleBillDTO> getSingleBills(DataControllerRequest request);

    /**
     * Used to get the detailed details of receivable single bill.
     *
     * @param billReference
     * @param request
     * @return
     */
    ReceivableSingleBillDTO getSingleBillById(String billReference, DataControllerRequest request);

}
