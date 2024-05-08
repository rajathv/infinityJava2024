package com.infinity.dbx.temenos.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This Util class is used to handle merging and splitting of multi value structures in T24
 *
 * @author smugesh
 *
 */
public class MultiValueUtil {

    private static final Logger logger = LogManager.getLogger(MultiValueUtil.class);
    
    /*
     * Method     - Splitting single value into multi values
     * objectName - narrative
     * length     - 17 (length to split by, Mostly T24 length)
     * value      - String sent from UI
     * Input      - narrative, 17, Payment initiated By bank
     * Output     - [{"narrative" : "Payment initiated"},{"narrative" : " By bank"}]
     */
    public static String splitMultiValue(String objectName, int length, String value) {

        String multiValue = "";
        logger.error("Single Value :" + value);

        if (StringUtils.isNotBlank(value)) {
            int index = 0;
            JsonArray multiValueArray = new JsonArray();
            while (index < value.length()) {
                String singleValue = value.substring(index, Math.min(index + length, value.length()));
                JsonObject singleObject = new JsonObject();
                singleObject.addProperty(objectName, singleValue);
                multiValueArray.add(singleObject);
                index += length;
            }
            multiValue = multiValueArray.toString();

        }
        logger.error("Processed Multi Value :" + multiValue);
        return multiValue;
    }
    
    /*
     * Merging multi value to a single value
     * value - multi value json array from T24 in String format
     * ObjectName - the name we need to pick for the values
     * Input - "[{"narrative" : "Payment initiated"},{"narrative" : " By bank"}]", narrative
     * OutPut - Payment initiated By bank
     */

    public static String mergeMultiValue(String value, String ObjectName) {
        
        logger.error("Multi Value :" + value);
        String mergedValue = "";
        JsonParser parser = new JsonParser();
        JsonObject SingleObject = new JsonObject();
        List<String> values = new ArrayList<>();
        JsonElement multiValueElement = parser.parse(value.toString());
        JsonArray multiValueArray = multiValueElement.getAsJsonArray();
        if (multiValueArray != null && multiValueArray.size() > 0) {
            for (JsonElement jsonElement : multiValueArray) {
                if ((jsonElement instanceof JsonObject) && !jsonElement.isJsonNull())
                    SingleObject = jsonElement.getAsJsonObject();
                if (SingleObject.has(ObjectName))
                    values.add(SingleObject.get(ObjectName).getAsString());
            }
            mergedValue = String.join("", values);
        }
        logger.error("Processed Single Value :" + mergedValue);
        return mergedValue;
    }

}
