/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PARAM_PROPERTY;

public class TradeFinanceHelper {
    private static final Logger LOG = LogManager.getLogger(TradeFinanceHelper.class);
    private static final Properties properties = new Properties();

    public void loadTypeAndSubType() {
        String fileName = PARAM_PROPERTY + ".properties";
        try (InputStream inputStream = TradeFinanceHelper.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(inputStream);
        } catch (Exception e) {
            LOG.error("Error while loading properties", e);
        }
    }

    public static String getProperty(String value) {
        return properties.getProperty(value);
    }
}
