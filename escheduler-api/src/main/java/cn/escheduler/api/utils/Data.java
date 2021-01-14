package cn.escheduler.api.utils;

public class Data {

    private String timestamp;

    private Double value;

    private String type;

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
