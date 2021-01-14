package cn.escheduler.server.worker.task.featureEngineering;

import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.TaskProps;
import org.slf4j.Logger;

public class FilteringFeatureSelectionTask extends AbstractTask {
    /**
     * @param taskProps
     * @param logger
     */
    public FilteringFeatureSelectionTask(TaskProps taskProps, Logger logger) {
        super(taskProps, logger);
    }

    @Override
    public void handle() throws Exception {

    }

    @Override
    public AbstractParameters getParameters() {
        return null;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }
}
