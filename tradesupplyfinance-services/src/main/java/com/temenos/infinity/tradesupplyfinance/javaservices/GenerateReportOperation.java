/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableSingleBillBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.constants.GeneratedFileDetailsEnum;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import com.temenos.infinity.tradesupplyfinance.utils.PdfContentStyle;
import com.temenos.infinity.tradesupplyfinance.utils.PdfGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum.ERR_30009;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.PdfGenerator.FontStyle.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.*;

/**
 * @author k.meiyazhagan
 */
public class GenerateReportOperation implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(GenerateReportOperation.class);
    private String orderId;
    private DataControllerRequest request;
    private String prefix;
    private String pdfTitle;
    private LinkedHashMap<PdfContentStyle, PdfContentStyle> reportData;

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();
        this.request = request;
        this.orderId = request.getParameter("orderId");
        if (StringUtils.isBlank(orderId)) {
            LOG.error("Input params are missing");
            return ErrorCodeEnum.ERR_30004.setErrorCode(result);
        }

        try {
            this.retrieveData(methodId);
            result.addParam(PARAM_FILE_ID, PdfGenerator.generatePdf(request, prefix, pdfTitle, reportData));
        } catch (Exception e) {
            LOG.error("Error generating trade finance report", e);
            result.addErrMsgParam(ERR_30009.getErrorMessage());
        }
        return result;
    }

    private void retrieveData(String methodId) {
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);
        try {
            if (METHODID_GENERATERECEIVABLESINGLEBILLREPORT.equals(methodId)) {
                pdfTitle = MODULE_RECEIVABLE_SINGLE_BILLS;
                prefix = GeneratedFileDetailsEnum.RSBA.name();
                ReceivableSingleBillDTO billDto = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ReceivableSingleBillBusinessDelegate.class).getSingleBillById(orderId, this.request);
                reportData = new LinkedHashMap<>();
                reportData.put(new PdfContentStyle("Bill Overview", HEADING), null);
                reportData.put(new PdfContentStyle("Bill Reference:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBillReference()));
                reportData.put(new PdfContentStyle("Updated On:", REGULAR_LIGHT), new PdfContentStyle(billDto.getUpdatedOnFormatted()));
                reportData.put(new PdfContentStyle("Status:", REGULAR_LIGHT), new PdfContentStyle(billDto.getStatus(), REGULARBOLD_DARK));
                if (StringUtils.isNotBlank(billDto.getReasonForRejection()))
                    reportData.put(new PdfContentStyle("Reason for Rejection:", REGULAR_LIGHT), new PdfContentStyle(billDto.getReasonForRejection(), REGULARBOLD_DARK));
                if (StringUtils.isNotBlank(billDto.getReasonForReturn()))
                    reportData.put(new PdfContentStyle("Reason for Returned:", REGULAR_LIGHT), new PdfContentStyle(billDto.getReasonForReturn(), REGULARBOLD_DARK));
                reportData.put(new PdfContentStyle("Bill Details", HEADING), null);
                reportData.put(new PdfContentStyle("Bill Name (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getBillName()));
                reportData.put(new PdfContentStyle("Bill Number:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBillNumber()));
                reportData.put(new PdfContentStyle("Bill Date:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBillDateFormatted()));
                reportData.put(new PdfContentStyle("Bill Type:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBillType()));
                reportData.put(new PdfContentStyle("Due Date:", REGULAR_LIGHT), new PdfContentStyle(billDto.getDueDateFormatted()));
                reportData.put(new PdfContentStyle("Payment Terms:", REGULAR_LIGHT), new PdfContentStyle(billDto.getPaymentTerms()));
                reportData.put(new PdfContentStyle("Amount & Account", HEADING), null);
                reportData.put(new PdfContentStyle("Amount:", REGULAR_LIGHT), new PdfContentStyle(getAmountWithCurrency(billDto.getCurrency(), billDto.getAmount())));
                reportData.put(new PdfContentStyle("Account Receivable:", REGULAR_LIGHT), new PdfContentStyle(getMaskedAccountDetails(customerId, billDto.getReceivableAccount())));
                reportData.put(new PdfContentStyle("Need Finance:", REGULAR_LIGHT), new PdfContentStyle(billDto.getRequestFinance()));
                reportData.put(new PdfContentStyle("Buyer", HEADING), null);
                reportData.put(new PdfContentStyle("Buyer Name:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBuyerName()));
                reportData.put(new PdfContentStyle("Buyer Address:", REGULAR_LIGHT), new PdfContentStyle(billDto.getBuyerAddress()));
                reportData.put(new PdfContentStyle("Goods & Shipment", HEADING), null);
                reportData.put(new PdfContentStyle("Goods Description:", REGULAR_LIGHT), new PdfContentStyle(billDto.getGoodsDescription()));
                reportData.put(new PdfContentStyle("Shipment Date:", REGULAR_LIGHT), new PdfContentStyle(billDto.getShipmentDateFormatted()));
                reportData.put(new PdfContentStyle("Shipment & Tracking:", REGULAR_LIGHT), new PdfContentStyle(billDto.getShipmentTrackingDetails()));
                reportData.put(new PdfContentStyle("Country of Origin:", REGULAR_LIGHT), new PdfContentStyle(billDto.getCountryOfOrigin()));
                reportData.put(new PdfContentStyle("Country of Destination:", REGULAR_LIGHT), new PdfContentStyle(billDto.getCountryOfDestination()));
                reportData.put(new PdfContentStyle("Mode of Shipment (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getModeOfShipment()));
                reportData.put(new PdfContentStyle("Port of Loading (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getPortOfLoading()));
                reportData.put(new PdfContentStyle("Port of Discharge (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getPortOfDischarge()));
                reportData.put(new PdfContentStyle("Final Destination (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getFinalDestination()));
                reportData.put(new PdfContentStyle("Documents & Message to Bank", HEADING), null);
                reportData.put(new PdfContentStyle("Uploaded Documents:", REGULAR_LIGHT), new PdfContentStyle(formatDocumentsList(billDto.getUploadedDocuments(), "\n")));
                reportData.put(new PdfContentStyle("Message to Bank (Optional):", REGULAR_LIGHT), new PdfContentStyle(billDto.getMessageToBank()));
            }
        } catch (Exception e) {
            LOG.error("Failed to fetch details", e);
            reportData = null;
        }
    }
}
