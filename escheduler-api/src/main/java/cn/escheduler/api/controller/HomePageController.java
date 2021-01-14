package cn.escheduler.api.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.escheduler.api.service.BaseService;
import cn.escheduler.api.service.HomePageService;
import cn.escheduler.dao.model.TopPV;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * get model catalog
 */

@RestController
@RequestMapping("homePage")
public class HomePageController extends BaseService {


    @Autowired
    private HomePageService homePageService;


    /**
     * getInfo
     *
     * @param userId
     * @return
     */
    @GetMapping(value = "/getInfo")
    @ResponseStatus(HttpStatus.OK)
    public Map getLayer(@RequestParam(value = "userId") String userId){
        return     homePageService.getInfo(userId);
    }


    /**
     * getHiveBaseSize
     *
     * @return
     */
    @GetMapping(value = "/getHiveBaseSize")
    @ResponseStatus(HttpStatus.OK)
    public List<Map> getHiveBaseSize() throws IOException {
        return     homePageService.getHiveBaseSize();
    }

    /**
     * getTopHiveTableSize
     *
     * @return
     */
    @GetMapping(value = "/getTopHiveTableSize")
    @ResponseStatus(HttpStatus.OK)
    public List getTopHiveTableSize() throws IOException {
        return     homePageService.getTopHiveTableSize();
    }


    /**
     * getTopHiveTableAddSize
     *
     * @return
     */
    @GetMapping(value = "/getTopHiveTableAddSize")
    @ResponseStatus(HttpStatus.OK)
    public List getTopHiveTableAddSize() throws IOException {
        return     homePageService.getTopHiveTableAddSize();
    }


//    /**
//     * updateBeforHiveSize
//     *
//     * @return
//     */
//    @GetMapping(value = "/updateBeforHiveSize")
//    @ResponseStatus(HttpStatus.OK)
//    public int updateBeforHiveSize() throws IOException {
//        return     homePageService.updateBeforHiveSize();
//    }

//    /**
//     * getTopNAPI
//     *
//     * @return
//     */
//    @GetMapping(value = "/getTopNApi")
//    @ResponseStatus(HttpStatus.OK)
//    public List getTopNApi(@RequestParam(value = "topN") int topN,
//                           @RequestParam(value = "startDate") String startDate,
//                           @RequestParam(value = "endDate") String endDate) throws IOException {
//        return     homePageService.getTopNApi(topN,startDate,endDate);
//    }

    /**
     * getTopNAPI
     *
     * @return
     */
    @GetMapping(value = "/getTopPV")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,String>> getTopPV() {
        List<TopPV> topPV = homePageService.getTopPV();
        List<Map<String,String>> list=new ArrayList<>();
        for(int i=0;i<topPV.size();i++){
            Map<String,String> map=new HashMap<>();
            map.put("sjmc", topPV.get(i).getSjmc());
            map.put("pv",String.valueOf(Integer.valueOf(topPV.get(i).getPv())*5));
            list.add(map);
        }

        Collections.sort(list, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                String pv1 = o1.get("pv");
                String pv2 = o2.get("pv");

                Integer name1 = Integer.valueOf(pv1) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(pv2) ; //name1是从你list里面拿出来的第二个name
                return name2.compareTo(name1);
            }
        });
        return list;
    }

    /**
     * getTopNAPI
     *
     * @return
     */
    @GetMapping(value = "/exportTopPV")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, String>> exportTopPV(HttpServletResponse response) throws IOException {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(date);

        List<TopPV> list=homePageService.getTopPV();
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("交管大数据平台应用总队机关排名","交管大数据平台应用总队机关排名" ), TopPV.class, list);

        response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''" + URLEncoder.encode("交管大数据平台应用总队机关排名" + day+ ".xls", "UTF-8"));
        workbook.write(response.getOutputStream());

        return null;
    }

    /**
     * getTopNAPI
     *
     * @return
     */
    @GetMapping(value = "/getTopZD")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String,String>> getTopZD() {
        List<TopPV> topPV = homePageService.getTopZD();
        List<Map<String,String>> list=new ArrayList<>();
        for(int i=0;i<topPV.size();i++){
            Map<String,String> map=new HashMap<>();
            map.put("sjmc", topPV.get(i).getSjmc());
            map.put("pv",String.valueOf(Integer.valueOf(topPV.get(i).getPv())*5));
            list.add(map);
        }

        Collections.sort(list, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                String pv1 = o1.get("pv");
                String pv2 = o2.get("pv");

                Integer name1 = Integer.valueOf(pv1) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(pv2) ; //name1是从你list里面拿出来的第二个name
                return name2.compareTo(name1);
            }
        });
        return list;
    }

    /**
     * getTopNAPI
     *
     * @return
     */
    @GetMapping(value = "/exportTopZD")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, String>> exportTopZD(HttpServletResponse response) throws IOException {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(date);

        List<TopPV> list=homePageService.getTopZD();
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("交管大数据平台应用支队排名","交管大数据平台应用支队排名" ), TopPV.class, list);

        response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''" + URLEncoder.encode("交管大数据平台应用支队排名" + day+ ".xls", "UTF-8"));
        workbook.write(response.getOutputStream());

        return null;
    }


}
