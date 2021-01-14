package cn.escheduler.api.controller;

import cn.escheduler.api.service.KylinService;
import cn.escheduler.api.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @description:
 * @author: jgn
 * @Date: 2020/12/9
 * @version:
 */
@RestController
@RequestMapping("kylin")
public class KylinController extends BaseController{

    @Autowired
    private KylinService kylinService;

    private static final Logger logger = LoggerFactory.getLogger(KylinController.class);

    /**
     * getCubesList
     *
     * @param
     * @return
     */
    @GetMapping(value = "/getCubesList")
    @ResponseStatus(HttpStatus.OK)
    public Result getCubesList(String searchVal) {
        Map<String, Object> io = kylinService.getCubesList(searchVal);
        return returnDataList(io);
    }


    /**
     * getCubesList
     *
     * @param
     * @return
     */
    @GetMapping(value = "/build")
    @ResponseStatus(HttpStatus.OK)
    public Result build(String cubeName,String body) {
        Map<String, Object> io = kylinService.build(cubeName,body);
        return returnDataList(io);
    }


    /**
     * getCubesList
     *
     * @param
     * @return
     */
    @GetMapping(value = "/getLogsList")
    @ResponseStatus(HttpStatus.OK)
    public Result getLogsList(String cubeName,String status) {
        Map<String, Object> result = kylinService.getLogsList(cubeName,status);
        return returnDataList(result);
    }

    /**
     * getCubesList
     *
     * @param
     * @return
     */
    @GetMapping(value = "/getStepLog")
    @ResponseStatus(HttpStatus.OK)
    public Result getStepLog(String id,String stepId) {
        Map<String, Object> result = kylinService.getStepLog(id,stepId);
        return returnDataList(result);
    }
}
