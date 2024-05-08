package com.temenos.infinity.tradefinanceservices.resource.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dbp.core.util.JSONUtils;
import com.temenos.infinity.tradefinanceservices.utils.AlertsEnum;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.CreateLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.ExportLetterOfCreditsDrawingsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetAmendmentsLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetLetterOfCreditsByIDBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.UpdateLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.BBRequestDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditStatusDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsActionDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.CreateLetterOfCreditsResource;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.*;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getCurrentDateTimeUTF;

public class CreateLetterOfCreditsResourceImpl implements CreateLetterOfCreditsResource {
    private static final Logger LOG = LogManager.getLogger(CreateLetterOfCreditsResourceImpl.class);
    private boolean isAlertEligible = false;
    static String[] keys = new String[]{"availableWith1", "availableWith2", "availableWith3", "availableWith4",
            "documentCharges", "lcReferenceNo", "expiryPlace", "beneficiaryName", "beneficiaryAddressLine1",
            "beneficiaryAddressLine2", "beneficiaryBankPostCode", "beneficiaryBank", "beneficiaryBankAdressLine1",
            "beneficiaryBankAdressLine2", "beneficiaryBankCountry", "placeOfTakingIncharge", "portOfLoading",
            "portOfDischarge", "placeOfFinalDelivery", "additionalConditionsCode", "supportDocuments", "messageToBank",
            "lcAmount", "maximumCreditAmount", "chargesPaid", "creditAmount"};
    static Integer[] values = new Integer[]{50, 50, 50, 50, 35, 35, 35, 35, 50, 50, 35, 35, 50, 50, 35, 35, 35, 35,
            35, 35, 35, 200, 35, 35, 35, 35};
    static Map<String, Integer> validationMap = IntStream.range(0, keys.length).boxed()
            .collect(Collectors.toMap(i -> keys[i], i -> values[i]));
    GetAmendmentsLetterOfCreditsBusinessDelegate amendmentBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(GetAmendmentsLetterOfCreditsBusinessDelegate.class);
    AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getInstance().getFactoryInstance(BusinessDelegateFactory.class)
            .getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
    CreateLetterOfCreditsBusinessDelegate lcCreateBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(CreateLetterOfCreditsBusinessDelegate.class);
    UpdateLetterOfCreditsBusinessDelegate lcUpdateBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(UpdateLetterOfCreditsBusinessDelegate.class);
    GetLetterOfCreditsByIDBusinessDelegate lcGetBusinessDelegate = DBPAPIAbstractFactoryImpl
            .getBusinessDelegate(GetLetterOfCreditsByIDBusinessDelegate.class);

    @SuppressWarnings("unused")
    @Override
    public Result createLetterOfCredits(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request) {
        LetterOfCreditsDTO LCOrder;
        Result result = new Result();
        LetterOfCreditsDTO isSRMSID_match;
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);

