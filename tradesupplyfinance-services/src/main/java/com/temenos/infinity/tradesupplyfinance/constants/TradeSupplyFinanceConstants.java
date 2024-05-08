/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.constants;

/**
 * @author k.meiyazhagan
 */
public interface TradeSupplyFinanceConstants {

    String PARAM_PROPERTY = "OMSTradeSupplyFinance";
    String PARAM_CUSTOMER_ID = "customerId";
    String PARAM_ORDER_ID = "orderId";
    String PARAM_PARTY_ID = "partyId";
    String PARAM_TYPE = "type";
    String PARAM_subType = "subType";
    String PARAM_subtype = "subtype";
    String PARAM_PAGE_SIZE = "pageSize";
    String DEFAULT_PAGE_SIZE = "100000";
    String PARAM_REQUEST_BODY = "requestBody";
    String PARAM_SRMSID = "srmsId";
    String PARAM_SERVICE_REQUESTS = "serviceReqs";
    String PARAM_SERVICE_REQ_ID = "serviceReqId";
    String PARAM_SERVICE_REQUEST_ID = "serviceRequestId";
    String PARAM_SERVICE_REQUEST_SRMS_ID = "serviceRequestSrmsId";
    String PARAM_SERVICE_REQUEST_IDS = "serviceRequestIds";
    String PARAM_SERVICE_REQ_REQUEST_IN = "serviceReqRequestIn";
    String PARAM_ORDER_PROCESSED_TIME = "serviceReqProcessedTime";
    String PARAM_LASTUPDATEDTIMESTAMP = "lastUpdatedTimeStamp";

    String TRADESUPPLYFINANCE_PAYEE_VIEW = "CORPORATE_RECIPIENT_VIEW";

    String PARAM_ALERT_NAME = "alertName";
    String PARAM_ALERT_DATA = "alertData";

    String PARAM_FILE_ID = "fileId";
    String PARAM_BILLS = "bills";
    String PARAM_LOOP_COUNT = "loop_count";
    String PARAM_FILE_REFERENCE = "fileReference";
    String PARAM_BILL_REFERENCE = "billReference";
    String SEPARATOR_BILLS_ORCHESTRATION = ",‽";

    String MODULE_RECEIVABLE_SINGLE_BILLS = "Receivable Financing - Single Application";
    String PREFIX_TRADESUPPLYFINANCE_DOCUMENT = "TSFD";
    String METHODID_GENERATERECEIVABLESINGLEBILLREPORT = "GenerateReceivableSingleBillReport";

    String HTTP_HEADER_X_KONY_AUTHORIZATION = "X-Kony-Authorization";
    String HTTP_HEADER_X_KONY_REPORTING_PARAMS = "X-Kony-ReportingParams";
    String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    String HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    int PARAM_MAX_RETURNS = 5;
    int PARAM_MAX_CSV_RECORDS = 50;

    String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String UTC_DATE_FORMAT = "yyyy-MM-dd";
    String DISPLAY_DATE_FORMAT = "MM/dd/yyyy";

    String MESSAGE = "message";
    String BACKEND_MESSAGE = "backendMessage";
    String PARAM_DBP_ERR_CODE = "dbpErrCode";
    String PARAM_DBP_ERR_MSG = "dbpErrMsg";
    String PARAM_DOCUMENT_NAME = "documentName";
    String PARAM_DOCUMENT_REFERENCE = "documentReference";
    String PARAM_ORIGIN_SINGLE = "Single";
    String PARAM_OPTION_YES = "YES";
    String PARAM_OPTION_NO = "NO";
    String PARAM_OPTION_MANUAL = "MANUAL";
    String PARAM_OPTION_EXISTING = "EXISTING";
    String PARAM_MODE_SEA = "SEA";
    String PARAM_MODE_ROAD = "ROAD";
    String PARAM_MODE_AIR = "AIR";

    String PAYLOAD_EMPTY_CSV_BILL = "{'amount':'','receivableAccount':'','billType':'','dueDate':'','finalDestination':'','shipmentTrackingDetails':'','countryOfDestination':'','portOfDischarge':'','billDate':'','requestFinance':'','modeOfShipment':'','buyerId':'','buyerName':'','billName':'','buyerAddress':'','messageToBank':'','currency':'','countryOfOrigin':'','shipmentDate':'','billNumber':'','portOfLoading':'','goodsDescription':'','paymentTerms':''}";
}
