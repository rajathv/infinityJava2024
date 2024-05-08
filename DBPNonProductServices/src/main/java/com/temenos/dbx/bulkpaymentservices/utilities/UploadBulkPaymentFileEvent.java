package com.temenos.dbx.bulkpaymentservices.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.konylabs.middleware.api.events.EventData;
import com.konylabs.middleware.api.events.EventSubscriber;
import com.konylabs.middleware.api.events.IntegrationEventSubscriber;
import com.temenos.dbx.product.bulkpaymentservices.businessdelegate.api.BulkPaymentFileBusinessDelegate;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentFileDTO;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;

@IntegrationEventSubscriber(topics = {"bulkpayments/upload"})
public class UploadBulkPaymentFileEvent implements EventSubscriber {

	private static final Logger LOG = LogManager.getLogger(UploadBulkPaymentFileEvent.class);

	@Override
	public void onEvent(EventData eventData) {

		BulkPaymentFileBusinessDelegate bulkPaymentFileBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(BulkPaymentFileBusinessDelegate.class);
		BulkPaymentFileDBOperations bulkPaymentFileDBOperations = new BulkPaymentFileDBOperations(); 

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (Map<String, Object>) eventData.getData();	

		String fileContent = inputParams.get("content") != null ? inputParams.get("content").toString() : null;
		String fileName = inputParams.get("fileName") != null ? inputParams.get("fileName").toString().trim() : null;
		if(fileName != null) {
			fileName = FilenameUtils.getName(fileName);
		}
		String fileDir = System.getProperty("java.io.tmpdir");		
		
		BulkPaymentFileDTO bulkPaymentFileDTO = null;
		File uploadedFile = null;
		BufferedWriter fileWriter = null;
		
		try {
			uploadedFile = new File(fileDir, fileName);
			fileWriter = new BufferedWriter(new FileWriter(uploadedFile));
			byte[] bulkPaymentsFileContents = Base64.decodeBase64(fileContent);
			fileWriter.write(new String(bulkPaymentsFileContents, "UTF-8"));			
			fileWriter.close();
			bulkPaymentFileDTO = bulkPaymentFileBusinessDelegate.parseFile(uploadedFile);				
		} 
		catch (Exception e) {
			LOG.error("Error occured while invoking uploadBulkPaymentFile: ", e);			
		}
		
		if (bulkPaymentFileDTO == null || bulkPaymentFileDTO.getDbpErrorCode() != null) {
			LOG.error("Error occured while parsing Bulk Payment File in the event");	
			return ;
		}
		bulkPaymentFileDTO.setDescription(inputParams.get("description").toString());
		bulkPaymentFileDTO.setFileId(inputParams.get("fileId").toString());
		AccountBusinessDelegate accountDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
		CustomerAccountsDTO accountDTO = accountDelegate.getAccountDetails(inputParams.get("uploadedBy").toString(), bulkPaymentFileDTO.getFromAccount());
		if(accountDTO == null) {
			LOG.error("Error occured while fetching the account details");	
			return ;
		}
		bulkPaymentFileDTO.setCompanyId(accountDTO.getOrganizationId());
		bulkPaymentFileDTO.setUploadedBy(inputParams.get("uploadedBy").toString());
		bulkPaymentFileDTO.setFileSize(String.valueOf(uploadedFile.getTotalSpace()));
		bulkPaymentFileDTO.setFileName(uploadedFile.getName());
		bulkPaymentFileDTO.setContent(fileContent);
		bulkPaymentFileDTO.setStatus(TransactionStatusEnum.UPLOADED.getStatus());
		bulkPaymentFileDTO.setSysGeneratedFileName(inputParams.get("sysGeneratedFileName").toString());
		bulkPaymentFileDTO.setBatchMode(bulkPaymentFileDTO.getBulkPaymentRecord().getBatchMode());
	
		bulkPaymentFileDTO = bulkPaymentFileDBOperations.createBulkPaymentFileEntry(bulkPaymentFileDTO);
				
		if (bulkPaymentFileDTO == null) {
			LOG.error("BulkPayment file dto is null. Error occured while uploading Bulk Payment File");
		}
	}
}