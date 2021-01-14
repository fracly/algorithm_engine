package cn.escheduler.dao.model;


import cn.afterturn.easypoi.excel.annotation.Excel;


public class TopPV {
    @Excel(name = "部门名称", width = 25, orderNum = "0",needMerge = true)
    private String sjmc;

    @Excel(name = "使用次数", width = 25, orderNum = "0",needMerge = true)
    private String pv;

    public String getSjmc() {
        return sjmc;
    }

    public void setSjmc(String sjmc) {
        this.sjmc = sjmc;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }
}
