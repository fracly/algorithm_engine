package cn.escheduler.server.worker.task.enums;

public enum MainClassName {
    LINEAR_REGRESSION("com.bcht.algorithms.engine.LinearRegression");

    private final String className;

    MainClassName(String className){
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
