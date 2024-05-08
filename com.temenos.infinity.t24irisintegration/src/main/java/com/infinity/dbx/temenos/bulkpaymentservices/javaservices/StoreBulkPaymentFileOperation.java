package com.infinity.dbx.temenos.bulkpaymentservices.javaservices;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.tocf.tcc.TCCFactory;
import com.temenos.tocf.tcc.TCClientException;
import com.temenos.tocf.tcc.TCConnection;
import com.temenos.tocf.tcc.TCOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StoreBulkPaymentFileOperation implements JavaService2{
    private static final Logger LOG = LogManager.getLogger(StoreBulkPaymentFileOperation.class);
    //private static final String CHANNEL_XML  = System.getProperty("middleware.home") +"//middleware//middleware-bootconfig//customlib//default//conf//channels.xml";
    private static final String CHANNEL_NAME = "FileUploadDownloadChannel";
    //private static final String PORT_XPATH = "/CHANNELS/CHANNEL/ADAPTER/PORT";
    //private static final String HOST_XPATH = "/CHANNELS/CHANNEL/ADAPTER/SUPPLIER/INITIATOR/HOSTNAME";
    //private static final String T24_STORE_HOST_AND_PORT = "T24_STORE_API_HOST_URL";
    private static final String UPLOAD_FOLDER_CSV = "BULK.PAYMENTS.CSV";
    private static final String UPLOAD_ID_CSV = "BULK.PAYMENT.CSV";
    private static final String UPLOAD_FOLDER_XML = "BULK.PAYMENTS.XML";
    private static final String UPLOAD_ID_XML = "BULK.PAYMENT.PAIN001";
    private static final String UPLOAD_ID_XML_V9 = "BULK.PAYMENT.PAIN001V9";
    private static final String UPLOAD_ID_XML_DESCRIPTION = "pain.001.001.03";
    
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {    
        
        Result result= null;
        try{
        	
        	//String storeHost = EnvironmentConfigurationsHandler.getValue(T24_STORE_HOST_AND_PORT, request);
        	//String[] storeapi = storeHost.split(":");
        	result =  new Result();
        	/*if(!createChannelXML()) {
        		result.addParam(new Param("errMsg", "Failed to create channels xml"));
        		return null;
        	}*/
            /*if( updateChannelXMLFile(storeapi[0], storeapi[2])) { */
            	
            	@SuppressWarnings("unchecked")
				Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
            	
            	String fileName = inputParams.get("fileName") != null ? inputParams.get("fileName").toString() : null;
            	String content = inputParams.get("content") != null ? inputParams.get("content").toString() : null;
            	String description = inputParams.get("description") != null ? inputParams.get("description").toString() : null;
            	
                String fileBaseName = FilenameUtils.getBaseName(fileName) + getCurrentTimeStamp();
                String fileExtension = FilenameUtils.getExtension(fileName);
                String uploadTypeId = null;
                String uploadFolder = null;
                
                String systemGenFileName = fileBaseName + "." + fileExtension;
                byte[] bulkPaymentFileContent = Base64.getDecoder().decode(new String(content).getBytes("UTF-8"));              
                                
                if("csv".equalsIgnoreCase(fileExtension)) {
                	uploadTypeId = UPLOAD_ID_CSV;
                	uploadFolder = UPLOAD_FOLDER_CSV;
                }
                else if("xml".equalsIgnoreCase(fileExtension)) {					
					String utf = new String(bulkPaymentFileContent, "UTF-8");
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setNamespaceAware(true);
					factory.setValidating(true);
					factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
					factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
					DocumentBuilder builder = factory.newDocumentBuilder();
					InputSource is = new InputSource(new StringReader(utf));
					Document doc = builder.parse(is);
					if (doc != null && doc.getFirstChild() != null && doc.getFirstChild().getNamespaceURI() != null
							&& doc.getFirstChild().getNamespaceURI().contains(UPLOAD_ID_XML_DESCRIPTION)) {
						uploadTypeId = UPLOAD_ID_XML;
					} else {
						uploadTypeId = UPLOAD_ID_XML_V9;
					}
					uploadFolder = UPLOAD_FOLDER_XML;
                }
                
                if(openConnectionAndUploadFile(bulkPaymentFileContent, systemGenFileName, uploadFolder)) {
                
	                result.addParam(new Param("fileName", systemGenFileName + "||" +bulkPaymentFileContent.length + "||" + fileName));
	                result.addParam(new Param("uploadSize", bulkPaymentFileContent.length + ""));
	                result.addParam(new Param("uploadTypeId", uploadTypeId));
	                result.addParam(new Param("displayName", description));
	                result.addParam(new Param("sysGeneratedFileName", systemGenFileName));
                }
                else {
                	result.addParam(new Param("errMsg", "Failed while connecting and uploading the file"));
                }
           /* }
            else {
            	result.addParam(new Param("errMsg", "Failed while updating the channel xml file"));
            }*/
            
        }
        catch(Exception e) {
            LOG.error("Error occured while storing bulk payment file", e);
            return null;
        }

        return result;
    }
    
    /*private boolean updateChannelXMLFile(String hostname, String port) {
    	
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(CHANNEL_XML));
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node portNode = (Node) xPath.compile(PORT_XPATH).evaluate(doc, XPathConstants.NODE);
            portNode.setTextContent(port);

            xPath = XPathFactory.newInstance().newXPath();
            Node hostNode = (Node) xPath.compile(HOST_XPATH).evaluate(doc, XPathConstants.NODE);
            hostNode.setTextContent(hostname);

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty(OutputKeys.METHOD, "xml");            

            DOMSource domSource = new DOMSource(doc);
            StreamResult sr = new StreamResult(new File(CHANNEL_XML));
            tf.transform(domSource, sr);
        } 
        catch (Exception e) {            
            LOG.error("Error occured while updating channel xml",e);
            return false;
        }
        
        return true;
    }
    */
   
    
    private boolean openConnectionAndUploadFile(byte[] bytes, String fileName, String uploadDir) throws TCClientException {
    	
    	TCConnection connection = null; 
    	TCOutputStream connOutStream = null;
    	boolean result = true;
    	
    	try
    	{
    		TCCFactory tcf = TCCFactory.getInstance();
    		
    		connection = tcf.createTCConnection(CHANNEL_NAME);
    		connOutStream = connection.getOutputStream();
    		
    		if (StringUtils.isNotBlank(fileName))
    			connOutStream.setFileName(fileName);
    		
    		if (StringUtils.isNotBlank(uploadDir))
    			connOutStream.setFilePath(uploadDir);

    		connOutStream.send(bytes);
    	}
    	catch (Exception e) {
    		LOG.error("Error occured while creating the connection with remote and uploading the file",e);
    		result = false;
    	} 
    	finally {
    		if(connection != null)
    			connection.close();
    	}

    	return result;
    }
    
    private String getCurrentTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmsss");
        Calendar calendar = Calendar.getInstance();

        return dateFormat.format(calendar.getTime());
    }
    
    /*private boolean createChannelXML() {
    	
		File fileObj = new File(CHANNEL_XML);
		
		if(fileObj.exists())
			return true;
		
    	try {
    		DocumentBuilderFactory dbFactory =
    				DocumentBuilderFactory.newInstance();

    		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    		Document doc = dBuilder.newDocument();

    		Element rootElement = doc.createElement("CHANNELS");
    		Element channel = doc.createElement("CHANNEL");
    		Element channelName = doc.createElement("NAME");
    		channelName.setTextContent(CHANNEL_NAME);
    		Element timeout = doc.createElement("TIMEOUT");
    		timeout.setTextContent("120");
    		Element adapter = doc.createElement("ADAPTER");
    		Element type = doc.createElement("TYPE");
    		type.setTextContent("tcp");
    		Element port = doc.createElement("PORT");
    		port.setTextContent("10003");
    		Element supplier = doc.createElement("SUPPLIER");
    		Element consume = doc.createElement("CONSUMER");
    		Element initiator = doc.createElement("INITIATOR");
    		Element hostname = doc.createElement("HOSTNAME");
    		hostname.setTextContent("127.0.0.1");
    		Element max_session = doc.createElement("MAX_SESSION");
    		max_session.setTextContent("5");
    		Element accpetor = doc.createElement("ACCEPTOR");
    		Element backlog = doc.createElement("BACKLOG");
    		backlog.setTextContent("30");

    		doc.appendChild(rootElement);
    		rootElement.appendChild(channel);
    		channel.appendChild(channelName);
    		channel.appendChild(timeout);
    		channel.appendChild(adapter);

    		adapter.appendChild(type);
    		adapter.appendChild(port);
    		adapter.appendChild(supplier);
    		adapter.appendChild(consume);

    		supplier.appendChild(initiator);
    		initiator.appendChild(hostname);

    		consume.appendChild(max_session);
    		consume.appendChild(accpetor);

    		accpetor.appendChild(backlog);
    		
    		TransformerFactory transformerFactory = TransformerFactory.newInstance();
    		Transformer transformer = transformerFactory.newTransformer();
    		DOMSource source = new DOMSource(doc);
    		
    		Path pathToFile = Paths.get(CHANNEL_XML);
    		Files.createDirectories(pathToFile.getParent());
    		Files.createFile(pathToFile);
    		
    		FileWriter writer = new FileWriter(new File(CHANNEL_XML));
    		StreamResult result = new StreamResult(writer);
    		transformer.transform(source, result);
    	}
    	catch(ParserConfigurationException e) {
    		LOG.error("error in File Parsing",e);
    		return false;
    	}
    	catch(TransformerException e) {
    		LOG.error("error in transform",e);
    		return false;
    	}
    	catch(IOException e) {
    		LOG.error("error in File write",e);
    		return false;
    	}
    	
    	return true;
    }
    */
    
}