        if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());

        // checking fields
        String lcReferenceNo = letterOfCredit.getLcReferenceNo();
        String issueDate = request.getParameter("issueDate") != null ? request.getParameter("issueDate") : "";
        String expiryDate = request.getParameter("expiryDate") != null ? request.getParameter("expiryDate") : "";
        String latestShippingDate = request.getParameter("latestShippingDate") != null
                ? request.getParameter("latestShippingDate")
                : "";
        String chargesAccount = request.getParameter("chargesAccount") != null ? request.getParameter("chargesAccount")
                : "";
        String marginAccount = request.getParameter("marginAccount") != null ? request.getParameter("marginAccount")
                : "";
        String commisionAccount = request.getParameter("commisionAccount") != null
                ? request.getParameter("commisionAccount")
                : "";
        Map<String, String> inputMap = null;
        try {
            inputMap = JSONUtils.parseAsMap((new JSONObject(letterOfCredit)).toString(), String.class, String.class);
        } catch (IOException e) {
            LOG.error("Failed to create letter of credits", e);
        }
        boolean isValid = validateInputParams(letterOfCredit, inputMap);
        if (!isValid) {
            return ErrorCodeEnum.ERR_10118.setErrorCode(new Result());
        }

        if (StringUtils.isNotBlank(issueDate)) {
            if (!(_isDateValidate(issueDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }

        if (StringUtils.isNotBlank(expiryDate)) {
            if (!(_isDateValidate(expiryDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }

        if (StringUtils.isNotBlank(latestShippingDate)) {
            if (!(_isDateValidate(latestShippingDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }

        // Check whether user has the permission to the account
        String accountEnding = "";
        String errorMessage = "";
        if (StringUtils.isNotBlank(chargesAccount)) {
            if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(customerId, chargesAccount)) {
                if (chargesAccount.length() > 3)
                    accountEnding = chargesAccount.substring(chargesAccount.length() - 3);
                errorMessage = "You do not have permission to the Charges Account ending xxx" + accountEnding + ".";
                return ErrorCodeEnum.ERR_10118.setErrorCode(result, errorMessage);
            }
        }
        if (StringUtils.isNotBlank(marginAccount)) {
            if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(customerId, marginAccount)) {
                if (marginAccount.length() > 3)
                    accountEnding = marginAccount.substring(marginAccount.length() - 3);
                errorMessage = "You do not have permission to the Margin Account ending xxx" + accountEnding + ".";
                return ErrorCodeEnum.ERR_10118.setErrorCode(result, errorMessage);
            }
        }
        if (StringUtils.isNotBlank(commisionAccount)) {
            if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(customerId, commisionAccount)) {
                if (commisionAccount.length() > 3)
                    accountEnding = commisionAccount.substring(commisionAccount.length() - 3);
                errorMessage = "You do not have permission to the Commission Account ending xxx" + accountEnding + ".";
                return ErrorCodeEnum.ERR_10118.setErrorCode(result, errorMessage);
            }
        }

        // Mandatory Flow type - for Final submit
        if (letterOfCredit.getFlowType().equalsIgnoreCase("finalSubmit")) {
            if (StringUtils.isNotBlank(letterOfCredit.getLcReferenceNo())
                    && StringUtils.isNotBlank(String.valueOf(letterOfCredit.getLcAmount()))
                    && StringUtils.isNotBlank(letterOfCredit.getLcCurrency())
                    && StringUtils.isNotBlank(letterOfCredit.getPaymentTerms())
                    && StringUtils.isNotBlank(letterOfCredit.getAvailableWith1())
                    && StringUtils.isNotBlank(letterOfCredit.getIssueDate())
                    && StringUtils.isNotBlank(letterOfCredit.getExpiryDate())
                    && StringUtils.isNotBlank(letterOfCredit.getExpiryPlace())
                    && StringUtils.isNotBlank(letterOfCredit.getBeneficiaryName())
                    && StringUtils.isNotBlank(letterOfCredit.getBeneficiaryAddressLine1())
                    && StringUtils.isNotBlank(letterOfCredit.getBeneficiaryBank())
                    && StringUtils.isNotBlank(letterOfCredit.getIncoTerms())
                    && StringUtils.isNotBlank(letterOfCredit.getDocumentCharges())
                    && StringUtils.isNotBlank(letterOfCredit.getConfirmationInstruction())) {
                if (letterOfCredit.getSrmsReqOrderID() != "") {
                    try {
                        isSRMSID_match = lcGetBusinessDelegate.getLetterOfCreditsByID(letterOfCredit, request);
                        if (isSRMSID_match.isReferenceNomatch()) {
                            try {
                                if (StringUtils.isNotBlank(customerId)
                                        && customerId.equalsIgnoreCase(isSRMSID_match.getCustomerId())) {
                                    LCOrder = lcUpdateBusinessDelegate.updateLetterOfCredits(letterOfCredit,
                                            request);
                                    LOG.info("Finalsubmit LOC has been updated");
                                } else {
                                    return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
                                }
                            } catch (Exception e) {
                                LOG.error("Failed to Update letter of credits request in OMS", e);
                                return ErrorCodeEnum.ERRTF_29048.setErrorCode(new Result());
                            }
                        } else {
                            try {
                                LCOrder = lcCreateBusinessDelegate.createLetterOfCredits(letterOfCredit, request);
                                LOG.info("Finalsubmit LOC has been created");
                            } catch (Exception e) {
                                LOG.error("Failed to Create letter of credits request in OMS", e);
                                return ErrorCodeEnum.ERRTF_29049.setErrorCode(new Result());
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to verify Request ID of letter of credits request in OMS", e);
                        return ErrorCodeEnum.ERRTF_29048.setErrorCode(new Result());
                    }
                } else {
                    try {
                        LCOrder = lcCreateBusinessDelegate.createLetterOfCredits(letterOfCredit, request);
                    } catch (Exception e) {
                        LOG.error("Failed to create letter of credits request in OMS", e);
                        return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
                    }

                }
                isAlertEligible = StringUtils.isNotBlank(LCOrder.getSrmsReqOrderID());
            } else {
                LOG.error("Mandatory fields are missing");
                return ErrorCodeEnum.ERRTF_29049.setErrorCode(new Result());
            }
        }

        // Draft Flow type
        else {
            if (StringUtils.isNotBlank(lcReferenceNo)) {
                // In case of Draft to updated
                if (letterOfCredit.getSrmsReqOrderID() != "") {
                    try {
                        isSRMSID_match = lcGetBusinessDelegate.getLetterOfCreditsByID(letterOfCredit, request);
                        if (isSRMSID_match.isReferenceNomatch()) {
                            try {
                                if (StringUtils.isNotBlank(customerId)
                                        && customerId.equalsIgnoreCase(isSRMSID_match.getCustomerId())) {
                                    LCOrder = lcUpdateBusinessDelegate.updateLetterOfCredits(letterOfCredit,
                                            request);
                                    LOG.info("Draft LOC has been updated");
                                } else {
                                    return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
                                }
                            } catch (Exception e) {
                                LOG.error("Mandatory fields are missing");
                                return ErrorCodeEnum.ERRTF_29049.setErrorCode(new Result());
                            }
                        } else {
                            return ErrorCodeEnum.ERRTF_29051.setErrorCode(new Result(), "SRMSID fetch failed");
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to verify Request ID of letter of credits request in OMS", e);
                        return ErrorCodeEnum.ERRTF_29048.setErrorCode(new Result());
                    }

                }
                // In case of Draft to Created
                else {

                    try {
                        LCOrder = lcCreateBusinessDelegate.createLetterOfCredits(letterOfCredit, request);
                    } catch (Exception e) {
                        LOG.error("Failed to create letter of credits request in OMS", e);
                        return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
                    }
                }
            } else {
                LOG.error("Field ReferenceNo is Blank");
                return ErrorCodeEnum.ERRTF_29049.setErrorCode(new Result());
            }
        }

        if (LCOrder == null) {
            LOG.error("Error occurred while Creating letter of credits from backend");
        }
        if (StringUtils.isBlank(LCOrder.getSrmsReqOrderID())) {
            return ErrorCodeEnum.ERRTF_29052.setErrorCode(new Result());
        }
        if (StringUtils.isNotBlank(LCOrder.getMsg())) {
            result.addParam("ErrorMsg", LCOrder.getMsg());
            return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result(), LCOrder.getMsg());
        }

        JSONObject responseObj = new JSONObject(LCOrder);
        result = JSONToResult.convert(responseObj.toString());
        result.addParam("srmsReqOrderId", LCOrder.getSrmsReqOrderID());
        result.addParam("status", LCOrder.getStatus());
        if (isAlertEligible) {
            TradeFinanceCommonUtils.setAlertDataInResult(result, AlertsEnum.IMPORT_LC_CREATED, LCOrder.getSrmsReqOrderID());
        }
        return result;
    }

    @Override
    public Result executeLetterOfCreditsRequest(String methodId, Object[] inputArray, DataControllerRequest request,
                                                DataControllerResponse response) {
        Result result = new Result();
        try {
            Map<String, Object> customer = CustomerSession.getCustomerMap(request);
            String customerId = CustomerSession.getCustomerId(customer);
            Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
            String serviceRequestId = request.getParameter("serviceRequestId") != null
                    ? request.getParameter("serviceRequestId").toString()
                    : null;
            String requestId = request.getParameter("requestId") != null ? request.getParameter("requestId").toString()
                    : "";
            String comments = inputParams.get("comments") != null ? inputParams.get("comments").toString() : "";
            String signatoryApproved = inputParams.get("signatoryApproved") != null
                    ? inputParams.get("signatoryApproved").toString()
                    : "";

            LetterOfCreditsActionDTO lcOrder = new LetterOfCreditsActionDTO();
            lcOrder.setRequestId(serviceRequestId);
            lcOrder.setComments(comments);
            lcOrder.setSignatoryAction(signatoryApproved);

            CreateLetterOfCreditsBusinessDelegate letterOfCreditBusinessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(CreateLetterOfCreditsBusinessDelegate.class);
            BBRequestDTO bbrequest = letterOfCreditBusinessDelegate.getAccountId(requestId);

            LetterOfCreditStatusDTO letterOfCreditDTO = new LetterOfCreditStatusDTO();
            letterOfCreditDTO.setAccountId(bbrequest.getAccountId());
            letterOfCreditDTO.setRequestId(requestId);
            letterOfCreditDTO.setCustomerId(customerId);
            letterOfCreditDTO = letterOfCreditBusinessDelegate.validateForApprovals(letterOfCreditDTO, request);

            if (letterOfCreditDTO == null) {
                LOG.error("Error occured while validating for approvals");
                return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
            }
            if (letterOfCreditDTO.getStatus().equalsIgnoreCase("sent")) {

                try {

                    lcOrder = letterOfCreditBusinessDelegate.executeLetterOfCredits(lcOrder, request);

                    JSONObject letterOfOrderDTO = new JSONObject(lcOrder);
                    result = JSONToResult.convert(letterOfOrderDTO.toString());

                } catch (Exception e) {
                    LOG.error(e);
                    LOG.debug("Failed to create cheque book request in OMS " + e);
                    return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
                }
            } else if (letterOfCreditDTO.getStatus().equalsIgnoreCase("pending")) {
                // return pending status;

            }

        } catch (Exception e) {
            LOG.error("Caught exception at approve method: " + e);
            return ErrorCodeEnum.ERRTF_29045.setErrorCode(result);
        }
        return result;
    }

    public boolean validateInputParams(LetterOfCreditsDTO letterOfCredit, Map<String, String> inputParams) {
        String alphaNumericMax35Chars = "^[a-zA-Z0-9]{0,35}$";
        String alphaNumericMax35CharsWithSapce = "^[a-zA-Z0-9 ]{0,35}$";
        String alphaNumericMax200Chars = "^[a-zA-Z0-9]{0,200}$";
        String alphaNumericMax200CharsWithSpace = "^[a-zA-Z0-9 ]{0,200}$";
        String specialCharUnacceptable = "[<>=*]";
        Matcher matcher;
        boolean isValid = false;
        Pattern alphaNumericMax35CharsPattern = Pattern.compile(alphaNumericMax35Chars);
        Pattern alphaNumericMax200CharsPattern = Pattern.compile(alphaNumericMax200Chars);
        Pattern alphaNumericMax35CharsWithSapcePattern = Pattern.compile(alphaNumericMax35CharsWithSapce);
        Pattern alphaNumericMax200CharsWithSpacePattern = Pattern.compile(alphaNumericMax200CharsWithSpace);
        Pattern specialCharUnacceptablePattern = Pattern.compile(specialCharUnacceptable);

        if (StringUtils.isNotBlank(letterOfCredit.getAdditionalAmountPayable())) {
            try {
                Double.parseDouble(letterOfCredit.getAdditionalAmountPayable().trim());
            } catch (Exception e) {
                return false;
            }
        }

        if (StringUtils.isNotBlank(letterOfCredit.getBeneficiaryCountry())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getBeneficiaryCountry());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getPresentationPeriod())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getPresentationPeriod());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getTransshipment())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getTransshipment());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getPartialShipments())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getPartialShipments());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getIncoTerms())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getIncoTerms());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getModeOfShipment())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getModeOfShipment());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getDescriptionOfGoods())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getDescriptionOfGoods());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getDocumentsRequired())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getDocumentsRequired());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getConfirmationInstruction())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getConfirmationInstruction());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getTransferable())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getTransferable());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getStandByLC())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getStandByLC());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getLcCurrency())) {
            matcher = alphaNumericMax200CharsPattern.matcher(letterOfCredit.getLcCurrency());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getPaymentTerms()) && !Arrays.asList("Sight", "Deferred", "Acceptance", "Negotiation Sight", "Negotiation Acceptance").contains(letterOfCredit.getPaymentTerms())) {
            return false;
        }
        List<String> currencyList = Arrays.asList("INR", "EUR", "GBP", "USD");

        if (StringUtils.isNotBlank(letterOfCredit.getLcCurrency()) && !currencyList.contains(letterOfCredit.getLcCurrency())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getAdditionalPayableCurrency()) && !currencyList.contains(letterOfCredit.getAdditionalPayableCurrency())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getTransshipment()) && !Arrays.asList("Allowed", "Not Allowed").contains(letterOfCredit.getTransshipment())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getPartialShipments()) && !Arrays.asList("Allowed", "Not Allowed", "Conditional").contains(letterOfCredit.getPartialShipments())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getIncoTerms()) && !Arrays.asList("CIF", "CFR", "FOB", "FCA", "FAS", "CPT", "CIP", "DAP", "DDP", "DDU", "DES", "DEQ", "EXW").contains(letterOfCredit.getIncoTerms())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getModeOfShipment()) && !Arrays.asList("Air", "Sea", "Road").contains(letterOfCredit.getModeOfShipment())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getConfirmationInstruction()) && !Arrays.asList("Confirm", "May Add", "Without").contains(letterOfCredit.getConfirmationInstruction())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getTransferable()) && !Arrays.asList("Yes", "No").contains(letterOfCredit.getTransferable())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getStandByLC()) && !Arrays.asList("Yes", "No").contains(letterOfCredit.getStandByLC())) {
            return false;
        }

        if (StringUtils.isNotBlank(letterOfCredit.getTolerancePercentage())) {
            matcher = alphaNumericMax35CharsWithSapcePattern.matcher(letterOfCredit.getTolerancePercentage());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getAmountType())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getAmountType());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getOtherAmendments())) {
            matcher = alphaNumericMax200CharsWithSpacePattern.matcher(letterOfCredit.getOtherAmendments());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getAmendCharges())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getAmendCharges());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getAmendStatus())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getAmendStatus());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (StringUtils.isNotBlank(letterOfCredit.getLcSRMSId())) {
            matcher = alphaNumericMax35CharsPattern.matcher(letterOfCredit.getLcSRMSId());
            isValid = matcher.matches();
            if (!isValid)
                return false;
        }
        if (inputParams != null) {
            for (String key : inputParams.keySet()) {
                String value = inputParams.get(key);
                if ((validationMap.containsKey(key)) && value.length() > validationMap.get(key))
                    return false;

                matcher = specialCharUnacceptablePattern.matcher(value);
                if (matcher.find())
                    return false;
            }
        }

        return isValid;

    }

    public boolean _isDateValidate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
            formatter.parse(date);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Result amendLetterOfCredits(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request) {
        LetterOfCreditsDTO amendmentResponse;
        LetterOfCreditsDTO lcData;
        Result result = new Result();
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);

        if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());

        try {
            letterOfCredit.setSrmsReqOrderID(letterOfCredit.getLcSRMSId());
            lcData = lcGetBusinessDelegate.getLetterOfCreditsByID(letterOfCredit, request);
            if(!StringUtils.equals(lcData.getStatus(), PARAM_STATUS_APPROVED)) {
                return ErrorCodeEnum.ERR_12006.setErrorCode(new Result());
            }
        } catch (Exception e) {
            LOG.error("Unable to get amend Letter Of Credit " + e);
            return ErrorCodeEnum.ERRTF_29057.setErrorCode(new Result());
        }
        Map<String, String> inputMap = null;
        try {
            inputMap = JSONUtils.parseAsMap((new JSONObject(letterOfCredit)).toString(), String.class, String.class);
        } catch (IOException e) {
            LOG.error("Failed to create letter of credits", e);
        }
        // Validations Check
        boolean isValid = validateInputParams(letterOfCredit, inputMap);
        if (!isValid) {
            return ErrorCodeEnum.ERR_10118.setErrorCode(new Result());
        }
        String expiryDate = request.getParameter("expiryDate") != null ? request.getParameter("expiryDate") : "";
        String latestShippingDate = request.getParameter("latestShippingDate") != null
                ? request.getParameter("latestShippingDate")
                : "";
        String amendmentExpiryDate = request.getParameter("amendmentExpiryDate") != null
                ? request.getParameter("amendmentExpiryDate")
                : "";
        String chargesAccount = request.getParameter("chargesAccount") != null ? request.getParameter("chargesAccount")
                : "";
        String issueDate = request.getParameter("issueDate") != null ? request.getParameter("issueDate") : "";
        String lcAmount = request.getParameter("lcAmount") != null ? request.getParameter("lcAmount") : "";
        String lcSRMSId = request.getParameter("lcSRMSId") != null ? request.getParameter("lcSRMSId") : "";
        String lcrefno = request.getParameter("lcReferenceNo") != null ? request.getParameter("lcReferenceNo") : "";
        String paymentTerms = request.getParameter("paymentTerms") != null ? request.getParameter("paymentTerms") : "";
        String benefeciaryName = request.getParameter("beneficiaryName") != null
                ? request.getParameter("beneficiaryName")
                : "";

        // Validate values with the importLC values
        if ((!StringUtils.equals(lcrefno, lcData.getLcReferenceNo()))
                || (!StringUtils.equals(paymentTerms, lcData.getPaymentTerms()))
                || (!StringUtils.equals(benefeciaryName, lcData.getBeneficiaryName()))) {
            return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
        }
        // Validate issue date with the importLC issueDate
        try {
            String issuedate1 = HelperMethods.changeDateFormat(issueDate, Constants.TIMESTAMP_FORMAT);
            if (!(lcData.getIssueDate().equals(issuedate1))) {
                LOG.error("Issue Date does not match with the importLC issue Date");
                return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
            }
        } catch (Exception e) {
            LOG.error("Error" + e);
        }

        if (!StringUtils.isNumeric(lcAmount)) {
            return ErrorCodeEnum.ERR_10118.setErrorCode(new Result());
        }

        if (StringUtils.isNotBlank(issueDate)) {
            if (!(_isDateValidate(issueDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }

        if (StringUtils.isNotBlank(expiryDate)) {
            if (!(_isDateValidate(expiryDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }
        if (StringUtils.isNotBlank(amendmentExpiryDate)) {
            if (!(_isDateValidate(amendmentExpiryDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }
        if (StringUtils.isNotBlank(latestShippingDate)) {
            if (!(_isDateValidate(latestShippingDate))) {
                return ErrorCodeEnum.ERRTF_29060.setErrorCode(new Result());
            }
        }
        if (StringUtils.isBlank(lcSRMSId)) {
            return ErrorCodeEnum.ERRTF_29065.setErrorCode(new Result());
        }
        // Set amendmentApproveDate
        letterOfCredit.setAmendmentDate(getCurrentDateTimeUTF());
        letterOfCredit.setAmendStatus(PARAM_STATUS_SUBMITTED_TO_BANK);

        String accountEnding = "";
        String errorMessage = "";
        if (StringUtils.isNotBlank(chargesAccount)) {
            if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(customerId, chargesAccount)) {
                if (chargesAccount.length() > 3)
                    accountEnding = chargesAccount.substring(chargesAccount.length() - 3);
                errorMessage = "You do not have permission to the Charges Account ending xxx" + accountEnding + ".";
                return ErrorCodeEnum.ERR_10118.setErrorCode(result, errorMessage);
            }
        }
        LetterOfCreditsDTO drawingdto = new LetterOfCreditsDTO();

        ExportLetterOfCreditsDrawingsBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(ExportLetterOfCreditsDrawingsBusinessDelegate.class);
        if (StringUtils.isNotBlank(letterOfCredit.getLcSRMSId())) {
            drawingdto.setSrmsReqOrderID(letterOfCredit.getLcSRMSId());
            try {
                if (!businessDelegate.matchSRMSId(drawingdto, customerId, request)) {
                    return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
                }
            } catch (Exception e) {
                LOG.error("Failed to verify Request ID of export letter of credits request in OMS", e);
                return ErrorCodeEnum.ERRTF_29072.setErrorCode(new Result());
            }
        } else {
            return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
        }
        // Call BusinessDelegateClass
        try {
            amendmentResponse = lcCreateBusinessDelegate.amendLetterOfCredit(letterOfCredit, request);
        } catch (ApplicationException e) {
            LOG.error("Failed to amend letter of credits in OMS");
            return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
        }

        result = JSONToResult.convert(new JSONObject(amendmentResponse).toString());
        TradeFinanceCommonUtils.setAlertDataInResult(result, AlertsEnum.IMPORT_LC_AMENDMENT_SUBMITTED_FOR_BANK_APPROVAL, amendmentResponse.getAmendmentReference());
        return result;
    }

    @Override
    public Result updateImportLCAmendmentByBank(LetterOfCreditsDTO inputDto, DataControllerRequest request) {
        Result result;
        AlertsEnum alertToPush = null;
        try {
            LetterOfCreditsDTO amendmentDto = amendmentBusinessDelegate.getAmendmentsById(inputDto.getAmendmentReference(), request);
            switch (inputDto.getAmendStatus()) {
                case PARAM_STATUS_PROCESSING_by_BANK:
                    alertToPush = AlertsEnum.IMPORT_LC_AMENDMENT_SUBMITTED_FOR_APPROVAL;
                    break;
                case PARAM_STATUS_APPROVED:
                    alertToPush = AlertsEnum.IMPORT_LC_AMENDMENT_APPROVED;
                    break;
                case PARAM_STATUS_REJECTED:
                    alertToPush = AlertsEnum.IMPORT_LC_AMENDMENT_REJECTED;
                    break;
                default:
                    return ErrorCodeEnum.ERRTF_29077.setErrorCode(new Result());
            }

            amendmentDto.setAmendStatus(inputDto.getAmendStatus());
            amendmentDto.setChargesPaid(inputDto.getChargesPaid());
            LetterOfCreditsDTO letterOfCredits = lcCreateBusinessDelegate.updateAmendLC(amendmentDto, request);
            result = JSONToResult.convert(new JSONObject(letterOfCredits).toString());
            TradeFinanceCommonUtils.setAlertDataInResult(result, alertToPush, letterOfCredits.getAmendmentReference());
            result.addParam("message", "Record updated Successfully");
        } catch (Exception e) {
            LOG.debug("Failed to Update amend letter of credits by Bank from OMS " + e);
            return ErrorCodeEnum.ERRTF_29048.setErrorCode(new Result());
        }
        return result;
    }

}
