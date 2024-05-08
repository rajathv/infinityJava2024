/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.PARAM_PROPERTY;

/**
 * @author k.meiyazhagan
 */
public class TradeSupplyFinanceProperties {
    private static final Logger LOG = LogManager.getLogger(TradeSupplyFinanceProperties.class);
    private static final Properties properties = new Properties();

    public void loadTypeAndSubType() {
        String fileName = PARAM_PROPERTY + ".properties";
        try (InputStream inputStream = TradeSupplyFinanceProperties.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(inputStream);
        } catch (Exception e) {
            LOG.error("Error while loading properties", e);
        }
    }

    public static String getProperty(String value) {
        return properties.getProperty(value);
    }
}