package com.temenos.infinity.tradefinanceservices.constants;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;

public interface Constants {

    String SCHEMA_NAME = EnvironmentConfigurationsHandler.getValue("DBX_SCHEMA_NAME");


    String GET_LOC_FEATURE_ACTION_ID = "IMPORT_LC_VIEW";
    String CREATE_LOC_FEATURE_ACTION_ID = "IMPORT_LC_CREATE";
    String DELETE_LOC_FEATURE_ACTION_ID = "IMPORT_LC_DELETE";
    String VIEW_LC_DRAWINGS_FEATURE_ACTION_ID = "IMPORT_LC_DRAWINGS_VIEW";
    String SUBMIT_LC_DRAWINGS_FEATURE_ACTION_ID = "IMPORT_LC_DRAWINGS_SUBMIT";


    String DBP_APPROVAL_REQUEST_SERVICES = "dbpApprovalRequestServices";
    String DBPRBLOCALSERVICEDB = "dbpRbLocalServicesdb";
    String VALIDATE_FOR_APPROVALS = "ValidateForApprovals";
    String UPDATE_BACKENDID_IN_APPROVALQUEUE = "UpdateBackendIdInApprovalQueue";
    String DB_BBREQUEST_GET = SCHEMA_NAME + "_bbrequest_get";
    String DB_BBREQUEST_DELETE = SCHEMA_NAME + "_bbrequest_delete";
    String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    String SENT = "sent";

    String FETCH_CUSTOMER_ID_FAILED = "Failed to fetch Customer ID";
    String FETCH_FEATURES_FAILED = "Failed to Fetch Features";
    String USER_UNAUTHORIZED = "Logged in User is not authorized to perform this action";
    String VALIDATION_FAILED = "Input Validation Failed";
    String USER_ACCESS_FOR_THIS_ACTION_FAILED = "User doesn't have permission for this Action";
    String UNABLE_TO_CREATE_LOC_REQUEST = "Unable to create letter of credits request";
    String UNABLE_TO_GET_LOC_REQUEST = "Unable to Fetch letter of credits requests";
    String UNABLE_TO_UPDATE_LOC = "Unable to Update letter of credits requests";
    String FAILED_TO_UPDATE_LOC = "Failed to Update letter of credits";
    String MANDATORY_FIELDS_MISSING = "Mandatory fields are missing";
    String FETCH_SRMSID_FAILED = "SRMSID fetch failed";
    String SRMSID_NOT_FOUND_IN_RESPONSE = "Unable to create order. Failed to find service request id in Response";
    String FETCH_LOC_FAILED = "Failed to fetch letter of credits";
    String NO_LOC_AVAILABLE_BETWEEN_RANGE = "No letter of credits available between entered range";
    String RECORD_NOT_FOUND = "Record for the specified SRMSID not found";
    String INITIATE_TRADE_FINANCE_DOWNLOAD_FAILED = "Error occurred while initiating the trade finance download";
    String PROVIDE_MANDATORY_FIELDS = "Please provide mandatory fields";
    String ERROR_WHILE_GENERATING_REPORT = "Something went wrong while generating report";
    String TRADE_FINANCE_FILE_GENERATION_FAILED = "Error occurred while generating the trade finance file";
    String FILE_ID_MISSING = "FileId is missing";
    String DOWNLOAD_ERROR = "Error while downloading the TradeFinance Acknowledgment pdf";
    String XLSX_DOWNLOAD_ERROR = "Error while downloading the TradeFinance Acknowledgment Xlsx";
    String DATE_VALIDATION_FAILED = "Date Validation Failed";
    String UNABLE_TO_GET_DRAWING_REQUEST = "Unable to fetch drawing request";
    String FETCH_DRAWING_FAILED = "Failed to fetch drawing";
    String FAILED_TO_CREATE_LOC_DRAWINGS = "Unable to create Letter of Credits Drawings";
    String FAILED_TO_CREATE_LOC_SWIFT_AND_ADVICES = "Unable to create Letter of Credits Swifts And Advices";
    String UNABLE_TO_GETBYID_LOC = "Error while Get LOC Call by ID";
    String FAILED_TO_UPDATE_DRAWING = "Updation of Drawing record has Failed";
    String REQUESTED_DRAWING_COMPLETED = "Requested Drawing is already in Completed status,Please provide valid details.";
    String VALID_STATUS = "Please enter Valid Drawing Status";
    String FAILED_TO_GET_SWIFT_AND_ADVICES = "Failed to fetch Swift And Advices";
    String SECURITY_EXCEPTION_UNAUTHORIZED_ACCESS = "SECURITY EXCEPTION - UNAUTHORIZED ACCESS";
    String RECORD_WITH_SRMSID_NOT_FOUND = "Record with SRMS ID not found";
    String FAILED_TO_CREATE_EXPORTDRAWING = "Failed to create export drawing";
    String RECORD_CANNOT_BE_EDITED = "Record cannot be edited further";
    String UNABLE_TO_DELETE_RECORD = "Unable to delete record";
    String UNABLE_TO_UPDATE_DRAWING = "Unable to update the drawing";
    String FAILED_TO_FETCH_ATTACHMENT = "Failed to fetch the attachment";
    String FAILED_TO_UPLOAD_ATTACHMENT = "Failed to upload the attachment";
    String FAILED_TO_DELETE_ATTACHMENT = "Failed to delete the attachment";
    String UNKNOWN_STATUS = "Unknown Status";
    String DRAWING_NOT_ELIGIBE_PAYMENT_ADVICE = "Requested drawing is not eligible to create payment advices";
    String AMENDMENT_UPDATE_FAILED = "Unable to update amendment";
    String FETCH_AMENDMENT_FAILED = "Failed to fetch the amendment record";
    String CREATE_REQUEST_FAILED = "Failed to create record";
    String UPDATE_REQUEST_FAILED = "Failed to update request";
    String GET_REQUEST_FAILED = "Failed to fetch record";
    String INTERNAL_SERVICE_ERROR = "Internal Service Error";
    String UNAUTHORIZED_USER = "Logged in User is not authorized to perform this action";
    String INVALID_INPUT = "Invalid input, Malformed request";
    String INVALID_STATUS = "Record cannot be updated with current status";
    String BACKEND_FAILED_ERROR = "Error occurred while sending request";
    String MAXIMUM_RETURN_REACHED = "Record is not eligible to update. Reached maximum returns.";
    String LC_NOT_ELIGIBLE_TO_CREATE_AMENDMENT = "The requested letter of credit is not eligible to create amendment.";
}