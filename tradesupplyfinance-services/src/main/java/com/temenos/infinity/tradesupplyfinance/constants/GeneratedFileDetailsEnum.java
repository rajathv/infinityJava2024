/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.constants;

/**
 * @author k.meiyazhagan
 */
public enum GeneratedFileDetailsEnum {
    RSBA("Receivable Single Bill.pdf", "application/pdf", "Receivable Single Bill"),
    RSBB("Receivable Single Bills.xlsx", "application/octet-stream", "Receivable Single Bill");

    private final String fileName;
    private final String contentType;
    private final String displayName;

    GeneratedFileDetailsEnum(String fileName, String contentType, String displayName) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.displayName = displayName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDisplayName() {
        return displayName;
    }
}
