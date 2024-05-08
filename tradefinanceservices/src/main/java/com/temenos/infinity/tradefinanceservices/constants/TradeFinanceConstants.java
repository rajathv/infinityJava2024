package com.temenos.infinity.tradefinanceservices.constants;

import java.awt.Color;

public interface TradeFinanceConstants {
    String PARAM_CUSTOMER_ID = "customerId";
    String PARAM_ORDER_ID = "orderId";
    String PARAM_ORDER_STATUS = "status";
    String PARAM_PROPERTY = "OMSTradeFinance";
    String PARAM_REQUEST_ID = "requestId";
    String PARAM_REQUEST_BODY = "requestBody";
    String PARAM_TRANS_ID = "serviceReqId";
    String PARAM_PARTY_ID = "partyId";
    String PARAM_REFERENCE_ID = "srmsReqOrderID";
    String PARAM_REQUEST_DATE_TIME = "requestCreatedTime";

    String PARAM_SERVICE_REQUESTS = "serviceReqs";
    String PARAM_INPUT_PAYLOAD = "serviceReqRequestIn";
    String PARAM_SERVICE_REQ_REQUEST_IN = "serviceReqRequestIn";
    String PARAM_PAGE_SIZE = "pageSize";
    String DEFAULT_PAGE_SIZE = "100000";
    String PARAM_TYPE = "type";
    String PARAM_subType = "subType";
    String PARAM_subtype = "subtype";
    String PARAM_ORDER_PROCESSED_TIME = "serviceReqProcessedTime";
    String PARAM_RESPONSE_PAYLOAD = "serviceReqResponse";
    String PARAM_SERVICE_REQ_STATUS = "serviceReqStatus";
    String PARAM_SRMS_ReqOrderID = "srmsReqOrderID";
    String PARAM_SRMSID = "srmsId";
    String PARAM_SERVICE_REQ_ID = "serviceReqId";
    String PARAM_SERVICE_REQUEST_ID = "serviceRequestId";
    String PARAM_SERVICE_REQUEST_IDS = "serviceRequestIds";
    String PARAM_LASTUPDATEDTIMESTAMP = "lastUpdatedTimeStamp";
    String PARAM_ALERT_NAME = "alertName";
    String PARAM_ALERT_DATA = "alertData";
    String PARAM_DBP_ERR_CODE = "dbpErrCode";
    String PARAM_DBP_ERR_MSG = "dbpErrMsg";


