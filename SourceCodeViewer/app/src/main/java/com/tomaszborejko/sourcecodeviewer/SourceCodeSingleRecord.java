package com.tomaszborejko.sourcecodeviewer;

/**
 * Created by Borco on 2017-05-24.
 */

public class SourceCodeSingleRecord {
    private String websiteUrl;
    private String sourceCode;

    public SourceCodeSingleRecord(String websiteUrl, String sourceCode) {
        this.websiteUrl = websiteUrl;
        this.sourceCode = sourceCode;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getSourceCode() {
        return sourceCode;
    }
}