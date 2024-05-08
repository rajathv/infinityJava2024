/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.constants.GeneratedFileDetailsEnum;
import com.temenos.infinity.tradesupplyfinance.utils.ExportListBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.utils.ExportListEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.PARAM_FILE_REFERENCE;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceStatus.PARAM_STATUS_DELETED;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceStatus.PARAM_STATUS_IN_REVIEW;

public class GenerateExcelListOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GenerateExcelListOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {

        HashMap<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

        methodID = methodID.toUpperCase();
        ExportListEnum listDetails = ExportListEnum.valueOf(methodID);
        LinkedHashMap<Integer, ArrayList<Object>> headerValuesMap;
        try {
            headerValuesMap = getHeaderValuesMap(methodID, listDetails.getFieldsList(), inputParams, request, listDetails.getBusinessDelegateImplName());
            return generateExcelList(listDetails.getPrefix(), listDetails.getHeadersList(), headerValuesMap);
        } catch (Exception e) {
            LOG.error("Error occurred while generating the trade finance file. Error: ", e);
        }
        return ErrorCodeEnum.ERR_30009.setErrorCode(new Result());
    }

    public Result generateExcelList(String prefix, String[] headerList, LinkedHashMap<Integer, ArrayList<Object>> headerValuesMap)
            throws IOException {

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(GeneratedFileDetailsEnum.valueOf(prefix).getDisplayName());

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = sheet.getWorkbook().createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        font.setFontName("Segoe UI");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        Cell cell;
        Row row;
        int r = 0;
        Row rowHead = sheet.createRow(0);
        rowHead.setHeight((short) 500);
        if (headerList != null) {
            for (String header : headerList) {
                cell = rowHead.createCell(r);
                cell.setCellValue(header);
                cell.setCellStyle(style);
                r++;
            }
        }
        int i = 1;
        for (Integer key : headerValuesMap.keySet()) {
            ArrayList<Object> arrayList = headerValuesMap.get(key);
            row = sheet.createRow(i);
            int j = 0;
            for (Object obj : arrayList) {
                if (obj instanceof Integer)
                    row.createCell(j).setCellValue((int) obj);
                else
                    row.createCell(j).setCellValue(obj != null && StringUtils.isNotBlank(String.valueOf(obj)) ? String.valueOf(obj) : "N/A");
                j++;
            }
            i++;
        }
        for (int j = 0; j < headerList.length; j++) {
            int width = ((int) (headerList[j].length() * 1.14388)) * 300;
            sheet.setColumnWidth(j, width);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
            workbook.close();
        }
        byte[] bytes = bos.toByteArray();
        Result result = new Result();
        String fileId = prefix + CommonUtils.generateUniqueID(32);
        MemoryManager.saveIntoCache(fileId, Base64.encodeBase64String(bytes), 120);
        result.addParam("fileId", fileId);
        return result;
    }

    public <T> LinkedHashMap<Integer, ArrayList<Object>> getHeaderValuesMap(String methodId, String[] headerDTOList, HashMap<String, Object> inputParams,
                                                                            DataControllerRequest request, Class<? extends ExportListBusinessDelegate> businessDelegate) throws IOException, ApplicationException, InstantiationException, IllegalAccessException {
        LOG.debug("Generating Header Values Map");
        LinkedHashMap<Integer, ArrayList<Object>> headerValuesMap = new LinkedHashMap<>();
        Integer i = 1;
        TsfFilterDTO filterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), TsfFilterDTO.class);
        Set<String> set = new HashSet<>();
        set.add(PARAM_STATUS_DELETED);
        if (!filterDTO.get_filterByParam().contains(PARAM_FILE_REFERENCE))
            set.add(PARAM_STATUS_IN_REVIEW);
        List<T> filteredListDto;
        LOG.debug("Filtering the header values map");
        List<T> responseListDto = getSpecificDtoList(request, businessDelegate);
        if (responseListDto != null)
            responseListDto.removeAll(Collections.singletonList(null));
        filteredListDto = filterDTO.filter(responseListDto);
        Map<String, Object> dtoMap;
        for (T responseDto : filteredListDto) {
            dtoMap = JSONUtils.parseAsMap(new JSONObject(responseDto).toString(), String.class, Object.class);
            ArrayList<Object> res = new ArrayList<>();
            for (String val : headerDTOList) {
                res.add(dtoMap.get(val));
            }
            headerValuesMap.put(i, res);
            i++;
        }
        LOG.error("Filtered Header values map generated successfully for excel : ", methodId);
        return headerValuesMap;
    }

    private <T> List<T> getSpecificDtoList(DataControllerRequest request, Class<? extends ExportListBusinessDelegate> businessDelegate) throws InstantiationException, IllegalAccessException, ApplicationException {
        return businessDelegate.newInstance().getRecordsList(request);
    }
}