    String PARAM_LC_REFERENCE_NO = "lcReferenceNo";
    String PARAM_LC_AMOUNT = "lcAmount";
    String PARAM_LC_CURRENCY = "lcCurrency";
    String PARAM_TOLERENCEPERCENTAGE = "tolerancePercentage";
    String PARAM_MAXIMUMCREDITAMOUNT = "maximumCreditAmount";
    String PARAM_ADDITIONALAMOUNTPAYABLE = "additionalAmountPayable";
    String PARAM_PAYMENTTERMS = "paymentTerms";
    String PARAM_AVAILABLEWITH1 = "availableWith1";
    String PARAM_AVAILABLEWITH2 = "availableWith2";
    String PARAM_AVAILABLEWITH3 = "availableWith3";
    String PARAM_AVAILABLEWITH4 = "availableWith4";
    String PARAM_ISSUEDATE = "issueDate";
    String PARAM_EXPIRYDATE = "expiryDate";
    String PARAM_EXPIRYPLACE = "expiryPlace";
    String PARAM_CHARGEAMOUNT = "chargesAccount";
    String PARAM_COMMISSIONACCOUNT = "commisionAccount";
    String PARAM_MARGINAMOUNT = "marginAccount";
    String PARAM_MESSAGETOBANK = "messageToBank";
    String PARAM_BENEFICIARYNAME = "beneficiaryName";
    String PARAM_BENEFICIARYADDRESSLINE1 = "beneficiaryAddressLine1";
    String PARAM_BENEFICIARYADDRESSLINE2 = "beneficiaryAddressLine2";
    String PARAM_BENEFICIARYPOSTCODE = "beneficiaryPostCode";
    String PARAM_BENEFICIARYCOUNTRY = "beneficiaryCountry";
    String PARAM_BENEFICIARYCITY = "beneficiaryCity";
    String PARAM_BENEFICIARYSTATE = "beneficiaryState";
    String PARAM_BENEFICIARYBANK = "beneficiaryBank";
    String PARAM_BENEFICIARY_BANK_ADDRESS_LINE1 = "beneficiaryBankAdressLine1";
    String PARAM_BENEFICIARY_BANK_ADDRESS_LINE2 = "beneficiaryBankAdressLine2";
    String PARAM_BENEFICIARY_BANK_POST_CODE = "beneficiaryBankPostCode";
    String PARAM_BENEFICIARY_BANK_COUNTRY = "beneficiaryBankCountry";
    String PARAM_BENEFICIARY_BANK_CITY = "beneficiaryBankCity";
    String PARAM_BENEFICIARY_BANK_STATE = "beneficiaryBankState";
    String PARAM_PLACE_OF_TAKING_INCHARGE = "placeOfTakingIncharge";
    String PARAM_PORT_OF_LOADING = "portOfLoading";
    String PARAM_PORT_OF_DISCHARGE = "portOfDischarge";
    String PARAM_PLACE_OF_FINAL_DELIVERY = "placeOfFinalDelivery";
    String PARAM_LATEST_SHIPPING_DATE = "latestShippingDate";
    String PARAM_PRESENTATION_PERIOD = "presentationPeriod";
    String PARAM_TRANS_SHIPMENT = "transshipment";
    String PARAM_PARTIAL_SHIPMENT = "partialShipments";
    String PARAM_INCO_TERMS = "incoTerms";
    String PARAM_MODE_OF_SHIPMENT = "modeOfShipment";
    String PARAM_DESCRIPTION_OF_GOODS = "descriptionOfGoods";
    String PARAM_DOCUMENTS_REQUIRED = "documentsRequired";
    String PARAM_ADDITIONAL_CONDITIONS_CODE = "additionalConditionsCode";
    String PARAM_OTHER_ADDITIONAL_CONDITIONS = "otherAdditionalConditions";
    String PARAM_DOCUMENT_CHARGES = "documentCharges";
    String PARAM_SUPPORT_DOCUMENTS = "supportDocuments";
    String PARAM_FILE_TO_UPLOAD = "fileToUpload";
    String PARAM_CONFIRMATION_INSTRUCTION = "confirmationInstruction";
    String PARAM_TRANSFERABLE = "transferable";
    String PARAM_STAND_BY_LC = "standByLC";
    String PARAM_IS_DRAFT = "isDraft";
    String PARAM_ADDITIONAL_PAYABLE_CURRENCY = "additionalPayableCurrency";
    String PARAM_LCTYPE = "lcType";
    String PARAM_LC_ISSUEDATE = "lcIssueDate";
    String PARAM_LC_EXPIRYDATE = "lcExpiryDate";
    String PARAM_DRAWING_REFERENCENO = "drawingReferenceNo";
    String PARAM_DOCUMENTSTATUS = "documentStatus";
    String PARAM_DRAWING_CREATIONDATE = "drawingCreationDate";
    String PARAM_DRAWING_CURRENCY = "drawingCurrency";
    String PARAM_DRAWING_AMOUNT = "drawingAmount";
    String PARAM_DRAWING_STATUS = "drawingStatus";
    String PARAM_PRESENTORREFERENCE = "presentorReference";
    String PARAM_PRESENTORNAME = "presentorName";
    String PARAM_DOCUMENTS_RECEIVED = "documentsReceived";
    String PARAM_FORWARDCONTACT = "forwardContact";
    String PARAM_SHIPPING_GUARANTEE_REFERENCE = "shippingGuaranteeReference";
    String PARAM_APPROVALDATE = "approvalDate";
    String PARAM_TOTALDOCUMENTS = "totalDocuments";
    String PARAM_DOCUMENTNAME = "documentName";
    String PARAM_DISCREPANCY_DESCRIPTION = "discrepancyDescription";
    String PARAM_PAYMENTSTATUS = "paymentStatus";
    String PARAM_REJECTEDDATE = "rejectedDate";
    String PARAM_TOTALAMOUNT_TO_BE_PAID = "totalAmountToBePaid";
    String PARAM_ACCOUNT_TO_BE_DEBITED = "accountToBeDebited";
    String PARAM_MESSAGEFROMBANK = "messageFromBank";
    String PARAM_TOTAL_PAID_AMOUNT = "totalPaidAmount";
    String PARAM_PAYMENTDATE = "paymentDate";
    String PARAM_REASON_FOR_REJECTION = "reasonForRejection";
    String PARAM_DISCREPANCIES = "discrepancies";
    String PARAM_ACCEPTANCE = "acceptance";
    String PARAM_MESSAGETYPE = "messageType";
    String PARAM_DELIVERYDESTINATION = "deliveryDestination";
    String PARAM_MESSAGEDATE = "messageDate";
    String PARAM_MESSAGECATEGORY = "messageCategory";
    String PARAM_LCSRMSREQORDERID = "lcSrmsReqOrderID";


