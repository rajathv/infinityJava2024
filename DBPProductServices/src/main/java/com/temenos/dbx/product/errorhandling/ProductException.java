package com.temenos.dbx.product.errorhandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class ProductException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private String errCode;
  private String errMsg;
  private static final Logger LOGGER = LogManager.getLogger(ProductException.class);
  
  public ProductException(ErrorCodeEnum errorCodeEnum) {
    if (errorCodeEnum == null) {
      
      LOGGER.error("Invalid input errorCodeEnum: null");
      initializeProductException(ErrorCodeEnum.ERR_10000);
    } else {
      initializeProductException(errorCodeEnum);
    } 
  }
  
  public ProductException(ErrorCodeEnum errorCodeEnum, String customErrorMessageToAppend) {
    if (errorCodeEnum == null) {
      
      LOGGER.error("Invalid input errorCodeEnum: null");
      initializeProductException(ErrorCodeEnum.ERR_10000, customErrorMessageToAppend);
    } else {
      initializeProductException(errorCodeEnum, customErrorMessageToAppend);
    } 
  }
  
  private void initializeProductException(ErrorCodeEnum errorCodeEnum, String customErrorMessageToAppend) {
    this.errCode = errorCodeEnum.getErrorCodeAsString();
    this.errMsg = errorCodeEnum.getMessage() + ". " + customErrorMessageToAppend;
  }
  
  private void initializeProductException(ErrorCodeEnum errorCodeEnum) {
    this.errCode = errorCodeEnum.getErrorCodeAsString();
    this.errMsg = errorCodeEnum.getMessage();
  }

  
  public String getErrorCode() { return this.errCode; }


  
  public String getErrorMessage() { return this.errMsg; }


  
  public void appendToErrorMessage(String stringToBeAppended) { this.errMsg += ". " + stringToBeAppended; }


  
  public Result constructResultObject() {
    Result result = new Result();
    result.addParam(new Param("errorCode", this.errCode));
    result.addParam(new Param("errorMessage", this.errMsg));
    return result;
  }
  
  public Result updateResultObject(Result result) {
    result.addParam(new Param("errorCode", this.errCode));
    result.addParam(new Param("errorMessage", this.errMsg));
    return result;
  }
}
