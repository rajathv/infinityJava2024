package com.kony.dbputilities.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Timer {

    private static final Logger LOG = LogManager.getLogger(Timer.class);
    Long start;
    String url = "";

    public Timer(String url) {
        this.url = url;
        start = System.currentTimeMillis();
    }

    public void printEndTime() {
        LOG.error(
                "Execution took -->" + (System.currentTimeMillis() - start) + " milliseconds time for service " + url);
    }

}
