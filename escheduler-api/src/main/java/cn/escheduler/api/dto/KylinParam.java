package cn.escheduler.api.dto;

public class KylinParam {

    private String cube;
    private String body;


    public KylinParam(){

    }

    public KylinParam(String cube, String body) {
        this.cube = cube;
        this.body = body;
    }

    public String getCube() {
        return cube;
    }

    public void setCube(String cube) {
        this.cube = cube;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "KylinParam{" +
                "cube='" + cube + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
