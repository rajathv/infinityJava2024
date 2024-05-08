package com.kony.postprocessors;

import com.dbp.core.constants.DBPConstants;

public interface ObjectServicesConstants {

	String PARAM_OP_STATUS = DBPConstants.FABRIC_OPSTATUS_KEY;
	String PARAM_ENABLE_EVENTS = "ENABLE_EVENTS";
	String PARAM_SID_EVENT_SUCCESS = "SID_EVENT_SUCCESS";
	String PARAM_SID_EVENT_FAILURE = "SID_EVENT_FAILURE";
	String PARAM_TRUE = "true";
	String PARAM_FALSE = "false";
	String PARAM_ACCOUNT_ACTION = "ACCOUNT_ACTION";
	String PARAM_ACC_TRANSACTIONS_SEARCH = "ACC_TRANSACTIONS_SEARCH";
	String PARAM_DBP_ERR_CODE = DBPConstants.DBP_ERROR_CODE_KEY;
	String PARAM_INTACC_NICKNAME_UPDATE = "INTACC_NICKNAME_UPDATE";
	String PARAM_EXTACC_NICKNAME_UPDATE = "EXTACC_NICKNAME_UPDATE";
	String PARAM_INTACC_FAVOURITE_UPDATE = "INTACC_FAVOURITE_UPDATE";
	String PARAM_CREDENTIAL_CHANGE = "CREDENTIAL_CHANGE";
	String PARAM_USERNAME_CHANGE = "USERNAME_CHANGE";

	/**
	 * Profile update eventtype and eventsubtypes
	 */
	String PARAM_PROFILE_UPDATE = "PROFILE_UPDATE";
	String PARAM_EMAIL_CHANGE = "EMAIL_CHANGE";
	String PARAM_ADDRESS_CHANGE = "ADDRESS_CHANGE";
	String PARAM_PHONE_CHANGE = "PHONE_CHANGE";

	String PARAM_STATUS_NEW = "STATUS_NEW";
	String PARAM_BO_STATUS_NEW = "BO_STATUS_NEW";

