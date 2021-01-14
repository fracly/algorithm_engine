package cn.escheduler.api.controller;
import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.SystemService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.common.enums.UserType;
import cn.escheduler.dao.model.Menu;
import cn.escheduler.dao.model.Role;
import cn.escheduler.dao.model.User;
import com.google.inject.internal.util.$StackTraceElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 系统Controller
 * 对应系统的管理，权限控制
 *      1、菜单管理
 *      2、角色管理
 *      3、用户管理
 *
 *  @author fracly
 *  @date 2020-06-01 15:18:00
 */
@RestController
@RequestMapping("/system")
public class SystemController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemService systemService;

    /** menu actions **/
    @PostMapping("/create-menu")
    public Result createMenu(String name, String code, String url, Integer pid, Integer sort) {
        Menu menu = new Menu();
        menu.setCode(code);
        menu.setUrl(url);
        menu.setName(name);
        menu.setPid(pid);
        menu.setStatus(1);
        menu.setSort(sort);
        Result result = systemService.createMenu(menu);
        return result;
    }

    @GetMapping("/delete-menu")
    public Result deleteMenu(Integer id) {
        return systemService.deleteMenu(id);
    }

    @PostMapping("/update-menu")
    public Result updateMenu(int id, String name, String code, String url, Integer pid, Integer sort){
        return systemService.updateMenu(id, name, code, url, pid, sort);
    }

    @GetMapping("/enable-menu")
    public Result enableMenu(int id){
        return systemService.enableMenu(id);
    }

    @GetMapping("/disable-menu")
    public Result disableMenu(int id){
        return systemService.disableMenu(id);
    }

    @GetMapping("/search-menu/tree")
    public Result searchTreeMenu(String name, int status){
        return systemService.searchTreeMenu(name, status);
    }

    @GetMapping("/search-menu/flat")
    public Result searchFlatMenu(String name, int status){
        return systemService.searchFlatMenu(name, status);
    }

    /** role actions **/
    @PostMapping("/create-role")
    public Result createRole(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, String code, String desc) {
        // check code
        boolean exists = systemService.checkRoleCodeExist(code);
        if(exists) {
            Result result = new Result();
            putMsg(result, Status.CUSTOM_FAILED, "角色编码已存在，请检查");
            return result;
        }
        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role.setStatus(1);
        role.setCreateTime(new Date());
        role.setCreatorId(loginUser.getId());
        role.setDesc(desc);
        Result result = systemService.createRole(role);
        return result;
    }

    @GetMapping("/delete-role")
    public Result deleteRole(int id){
        return systemService.deleteRole(id);
    }

    @PostMapping("/update-role")
    public Result updateRole(String name, String code, int id, String desc){
        return systemService.updateRole(id, name, code, desc);
    }

    @GetMapping("/disable-role")
    public Result disableRole(int id){
        return systemService.disableRole(id);
    }

    @GetMapping("/enable-role")
    public Result enableRole(int id){
        return systemService.enableRole(id);
    }

    @GetMapping("/search-role")
    public Result searchRole(String name, int status, Integer pageNo, Integer pageSize){
        return systemService.searchRole(name, status, pageNo, pageSize);
    }

    @GetMapping("/role-menu/list")
    public Result roleMenuList(Integer roleId){
        return systemService.roleMenuList(roleId);
    }

    @PostMapping("/role-menu/update")
    public Result updateRoleMenu(Integer roleId, String idArray){
        return systemService.updateRoleMenu(roleId, idArray);
    }

    /** user actions **/
    @PostMapping("/create-user")
    public Result createUser(String userCNName, String userName, String userPassword, String phone, String email, String desc, Integer tenantId, String queue) {
        boolean exists = systemService.checkUserNameExist(userName);
        if(exists) {
            Result result  = new Result();
            putMsg(result, Status.CUSTOM_FAILED, "账号已被占用，请检查");
            return result;
        }
        User user = new User();
        user.setUserName(userName);
        user.setUserCNName(userCNName);
        user.setDesc(desc);
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUserPassword(userPassword);
        user.setErrorCount(0);
        user.setTenantId(tenantId);
        user.setQueue(queue);
        Result result = systemService.createUser(user);
        return result;
    }

    @GetMapping("/delete-user")
    public Result deleteUser(int id){
        return systemService.deleteUser(id);
    }

    @PostMapping("/update-user")
    public Result updateUser(int id, String userCNName, String userName, String userPassword, String phone, String email, String desc, Integer tenantId, String queue) {
        return systemService.updateUser(id, userCNName, userName, userPassword, phone, email, desc, tenantId, queue);
    }

    @GetMapping("/disable-user")
    public Result disableUser(int id){
        return systemService.disableUser(id);
    }

    @GetMapping("/enable-user")
    public Result enableUser(int id){
        return systemService.enableUser(id);
    }

    @PostMapping("/user/modify-password")
    public Result userModifyPassword(int id, String password) {
        return systemService.userModifyPassword(id, password);
    }

    @GetMapping("/search-user")
    public Result searchUser(String name, int status, Integer pageNo, Integer pageSize){
        return systemService.searchUser(name, status, pageNo, pageSize);
    }

    @GetMapping("/user-role/list")
    public Result userRoleList(Integer userId){
        return systemService.userRoleList(userId);
    }

    @PostMapping("/user-role/update")
    public Result userRoleUpdate(Integer userId, String idArray){
        return systemService.userRoleUpdate(userId, idArray);
    }

    @GetMapping("/conf")
    public Result conf(String key) {
        return systemService.getConf(key);
    }
}
