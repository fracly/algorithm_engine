package cn.escheduler.api.controller;

import cn.escheduler.api.service.BaseService;
import cn.escheduler.api.service.ModelDesignService;
import cn.escheduler.api.service.RunRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("runRule")
public class RunRuleController extends BaseService {

    @Autowired
    private RunRuleService runRuleService;


    /**
     * updateTblRule
     *
     * @param base
     * @param tblName
     * @return
     */
    @GetMapping(value = "/runTblRule")
    @ResponseStatus(HttpStatus.OK)
    public int runTblRule(@RequestParam(value = "base") String base,
                             @RequestParam(value = "tblName")String tblName ) throws SQLException {
        runRuleService.runTblRule(base,tblName);
        return 1;
    }

    /**
     * updateTblRule
     *
     * @param base
     * @param tblName
     * @return
     */
    @GetMapping(value = "/runColRule")
    @ResponseStatus(HttpStatus.OK)
    public int runColRule(@RequestParam(value = "base") String base,
                          @RequestParam(value = "tblName")String tblName ) throws SQLException {
        runRuleService.runColRule(base,tblName);
        return 1;
    }


}