	String PARAM_STATUS_ACTIVE = "STATUS_ACTIVE";
	String PARAM_BO_STATUS_ACTIVE = "BO_STATUS_ACTIVE";
    String PARAM_LOGIN_ADMIN="LOGIN_ADMIN";
    String PARAM_ACCOUNT_SUSPENDED_ADMIN="ACCOUNT_SUSPENDED_ADMIN";
    String PARAM_ACCOUNT_ACTIVATED_ADMIN="ACCOUNT_ACTIVATED_ADMIN";
	String PARAM_STATUS_SUSPENDED = "STATUS_SUSPENDED";
	String PARAM_BO_STATUS_SUSPENDED = "BO_STATUS_SUSPENDED";
	String PARAM_ACCOUNT_SUSPENDED = "ACCOUNT_SUSPENDED";
	String PARAM_INTACC_STATEMENT_FETCH = "INTACC_STATEMENT_FETCH";
	String PARAM_PSWD_CHANGE = "PASSWORD_CHANGE";
	String PARAM_PSWD_UPDATE = "PASSWORD_UPDATE";
	String PARAM_EXTERNAL_ACCOUNTS_FETCH = "EXTERNAL_ACCOUNTS_FETCH";
	String PARAM_INTACC_TRANSACTIONS_DOWNLOAD = "INTACC_TRANSACTIONS_DOWNLOAD";
	String PARAM_ACC_ALL_TRANSACTIONS_FETCH = "ACC_ALL_TRANSACTIONS_FETCH";
	String PARAM_INTERNAL_ACCOUNTS_FETCH = "INTERNAL_ACCOUNTS_FETCH";
	String PARAM_PAYEE_NICKNAME = "payeeNickName";
	String PARAM_BILL_PAYEE = "BILL_PAYEE";
	String PARAM_NON_REG_BILL_PAYEE_ADDED = "NON_REG_BILL_PAYEE_ADDED";
	String PARAM_REGISTERED_BILL_PAYEE_ADDED = "REGISTERED_BILL_PAYEE_ADDED";
	String PARAM_BILL_PAYEE_UPDATED = "BILL_PAYEE_UPDATED";
	String PARAM_BILL_PAYEE_DELETED = "BILL_PAYEE_DELETED";
	String PARAM_TRANSFER_RECIPIENT = "TRANSFER_RECIPIENT";
	String PARAM_IS_INTERNATIONAL_ACCOUNT = "isInternationalAccount";
	String PARAM_IS_SAME_BANK_ACCOUNT = "isSameBankAccount";
	String PARAM_INT_TRANSFER_RECIPIENT_ADDED = "INT_TRANSFER_RECIPIENT_ADDED";
	String PARAM_OTHER_BANK_RECIPIENT_ADDED = "OTHER_BANK_RECIPIENT_ADDED";
	String PARAM_SAME_BANK_RECIPIENT_ADDED = "SAME_BANK_RECIPIENT_ADDED";
	String PARAM_NICK_NAME = "nickName";
	String PARAM_RECIPIENT_NICK_NAME = "RecipientNickName";
	String PARAM_P2P_RECIPIENT = "P2P_RECIPIENT";
	String PARAM_P2P_TRANSFER = "p2p";
	String PARAM_WIRE_TRANSFER = "wire";
	String PARAM_DEPOSIT_TRANSFER = "deposit";
	String PARAM_BILLPAY_TRANSFER = "billpay";
	String PARAM_P2P_RECIPIENT_ADDED = "P2P_RECIPIENT_ADDED";
	String PARAM_P2P_RECIPIENT_UPDATED = "P2P_RECIPIENT_UPDATED";
	String PARAM_P2P_RECIPIENT_DELETED = "P2P_RECIPIENT_DELETED";
	String PARAM_WIRE_ACCOUNT_TYPE = "wireAccountType";
	String PARAM_INTERNATIONAL = "international";
	String PARAM_DOMESTIC = "domestic";
	String PARAM_INT_WIRE_RECIPIENT_ADDED = "INT_WIRE_RECIPIENT_ADDED";
	String PARAM_DOM_WIRE_RECIPIENT_ADDED = "DOM_WIRE_RECIPIENT_ADDED";
	String PARAM_MAKE_TRANSFER = "MAKE_TRANSFER";
	String PARAM_TRANSACTION_TYPE = "transactionType";
	String PARAM_INTERNAL_TRANSFER = "internaltransfer";
	String PARAM_FREQUENCY_TYPE = "frequencyType";
	String PARAM_IS_SCHEDULED = "isScheduled";
	String PARAM_SCHEDULED_OWN_ACCOUNT_TRANSFER = "SCHEDULED_OWN_ACCOUNT_TRANSFER";
	String PARAM_RECURRING_OWN_ACCOUNT_TRANSFER = "RECURRING_OWN_ACCOUNT_TRANSFER";
	String PARAM_ONETIME_OWN_ACCOUNT_TRANSFER = "ONETIME_OWN_ACCOUNT_TRANSFER";
	String PARAM_SCHEDULED_SAMEBANK_OTHER_ACCOUNT_TRANSFER = "SCHEDULED_SAMEBANK_OTHER_ACCOUNT_TRANSFER";
	String PARAM_RECURRING_SAMEBANK_OTHER_ACCOUNT_TRANSFER = "RECURRING_SAMEBANK_OTHER_ACCOUNT_TRANSFER";
	String PARAM_ONETIME_SAMEBANK_OTHER_ACCOUNT_TRANSFER = "ONETIME_SAMEBANK_OTHER_ACCOUNT_TRANSFER";
	String PARAM_EXTERNAL_TRANSFER = "externaltransfer";
	String PARAM_SCHEDULED_OTHER_BANK_TRANSFER = "SCHEDULED_OTHER_BANK_TRANSFER";
	String PARAM_RECURRING_OTHER_BANK_TRANSFER = "RECURRING_OTHER_BANK_TRANSFER";
	String PARAM_ONETIME_OTHER_BANK_TRANSFER = "ONETIME_OTHER_BANK_TRANSFER";
	String PARAM_SCHEDULED_INTERNATIONAL_BANK_TRANSFER = "SCHEDULED_INTERNATIONAL_BANK_TRANSFER";
	String PARAM_RECURRING_INTERNATIONAL_BANK_TRANSFER = "RECURRING_INTERNATIONAL_BANK_TRANSFER";
	String PARAM_ONETIME_INTERNATIONAL_BANK_TRANSFER = "ONETIME_INTERNATIONAL_BANK_TRANSFER";
	String PARAM_FROM_ACCOUNT_NUMBER = "fromAccountNumber";
	String PARAM_TO_ACCOUNT_NUMBER = "toAccountNumber";
	String PARAM_SERVER_DATE = "ServerDate";
	String PARAM_REFERENCE_ID = "referenceId";
	String PARAM_EXTACC_TRANSACTIONS_FETCH = "EXTACC_TRANSACTIONS_FETCH";
	String PARAM_DELETE_TRANSACTION = "DELETE_TRANSACTION";
	String PARAM_DELETE_SCHEDULED_TRANSACTION_OCCURRENCE = "DELETE_SCHEDULED_TRANSACTION_OCCURRENCE";
	String PARAM_BILL_PAYMENT = "BILL_PAYMENT";
	String PARAM_BULK_BILL_PAYMENT = "BULK_BILL_PAYMENT";
	String PARAM_ACC_TRANS_TRANSFERS_FETCH = "ACC_TRANS_TRANSFERS_FETCH";
	String PARAM_ACC_TRANS_DEPOSITS_FETCH = "ACC_TRANS_DEPOSITS_FETCH";
	String PARAM_ACC_TRANS_CHECKS_FETCH = "ACC_TRANS_CHECKS_FETCH";
	String PARAM_ACC_TRANS_WITHDRAWALS_FETCH = "ACC_TRANS_WITHDRAWALS_FETCH";
	String PARAM_EMAILIDS = "EmailIds";
	String PARAM_CONTACTNUMBERS = "ContactNumbers";
	String PARAM_IS_PRIMARY = "isPrimary";
	String PARAM_VALUE = "value";
	String PARAM_PHONE_NUMBERS = "phoneNumbers";
	String PARAM_ADDRESSES = "Addresses";
	String PARAM_EBILL_ENABLE = "EBillEnable";
	String PARAM_BILL_PAYMENT_ENABLED = "EBILL_ENABLED";
	String PARAM_BILL_PAYMENT_DISABLED = "EBILL_DISABLED";
	String PARAM_INT_TRANSFER_RECIPIENT_UPDATED = "INT_TRANSFER_RECIPIENT_UPDATED";
	String PARAM_OTHER_BANK_RECIPIENT_UPDATED = "OTHER_BANK_RECIPIENT_UPDATED";
	String PARAM_SAME_BANK_RECIPIENT_UPDATED = "SAME_BANK_RECIPIENT_UPDATED";
	String PARAM_TRANSFER_RECIPIENT_DELETED = "TRANSFER_RECIPIENT_DELETED";
	String PARAM_INT_WIRE_RECIPIENT_UPDATED = "INT_WIRE_RECIPIENT_UPDATED";
	String PARAM_DOM_WIRE_RECIPIENT_UPDATED = "DOM_WIRE_RECIPIENT_UPDATED";
	String PARAM_WIRE_RECIPIENT_DELETED = "WIRE_RECIPIENT_DELETED";
	String PARAM_TRANSFER_RECIPIENT_UPDATED = "TRANSFER_RECIPIENT_UPDATED";
	String PARAM_WIRE_RECIPIENT_UPDATED = "WIRE_RECIPIENT_UPDATED";
	String PARAM_REGISTER_DEVICE = "REGISTER_DEVICE";
	String PARAM_REGISTER_CUSTOMER_DEVICE = "REGISTER_CUSTOMER_DEVICE";
	String CREDIT_CARD = "CREDIT_CARD";
	String FETCH_CREDIT_CARDS = "FETCH_CREDIT_CARDS";
	String UNLOCK_CARD = "UNLOCK_CARD";
	String LOCK_CARD = "LOCK_CARD";
	String REPORT_LOST_CARD = "REPORT_LOST_CARD";
	String REPLACE_CARD = "REPLACE_CARD";
	String CHANGE_PIN = "CHANGE_PIN";
	String CARD_PIN_CHANGE_MFA = "CARD_PIN_CHANGE_MFA";
	String CARD_UPDATE_MFA = "CARD_UPDATE_MFA";
	String CARD_ACTIVATE_MFA = "CARD_ACTIVATE_MFA";
	String CARD_LOCK_MFA = "CARD_LOCK_MFA";
	String CARD_REPLACE_MFA = "CARD_REPLACE_MFA";
	String CARD_LOST_MFA = "CARD_LOST_MFA";
	String CARD_CANCEL_MFA = "CARD_CANCEL_MFA";
	String PARAM_LOGIN = "LOGIN";
	String PARAM_CANT_LOGIN = "CANT_LOGIN";
	String PARAM_REGISTERED_BILL_PAYMENT = "REGISTERED_BILL_PAYMENT";
	String PARAM_NON_REGISTERED_BILL_PAYMENT = "NON_REGISTERED_BILL_PAYMENT";
	String PARAM_REGISTERED_P2P_TRANSFER = "REGISTERED_P2P_TRANSFER";
	String PARAM_NON_REGISTERED_P2P_TRANSFER = "NON_REGISTERED_P2P_TRANSFER";
	String PARAM_REG_DOM_WIRE_TRANSFER = "REG_DOM_WIRE_TRANSFER";
	String PARAM_REG_INTERNATIONAL_WIRE_TRANSFER = "REG_INTERNATIONAL_WIRE_TRANSFER";
	String PARAM_NON_REG_DOM_WIRE_TRANSFER = "NON_REG_DOM_WIRE_TRANSFER";
	String PARAM_NON_REG_INTERNATIONAL_WIRE_TRANSFER = "NON_REG_INTERNATIONAL_WIRE_TRANSFER";
	String PARAM_RDC_TRANSFER = "RDC_TRANSFER";
	String PARAM_DELETE_INTERNAL_TRANSFER = "DELETE_INTERNAL_TRANSFER";
	String PARAM_DELETE_EXTERNAL_TRANSFER = "DELETE_EXTERNAL_TRANSFER";
	String PARAM_DELETE_P2P_TRANSFER = "DELETE_P2P_TRANSFER";
	String PARAM_DELETE_WIRE_TRANSFER = "DELETE_WIRE_TRANSFER";
	String PARAM_DELETE_BILL_PAYMENT = "DELETE_BILL_PAYMENT";
	String PARAM_DELETE_RDC = "DELETE_RDC";
	String PARAM_ACCOUNT_UPDATE = "ACCOUNT_UPDATE";
	String PARAM_BILLER_ID = "billerId";
	String PARAM_BULK_BILLPAY_STRING = "bulkPayString";
	String PARAM_PAYEE_ID = "payeeId";
	String PARAM_BILLER_NAME = "billerName";
	String PARAM_NO_OF_OTHER_BILLERS = "noOfOtherBillers";
	String PARAM_PAID_AMOUNT = "paidAmount";
	String PARAM_AMOUNT = "amount";
	String PARAM_TRANSFERS = "Transfers";
	String PARAM_DEPOSITS = "Deposits";
	String PARAM_CHECKS = "Checks";
	String PARAM_WITHDRAWLS = "Withdrawls";
	String PARAM_ALL = "All";
	String PARAM_NO_OF_ACCOUNTS = "noOfAccounts";
	String PARAM_RECORDS = "records";
	String PARAM_NO_OF_TRANSACTIONS = "noOfTransactions";
	String PARAM_SERVICE_NAME = "serviceName";
	String PARAM_MFA_ATRIBUTES = "MFAAttributes";
	String PARAM_ONCE = "once";
	String PARAM_PERSON_ID = "personId";
	String PARAM_MFA_TYPE = "mfaType";
	String PARAM_ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
	String PARAM_TRANSACTIONS = "Transactions";
	String PARAM_ALERTS_TABULAR_DATA = "ALERTS_TABULAR_DATA";
	String PARAM_TRANSACTION_NOTES = "transactionsNotes";
	String PARAM_SID_FAILED = "SID_FAILED";
	String PARAM_SID_SCHEDULED = "SID_SCHEDULED";
	String PARAM_SID_POSTED = "SID_POSTED";
	String PARAM_SID_TRANS_FAILED = "SID_TRANS_FAILED";
	String PARAM_SID_TRANS_SCHEDULED = "SID_TRANS_SCHEDULED";
	String PARAM_SID_TRANS_POSTED = "SID_TRANS_POSTED";
	String PARAM_SE_ID = "schedulerMoneyMovementId";
	String PARAM_NO_OF_RECURRENCES = "numberOfRecurrences";
	String PARAM_FREQUENCY_END_DATE = "frequencyEndDate";
	String PARAM_SCHEDULED_DATE = "scheduledDate";
	String PARAM_NEW_PIN = "newPin";
	String PARAM_OLD_PSWD = "oldPassword";
	String PARAM_NEW_PSWD = "newPassword";

