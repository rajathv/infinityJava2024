/**
 * 
 */
package com.temenos.infinity.api.wealth.javaservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealth.resource.api.DownloadAttachmentsPDFResource;

/**
 * @author himaja.sridhar
 *
 */
public class GeneratePDF implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GeneratePDF.class);
	Map<String, String> responseHeaders = new HashMap<String, String>();

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			responseHeaders.put(HttpHeaders.CONTENT_TYPE, "application/pdf");
			responseHeaders.put("Content-Disposition", "attachment; filename=\"" + "Holdings" + ".pdf" + "\"");
			DownloadAttachmentsPDFResource downloadAttachmentsPDFResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(DownloadAttachmentsPDFResource.class);
			result = downloadAttachmentsPDFResource.generatePDF(methodId, inputArray, request, response);
			if(result.getParamValueByName("base64")!=null) {
			if (result.getParamValueByName("base64").length() > 0) {

				/*
				 * File file =
				 * constructFileObjectFromBase64String(result.getParamValueByName("base64"));
				 * 
				 * FileEntity fentity = new FileEntity(file);
				 * 
				 * BufferedHttpEntity BufferedHttpEntity = new BufferedHttpEntity(fentity);
				 * response.setAttribute(FabricConstants.CHUNKED_RESULTS_IN_JSON, new
				 * BufferedHttpEntity(new FileEntity(file))); response.setAttribute("resultVal",
				 * BufferedHttpEntity); response.getHeaders().putAll(responseHeaders);
				 * file.deleteOnExit(); response.setStatusCode(HttpStatus.SC_OK);
				 * LOG.info("Succesfully constructed the File object");
				 */
				return result;
			} else {
				LOG.error("Unable to return the file object.");
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
				
			}
			}
			else {
			 return result;
			}
		} catch (Exception e) {
			LOG.error("Caught exception at invoke of GeneratePDF: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
	}

	public File constructFileObjectFromBase64String(String base64FileContent) throws Exception {
		byte[] decodedFileContent = Base64.decodeBase64(base64FileContent.getBytes("UTF-8"));
		File file = File.createTempFile("Holdings", "20211101.pdf");
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(decodedFileContent);
		} catch (IOException e) {
			LOG.error("Exception in Constructing  File Output Stream", e);
		}

		finally {
			try {
				if (fileOutputStream != null)
					fileOutputStream.close();
			} catch (IOException e) {
				LOG.error("Exception in Closing  File Output Stream", e);
			}

		}
		return file;
	}

}
