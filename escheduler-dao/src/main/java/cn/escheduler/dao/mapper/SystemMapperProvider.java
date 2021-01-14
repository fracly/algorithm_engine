package cn.escheduler.dao.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * system mapper provider
 */
public class SystemMapperProvider {

    private static final String MENU_TABLE_NAME = "t_escheduler_menu";

    private static final String ROLE_TABLE_NAME = "t_escheduler_role";

    private static final String USER_TABLE_NAME = "t_escheduler_user";

    private static final String ROLE_MENU_RELATION_TABLE_NAME = "t_escheduler_relation_role_menu";

    private static final String USER_ROLE_RELATION_TABLE_NAME = "t_escheduler_relation_user_role";

    private static final String CONF_TABLE_NAME = "t_sys_setup";

    /**
     * menu actions
     */
    public String insertMenu(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(MENU_TABLE_NAME);
                VALUES("`url`",  "#{menu.url}");
                VALUES("`pid`",  "#{menu.pid}");
                VALUES("`sort`", "#{menu.sort}");
                VALUES("`name`", "#{menu.name}");
                VALUES("`code`", "#{menu.code}");
                VALUES("`status`", "#{menu.status}");
            }
        }.toString();
    }

    public String deleteMenu(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(MENU_TABLE_NAME);
                WHERE("`id`=#{id}");
            }
        }.toString();
    }

    public String disableMenu(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(MENU_TABLE_NAME);
            SET("`status` = 2");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enableMenu(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(MENU_TABLE_NAME);
            SET("`status` = 1");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateMenu(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(MENU_TABLE_NAME);
            SET("`url` = #{url}");
            SET("`pid` = #{pid}");
            SET("`name` = #{name}");
            SET("`code` = #{code}");
            SET("`sort` = #{sort}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String searchMenu(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("id, id as 'key', pid, sort, name, code, status, url, operations");
                FROM(MENU_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
                ORDER_BY("sort");
            }
        }.toString();
        return sql;
    }

    /**
     * role actions
     */
    public String insertRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(ROLE_TABLE_NAME);
                VALUES("`name`", "#{role.name}");
                VALUES("`status`", "#{role.status}");
                VALUES("`code`", "#{role.code}");
                VALUES("`desc`", "#{role.desc}");
                VALUES("`create_time`", "#{role.createTime}");
                VALUES("`creatorId`", "#{role.creatorId}");
            }
        }.toString();
    }

    public String deleteRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(ROLE_TABLE_NAME);
                WHERE("`id`=#{id}");
            }
        }.toString();
    }

    public String disableRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`status` = 9");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enableRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`status` = 1");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`name` = #{name}");
            SET("`code` = #{code}");
            SET("`desc` = #{desc}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String searchRole(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("*, id as 'key'");
                FROM(ROLE_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
                ORDER_BY("create_time desc limit #{offset}, #{pageSize}");
            }
        }.toString();
        return sql;
    }

    public String countRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1) as total");
                FROM(ROLE_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
            }
        }.toString();
    }

    public String roleMenuList(Map<String, Object> parameter) {
        String sql = new SQL() {{
            SELECT("menu_id as id");
            FROM(ROLE_MENU_RELATION_TABLE_NAME);
            WHERE("`role_id` = #{roleId}");
        }}.toString();
        return sql;
    }

    public String insertRoleMenuRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(ROLE_MENU_RELATION_TABLE_NAME);
                VALUES("`role_id`", "#{roleId}");
                VALUES("`menu_id`", "#{menuId}");
            }
        }.toString();
    }

    public String deleteRoleMenuRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(ROLE_MENU_RELATION_TABLE_NAME);
                WHERE("`role_id` = #{roleId}");
            }
        }.toString();
    }

    /**
     * user actions
     */
    public String insertUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(USER_TABLE_NAME);
                VALUES("`user_cn_name`", "#{user.userCNName}");
                VALUES("`user_name`", "#{user.userName}");
                VALUES("`user_password`", "#{user.userPassword}");
                VALUES("`status`", "#{user.status}");
                VALUES("`phone`", "#{user.phone}");
                VALUES("`email`", "#{user.email}");
                VALUES("`desc`", "#{user.desc}");
                VALUES("`create_time`", "#{user.createTime}");
                VALUES("`user_type`", "#{user.userType}");
                VALUES("`error_count`", "#{user.errorCount}");
                VALUES("`tenant_id`", "#{user.tenantId}");
                VALUES("`queue`", "#{user.queue}");
            }
        }.toString();
    }

    public String insertUserRoleRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(USER_ROLE_RELATION_TABLE_NAME);
                VALUES("`user_id`", "#{userId}");
                VALUES("`role_id`", "#{roleId}");
            }
        }.toString();
    }

    public String deleteUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(USER_TABLE_NAME);
                WHERE("`id`=#{id}");
            }
        }.toString();
    }

    public String deleteUserRoleRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(USER_ROLE_RELATION_TABLE_NAME);
                WHERE("`user_id`= #{userId}");
            }
        }.toString();
    }

    public String disableUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`status` = 9");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enableUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`status` = 1");
            SET("`error_count` = 0");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`user_cn_name` = #{userCNName}");
            SET("`user_name` = #{userName}");
            Object passw = parameter.get("userPassword");
            if (passw != null && StringUtils.isNotEmpty(passw.toString())) {
                SET("`user_password` = #{userPassword}");
            }
            SET("`phone` = #{phone}");
            SET("`email` = #{email}");
            SET("`update_time` = now()");
            SET("`desc` = #{desc}");
            SET("`tenant_id` = #{tenantId}");
            SET("`queue` = #{queue}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateUserLoginInfo(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`last_login_time` = #{lastLoginTime}");
            SET("`last_login_ip` = #{lastLoginIp}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String userModifyPassword(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`password` = #{password}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String searchUser(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("*");
                FROM(USER_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("user_cn_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
                ORDER_BY("create_time desc limit #{offset}, #{pageSize}");
            }
        }.toString();
        return sql;
    }

    public String countUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1) as total");
                FROM(USER_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("user_cn_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
            }
        }.toString();
    }

    public String userRoleList(Map<String, Object> parameter) {
        String sql = new SQL() {{
            SELECT("role_id as id");
            FROM(USER_ROLE_RELATION_TABLE_NAME);
            WHERE("`user_id` = #{userId}");
        }}.toString();
        return sql;
    }

    public String queryUserByName(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1) as total");
            FROM(USER_TABLE_NAME);
            WHERE("`user_name` = #{userName}");
        }}.toString();
    }

    public String queryRoleByCode(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("count(1) as total");
            FROM(ROLE_TABLE_NAME);
            WHERE("`code` = #{code}");
        }}.toString();
    }

    public String getConf(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("*");
            FROM(CONF_TABLE_NAME);
            Object key = parameter.get("key");
            if(key != null && StringUtils.isNotEmpty(key.toString())) {
                WHERE("`KEY_NAME` = #{key}");
            }
        }}.toString();
    }
}
