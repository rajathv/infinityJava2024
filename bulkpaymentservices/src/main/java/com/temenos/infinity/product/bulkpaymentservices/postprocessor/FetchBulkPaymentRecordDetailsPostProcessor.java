package com.temenos.infinity.product.bulkpaymentservices.postprocessor;

import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.accounts.AccountsConstants;
import com.kony.dbx.BasePostProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class FetchBulkPaymentRecordDetailsPostProcessor extends BasePostProcessor implements AccountsConstants  {

	private static final Logger logger = LogManager.getLogger(FetchBulkPaymentRecordDetailsPostProcessor.class);
	
	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response) {
		try {
			result.addParam(new Param("recordId", request.getParameter("recordId")));
			List<Dataset> recordList = result.getAllDatasets();
            if(recordList !=null 
            	&& recordList.size()>0 
            	&& recordList.get(0)!=null 
            	&& recordList.get(0).getAllRecords()!=null 
            	&& recordList.get(0).getAllRecords().size() > 0)
            {      
			ListIterator<Record> recordItr = result.getAllDatasets().get(0).getAllRecords().listIterator();
			
			 while(recordItr.hasNext()) { 
				 Record record = recordItr.next();
				 
				 if(record.getParam("bulkReference")!=null
						 && record.getParam("description")==null )
				 {
					 record.addParam(new Param("description", record.getParam("bulkReference").getValue()));
				 }
			 }}

		}
		catch(Exception e) {
			logger.error("Error occured while invoking post processor for fetchBulkPaymentRecordbyId: ", e);
			return null;
		}
		return result;
	}
}
