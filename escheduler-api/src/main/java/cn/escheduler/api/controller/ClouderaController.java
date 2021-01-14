package cn.escheduler.api.controller;


import cn.escheduler.api.service.ClouderaService;

import cn.escheduler.api.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * clouder monitor controller
 */
@RestController
@RequestMapping("cloudera")
public class ClouderaController extends BaseController {

    @Autowired
    private ClouderaService clouderaService;

    private static final Logger logger = LoggerFactory.getLogger(AtlasLineageController.class);

    /**
     * getIO
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/getIO")
    @ResponseStatus(HttpStatus.OK)
    public Result getIO(@RequestParam(value = "param") String param
            ,@RequestParam(value = "type") String type
    ){
        Map<String, Object> io = clouderaService.getHdfsIO(param, type);
        return returnDataList(io);
    }

    /**
     * getIO
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/getMem")
    @ResponseStatus(HttpStatus.OK)
    public Result getMem(@RequestParam(value = "type") String type
    ){
        Map<String, Object> io = clouderaService.getMem(type);
        return returnDataList(io);
    }

    /**
     * getIO
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/getVc")
    @ResponseStatus(HttpStatus.OK)
    public Result getVc(@RequestParam(value = "type") String type
    ){
        Map<String, Object> io = clouderaService.getVc(type);
        return returnDataList(io);
    }

    /**
     * getCpu
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/getCpu")
    @ResponseStatus(HttpStatus.OK)
    public Result getCpu(@RequestParam(value = "type") String type
    ){
        Map<String, Object> cpu = clouderaService.getCpu(type);
        return returnDataList(cpu);
    }


    /**
     * getHosts
     * @return
     */
    @GetMapping(value = "/getHosts")
    @ResponseStatus(HttpStatus.OK)
    public Result getHosts(){
        Map<String, Object> result = clouderaService.getHosts();
        return returnDataList(result);
    }


}
