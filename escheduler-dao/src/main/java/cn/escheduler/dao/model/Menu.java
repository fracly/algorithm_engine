package cn.escheduler.dao.model;

import java.util.List;

public class Menu {
    // 父节点ID，如果为顶级菜单，值为0
    private int pid;

    // 节点ID
    private int id;

    // 菜单的Unique ID
    private int key;

    // 菜单的排序
    private int sort;

    // 菜单名称
    private String name;

    // 菜单代码
    private String code;

    // 菜单状态 1表示可见，2代表不可见
    private int status;

    // 菜单对应的URL
    private String url;

    // 菜单下可进行的操作集合
    private String operations;

    private List<Menu> children;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void addChildren(Menu menu) {
        this.children.add(menu);
    }
    public List<Menu> getChildren() {
        return this.children;
    }

    public void setChildren(List<Menu> menus) {
        this.children = menus;
    }

}