	String PARAM_APPROVAL_MATRIX = "APPROVAL_MATRIX";
	String PARAM_UPDATE_APPROVAL_MATRIX = "UPDATE_APPROVAL_MATRIX";

	String PARAM_BULKWIRE_TEMPLATE = "BULKWIRE_TEMPLATE";
	String PARAM_UPDATE_BULKWIRE_TEMPLATE = "UPDATE_BULKWIRE_TEMPLATE";
	String PARAM_CREATE_BULKWIRE_TEMPLATE = "CREATE_BULKWIRE_TEMPLATE";
	String PARAM_DELETE_BULKWIRE_TEMPLATE = "DELETE_BULKWIRE_TEMPLATE";
	
	String PARAM_PAY_MULTIPLE_BENEFICIARIES_CREATE_TRANSFER = "PAY_MULTIPLE_BENEFICIARIES_CREATE_TRANSFER";

	/**
	 * User create and update eventtype and eventsubtypes
	 */
	String PARAM_USER = "USER";

	String PARAM_USER_CREATE = "USER_CREATE";
	String PARAM_BO_USER_CREATE = "BO_USER_CREATE";

	String PARAM_USER_UPDATE = "USER_UPDATE";
	String PARAM_BO_USER_UPDATE = "BO_USER_UPDATE";
	String PARAM_ENABLE_EVENT = "enableEvent"; 
    String PARAM_EVENT_TYPE = "eventType";
    String PARAM_INCLUDED_OPERTAIONS = "includedOperations"; 
    String PARAM_OPERATION = "operation";
    String PARAM_EVENT_SUBTYPE = "eventSubType";
    String LOG_RESPONSE = "logResponse"; 
    String PARAM_EXCLUDED_FIELDS = "excludedFields";
    String PARAM_MASKED_FIELDS = "maskedFields";
    String PARAM_PRODUCER = "producer";
    String PARAM_CUSTOMER_NAME = "customerName";
    String PARAM_STORE_IN_CACHE = "StoreInCache";
    String PARAM_MASKING_LOGIC = "maskingLogic"; 
    String MASK_LAST_4_DIGITS = "Last4Digits";
    String MASK_FIRST_4_DIGITS = "First4Digits";
    String PARAM_VALIDATE = "validate";
    String PARAM_ACCOUNT_NUMBER = "accountNumber";
    String PARAM_IBAN = "IBAN";

}
