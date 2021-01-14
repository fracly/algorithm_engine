package cn.escheduler.dao.model;

import cn.escheduler.common.enums.TaskType;

public class SparkConfiguration {
    private long id;

    private TaskType taskType;

    private String deployModel;

    private String programType;

    private int dirverCores;

    private int executorNumber;

    private String driverMemory;

    private int executorCores;

    private String executorMemory;

    private String commandArgs;

    private String otherArgs;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getDeployModel() {
        return deployModel;
    }

    public void setDeployModel(String deployModel) {
        this.deployModel = deployModel;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public int getDirverCores() {
        return dirverCores;
    }

    public void setDirverCores(int dirverCores) {
        this.dirverCores = dirverCores;
    }

    public int getExecutorNumber() {
        return executorNumber;
    }

    public void setExecutorNumber(int executorNumber) {
        this.executorNumber = executorNumber;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public int getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(int executorCores) {
        this.executorCores = executorCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public String getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(String commandArgs) {
        this.commandArgs = commandArgs;
    }

    public String getOtherArgs() {
        return otherArgs;
    }

    public void setOtherArgs(String otherArgs) {
        this.otherArgs = otherArgs;
    }

    @Override
    public String toString() {
        return "SparkConfiguration{" +
                "id=" + id +
                ", taskType=" + taskType +
                ", deployModel='" + deployModel + '\'' +
                ", programType='" + programType + '\'' +
                ", dirverCores=" + dirverCores +
                ", executorNumber=" + executorNumber +
                ", driverMemory='" + driverMemory + '\'' +
                ", executorCores=" + executorCores +
                ", executorMemory='" + executorMemory + '\'' +
                ", commandArgs='" + commandArgs + '\'' +
                ", otherArgs='" + otherArgs + '\'' +
                '}';
    }
}
