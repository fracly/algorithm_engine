/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.dao.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * session mapper provider
 */
public class RolePermissionMapperProvider {

    private static final String ROLE_TABLE_NAME = "t_escheduler_role";

    private static final String MENU_TABLE_NAME = "t_escheduler_menu";

    private static final String ROLE_MENU_RELATION_TABLE_NAME = "t_escheduler_relation_role_menu";

    /**
     * query role's all permission
     *
     * @param parameter
     * @return
     */
    public String findPermissionsByRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*");
                FROM(MENU_TABLE_NAME + " a ");
                INNER_JOIN(ROLE_MENU_RELATION_TABLE_NAME + " b on a.id = b.menu_id ");
                INNER_JOIN(ROLE_TABLE_NAME + " c on c.id = b.role_id ");
                WHERE("c.id = #{role.id}");
            }
        }.toString();
    }

}
