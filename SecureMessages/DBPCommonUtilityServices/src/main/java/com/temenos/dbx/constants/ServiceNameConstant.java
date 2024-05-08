package com.temenos.dbx.constants;

public enum ServiceNameConstant {
	RDC,
	INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE,
	INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE,
	INTRA_BANK_FUND_TRANSFER_CREATE,
	TRANSFER_BETWEEN_OWN_ACCOUNT_CREATE,
	BILL_PAY_BULK,
	BILL_PAY_CREATE,
	DOMESTIC_WIRE_TRANSFER_CREATE,
	INTERNATIONAL_WIRE_TRANSFER_CREATE,
	P2P_CREATE,
	UNKNOWN; 
	/* Fixing DBB-8409 as Post processor is throwing error 
	 * java.lang.IllegalArgumentException: No enum constant 
	 * for service name BULK_PAYMENT_REQUEST_SUBMIT and may be below:
	 * 
	 * BULK_PAYMENT_REQUEST_SUBMIT,
	 * BULK_PAYMENT_REQUEST_EDIT,
	 * BULK_PAYMENT_FILES_SINGLE_UPLOAD_CSV,
	 * BULK_PAYMENT_FILES_SINGLE_UPLOAD_XML,
	 * BULK_PAYMENT_FILES_MULTI_UPLOAD_CSV,
	 * BULK_PAYMENT_FILES_MULTI_UPLOAD_XML,
	 * BULK_PAYMENT_REQUEST_ADD_PO,
	 * BULK_PAYMENT_REQUEST_EDIT_PO;
	 * */
}