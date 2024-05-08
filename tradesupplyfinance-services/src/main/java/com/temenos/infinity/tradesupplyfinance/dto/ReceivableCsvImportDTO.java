/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.formatDate;

/**
 * @author k.meiyazhagan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ReceivableCsvImportDTO implements DBPDTO {

    private String batchReferences;
    private String billReferences;
    @JsonIgnore
    private JSONArray bills;
    private String counts;
    private String createdOn;
    @JsonIgnore
    private String createdOnFormatted;
    private String fileDate;
    @JsonIgnore
    private String fileDateFormatted;
    private String fileReference;
    private String fileType;
    private String status;
    @JsonAlias({PARAM_LASTUPDATEDTIMESTAMP})
    private String updatedOn;
    @JsonIgnore
    private String updatedOnFormatted;
    private String dbpErrCode;
    private String dbpErrMsg;


    public String getBatchReferences() {
        return batchReferences;
    }

    public void setBatchReferences(String batchReferences) {
        this.batchReferences = new JSONObject(batchReferences).toString();
    }

    public String getBillReferences() {
        return billReferences;
    }

    public void setBillReferences(String billReferences) {
        this.billReferences = new JSONObject(billReferences).toString();
    }

    public JSONArray getBills() {
        return bills;
    }

    public void setBills(JSONArray bills) {
        this.bills = bills;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
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

    public void setCreatedOnFormatted(String createdOnFormatted) {
        this.createdOnFormatted = createdOnFormatted;
    }

    public String getFileDate() {
        return createdOn;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public String getFileDateFormatted() {
        return formatDate(fileDate, UTC_DATE_FORMAT, DISPLAY_DATE_FORMAT);
    }

    public void setFileDateFormatted(String fileDateFormatted) {
        this.fileDateFormatted = fileDateFormatted;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void setUpdatedOnFormatted(String updatedOnFormatted) {
        this.updatedOnFormatted = updatedOnFormatted;
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