    String PARAM_STATUS_PENDING_CONSENTS = "Pending Consents";
    String PARAM_STATUS_PENDING_CLOSURE = "Pending Closures";
    String PARAM_STATUS_PENDING_DRAFTS = "Pending Drafts";
    String PARAM_STATUS_RECENTLY_COMPLETED = "Recently Completed";
    String PARAM_STATUS_RECENTLY_REJECTED = "Recently Rejected";
    String PARAM_STATUS_DRAFT = "Draft";
    String PARAM_BANK_UPDATE = "BankUpdate";
    String PARAM_STATUS_APPROVED = "Approved";
    String PARAM_STATUS_DELETE = "Deleted";
    String PARAM_STATUS_NEW = "New";
    String PARAM_STATUS_PENDING_WITH_BANK = "Pending with Bank";
    String PARAM_STATUS_SETTLED = "Settled";
    String REJECTED_BY_BANK = "Rejected By Bank";
    String PARAM_YES = "Yes";
    String PARAM_NO = "No";
    String PARAM_APPROVED = "Approved";
    String PARAM_REJECTED = "Rejected";
    String PARAM_DISCREPANT = "Discrepant";
    String PARAM_CLEAN = "Clean";
    String PARAM_ACCOUNTID = "chargesAccount";
    String PARAM_STATUS_COMPLETED = "Completed";
    String PARAM_STATUS_PAID = "Paid";
    String PARAM_STATUS_DELETED = "Deleted";
    String PARAM_STATUS_SUBMITTED_TO_BANK = "Submitted to Bank";
    String PARAM_STATUS_RETURNED_BY_BANK = "Returned By Bank";
    String PARAM_STATUS_RETURNED_by_BANK = "Returned by Bank";
    String PARAM_STATUS_REJECTED_BY_BANK = "Rejected By Bank";
    String PARAM_STATUS_REJECTED_by_BANK = "Rejected by Bank";
    String PARAM_STATUS_PROCESSING_BY_BANK = "Processing By Bank";
    String PARAM_STATUS_PROCESSING_by_BANK = "Processing by Bank";
    String PARAM_STATUS_PROCESSING_WITH_BANK = "Processing with Bank";
    String PARAM_STATUS_CANCELLED = "Cancelled";
    String PARAM_STATUS_CANCELLATION_APPROVED = "Cancellation Approved";
    String PARAM_STATUS_REQUESTED = "Requested";
    String PARAM_STATUS_CLAIMHONOURED = "Claim Honoured";
    String PARAM_STATUS_CLAIMHONOURED_APPLICANTREJECTED = "Claim Honoured (Applicant Rejected)";
    String PARAM_STATUS_CLAIMHONOURED_PENDINGCONSENT = "Claim Honoured (Pending Consent)";
    String PARAM_STATUS_CLAIMEXTENDED = "Claim Extended";
    String PARAM_STATUS_CLAIMREJECTED = "Claim Rejected";
    String PARAM_STATUS_ACCEPTED = "Accepted";
    String PARAM_STATUS_REJECTED = "Rejected";
    String PARAM_STATUS_REJECT = "Reject";
    String PARAM_STATUS_PENDING = "Pending";
    String PARAM_STATUS_DONE = "Done";
    String PARAM_STATUS_OVERDUE = "Overdue";
    String PARAM_STATUS_PAYDUE = "Pay Due";
    String PARAM_PAYMENT_STATUS = "Payment Status";
    String PARAM_BILL_OF_EXCHANGE = "Bill of Exchange";
    String PARAM_STATUS_PARTIALLY_SETTLED = "Partially Settled";
    String PARAM_STATUS_CLOSED = "Closed";

    String PARAM_ACCEPTED_TO_PAY = "Accepted to Pay";
    String PARAM_ACCEPTED_TO_EXTEND = "Accepted to Extend";
    String PARAM_DEMAND = "Demand";
    String PARAM_PRESENTATION = "Presentation";
    String PARAM_PAY_OR_EXTEND = "Pay/Extend";
    String PARAM_PAY = "Pay";
    String PARAM_USANCE = "Usance";
    String PARAM_SIGHT = "Sight";
    String MESSAGE = "message";
    String BACKEND_MESSAGE = "backendMessage";


    long TOKEN_VALIDITY = 3600000;
    String DMS_BACKEND = "DMS";
    String ROLEID = "DocumentOwner";
    String USERNAME = "FABRICUSER";
    String BACKENDCERTNAME = "T24";
    String DMS_DOCUMENT_GROUP = "journey-retail-ABCDE";
    String DMS_OWNER_SYSTEM_ID = "corporate-los";


