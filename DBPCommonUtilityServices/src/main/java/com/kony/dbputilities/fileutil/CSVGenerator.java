package com.kony.dbputilities.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;

public class CSVGenerator implements FileGenerator {
    private static final Logger LOG = LogManager.getLogger(CSVGenerator.class);

    private String FIELD_SEPARATOR = ",";
    private String HEADER_SEPARATOR = "\t,";

    public String getFieldSeparator() {
        return FIELD_SEPARATOR;
    }

    public void setFieldSeparator(String fIELD_SEPARATOR) {
        FIELD_SEPARATOR = fIELD_SEPARATOR;
    }

    @Override
    public byte[] generateFile(JsonArray data, String title, String generatedBy, String startDate, String endDate,
            Map<String, String> fieldList, Map<String, Object> otherData, String filters) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            createHeader(bos, title, generatedBy, startDate, endDate, fieldList,
                    (String) otherData.get("accountNumber"), (String) otherData.get("bankName"));
            if (null != data && data.size() > 0) {
                for (int i = data.size() - 1; i >= 0; i--) {
                    bos.write(getCsvRow(fieldList, data.get(i).getAsJsonObject(),"MM/dd/yyyy").getBytes());
                    if (i > 0) {
                        bos.write(getBytes(System.lineSeparator()));
                    }
                }
            }
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw ioe;
        }
    }

    private String getCsvRow(Map<String, String> fieldList, JsonObject rowData,String paymentDateFormat) {
        StringBuilder csvRow = new StringBuilder();
        Iterator<String> itr = fieldList.keySet().iterator();
        String field = null;
        while (itr.hasNext()) {
            field = itr.next();
			try {
				if ("transactionDate".equals(field)) {
					csvRow.append(HelperMethods.convertDateFormat(rowData.get(field).getAsString(), "MM/dd/yyyy"));
				}else if ("Date".equals(field)) {
					csvRow.append(HelperMethods.convertDateFormat(rowData.get(field).getAsString(), paymentDateFormat));
				} else {
					csvRow.append((null == rowData.get(field)) ? "" : rowData.get(field).getAsString());
				}
				if (itr.hasNext()) {
					csvRow.append(FIELD_SEPARATOR);
				}
			} catch (ParseException e) {
				LOG.error("Error in creating csv row",e);	
			}
        }
        return csvRow.toString();
    }
    
    private String getCombinedStatementsCsvRow(Map<String, String> fieldList, JsonObject rowData) throws Exception{
        StringBuilder csvRow = new StringBuilder();
        Iterator<String> itr = fieldList.keySet().iterator();
        String field = null;
        while (itr.hasNext()) {
            field = itr.next();
			try {
				if ("transactionDate".equals(field)) {
					csvRow.append(HelperMethods.convertDateFormat(rowData.get(field).getAsString(), "MM/dd/yyyy"));
				} else {
					csvRow.append((null == rowData.get(field)) ? "" : rowData.get(field).getAsString());
				}
				if (itr.hasNext()) {
					csvRow.append(FIELD_SEPARATOR);
				}
			} catch (Exception e) {
				LOG.error("Error in creating csv row",e);	
				throw e;
			}
        }
        return csvRow.toString();
    }

    private void createHeader(ByteArrayOutputStream bos, String title, String generatedBy, String startDate,
            String endDate, Map<String, String> fieldList, String accountNumber, String bankName) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        bos.write(getBytes("Title : " + title));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated by : " + generatedBy));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated on : " + dateFormat.format(new Date())));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Account Number : " + accountNumber));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Bank Name : " + bankName));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Start Date : " + (StringUtils.isBlank(startDate) ? "NA" : startDate)));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("End Date : " + (StringUtils.isBlank(endDate) ? "NA" : endDate)));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes(System.lineSeparator()));

        StringBuilder csvRow = new StringBuilder();
        Iterator<String> itr = fieldList.keySet().iterator();
        while (itr.hasNext()) {
            csvRow.append(fieldList.get(itr.next()));
            if (itr.hasNext()) {
                csvRow.append(HEADER_SEPARATOR);
            }
        }
        bos.write(getBytes(csvRow.toString()));
        bos.write(getBytes(System.lineSeparator()));
    }

    private byte[] getBytes(String str) {
        if (null == str) {
            str = "";
        }
        return str.getBytes();
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }
    @Override
	public byte[] generateLoanFile(JsonArray data, String title, String generatedBy, 
			Map<String, String> fieldList, Map<String, Object> otherData, String filters,String installmentType,Map<String, String> summaryDetails,String paymentDateFormat) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
			createLoanHeader(bos, title, generatedBy, fieldList,
                    (String) otherData.get("accountNumber"), (String) otherData.get("accountName"), summaryDetails);
			
			JsonArray paid = new JsonArray();
			JsonArray overdue = new JsonArray();
			JsonArray future = new JsonArray();
			
			if (null != data && data.size() > 0) {
				 for (int i = data.size() - 1; i >= 0; i--) {
					 String Type = data.get(i).getAsJsonObject().get("InstallmentType").getAsString();
					 if(Type.equalsIgnoreCase(DBPUtilitiesConstants.PAID)) {
						 paid.add(data.get(i).getAsJsonObject());
					 }
					 else if(Type.equalsIgnoreCase(DBPUtilitiesConstants.DUE)) {
						 overdue.add(data.get(i).getAsJsonObject());
					 }
					 else if(Type.equalsIgnoreCase(DBPUtilitiesConstants.FUTURE)) {
						 future.add(data.get(i).getAsJsonObject());
					 }	 
				 }
			}
			
			if (installmentType.equalsIgnoreCase("All")) {
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("OverDue",overdue,bos,fieldList,paymentDateFormat);
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("Paid", paid, bos, fieldList,paymentDateFormat);
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("Future",future,bos,fieldList,paymentDateFormat);
			}
			else if ( installmentType.equalsIgnoreCase("Paid")) {
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("Paid", paid, bos, fieldList,paymentDateFormat);
			}
			
			else if ( installmentType.equalsIgnoreCase("overdue")) {
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("OverDue",overdue,bos,fieldList,paymentDateFormat);
			}

			else if (installmentType.equalsIgnoreCase("future")) {
				bos.write(getBytes(System.lineSeparator()));
				createInstallmentRows("Future",future,bos,fieldList,paymentDateFormat);
			}
			

            return bos.toByteArray();
        } catch (IOException ioe) {
            LOG.error("Error in generation of csv",ioe);
            throw ioe;
        }
	}
    
    public byte[] generateCombinedStatementsFile(JsonArray dataObject, String title, String generatedBy, String startDate,
            String endDate, Map<String, String> fieldList, String bankName, String currencyCode, String paymentDateFormat) throws Exception {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
			createStatementHeader(bos, title, generatedBy, startDate, endDate, fieldList, bankName);
			
			for(int i=0;i<dataObject.size();i++) {
	        	JsonElement accountElement = dataObject.get(i);
	        	JsonObject accountObject = accountElement.getAsJsonObject();
	        	String accountName = null;
	        	String accountNumber = null;
	        	JsonArray data = new JsonArray();
	        	if(StringUtils.isNotBlank(accountObject.get("accountName").toString()))
	        		accountName = accountObject.get("accountName").getAsString();
	        	if(StringUtils.isNotBlank(accountObject.get("accountNumber").toString()))
	        		accountNumber = accountObject.get("accountNumber").getAsString();
				if(StringUtils.isNotBlank(accountObject.get("transactionsList").toString()))
					data = accountObject.get("transactionsList").getAsJsonArray();
				JsonArray transactions = new JsonArray();
				if (null != data && data.size() > 0) {
					for (int j = data.size() - 1; j >= 0; j--) {
						transactions.add((JsonElement)data.get(j));
					}
				}
				bos.write(getBytes(System.lineSeparator()));
				createStatementRows(transactions, accountName, accountNumber, bos, fieldList, paymentDateFormat);
			}
            return bos.toByteArray();
        } catch (IOException ioe) {
            LOG.error("Error in generation of csv",ioe);
            throw ioe;
        } catch (Exception e) {
            LOG.error("Error while generating csv file",e);
            throw e;
        }
	}
	
	private void createInstallmentRows(String installemntType,JsonArray installmentObject,ByteArrayOutputStream bos,Map<String, String> fieldList,String paymentDateFormat) throws IOException {
		if (null != installmentObject && installmentObject.size() > 0) {
        	bos.write(getBytes(installemntType+" Installments"));
        	bos.write(getBytes(System.lineSeparator()));
        	createLoanSubHeader(bos, fieldList);
            for (int i = installmentObject.size() - 1; i >= 0; i--) {
                bos.write(getCsvRow(fieldList, installmentObject.get(i).getAsJsonObject(),paymentDateFormat).getBytes());
                if (i >= 0) {
                    bos.write(getBytes(System.lineSeparator()));
                }
            }
        }
	}
	
	private void createStatementRows(JsonArray statementObject, String accountName, String accountNumber, ByteArrayOutputStream bos,Map<String, String> fieldList,String paymentDateFormat) throws Exception {
		bos.write(getBytes("Account Name: "+accountName));
    	bos.write(getBytes(System.lineSeparator()));
    	bos.write(getBytes("Account Number: "+accountNumber));
    	bos.write(getBytes(System.lineSeparator()));
    	createLoanSubHeader(bos, fieldList);
		if (null != statementObject && statementObject.size() > 0) {
            for (int i = statementObject.size() - 1; i >= 0; i--) {
                bos.write(getCombinedStatementsCsvRow(fieldList, statementObject.get(i).getAsJsonObject()).getBytes());
                bos.write(getBytes(System.lineSeparator()));
            }
        }
	}
	
	private void createStatementHeader(ByteArrayOutputStream bos, String title, String generatedBy, String startDate,
            String endDate, Map<String, String> fieldList, String bankName) throws IOException {
        SimpleDateFormat reportGeneratedDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        bos.write(getBytes("Title : " + title));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Bank Name : " + bankName));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated by : " + generatedBy));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated on : " + reportGeneratedDateFormat.format(new Date())));
        bos.write(getBytes(System.lineSeparator()));
        try {
			startDate = HelperMethods.convertDateFormat(startDate, "MM/dd/yyyy");
			endDate = HelperMethods.convertDateFormat(endDate, "MM/dd/yyyy");
		} catch (ParseException e) {
			LOG.error("Error while parsing date" +e);
		}
        bos.write(getBytes("Start Date : " + (StringUtils.isBlank(startDate) ? "NA" : startDate)));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("End Date : " + (StringUtils.isBlank(endDate) ? "NA" : endDate)));
        bos.write(getBytes(System.lineSeparator()));
    }
	
	private void createLoanHeader(ByteArrayOutputStream bos, String title, String generatedBy, 
             Map<String, String> fieldList, String accountNumber, String accountName, Map<String, String> summary) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        bos.write(getBytes("Title : " + title));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Account Name : " + accountName));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Account Number : " + accountNumber));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated by : " + generatedBy));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes("Report generated on : " + dateFormat.format(new Date())));
        bos.write(getBytes(System.lineSeparator()));
        bos.write(getBytes(System.lineSeparator()));

		if (summary.size() > 0) {
			bos.write(getBytes("Summary"));
			bos.write(getBytes(System.lineSeparator()));
			if (summary.containsKey("OverDueInstallmentsCount")) {
				bos.write(getBytes("Overdue Payments:" +summary.get("OverDueInstallmentsCount") ));
				bos.write(getBytes(System.lineSeparator()));
			}
			if (summary.containsKey("PaidInstallmentsCount")) {
				bos.write(getBytes("Paid Installments:"+summary.get("PaidInstallmentsCount")));
				bos.write(getBytes(System.lineSeparator()));
			}
			if (summary.containsKey("FutureInstallmentsCount")) {
				bos.write(getBytes("Future Payments:"+summary.get("FutureInstallmentsCount")));
				bos.write(getBytes(System.lineSeparator()));
			}

		}
        bos.write(getBytes(System.lineSeparator()));


    }

	private void createLoanSubHeader(ByteArrayOutputStream bos,  Map<String, String> fieldList) throws IOException {
		
		StringBuilder csvRow = new StringBuilder();
        Iterator<String> itr = fieldList.keySet().iterator();
        while (itr.hasNext()) {
            csvRow.append(fieldList.get(itr.next()));
            if (itr.hasNext()) {
                csvRow.append(HEADER_SEPARATOR);
            }
        }
        bos.write(getBytes(csvRow.toString()));
        bos.write(getBytes(System.lineSeparator()));
		
	}
}
