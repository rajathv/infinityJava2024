/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.utils;

import com.temenos.infinity.tradesupplyfinance.businessdelegate.impl.ReceivableSingleBillBusinessDelegateImpl;
import com.temenos.infinity.tradesupplyfinance.constants.GeneratedFileDetailsEnum;

/**
 * @author k.meiyazhagan
 */
public enum ExportListEnum {

    GENERATERECEIVABLESINGLEBILLSLIST(
            GeneratedFileDetailsEnum.RSBB.name(),
            new String[]{"Bill Reference", "Updated on", "Status", "Bill Name (optional)", "Bill Number", "Bill Date", "Bill Type", "Due Date", "Payment Terms", "Currency", "Amount", "Account Receivable", "Need Finance", "Buyer Name", "Buyer Address",
                    "Goods Description", "Shipment Date", "Shipment Details & Tracking", "Country of Origin", "Country of Destination", "Mode of Shipment (Optional)", "Port of Loading (Optional)", "Port of Discharge (Optional)", "Final Destination (Optional)", "Uploaded Documents", "Message to Bank (Optional)"},
            new String[]{"billReference", "updatedOnFormatted", "status", "billName", "billNumber", "billDateFormatted", "billType", "dueDateFormatted", "paymentTerms", "currency", "amountFormatted", "receivableAccount", "requestFinance", "buyerName", "buyerAddress",
                    "goodsDescription", "shipmentDateFormatted", "shipmentTrackingDetails", "countryOfOrigin", "countryOfDestination", "modeOfShipment", "portOfLoading", "portOfDischarge", "finalDestination", "uploadedDocumentsFormatted", "messageToBank"},
            ReceivableSingleBillBusinessDelegateImpl.class);

    private final String prefix;
    private final String[] headersList;
    private final String[] fieldsList;
    private final Class<? extends ExportListBusinessDelegate> businessDelegateImplName;

    ExportListEnum(String prefix, String[] headersList, String[] fieldsList, Class<? extends ExportListBusinessDelegate> businessDelegateImplName) {
        this.prefix = prefix;
        this.fieldsList = fieldsList;
        this.headersList = headersList;
        this.businessDelegateImplName = businessDelegateImplName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String[] getHeadersList() {
        return headersList;
    }

    public String[] getFieldsList() {
        return fieldsList;
    }

    public Class<? extends ExportListBusinessDelegate> getBusinessDelegateImplName() {
        return businessDelegateImplName;
    }
}