    String CREATERECEIVEDGUARANTEECLAIM_METHODID = "CreateClaimOnGuaranteeReceived";
    String SAVERECEIVEDGUARANTEECLAIM_METHODID = "SaveReceivedGuaranteeClaims";
    String UPDATERECEIVEDGUARANTEECLAIMBYBANK_METHODID = "UpdateReceivedGuaranteeClaimByBank";
    String UPDATERECEIVEDGUARANTEECLAIM_METHODID = "UpdateReceivedGuaranteeClaim";
    String CREATEGUARANTEES_METHODID = "createGuarantees";
    String SAVEGUARANTEES_METHODID = "saveGuarantees";
    String DELETEGUARANTEE_METHODID = "DeleteGuaranteeLetterOfCredit";
    String UPDATEGUARANTEE_METHODID = "UpdateGuaranteeLetterOfCredit";
    String DELETERECEIVEDGUARANTEECLAIM_METHODID = "DeleteReceivedGuaranteeClaim";
    String GENERATEOUTWARDCOLLECTIONREPORT_METHODID = "GenerateOutwardCollectionReport";
    String GENERATEOUTWARDAMENDMENTREPORT_METHODID = "GenerateOutwardAmendmentReport";

    String TRADEFINANCE_PAYEE_CREATE = "CORPORATE_RECIPIENT_CREATE";
    String TRADEFINANCE_PAYEE_VIEW = "CORPORATE_RECIPIENT_VIEW";


    String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String STANDARDTIMESTAMP_FORMAT = "MM/dd/yyyy";
    String DASHBOARD_DATE_FORMAT = "yyyy-MM-dd";

    String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    String HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    String HTTP_HEADER_X_KONY_AUTHORIZATION = "X-Kony-Authorization";
    String HTTP_HEADER_X_KONY_REPORTING_PARAMS = "X-Kony-ReportingParams";


    String MODULENAME_TRADEFINANCE = "Trade Finance";
    String MODULENAME_IMPORT_LOC = "Import Letter Of Credit";
    String MODULENAME_IMPORT_DRAWING = "Import Drawing";
    String MODULENAME_IMPORT_AMENDMENT = "Import Amendment";
    String MODULENAME_EXPORT_LOC = "Export Letter Of Credit";
    String MODULENAME_EXPORT_DRAWING = "Export Drawing";
    String MODULENAME_EXPORT_AMENDMENT = "Export LC Amendment";
    String MODULENAME_GUARANTEE_LOC = "Guarantee and Standby LC";
    String MODULENAME_GUARANTEE_AMENDMENT = "Guarantee Amendment";
    String MODULENAME_RECEIVED_GUARANTEE = "Received Guarantee";
    String MODULENAME_RECEIVED_GUARANTEE_AMENDMENT = "Received Guarantee Amendment";
    String MODULENAME_RECEIVED_GUARANTEE_CLAIM = "Received Guarantee Claim";
    String MODULENAME_CLAIMS_RECEIVED = "Claim Received";
    String MODULENAME_INWARD_COLLECTIONS = "Inward Collection";
    String MODULENAME_INWARD_COLLECTION_AMENDMENT = "Inward Collection Amendment";
    String MODULENAME_OUTWARD_COLLECTION = "Outward Collection";
    String MODULENAME_OUTWARD_COLLECTION_AMENDMENT = "Outward Collection Amendment";

    //Receivables
    String GUARANTEE_CLAIMS_GET = "GuaranteeClaims";
    String OUTWARD_COLLECTIONS_GET = "OutwardCollections";
    String EXPORT_LC_DRAWINGS_GET = "ExportLCDrawings";

    //Payables
    String CLAIMS_RECEIVED_GET = "ClaimsReceived";
    String IMPORT_DRAWINGS_GET = "drawings";
    String INWARD_COLLECTIONS_GET = "InwardCollections";

    String PREFIX_DOCUMENT = "TFD";
    String PREFIX_IMPORT_LOC = "IMLC";
    String PREFIX_IMPORT_DRAWING = "IMDR";
    String PREFIX_IMPORT_AMENDMENT = "IMAD";
    String PREFIX_EXPORT_LOC = "EXLC";
    String PREFIX_EXPORT_DRAWING = "EXDR";
    String PREFIX_EXPORT_AMENDMENT = "EXAM";
    String PREFIX_GUARANTEE_LOC = "GULC";
    String PREFIX_GUARANTEE_AMENDMENT = "GUAM";
    String PREFIX_RECEIVED_GUARANTEE = "RGUA";
    String PREFIX_RECEIVED_GUARANTEE_AMENDMENT = "RGAM";
    String PREFIX_RECEIVED_GUARANTEE_CLAIM = "RGCL";
    String PREFIX_ISSUED_GUARANTEE_CLAIM = "IGCL";
    String PREFIX_INWARD_COLLECTIONS = "INCL";
    String PREFIX_INWARD_COLLECTION_AMENDMENT = "INAM";
    String PREFIX_OUTWARD_COLLECTION = "OUCL";
    String PREFIX_OUTWARD_COLLECTION_AMENDMENT = "OUAM";


    Color lightFont = new Color(114, 114, 114);
    Color darkFont = new Color(66, 66, 66);
    Color darkerFont = new Color(0, 0, 0);
    Color blueFont = new Color(66, 135, 245);

}
