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
package cn.escheduler.api.controller;


import cn.escheduler.api.enums.Status;
import cn.escheduler.api.service.SessionService;
import cn.escheduler.api.service.UsersService;
import cn.escheduler.api.utils.Constants;
import cn.escheduler.api.utils.Result;
import cn.escheduler.dao.model.Role;
import cn.escheduler.dao.model.User;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static cn.escheduler.api.enums.Status.*;

/**
 * user login controller
 */
@RestController
@RequestMapping("")
public class LoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UsersService userService;

    /**
     * login
     *
     * @param userName
     * @param userPassword
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/login")
    public Result login(@RequestParam(value = "userName") String userName,
                        @RequestParam(value = "userPassword") String userPassword,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        Result result = new Result();
        try {
            logger.info("login user name: {} ", userName);

            User user = userService.queryByUserName(userName);

            // 判断用户状态是否正常
            if(user == null) {
                putMsg(result, CUSTOM_FAILED, "账号不存在，请检查");
                return result;
            }

            if(user.getStatus() == 9) {
                putMsg(result, CUSTOM_FAILED, "此账号已被禁用，请联系管理员");
                return result;
            }

            if(user.getStatus() == 8) {
                putMsg(result, CUSTOM_FAILED, "此账号已被锁定，请联系管理员");
                return result;
            }

            if(!user.getUserPassword().equals(userPassword)) {
                if(user.getErrorCount() >= 4) {
                    userService.updateStatus(user.getId(), 8);
                    putMsg(result, CUSTOM_FAILED, "账号密码不正确，您的账号已被锁定，请联系管理员");
                    return result;
                }
                int count = user.getErrorCount();
                userService.incrementErrorCount(user.getId(), count + 1);
                putMsg(result, CUSTOM_FAILED, "账号密码不正确，您还剩" + (4 - user.getErrorCount()) + "次输入机会");
                return result;
            }

            boolean hasAnyPermission = userService.queryUserRoles(user.getId());
            if(!hasAnyPermission) {
                putMsg(result, CUSTOM_FAILED, "该用户没有任何权限");
                return result;
            }

            // 如果以上检测都通过，表示登录成功，记录登录记录
            userService.incrementErrorCount(user.getId(), 0);
            String ip = getClientIpAddress(request);
            userService.userLogin(user, ip);

            // create session
            String sessionId = sessionService.createSession(user, ip);

            if (sessionId == null) {
                return error(Status.LOGIN_SESSION_FAILED.getCode(),
                        Status.LOGIN_SESSION_FAILED.getMsg()
                );
            }

            response.setStatus(HttpStatus.SC_OK);
            response.addCookie(new Cookie(Constants.SESSION_ID, sessionId));

            logger.info("sessionId = " + sessionId);
            return success(LOGIN_SUCCESS.getMsg(), sessionId);
        } catch (Exception e) {
            logger.error(USER_LOGIN_FAILURE.getMsg(),e);
            return error(USER_LOGIN_FAILURE.getCode(), USER_LOGIN_FAILURE.getMsg());
        }
    }

    /**
     * sign out
     *
     * @param loginUser
     * @return
     */
    @PostMapping(value = "/signOut")
    public Result signOut(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                          HttpServletRequest request) {

        try {
            logger.info("login user:{} sign out", loginUser.getUserName());
            String ip = getClientIpAddress(request);
            sessionService.signOut(ip, loginUser);
            //clear session
            request.removeAttribute(Constants.SESSION_USER);
            return success();
        } catch (Exception e) {
            logger.error(SIGN_OUT_ERROR.getMsg(),e);
            return error(SIGN_OUT_ERROR.getCode(), SIGN_OUT_ERROR.getMsg());
        }
    }

    @GetMapping(value = "/user/info")
    public Result userInfo(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, HttpServletRequest request) {
        logger.info("get login user {} information", loginUser.getUserName());
        return userService.getUserInfo(loginUser);
    }
}
