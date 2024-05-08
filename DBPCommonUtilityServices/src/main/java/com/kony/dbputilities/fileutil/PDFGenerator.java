package com.kony.dbputilities.fileutil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.awt.Color;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.lang.model.element.ElementKind;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;

public class PDFGenerator implements FileGenerator {

	private JsonObject userDetails;
    private static final Logger LOG = LogManager.getLogger(PDFGenerator.class);
	   private String imgFileName = "RetailBankingBanner.png";
	   private String logoFileName = "kony_logo.png";
	   private String currencyCode = "$";
	   private Set<String> depositTypes;
	   private String[] loanUserDisplayName = new String[] { "Name: ", "Account Name: ", "Account: " };
	   private String[] userDisplayName = new String[] { "Name: ", "Account: ", "Branch: " };
	   private String[] fields = new String[] { "transactionDate", "description", "transactionId" };
	   private String[] userKeys = new String[] {  "userfirstname", "account", "bankName" };
	 private String[] loanUserKeys = new String[] { "userfirstname", "accountName", "account" };
	 private String[] headers = new String[] {  "Date", "Description", "TransactionId" ,"Withdrawal","Deposit","Balance" };
	 private String[] loanFields = new String[] { "Date", "Principal", "Interest", "Charges","Amount","OutstandingBalance" };
	 String[] loanHeaders = new String[] { "Payment Date", "Principal", "Interest", "Charge", "Amount($)", "OutStanding Amount($)" };
	 private String[] combinedStatementHeaders = new String[] { "Transaction Date","Ref #Number", "Description", "Amount", "Balance"};
	    private String[] combinedStatementFields = new String[] { "transactionDate", "transactionId","description", "amount","fromAccountBalance"};
	    private String[] combinedStatementReportDetails = new String[] {"Report Generated on:","Report Generated by:"};
	    private String[] combinedStatementDetails = new String[] {"Bank Name:","Start Date:","End Date:"};
	 private String paymentDateFormat="MM/dd/yyyy";
	 Font font = new Font(Font.TIMES_ROMAN, 12, Font.ITALIC, Color.BLACK);
	 Font combinedFont = new Font(Font.HELVETICA, 11f, Font.ITALIC, Color.BLACK);
	 Font combinedBoldFont = new Font(Font.HELVETICA, 12f, Font.BOLD, Color.BLACK);
	private Map<String, String> summary;
	@Override
	public byte[] generateFile(JsonArray data, String title, String generatedBy, String startDate, String endDate,
			Map<String, String> fieldList, Map<String, Object> otherData, String filters) throws IOException {
		// TODO Auto-generated method stub
		
	     /*
		 // code snippet for password protection
		 final String USER_PWD="user";
		 final String OWNER_PWD="owner";
		 PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream());
		 writer.setEncryption(USER_PWD.getBytes(),OWNER_PWD.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128); */
		 InputStream log4jInputStream = PDFGenerator.class.getClassLoader().getResourceAsStream("log4j.properties");
		 PropertyConfigurator.configure(log4jInputStream);
		 InputStream is = PDFGenerator.class.getClassLoader().getResourceAsStream(this.imgFileName);
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		  try {
			  
			  Document document = new Document();
			  PdfWriter writer = PdfWriter.getInstance(document, os);
		   //setting font family, color
		   Font font = new Font(Font.TIMES_ROMAN, 12, Font.ITALIC, Color.BLACK);
	       Image image = Image.getInstance(IOUtils.toByteArray(is));
		   document.open();
		   image.setSpacingAfter(70);
		   image.scaleAbsoluteHeight(50);
		   image.scaleAbsoluteWidth(500);
		   document.add(image);
		    PdfPTable tableMain=new PdfPTable(2);
		    tableMain.getDefaultCell().setBorder(0);
		    Phrase p=new Phrase("Note:\n Please note that the content of this statement will be considered correct if no error is reported within 30 days of reciept of statement. The "
	                + "address of the statement is that on record with the bank as the day of requesting this content. Please note that the content of this statement"
	                + "will be considered correct if no error is reported within 30 days of reciept of statement. The address of the statement is that on record with the bank as the day of requesting this content.",font);
		    if (null != otherData.get("userDetails")) {
	            this.userDetails = (JsonObject) otherData.get("userDetails");
	        }
				PdfPTable tabInner1 = new PdfPTable(2);
				tabInner1.setTotalWidth(new float[]{ 72, 216 });
				getUserDetails(tabInner1,userDisplayName,userKeys,userDetails);
				getContactNumber(tabInner1, userDetails);
				getEmailId(tabInner1, userDetails);
				getAddress(tabInner1, userDetails);
				tableMain.setSpacingBefore(10);
				tableMain.setHorizontalAlignment(0);
				tableMain.setWidthPercentage(95);
				tableMain.setComplete(true);
			    PdfPTable tabinner = new PdfPTable(1);
			    tabinner.addCell(tabInner1);
			    tableMain.addCell(tabinner);
			    tableMain.addCell(p);
			    tableMain.setSpacingAfter(20);
			    document.add(tableMain);
			    PdfPTable tabAcc = new PdfPTable(1);
			    tabAcc.getDefaultCell().setBorder(0);
			    addDate(tabAcc, startDate, endDate);
			    tabAcc.setHorizontalAlignment(0);
			    document.add(tabAcc);
			    PdfPTable table = getTransactionsTable();
			    addTransactionsHeader(table, headers);
			    addTransactionsData(data, table);
			    document.add(table);
		     
		document.close();
		writer.close();    
		  } catch ( IOException e) {
		   // TODO Auto-generated catch block
			  LOG.error(e); 
		 } finally {
			try {
				if (is != null) {
					is.close();
				}
				if (log4jInputStream != null) {
					log4jInputStream.close();
				}
			}
			catch(Exception e){
				LOG.error(e);
			}
	    }
		  return os.toByteArray();
	}
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return "application/pdf";
	}

	@Override
	public byte[] generateLoanFile(JsonArray data, String title, String generatedBy, Map<String, String> fieldList,
			Map<String, Object> otherData, String filters, String installmentType, Map<String, String> summaryDetails,
			String paymentDateFormat) throws IOException {
		InputStream log4jInputStream = PDFGenerator.class.getClassLoader().getResourceAsStream("log4j.properties");
		PropertyConfigurator.configure(log4jInputStream);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InputStream is = PDFGenerator.class.getClassLoader().getResourceAsStream(this.imgFileName);
		// TODO Auto-generated method stub
		 try {
			  
			  Document document = new Document();
			  PdfWriter writer = PdfWriter.getInstance(document, os);
		   Font font = new Font(Font.TIMES_ROMAN, 12, Font.ITALIC, Color.BLACK);
		   this.summary=summaryDetails;
	       Image image = Image.getInstance(IOUtils.toByteArray(is));
		   document.open();
		   image.setSpacingAfter(70);
		   image.scaleAbsoluteHeight(50);
		   image.scaleAbsoluteWidth(500);
		   document.add(image);
		    PdfPTable tableMain=new PdfPTable(2);
		    tableMain.getDefaultCell().setBorder(0);
		    Phrase p=new Phrase("Note:\n Please note that the content of this statement will be considered correct if no error is reported within 30 days of reciept of statement. The "
	                + "address of the statement is that on record with the bank as the day of requesting this content. Please note that the content of this statement"
	                + "will be considered correct if no error is reported within 30 days of reciept of statement. The address of the statement is that on record with the bank as the day of requesting this content.",font);
		    if (null != otherData.get("userDetails")) {
	            this.userDetails = (JsonObject) otherData.get("userDetails");
	        }
		    PdfPTable tabInner1 = new PdfPTable(2);
		    tabInner1.setTotalWidth(new float[]{ 72, 216 });
		    tabInner1.getDefaultCell().setBorder(0);
		    getUserDetails(tabInner1,loanUserDisplayName,loanUserKeys,userDetails);
		    getContactNumber(tabInner1, userDetails);
		    getEmailId(tabInner1, userDetails);
		    getAddress(tabInner1, userDetails);
		    PdfPTable tabInner2 = new PdfPTable(2);
		    tabInner2.getDefaultCell().setBorder(0);
		    tabInner2.setTotalWidth(new float[]{ 120,120 });
		    addLoanDetails(tabInner2, summaryDetails);
		    tabInner1.setSpacingAfter(20.0f);
           tableMain.setSpacingBefore(10);
		    tableMain.setHorizontalAlignment(0);
		    tableMain.setWidthPercentage(95);
		    tableMain.setComplete(true);
		   PdfPTable tabinner = new PdfPTable(1);
		   tabinner.getDefaultCell().setBorder(0);
	       tabinner.addCell(tabInner1);
	       tabinner.addCell(tabInner2);
	       PdfPTable tabouter = new PdfPTable(1);
	       tabouter.addCell(tabinner);
		   tableMain.addCell(tabouter);
		   tableMain.addCell(p);
		   tableMain.setSpacingAfter(10);
	       document.add(tableMain);
	       PdfPTable tabAcc = new PdfPTable(1);
	       tabAcc.getDefaultCell().setBorder(0);
	       tabAcc.setHorizontalAlignment(0);
	       document.add(tabAcc);
		
	       PdfPTable overdueTable = getTransactionsLoanTable();
	       PdfPTable paidTable = getTransactionsLoanTable();
	       PdfPTable futureTable = getTransactionsLoanTable();
	       
	       addTransactionsHeader(paidTable, loanHeaders);
	       addTransactionsHeader(overdueTable, loanHeaders);
	       addTransactionsHeader(futureTable, loanHeaders);
			if (null != data && data.size() > 0) {
				 for (int i = data.size() - 1; i >= 0; i--) {
					 String Type = data.get(i).getAsJsonObject().get("InstallmentType").getAsString();
					 if(Type.equalsIgnoreCase(DBPUtilitiesConstants.PAID)) {
						 addRow(paidTable, data.get(i).getAsJsonObject(),this.loanFields);
					 }
					 else if(Type.equalsIgnoreCase(DBPUtilitiesConstants.DUE)) {
						 addRow(overdueTable, data.get(i).getAsJsonObject(),this.loanFields);
					 }
					 else if(Type.equalsIgnoreCase(DBPUtilitiesConstants.FUTURE)) {
						 addRow(futureTable, data.get(i).getAsJsonObject(),this.loanFields);
					 }	 
				 }
			}
			
			if (installmentType.equalsIgnoreCase("All") ) {
				addHeaderLabel(document,"Overdue Payments");
				 document.add(overdueTable);
				 addHeaderLabel(document,"Paid Installlments");
				 document.add(paidTable);
				 addHeaderLabel(document,"Future Payments");
				 document.add(futureTable);
				
			}
			else if ( installmentType.equalsIgnoreCase("Paid")) {
				addHeaderLabel(document,"Paid Installlments");
				document.add(paidTable);
			}
			
			else if ( installmentType.equalsIgnoreCase("overdue")) {
				addHeaderLabel(document,"Overdue Payments");
				document.add(overdueTable);
			}
			else if (installmentType.equalsIgnoreCase("future")) {
				 addHeaderLabel(document,"Future Payments");
				document.add(futureTable);
			}
			
	       document.close();
		   writer.close();  
	} catch (IOException e) {
		// TODO Auto-generated catch block
		LOG.error(e);
	} finally {
		try {
			if (is != null) {
				is.close();
			}
			if (log4jInputStream != null) {
				log4jInputStream.close();
			}
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	return os.toByteArray();
	}
	@Override
	public byte[] generateCombinedStatementsFile(JsonArray dataObject, String title, String generatedBy,
			String startDate, String endDate, Map<String, String> fieldList, String bankName, String currencyCode,
			String paymentDateFormat) throws Exception {
		InputStream log4jInputStream = PDFGenerator.class.getClassLoader().getResourceAsStream("log4j.properties");
		PropertyConfigurator.configure(log4jInputStream);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InputStream is = PDFGenerator.class.getClassLoader().getResourceAsStream(this.logoFileName);
		 try{
			   Document document = new Document();
			   PdfWriter writer = PdfWriter.getInstance(document, os);
			   this.paymentDateFormat=paymentDateFormat;
		       Image image = Image.getInstance(IOUtils.toByteArray(is));
			   document.open();
			   image.setSpacingAfter(70);
			   image.scaleAbsoluteHeight(50);
			   image.scaleAbsoluteWidth(80);
			   document.add(image);
			   SimpleDateFormat reportGeneratedDateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm a");
		       String generatedOn = reportGeneratedDateFormat.format(new Date());
		       this.currencyCode = StringUtils.isBlank(currencyCode) ? "$" : currencyCode;
		        try {
		        	startDate = StringUtils.isBlank(startDate) ? "NA" : HelperMethods.convertDateFormat(startDate, "MM/dd/yyyy");
		        	endDate = StringUtils.isBlank(endDate) ? "NA" : HelperMethods.convertDateFormat(endDate, "MM/dd/yyyy");
				} catch (ParseException e) {
					LOG.error("Error while parsing date" +e);
				}
	        PdfPTable MainTable = new PdfPTable(1);
	        PdfPTable outer1 = new PdfPTable(2);
	        outer1.getDefaultCell().setBorder(0);
	        PdfPTable outer2 = new PdfPTable(1);
	        PdfPTable inner = new PdfPTable(1);
	        inner.getDefaultCell().setBorder(0);
	        Phrase p0 = new Phrase("Combined Statement");
	        PdfPCell cel0 = new PdfPCell(p0);
	        cel0.setBorder(0);
	        inner.addCell(cel0);
	        PdfPTable inner1 = generateReportHeaderDetails(combinedStatementReportDetails,generatedOn,generatedBy);
	        PdfPTable inner2 = generateStatementDetails(combinedStatementDetails, bankName, startDate, endDate);
	        outer1.addCell(inner1);
	        outer1.addCell(inner2);
	        outer2.addCell(inner);
	        outer2.addCell(outer1);
	        MainTable.setSpacingAfter(20.0F);
	        MainTable.setHorizontalAlignment(0);
	        MainTable.setWidthPercentage(95.0F);
	        MainTable.addCell(outer2);
	        document.add(MainTable);
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
	           PdfPTable accountTransactions=getCombinedStatementTable();
	       	document.add(addCombinedStatementAccountDetails(accountName, accountNumber));
	       	addCombinedStatmentHeader(accountTransactions, this.combinedStatementHeaders);
	           if (null != data && data.size() > 0) {
					for (int j = data.size() - 1; j >= 0; j--) {
						try {
							addCombinedStatmentRow(accountTransactions, (JsonObject) data.get(j), this.combinedStatementFields);
						} catch (Exception e) {
							LOG.error("Error while adding data  to pdf");
						}
					}
				}
	          document.add(accountTransactions);
	        }
	        document.close();
			   writer.close();  	   
		       
	} catch ( IOException e) {
		LOG.error(e);
	} finally {
		try {
			if (is != null) {
				is.close();
			}
			if (log4jInputStream != null) {
				log4jInputStream.close();
			}
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	return os.toByteArray();
	}
	public void addHeaderLabel(Document document,String label) {
		PdfPTable tabAcc = new PdfPTable(1);
		Phrase stat = new Phrase("\n"+label+"\n", FontFactory.getFont("Helvetica", 12.0F, 3));
	      PdfPCell lbl = new PdfPCell(stat);
	      lbl.setBorder(0);
	      tabAcc.addCell(lbl);
	       tabAcc.getDefaultCell().setBorder(0);
	        tabAcc.setHorizontalAlignment(0);
	       document.add(tabAcc);       
	}
	
	public PdfPTable addCombinedStatementAccountDetails(String accountName,String accountNumber) {
		PdfPTable table=new PdfPTable(2);
		table.setTotalWidth(new float[]{ 100, 320 });
	    table.setHorizontalAlignment(0);
       table.getDefaultCell().setBorder(0);
       Phrase stat = new Phrase(accountName, combinedFont);
	      PdfPCell lbl = new PdfPCell(stat);
	      lbl.setBorder(0);
	      table.addCell(lbl);
	      Phrase stat1 = new Phrase(accountNumber,combinedBoldFont);
	      PdfPCell lbl2 = new PdfPCell(stat1);
	      lbl2.setBorder(0);
	      table.addCell(lbl2);
	      return table;
		
	}
	 private void init() {
	        depositTypes = new HashSet<>();
	        depositTypes.add("Deposit");
	        depositTypes.add("Interest");
	        depositTypes.add("ReceivedP2P");
	        depositTypes.add("ReceivedRequest");
	        depositTypes.add("Credit");
	    }
	public PdfPTable generateReportHeaderDetails(String[] combinedStatementReportDetails,String generatedOn,String generatedBy) {
		
		PdfPTable table=new  PdfPTable(1);
		table.getDefaultCell().setBorder(0);
	    Phrase p1 = new Phrase(combinedStatementReportDetails[0],combinedFont);
        PdfPCell cel = new PdfPCell(p1);
        cel.setBorder(0);
        table.addCell(cel);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        table.addCell(space);
        Phrase p2 = new Phrase(generatedBy,combinedBoldFont);
            PdfPCell cel2 = new PdfPCell(p2);
            cel2.setBorder(0);
           table.addCell(cel2);
           table.addCell(space);
           Phrase p3 = new Phrase(combinedStatementReportDetails[1],combinedFont);
           PdfPCell cel3 = new PdfPCell(p3);
           cel3.setBorder(0);
           table.addCell(cel3);
           table.addCell(space);
           Phrase p4 = new Phrase(generatedOn,combinedBoldFont);
           PdfPCell cel4 = new PdfPCell(p4);
           cel4.setBorder(0);
           table.addCell(cel4);
           
           return table;		
	}
	public PdfPTable generateStatementDetails(String[] combinedStatementDetails,String bankName,String startDate,String endDate) 
		{
		 PdfPTable table = new PdfPTable(2);
		 table.getDefaultCell().setBorder(0);
	        table.setTotalWidth(new float[]{ 120, 120 });
		 PdfPCell space = new PdfPCell();
	        space.setBorder(0);
		  Phrase p5 = new Phrase(combinedStatementDetails[0],combinedFont);
	      PdfPCell cel4 = new PdfPCell(p5);
	      cel4.setBorder(0);
	      table.addCell(cel4);
	      
	      Phrase p6 = new Phrase(bankName,combinedBoldFont);
	      PdfPCell cel5 = new PdfPCell(p6);
	      cel5.setBorder(0);
	      table.addCell(cel5);
	      
	      table.addCell(space);
	      table.addCell(space);
	      
	      Phrase p7 = new Phrase(combinedStatementDetails[1],combinedFont);
	      PdfPCell cel6 = new PdfPCell(p7);
	      cel6.setBorder(0);
	      table.addCell(cel6);
	      
	      Phrase p8 = new Phrase(startDate,combinedBoldFont);
	      PdfPCell cel7 = new PdfPCell(p8);
	      cel7.setBorder(0);
	      table.addCell(cel7);
	      
	      table.addCell(space);
	      table.addCell(space);
	      
	  
	      Phrase p9 = new Phrase(combinedStatementDetails[2],combinedFont);
	      PdfPCell cel8 = new PdfPCell(p9);
	      cel8.setBorder(0);
	      table.addCell(cel8);
	      
	      Phrase p10 = new Phrase(endDate,combinedBoldFont);
	      PdfPCell cel9 = new PdfPCell(p10);
	      cel9.setBorder(0);
	      table.addCell(cel9);
	      table.addCell(space);
			return table;
		}
	public void getUserDetails(PdfPTable table,String[] userDisplayName,String[] userKeys,JsonObject userDetails){
	
		for (int i = 0; i < userDisplayName.length; i++) {
		            Phrase p1 = new Phrase(userDisplayName[i]);
		            PdfPCell cel = new PdfPCell(p1);
		            cel.setBorder(0);
		            
		            if (null != this.userDetails && this.userDetails.has(userKeys[i])
		                    && !userDetails.get(userKeys[i]).isJsonNull()) {
		               Phrase p2 = new Phrase(userDetails.get(userKeys[i]).getAsString(), font);
		            PdfPCell cel2 = new PdfPCell(p2);
		            cel2.setBorder(0);
		            table.addCell(cel);
		            table.addCell(cel2);
		            
					}
					else{
					LOG.error("Error in generation of getUserDetails");
					}		
		}
	}
	private PdfPTable getTransactionsTable() {
		 PdfPTable table = new PdfPTable(6);
		   table.setSpacingBefore(10);
		   table.setWidthPercentage(100);
		   table.setWidths(new float[] { 1.5f,2.2f,3,1.5f,1f,1.3f});
		   return table;
    }
	private PdfPTable getTransactionsLoanTable() {
		 PdfPTable table = new PdfPTable(6);
		   table.setSpacingBefore(10);
		   table.setWidthPercentage(100);
		   table.setWidths(new float[] { 1.5f,2.2f,2.2f,1.2f,1.3f,1.3f});
		   return table;
   }
	private PdfPTable getCombinedStatementTable() {
		 PdfPTable table = new PdfPTable(5);
		   table.setSpacingBefore(10);
		   table.setWidthPercentage(100);
		   table.setWidths(new float[] { 2, 3, 3, 2, 2});
		   return table;
  }
	
	public void getContactNumber(PdfPTable table,JsonObject userDetails) {
		Font font = new Font(Font.TIMES_ROMAN, 12, Font.ITALIC, Color.BLACK);
	  if(JSONUtil.hasKey(this.userDetails, "ContactNumbers")) {
      	JsonArray communications =this.userDetails.getAsJsonArray("ContactNumbers");
      	for (JsonElement communicationElement : communications ) {
      	    JsonObject communicationObject = communicationElement.getAsJsonObject();
      	    if(JSONUtil.hasKey(communicationObject, "isPrimary" ) && 
      	    		communicationObject.get("isPrimary").getAsString().equals("true")) {
                  String contactNumber = JSONUtil.hasKey(communicationObject, "Value" ) ? 
                          		communicationObject.get("Value").getAsString() : "";
                  if(StringUtils.isNotBlank(contactNumber)) {
                	  Phrase p1 = new Phrase("Phone:");
  		            PdfPCell cel = new PdfPCell(p1);
  		          cel.setBorder(0);
  		        Phrase p2 = new Phrase(contactNumber, font);
	            PdfPCell cel2 = new PdfPCell(p2);
	            cel2.setBorder(0);
	            table.addCell(cel);
	            table.addCell(cel2);
                  }
      	    	break;
      	    }
      	}
      }
	}

	public void getEmailId(PdfPTable table,JsonObject userDetails) {
		 if(JSONUtil.hasKey(this.userDetails, "EmailIds")) {
	        	JsonArray communications =this.userDetails.getAsJsonArray("EmailIds");
	        	for (JsonElement communicationElement : communications ) {
	        	    JsonObject communicationObject = communicationElement.getAsJsonObject();
	        	    if(JSONUtil.hasKey(communicationObject, "isPrimary" ) && 
	        	    		communicationObject.get("isPrimary").getAsString().equals("true")) {
	                    String emailId = JSONUtil.hasKey(communicationObject, "Value" ) ? 
	                            		communicationObject.get("Value").getAsString() : "";
	                    if(StringUtils.isNotBlank(emailId)) {
                	  Phrase p1 = new Phrase("E-Mail:");
  		            PdfPCell cel = new PdfPCell(p1);
  		          cel.setBorder(0);
  		        Phrase p2 = new Phrase(emailId, font);
	            PdfPCell cel2 = new PdfPCell(p2);
	            cel2.setBorder(0);
	            table.addCell(cel);
	            table.addCell(cel2);
                  }
      	    	break;
      	    }
      	}
      }
	}
	
	public void getAddress(PdfPTable table,JsonObject userDetails) {
		Font font = new Font(Font.TIMES_ROMAN, 12, Font.ITALIC, Color.BLACK);
	  StringBuilder sb = new StringBuilder();
      String[] addressComps = new String[] { "AddressLine1", "AddressLine2", "CityName", "ZipCode" };
      
      JsonObject primaryAddress=new JsonObject();
      if(null != this.userDetails 
      		&& this.userDetails.has("Addresses")) {
      	JsonArray address=this.userDetails.getAsJsonArray("Addresses");
      	
      	for (JsonElement addressElement : address) {
      	    JsonObject addressObject = addressElement.getAsJsonObject();
      	    if(addressObject.has("isPrimary") && addressObject.get("isPrimary").getAsString().equals("true")) {
      	    	primaryAddress=addressObject;
      	    	break;
      	    }
      	}
      }
      for (int i = 0; i < addressComps.length; i++) {
          if (null != primaryAddress &&  primaryAddress.has(addressComps[i])) {
              if (sb.length() > 0) {
                  sb.append(", ");
              }
              sb.append(primaryAddress.get(addressComps[i]).getAsString());
          }
      }
      Phrase p1 = new Phrase("Address:");
        PdfPCell cel = new PdfPCell(p1);
      cel.setBorder(0);
    Phrase p2 = new Phrase(sb.toString(), font);
  PdfPCell cel2 = new PdfPCell(p2);
  cel2.setBorder(0);
  table.addCell(cel);
  table.addCell(cel2);
      
	}
	public void addLoanDetails(PdfPTable table,Map<String, String> summary) {
		if (this.summary != null && this.summary.size() > 0) {
			if (this.summary.containsKey("OverDueInstallmentsCount")) {
				Phrase p1 = new Phrase("OverDue Payments: ");
		        PdfPCell cel = new PdfPCell(p1);
		        cel.setBorder(0);
		          Phrase p2 = new Phrase(this.summary.get("OverDueInstallmentsCount"), font);
		            PdfPCell cel2 = new PdfPCell(p2);
		            cel2.setBorder(0);
		            table.addCell(cel);
		            table.addCell(cel2);
			}
			if (this.summary.containsKey("PaidInstallmentsCount")) {
				
				Phrase p1 = new Phrase("Paid Installments: ");
		        PdfPCell cel = new PdfPCell(p1);
		        cel.setBorder(0);
		          Phrase p2 = new Phrase(this.summary.get("PaidInstallmentsCount"), font);
		            PdfPCell cel2 = new PdfPCell(p2);
		            cel2.setBorder(0);
		            table.addCell(cel);
		            table.addCell(cel2);
			}
			if (this.summary.containsKey("FutureInstallmentsCount")) {
				
				Phrase p1 = new Phrase("Future Payments: ");
		        PdfPCell cel = new PdfPCell(p1);
		        cel.setBorder(0);
		          Phrase p2 = new Phrase(this.summary.get("FutureInstallmentsCount"), font);
		            PdfPCell cel2 = new PdfPCell(p2);
		            cel2.setBorder(0);
		            table.addCell(cel);
		            table.addCell(cel2);
				
			}

		}
	}
	public void addDate(PdfPTable table,String startDate,String endDate) {
		 Phrase stat = new Phrase("Account Statement\n\n", FontFactory.getFont("Helvetica", 12.0F, 3));
	      PdfPCell lbl = new PdfPCell(stat);
	      lbl.setBorder(0);
	      table.addCell(lbl);
	      if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
	      Phrase stat1 = new Phrase("Statement from "+startDate+" to "+endDate);
	      PdfPCell lbl2 = new PdfPCell(stat1);
	      lbl2.setBorder(0);
	      table.addCell(lbl2);
	      }
	}
	public void addTransactionsHeader(PdfPTable table,String[] headers) {
		for(int i=0;i<headers.length;i++) {
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(new Color(240,255,255));
		      cell.setPhrase(new Phrase(headers[i], font));
		       table.addCell(cell);
		}
	}
	public void addCombinedStatmentHeader(PdfPTable table,String[] headers) {
		for(int i=0;i<headers.length;i++) {
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(new Color(211,211,211));
		      cell.setPhrase(new Phrase(headers[i], font));
		       table.addCell(cell);
		}
	}
	private void addTransactionsData(JsonArray data, PdfPTable transTable) {
        if (null != data && data.size() > 0) {
            for (int i = data.size() - 1; i >= 0; i--) {
                JsonObject rowData = data.get(i).getAsJsonObject();
                addRow(transTable, rowData,this.fields);
                String amount = rowData.get("amount").getAsString();
                if (!amount.startsWith("-")) {
                	 transTable.addCell(new PdfPCell());
                    transTable.addCell(new Phrase(amount,this.font));
                } else {
                    transTable.addCell(new Phrase(amount,this.font));
                   transTable.addCell(new PdfPCell());
                }
                if (rowData.has("fromAccountBalance")) {
                    transTable.addCell(new Phrase(rowData.get("fromAccountBalance").getAsString(),this.font));
                    
                }
            }
        }
    }

    private void addRow(PdfPTable transTable, JsonObject rowData,String[] fields) {
        try {
            for (int i = 0; i < fields.length; i++) {
                if (rowData.has(fields[i])) {
                    String data = rowData.get(fields[i]).getAsString();
                    if ("transactionDate".equals(fields[i])) {
                        data = HelperMethods.convertDateFormat(data, "MM/dd/yyyy");
                    }
                    if ("Date".equals(fields[i])) {
                        data = HelperMethods.convertDateFormat(data, this.paymentDateFormat);
                    }
                    transTable.addCell(new Phrase(data,this.font));
                } else {
                	transTable.addCell(new PdfPCell());
                }
            }
        } catch (Exception e) {
            LOG.error("Error in generation of pdf",e);
        }
    }
    
    private void addCombinedStatmentRow(PdfPTable transTable, JsonObject rowData,String[] fields) {
        try {
            for (int i = 0; i < fields.length; i++) {
                if (rowData.has(fields[i]) && StringUtils.isNotBlank(rowData.get(fields[i]).getAsString())) {
                    String data = rowData.get(fields[i]).getAsString();
                    if ("transactionDate".equals(fields[i])) {
                        data = HelperMethods.convertDateFormat(data, "MM/dd/yyyy");
                    }
                    if ("amount".equals(fields[i])) {
                        data = this.currencyCode + data;
                    }
                    if ("fromAccountBalance".equals(fields[i])) {
                        data = this.currencyCode + data;
                    }
                    if ("transactionDate".equals(fields[i])) {
                    	transTable.addCell(new Phrase(data,this.combinedBoldFont));
                    } else {
                    	transTable.addCell(new Phrase(data,this.combinedFont));
                    }
                } else {
                	transTable.addCell(new PdfPCell());
                } 
            }
            
        } catch (Exception e) {
            LOG.error("Error in generation of pdf",e);
        }
    }

}