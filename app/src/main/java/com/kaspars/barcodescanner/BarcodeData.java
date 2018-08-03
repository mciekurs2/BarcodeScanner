package com.kaspars.barcodescanner;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BarcodeData {
    private String rawData;
    private String type;
    private String timeAdded;

    BarcodeData(){}

    public BarcodeData(String rawData, String type, String timeAdded) {
        this.rawData = rawData;
        this.type = type;
        this.timeAdded = timeAdded;
    }

    public String getRawData() {
        return rawData;
    }

    public String getType() {
        return type;
    }

    public String getTimeAdded() {
        return timeAdded;
    }
}
