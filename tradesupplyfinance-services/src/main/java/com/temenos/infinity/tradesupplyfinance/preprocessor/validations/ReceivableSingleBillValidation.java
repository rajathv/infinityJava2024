/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.preprocessor.validations;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.util.JSONUtils;
import com.kony.memorymgmt.CorporateManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import static com.kony.dbputilities.util.HelperMethods.getCustomerIdFromSession;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.updateErrorResponse;

/**
 * @author k.meiyazhagan
 */
public class ReceivableSingleBillValidation implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(ReceivableSingleBillValidation.class);
    Date today = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat(UTC_DATE_FORMAT);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        Pattern alphaMax3 = Pattern.compile("^[a-zA-Z]{0,3}$");
        Pattern amountMax15 = Pattern.compile("^[0-9]+([.][0-9]+?){0,50}$");
        Pattern alphaNumericMax35 = Pattern.compile("^[a-zA-Z0-9]{0,35}$");
        Pattern alphaNumericSpaceMax35 = Pattern.compile("^[a-zA-Z0-9 ]{0,35}$");
        Pattern alphaNumericWithSpaceMax200 = Pattern.compile("^[a-zA-Z0-9 .,+<>!?':-]{0,200}$");
        Pattern alphaNumericWithSpaceMax1000 = Pattern.compile("^[a-zA-Z0-9 .,+<>!?':-]{0,1000}$");
        AuthorizationChecksBusinessDelegate requestAuthBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

        try {
            ReceivableSingleBillDTO inputDto = JSONUtils.parse(fabricRequestManager.getPayloadHandler().getPayloadAsJson().toString(), ReceivableSingleBillDTO.class);
            CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager).getCountriesFromSession();
            if ((StringUtils.isNotBlank(inputDto.getBillName()) && !alphaNumericWithSpaceMax200.matcher(inputDto.getBillName()).matches())
                    || (StringUtils.isNotBlank(inputDto.getBillNumber()) && !alphaNumericMax35.matcher(inputDto.getBillNumber()).matches())
                    || (StringUtils.isNotBlank(inputDto.getBillType()) && !Arrays.asList("Promisory note", "Bill of exchange", "Deffered payment letter of credit", "Commercial Invoice", "Sales Agreement", "Purchase Order").contains(inputDto.getBillType()))
                    || (StringUtils.isNotBlank(inputDto.getPaymentTerms()) && !alphaNumericWithSpaceMax200.matcher(inputDto.getPaymentTerms()).matches())
                    || (StringUtils.isNotBlank(inputDto.getAmount()) && !amountMax15.matcher(inputDto.getAmount()).matches())
                    || (StringUtils.isNotBlank(inputDto.getCurrency()) && !alphaMax3.matcher(inputDto.getCurrency()).matches())
                    || (StringUtils.isNotBlank(inputDto.getRequestFinance()) && !Arrays.asList(PARAM_OPTION_YES, PARAM_OPTION_NO).contains(inputDto.getRequestFinance().toUpperCase()))
                    || (StringUtils.isNotBlank(inputDto.getBuyerName()) && !alphaNumericWithSpaceMax200.matcher(inputDto.getBuyerName()).matches())
                    || (StringUtils.isNotBlank(inputDto.getBuyerAddress()) && !alphaNumericWithSpaceMax1000.matcher(inputDto.getBuyerAddress()).matches())
                    || (StringUtils.isNotBlank(inputDto.getBuyerSelection()) && !Arrays.asList(PARAM_OPTION_MANUAL, PARAM_OPTION_EXISTING).contains(inputDto.getBuyerSelection().toUpperCase()))
                    || (StringUtils.isNotBlank(inputDto.getGoodsDescription()) && !alphaNumericWithSpaceMax1000.matcher(inputDto.getGoodsDescription()).matches())
                    || (StringUtils.isNotBlank(inputDto.getShipmentTrackingDetails()) && !alphaNumericWithSpaceMax1000.matcher(inputDto.getShipmentTrackingDetails()).matches())
                    || (StringUtils.isNotBlank(inputDto.getCountryOfOrigin()) && !corporateManager.isValidCountry(inputDto.getCountryOfOrigin()))
                    || (StringUtils.isNotBlank(inputDto.getCountryOfDestination()) && !corporateManager.isValidCountry(inputDto.getCountryOfDestination()))
                    || (StringUtils.isNotBlank(inputDto.getModeOfShipment()) && !Arrays.asList(PARAM_MODE_AIR, PARAM_MODE_ROAD, PARAM_MODE_SEA).contains(inputDto.getModeOfShipment().toUpperCase()))
                    || (StringUtils.isNotBlank(inputDto.getPortOfDischarge()) && !alphaNumericSpaceMax35.matcher(inputDto.getPortOfDischarge()).matches())
                    || (StringUtils.isNotBlank(inputDto.getPortOfLoading()) && !alphaNumericSpaceMax35.matcher(inputDto.getPortOfLoading()).matches())
                    || (StringUtils.isNotBlank(inputDto.getFinalDestination()) && !alphaNumericSpaceMax35.matcher(inputDto.getFinalDestination()).matches())
                    || (StringUtils.isNotBlank(inputDto.getUploadedDocuments()) && !areValidDocuments(inputDto.getUploadedDocuments()))
                    || (StringUtils.isNotBlank(inputDto.getMessageToBank()) && !alphaNumericWithSpaceMax1000.matcher(inputDto.getMessageToBank()).matches())
                    || (StringUtils.isNotBlank(inputDto.getCancellationDocuments()) && !areValidDocuments(inputDto.getCancellationDocuments()))
                    || (StringUtils.isNotBlank(inputDto.getReasonForCancellation()) && !alphaNumericWithSpaceMax1000.matcher(inputDto.getReasonForCancellation()).matches())
            ) {
                return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30001);
            }

            today = dateFormat.parse(dateFormat.format(new Date()));
            if ((StringUtils.isNotBlank(inputDto.getBillDate()) && !isValidDate(inputDto.getBillDate()))
                    || (StringUtils.isNotBlank(inputDto.getDueDate()) && !(isValidDate(inputDto.getDueDate()) || !isDateGreater(inputDto.getDueDate(), inputDto.getBillDate())))
                    || (StringUtils.isNotBlank(inputDto.getShipmentDate()) && !isValidDate(inputDto.getShipmentDate()))) {
                return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30017);
            }

            if (StringUtils.isNotBlank(inputDto.getReceivableAccount())
                    && !requestAuthBusinessDelegate.isOneOfMyAccounts(getCustomerIdFromSession(fabricRequestManager), inputDto.getReceivableAccount())) {
                return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30019);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while validating bills input", e);
            return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30018);
        }

        return true;
    }

    private boolean isValidDate(String inputDate) {
        try {
            Date date = dateFormat.parse(inputDate.trim());
            if (date.before(today)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDateGreater(String fromDate, String toDate) {
        try {
            if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate))
                return dateFormat.parse(fromDate).compareTo(dateFormat.parse(toDate)) < 0;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean areValidDocuments(String inputDocuments) {
        Pattern fileNameMax200 = Pattern.compile("^[a-zA-Z0-9 ._()/-]{0,200}$");
        try {
            JSONArray documents = new JSONArray(inputDocuments);
            for (int i = 0; i < documents.length(); i++) {
                JSONObject document = documents.getJSONObject(i);
                String[] temp = StringUtils.split(document.getString(PARAM_DOCUMENT_NAME), ".");
                if (!fileNameMax200.matcher(temp[0]).matches()
                        || !Arrays.asList("PDF", "ZIP", "JPG", "BMP", "JPEG", "XLSX", "CSV", "DOC", "DOCX").contains(temp[1].toUpperCase())
                        || StringUtils.isBlank(document.getString(PARAM_DOCUMENT_REFERENCE))) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
