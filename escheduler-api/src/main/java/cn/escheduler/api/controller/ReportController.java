package cn.escheduler.api.controller;

import cn.escheduler.api.service.reportService;
import cn.escheduler.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author ：Marinh
 * @Param:
 * @retrun:
 * @Creat :2020-06-11-14:21
 **/
@RestController
@RequestMapping("serviceReport")
public class ReportController {

    @Autowired
    private reportService reportService;

    @GetMapping(value = "/view/{reportCode}")
    @ResponseStatus(HttpStatus.OK)
    public Map reportParms(@PathVariable String reportCode,@RequestParam("status") String status,@RequestParam(value="glbmbm",required=false)String glbmbm){
        if(StringUtils.isEmpty(status) || "null".equals(status)){
            status="0";
        }
        Map map = reportService.queryParms(reportCode,status,glbmbm);
        return map;
    }
}
