/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.*;

/**
 * @author k.meiyazhagan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ReceivableSingleBillDTO implements DBPDTO {
    private String amount;
    @JsonIgnore
    private String amountFormatted;
    private String billDate;
    @JsonIgnore
    private String billDateFormatted;
    private String billName;
    private String billNumber;
    @JsonAlias({PARAM_SRMSID})
    private String billReference;
    private String billType;
    private String buyerAddress;
    private String buyerId;
    private String buyerName;
    private String buyerSelection;
    private String cancellationDocuments;
    private String cancellationStatus;
    private String countryOfOrigin;
    private String countryOfDestination;
    private String createdOn;
    @JsonIgnore
    private String createdOnFormatted;
    private String currency;
    private String dueDate;
    @JsonIgnore
    private String dueDateFormatted;
    private String finalDestination;
    private String fileReference;
    private String goodsDescription;
    private String messageToBank;
    private String modeOfShipment;
    private String origin;
    private String paymentTerms;
    private String portOfLoading;
    private String portOfDischarge;
    private String reasonForCancellation;
    private String reasonForRejection;
    private String reasonForReturn;
    private String returnedHistory;
    private String receivableAccount;
    private String requestFinance;
    @JsonIgnore
    private String serviceRequestSrmsId;
    private String shipmentDate;
    @JsonIgnore
    private String shipmentDateFormatted;
    private String shipmentTrackingDetails;
    private String status;
    private String uploadedDocuments;
    @JsonIgnore
    private String uploadedDocumentsFormatted;
    @JsonAlias({PARAM_LASTUPDATEDTIMESTAMP})
    private String updatedOn;
    @JsonIgnore
    private String updatedOnFormatted;
    private String dbpErrCode;
    private String dbpErrMsg;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountFormatted() {
        return formatAmount(amount);
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getBillDateFormatted() {
        return formatDate(billDate, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getBillReference() {
        return billReference;
    }

    public void setBillReference(String billReference) {
        this.billReference = billReference;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerSelection() {
        return buyerSelection;
    }

    public void setBuyerSelection(String buyerSelection) {
        this.buyerSelection = buyerSelection;
    }

    public String getCancellationDocuments() {
        return cancellationDocuments;
    }

    public void setCancellationDocuments(String cancellationDocuments) {
        this.cancellationDocuments = new JSONArray(cancellationDocuments).toString();
    }

    public String getCancellationStatus() {
        return cancellationStatus;
    }

    public void setCancellationStatus(String cancellationStatus) {
        this.cancellationStatus = cancellationStatus;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public String getCountryOfDestination() {
        return countryOfDestination;
    }

    public void setCountryOfDestination(String countryOfDestination) {
        this.countryOfDestination = countryOfDestination;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedOnFormatted() {
        return formatDate(createdOn, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateFormatted() {
        return formatDate(dueDate, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public String getFinalDestination() {
        return finalDestination;
    }

    public String getFileReference() {
        return StringUtils.isNotBlank(fileReference) ? fileReference : "";
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public void setFinalDestination(String finalDestination) {
        this.finalDestination = finalDestination;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
    }

    public String getMessageToBank() {
        return messageToBank;
    }

    public void setMessageToBank(String messageToBank) {
        this.messageToBank = messageToBank;
    }

    public String getModeOfShipment() {
        return modeOfShipment;
    }

    public void setModeOfShipment(String modeOfShipment) {
        this.modeOfShipment = modeOfShipment;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    public void setReasonForCancellation(String reasonForCancellation) {
        this.reasonForCancellation = reasonForCancellation;
    }

    public String getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(String reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public String getReasonForReturn() {
        return reasonForReturn;
    }

    public void setReasonForReturn(String reasonForReturn) {
        this.reasonForReturn = reasonForReturn;
    }

    public String getReturnedHistory() {
        return returnedHistory;
    }

    public void setReturnedHistory(String returnedHistory) {
        this.returnedHistory = new JSONArray(returnedHistory).toString();
    }

    public String getReceivableAccount() {
        return receivableAccount;
    }

    public void setReceivableAccount(String receivableAccount) {
        this.receivableAccount = receivableAccount;
    }

    public String getRequestFinance() {
        return requestFinance;
    }

    public void setRequestFinance(String requestFinance) {
        this.requestFinance = requestFinance;
    }

    public String getServiceRequestSrmsId() {
        return billReference;
    }

    public String getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(String shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public String getShipmentDateFormatted() {
        return formatDate(shipmentDate, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public String getShipmentTrackingDetails() {
        return shipmentTrackingDetails;
    }

    public void setShipmentTrackingDetails(String shipmentTrackingDetails) {
        this.shipmentTrackingDetails = shipmentTrackingDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUploadedDocuments() {
        return uploadedDocuments;
    }

    public void setUploadedDocuments(String uploadedDocuments) {
        this.uploadedDocuments = new JSONArray(uploadedDocuments).toString();
    }

    public String getUploadedDocumentsFormatted() {
        return formatDocumentsList(uploadedDocuments, ", ");
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedOnFormatted() {
        return formatDate(updatedOn, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public String getDbpErrCode() {
        return dbpErrCode;
    }

    public void setDbpErrCode(String dbpErrCode) {
        this.dbpErrCode = dbpErrCode;
    }

    public String getDbpErrMsg() {
        return dbpErrMsg;
    }

    public void setDbpErrMsg(String dbpErrMsg) {
        this.dbpErrMsg = dbpErrMsg;
    }
}
