package cn.escheduler.api.service;

import cn.escheduler.api.enums.Status;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.mapper.SystemMapper;
import cn.escheduler.dao.model.Menu;
import cn.escheduler.dao.model.Role;
import cn.escheduler.dao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SystemService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    @Autowired
    private SystemMapper systemMapper;

    /**
     * menu actions
     */
    public Result createMenu(Menu menu) {
        Result result = new Result();
        int count = systemMapper.insertMenu(menu);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建菜单成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建菜单失败");
        }
        return result;
    }

    public Result deleteMenu(int id) {
        Result result = new Result();
        int count = systemMapper.deleteMenu(id);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除菜单成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除菜单失败");
        }
        return result;
    }

    public Result updateMenu(int id, String name, String code, String url, Integer pid, Integer sort) {
        Result result = new Result();
        int count = systemMapper.updateMenu(id, name, code, url, pid, sort);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "更新菜单成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "更新菜单失败");
        }
        return result;
    }

    public Result searchTreeMenu(String name, int status) {
        Result result = new Result();
        List<Menu> list = systemMapper.searchMenu(name, status);

        // 将按钮分类
        List<Menu> resultList = getMenuChildren(0, list);
        result.setData(resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result searchFlatMenu(String name, int status) {
        Result result = new Result();
        List<Menu> list = systemMapper.searchMenu(name, status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    public Result createRole(Role role) {
        Result result = new Result();
        int count = systemMapper.insertRole(role);
        if (count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建角色成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建角色失败");
        }
        return result;
    }

    public Result createUser(User user) {
        Result result = new Result();
        int count = systemMapper.insertUser(user);
        if (count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建用户成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建用户失败");
        }

        return result;
    }

    public boolean checkUserNameExist(String userName) {
        int count = systemMapper.queryUserByName(userName);
        return count > 0;
    }

    public boolean checkRoleCodeExist(String code) {
        int count = systemMapper.queryRoleByCode(code);
        return count > 0;
    }



    public Result roleMenuList(Integer roleId) {
        Result result = new Result();
        List<Integer> list = systemMapper.roleMenuList(roleId);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userRoleList(Integer userId) {
        Result result = new Result();
        List<Integer> list = systemMapper.userRoleList(userId);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userRoleUpdate(Integer userId, String idArray) {
        Result result = new Result();
        systemMapper.deleteUserRoleRelation(userId);
        String[] array = idArray.split(",");
        for(String roleId : array) {
            systemMapper.insertUserRoleRelation(userId, Integer.parseInt(roleId));
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "更新用户角色成功");
        return result;
    }


    public Result updateRoleMenu(Integer roleId, String idArray) {
        Result result = new Result();
        systemMapper.deleteRoleMenuRelation(roleId);
        String[] array = idArray.split(",");
        for(String menuId : array) {
            systemMapper.insertRoleMenuRelation(roleId, Integer.parseInt(menuId));
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "更新角色权限成功");
        return result;
    }


    public Result searchRole(String name, int status, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        Integer offset  = (pageNo - 1) * pageSize;
        List<Role> list = systemMapper.searchRole(name, status, offset, pageSize);
        int count = systemMapper.countRole(name, status);
        Map<String, Object> map = new HashMap<>();
        map.put("total", count);
        result.setDataMap(map);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result searchUser(String name, int status, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        Integer offset = (pageNo - 1) * pageSize;
        List<User> list = systemMapper.searchUser(name, status, offset, pageSize);
        int count = systemMapper.countUser(name, status);
        Map<String, Object> map = new HashMap<>();
        map.put("total", count);
        result.setData(list);
        result.setDataMap(map);
        putMsg(result, Status.SUCCESS);
        return result;
    }


    public Result deleteRole(int id) {
        Result result = new Result();
        int count = systemMapper.deleteRole(id);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除角色成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除角色失败");
        }
        return result;
    }

    public Result deleteUser(int id) {
        Result result = new Result();
        int count = systemMapper.deleteUser(id);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除用户成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除用户失败");
        }
        return result;
    }

    public Result disableMenu(int id) {
        Result result = new Result();
        systemMapper.disableMenu(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用权限成功");
        return result;
    }

    public Result disableRole(int id) {
        Result result = new Result();
        systemMapper.disableRole(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用角色成功");
        return result;
    }

    public Result disableUser(int id) {
        Result result = new Result();
        systemMapper.disableUser(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用用户成功");
        return result;
    }

    public Result enableMenu(int id) {
        Result result = new Result();
        systemMapper.enableMenu(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "启用权限成功");
        return result;
    }

    public Result enableRole(int id) {
        Result result = new Result();
        systemMapper.enableRole(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "解封角色成功");
        return result;
    }

    public Result enableUser(int id) {
        Result result = new Result();
        systemMapper.enableUser(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "解封用户成功");
        return result;
    }


    public Result updateRole(int id, String name, String code, String desc) {
        Result result = new Result();
        int count = systemMapper.updateRole(name, code, id, desc);
        if (count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "更新角色成功");
        }else {
            putMsg(result, Status.CUSTOM_FAILED, "更新角色失败");
        }
        return result;
    }

    public Result updateUser(int id, String userCNName, String userName, String userPassword, String phone, String email, String desc, Integer tenantId, String queue) {
        Result result = new Result();
        int count = systemMapper.updateUser(id, userCNName, userName, userPassword, phone, email, desc, tenantId, queue);
        if (count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "更新用户成功");
        }else {
            putMsg(result, Status.CUSTOM_FAILED, "更新用户失败");
        }
        return result;
    }

    public Result updateUserLoginInfo(int id, Date lastLoginTime, String lastLoginIp) {
        Result result = new Result();
        systemMapper.updateUserLoginInfo(id, lastLoginTime, lastLoginIp);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userModifyPassword(int id, String password) {
        Result result = new Result();
        systemMapper.userModifyPassword(id, password);
        putMsg(result, Status.CUSTOM_SUCESSS, "重设密码成功");
        return result;
    }

    public Result getConf(String key) {
        Result result = new Result();
        List<Map> list = systemMapper.getConf(key);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    private List<Menu> getMenuChildren(Integer pid, List<Menu> rawList) {
        List<Menu> resultList = new ArrayList<>();
        List<Menu> temp = new ArrayList<>();
        for(int i = 0; i < rawList.size(); i ++) {
            if(rawList.get(i).getPid() == pid) {
                temp = getMenuChildren(rawList.get(i).getId(), rawList);
                if(temp.size() > 0) {
                    rawList.get(i).setChildren(temp);
                }
                resultList.add(rawList.get(i));
            }
        }
        return resultList;
    }


}
