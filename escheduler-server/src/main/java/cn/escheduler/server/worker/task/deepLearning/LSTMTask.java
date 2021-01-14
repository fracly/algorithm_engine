package cn.escheduler.server.worker.task.deepLearning;

import cn.escheduler.common.Constants;
import cn.escheduler.common.process.Property;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.deepLearning.LSTMForecastParameters;
import cn.escheduler.common.task.deepLearning.LSTMParameters;
import cn.escheduler.common.utils.JSONUtils;
import cn.escheduler.common.utils.ParameterUtils;
import cn.escheduler.dao.DaoFactory;
import cn.escheduler.dao.ProcessDao;
import cn.escheduler.dao.model.DataSource;
import cn.escheduler.dao.model.ProcessInstance;
import cn.escheduler.server.utils.ParamUtils;
import cn.escheduler.server.worker.task.AbstractTask;
import cn.escheduler.server.worker.task.ShellCommandExecutor;
import cn.escheduler.server.worker.task.TaskProps;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Set;

public class LSTMTask extends AbstractTask {

    private LSTMParameters lSTMParameters;

    /**
     * task dir
     */
    private String taskDir;

    private ShellCommandExecutor processTask;

    /**
     * process database access
     */
    private ProcessDao processDao;


    public LSTMTask(TaskProps props, Logger logger) {
        super(props, logger);

        this.taskDir = props.getTaskDir();

        this.processTask = new ShellCommandExecutor(this::logHandle,
                props.getTaskDir(), props.getTaskAppId(),
                props.getTenantCode(), props.getEnvFile(), props.getTaskStartTime(),
                props.getTaskTimeout(), logger);
        this.processDao = DaoFactory.getDaoInstance(ProcessDao.class);
    }

    @Override
    public void init() {
        logger.info("shell task params {}", taskProps.getTaskParams());

        lSTMParameters = JSONUtils.parseObject(taskProps.getTaskParams(), LSTMParameters.class);

        if (!lSTMParameters.checkParameters()) {
            throw new RuntimeException("shell task params is not valid");
        }
    }

    @Override
    public void handle() throws Exception {
        try {
            // construct process
            exitStatusCode = processTask.run(buildCommand(), processDao);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            exitStatusCode = -1;
        }
    }

    @Override
    public void cancelApplication(boolean cancelApplication) throws Exception {
        // cancel process
        processTask.cancelApplication();
    }

    /**
     *  create command
     * @return
     * @throws Exception
     */
    private String buildCommand() throws Exception {
        // generate scripts
        String fileName = String.format("%s/%s_node.sh", taskDir, taskProps.getTaskAppId());
        Path path = new File(fileName).toPath();

        if (Files.exists(path)) {
            return fileName;
        }

        String script = "python /opt/python_algorithms/LSTM_fit.py";

        // find process instance by task id
        ProcessInstance processInstance = processDao.findProcessInstanceByTaskId(taskProps.getTaskInstId());

        /**
         *  combining local and global parameters
         */
        Map<String, Property> paramsMap = ParamUtils.convert(taskProps.getUserDefParamsMap(),
                taskProps.getDefinedParams(),
                lSTMParameters.getLocalParametersMap(),
                processInstance.getCmdTypeIfComplement(),
                processInstance.getScheduleTime());
        if (paramsMap != null){

            DataSource dataSource = processDao.findDataSourceById(lSTMParameters.getDatasource());
            Map<String, String> map = JSONUtils.toMap(dataSource.getConnectionParams());
            String database=map.get("database");
            lSTMParameters.setDatabases(database);

            String inTable=lSTMParameters.getTable();

            String characterColumns= String.join(",",lSTMParameters.getCharacterColumns());

            String sql="select * from "+database+"."+inTable;


            script = ParameterUtils.convertParameterPlaceholders(script, ParamUtils.convert(paramsMap))+" \""+sql+"\""+" \""+characterColumns+"\" "+lSTMParameters.getLabelColumn()+" "+
                    lSTMParameters.getModelName()+" "+database+"."+lSTMParameters.getModelName().replace("-","_");
        }


        lSTMParameters.setRawScript(script);

        logger.info("raw script : {}", lSTMParameters.getRawScript());
        logger.info("task dir : {}", taskDir);

        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(Constants.RWXR_XR_X);
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);

        Files.createFile(path, attr);

        Files.write(path, lSTMParameters.getRawScript().getBytes(), StandardOpenOption.APPEND);

        return fileName;
    }

    @Override
    public AbstractParameters getParameters() {
        return lSTMParameters;
    }

    @Override
    public boolean checkParams() {
        this.errorMsg="参数有误：";
        return true;
    }

}
