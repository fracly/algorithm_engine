package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.dao.mapper.ProcessDefinitionMapper;
import cn.escheduler.dao.mapper.ProjectMapper;
import cn.escheduler.dao.model.ProcessDefinition;
import cn.escheduler.dao.model.Project;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * etl group service
 */
@Service
public class ETLGroupService extends BaseService{
    private static final Logger logger = LoggerFactory.getLogger(ETLGroupService.class);

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UsersService userService;

    @Autowired
    private ProcessDefinitionMapper processDefineMapper;

    /**
     * check login user if already has one data govern project
     */
    public Boolean checkExists(User loginUser) {
        logger.info("check login user's data govern project if exist!");
        Integer count = projectMapper.countDataGovernProjects(loginUser.getId());

        if(count >= 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * create data govern project for login user
     */
    public Map<String, Object> createProject(User loginUser) {
        Map<String, Object> result = new HashMap<>(5);

        /**
         * only general users can create projects. administrators have no corresponding tenants and can only view
         * 管理员没有对应的租户,只能查看,只有普通用户才可以创建项目
         */
        if (!userService.isGeneral(loginUser)) {
            putMsg(result, Status.USER_NO_OPERATION_PERM);
            return result;
        }
        if(!checkExists(loginUser)){
            Project project = new Project();
            Date now = new Date();
            project.setName("数据治理模型组");
            project.setDesc("数据治理模型组，此模型组每个用户只有一个，且由系统自动创建，只包含数据治理相关的模型");
            project.setUserId(loginUser.getId());
            project.setUserName(loginUser.getUserName());
            project.setCreateTime(now);
            project.setUpdateTime(now);
            project.setType(1);
            if(projectMapper.insert(project) > 0) {
                putMsg(result, Status.SUCCESS);
            } else {
                putMsg(result, Status.CREATE_PROJECT_ERROR);
            }
        } else{
            logger.info("login user already has data govern project!");
        }
        return result;
    }

    /**
     * get all process under user's data govern project
     */
    public Map<String, Object> queryAllProcessByLoginUser(User loginUser){
        HashMap<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryDataGovernProjectByLoginUser(loginUser.getId());
        List<ProcessDefinition> resourceList = processDefineMapper.queryAllDefinitionList(project.getId());
        result.put(Constants.DATA_LIST, resourceList);
        putMsg(result, Status.SUCCESS);

        return result;
    }
}
