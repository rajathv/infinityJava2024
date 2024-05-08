package com.temenos.dbx.product.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.dto.DBXResult;

public class HTTPOperations
{

    private static LoggerUtil logger;

    public static enum operations{
        POST,PUT,GET,DELETE
    }

    public static DBXResult sendHttpRequest(operations operation, String requestUrl, String payload, Map<String,Object> headers) {
        logger = new LoggerUtil(HTTPOperations.class);

        DBXResult dbxResult = new DBXResult();
        logger.debug("REQUEST_URL in request is : "+requestUrl);
        
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e1) {
            dbxResult.setDbpErrMsg("Invalid URL");
            return dbxResult;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
        } catch (IOException e1) {
            dbxResult.setDbpErrMsg("Unable to open connection");
            return dbxResult;
        }

        try {
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod(operation.toString());

            if(headers != null) {
                for(Entry<String, Object> entry : headers.entrySet()) {
                    if(StringUtils.isBlank((String)entry.getValue())) {
                        continue;
                    }
                    if(entry.getKey().equals("Authorization")) {
                        logger.debug("Authorization in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equals("channelName")) {
                        logger.debug("channel Name in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equalsIgnoreCase("companyid")) {
                        logger.debug("companyid in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equals("X-Kony-Authorization")) {
                        logger.debug("X-Kony-Authorization in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equals("x-pay-token")) {
                        logger.debug("x-pay-token in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equals("x-api-key")) {
                        logger.debug("x-api-key in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                    if(entry.getKey().equals("x-functions-key")) {
                        logger.debug("x-functions-key in request is : "+entry.getValue());
                        connection.setRequestProperty(entry.getKey(), (String)entry.getValue());
                    }
                }
            }

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if (payload != null && !payload.isEmpty()) {
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();
            } 
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            br.close();

            String successResponse = stringBuffer.toString();

            logger.debug("successResponse is : "+successResponse);
            
            dbxResult.setResponse(successResponse);

            connection.disconnect();
            return dbxResult;
        } catch (Exception e) {
            logger.error("Caught exception while Sending HTTP Request: for URL -> "+requestUrl +" ", e);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    stringBuffer.append(line);
                }
                br.close();
            } catch (IOException e1) {

            }


            String errorResponse = stringBuffer.toString();
            
            logger.debug("errorResponse is : "+errorResponse);

            if(StringUtils.isNotBlank(errorResponse)) {
                dbxResult.setResponse(errorResponse);
            }
            connection.disconnect();
            logger.error("Caught exception while Sending HTTP Request: for URL -> "+requestUrl +" ", e);
        } 


        return dbxResult;
    }
}
