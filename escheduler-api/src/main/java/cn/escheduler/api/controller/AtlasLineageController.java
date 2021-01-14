package cn.escheduler.api.controller;

import cn.escheduler.api.configuration.ConfigurationManager;

import cn.escheduler.api.utils.Constants;
import cn.escheduler.dao.mapper.ModelDesignMapper;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.SearchFilter;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.EntityMutationResponse;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Atlas lineage controller
 */
@RestController
@RequestMapping("atlaslineage")
public class AtlasLineageController {

    private static final Logger logger = LoggerFactory.getLogger(AtlasLineageController.class);

    @Autowired
    private ModelDesignMapper modelDesignMapper;

    /**
     * updateProcessInstance data source
     *
     * @param guid
     * @return
     */
    @GetMapping(value = "/getLineage")
    @ResponseStatus(HttpStatus.OK)
    public AtlasLineageInfo getAtlasLineage(@RequestParam(value = "guid") String guid) throws AtlasServiceException {

        String [] url={ConfigurationManager.getProperty(Constants.ATLAS_URL)};
        String [] use=ConfigurationManager.getProperty(Constants.ATLAS_PW).split(",");

        AtlasLineageInfo Lineage=null;
        AtlasClientV2 atlasClientV2 =new AtlasClientV2(new DefaultConfigurationBuilder(), url, use);
        try {
            AtlasLineageInfo atlasLineageInfo = atlasClientV2.getLineageInfo(guid,AtlasLineageInfo.LineageDirection.BOTH,3);
            Lineage= atlasLineageInfo;
            logger.info("load lineage for table {}  succeed",guid);

        } catch (AtlasServiceException e) {
            e.printStackTrace();
        }
        AtlasLineageInfo atlasLineageInfo = TransformAtlasLineageInfo(Lineage);
        return atlasLineageInfo;
    }

    /**
     *  血缘关系转换，只保留表之间的联系
     * @param lineage
     * @return
     */
    private AtlasLineageInfo TransformAtlasLineageInfo( AtlasLineageInfo lineage)
    {
        Set<AtlasLineageInfo.LineageRelation> oldRelationsSet = lineage.getRelations();
        List<AtlasLineageInfo.LineageRelation> oldRelations = new ArrayList<AtlasLineageInfo.LineageRelation>(oldRelationsSet);

        Map<String, AtlasEntityHeader> guidEntityMap = lineage.getGuidEntityMap();
        Iterator<AtlasLineageInfo.LineageRelation> iterator = oldRelations.iterator();
        List<AtlasLineageInfo.LineageRelation> newRelationsList = new ArrayList<AtlasLineageInfo.LineageRelation>();
        String fromEntityId = null;
        while (iterator.hasNext())
        {
            List<AtlasLineageInfo.LineageRelation> newTmpRelationsList = new ArrayList<AtlasLineageInfo.LineageRelation>();
            while (iterator.hasNext()) {
                AtlasLineageInfo.LineageRelation lineageRelation = iterator.next();
                fromEntityId = lineageRelation.getFromEntityId();
                AtlasEntityHeader entityHeader = guidEntityMap.get(fromEntityId);
                String typeName = entityHeader.getTypeName();
                if (typeName.equals("hive_table")) {
                    newTmpRelationsList.add(lineageRelation);
                    boolean removeFlag= oldRelations.remove(lineageRelation);
                    break;
                }
            }
            if (newTmpRelationsList.size()==0)
            {
                break;
            }
            // 上溯
            iterator = oldRelations.iterator();
            String fromEntryIdIter = fromEntityId;
            while (iterator.hasNext()) {
                AtlasLineageInfo.LineageRelation innerLineageRelation = iterator.next();
                if (innerLineageRelation.getToEntityId().equals(fromEntryIdIter))
                {
                    // 上一个节点的id
                    String previousId = innerLineageRelation.getFromEntityId();
                    AtlasEntityHeader entityHeader1 = guidEntityMap.get(previousId);
                    if (entityHeader1.getTypeName().equals("hive_table")) {
                        innerLineageRelation.setToEntityId(fromEntityId);
                        boolean removeFlag = oldRelations.remove(innerLineageRelation);
                        newTmpRelationsList.add(0, innerLineageRelation);
                        fromEntityId = previousId;
                        logger.info("fromEntityId:" + fromEntityId);
                    }

                    fromEntryIdIter = previousId;
                    iterator = oldRelations.iterator();
                }
            }
            // 下探
            iterator = oldRelations.iterator();
            String toEntityId = newTmpRelationsList.get(newTmpRelationsList.size() - 1).getToEntityId();
            String toEntryIdIter = toEntityId;
            while (iterator.hasNext()) {
                AtlasLineageInfo.LineageRelation innerLineageRelation = iterator.next();
                String afterId = innerLineageRelation.getFromEntityId();
                if (afterId.equals(toEntryIdIter)) {
                    AtlasEntityHeader atlasEntityHeader = guidEntityMap.get(afterId);
                    if (atlasEntityHeader.getTypeName().equals("hive_table")) {
                        newTmpRelationsList.get(newTmpRelationsList.size() - 1).setToEntityId(afterId);
                        oldRelations.remove(innerLineageRelation);
                        newTmpRelationsList.add(innerLineageRelation);
                        toEntityId = innerLineageRelation.getToEntityId();
                        logger.info("toEntityId:" + toEntityId);
                    }

                    iterator = oldRelations.iterator();
                    toEntryIdIter = innerLineageRelation.getToEntityId();
                }
            }
            AtlasEntityHeader atlasEntityHeader = guidEntityMap.get(toEntryIdIter);
            if (atlasEntityHeader.getTypeName().equals("hive_table")) {
                newTmpRelationsList.get(newTmpRelationsList.size() - 1).setToEntityId(toEntryIdIter);
                iterator = oldRelations.iterator();
                while (iterator.hasNext())
                {
                    AtlasLineageInfo.LineageRelation innerLineageRelation= iterator.next();
                    if (innerLineageRelation.getToEntityId().equals(toEntityId))
                    {
                        oldRelations.remove(innerLineageRelation);
                        break;
                    //    iterator = oldRelations.iterator();
                    }
                }
            }
            iterator = oldRelations.iterator();
            newRelationsList.addAll(newTmpRelationsList);
        }

        Iterator<String> keyIterator = guidEntityMap.keySet().iterator();
        while (keyIterator.hasNext())
        {
            String nextKey = keyIterator.next();
            AtlasEntityHeader entityHeader = guidEntityMap.get(nextKey);
            if (!entityHeader.getTypeName().equals("hive_table"))
            {
                guidEntityMap.remove(nextKey);
                keyIterator = guidEntityMap.keySet().iterator();
            }
        }
        lineage.setRelations( new HashSet<AtlasLineageInfo.LineageRelation>( newRelationsList));
        return lineage;
    }


    /**
     * updateProcessInstance data source
     * get all type
     * @return
     */
    @GetMapping(value = "/getAllType")
    @ResponseStatus(HttpStatus.OK)
    public List getAtlasLineage() throws AtlasServiceException {

        String [] url={ConfigurationManager.getProperty(Constants.ATLAS_URL)};
        String [] use=ConfigurationManager.getProperty(Constants.ATLAS_PW).split(",");
//        String [] use={"admin","admin"};

        AtlasClientV2 atlasClientV2 =new AtlasClientV2(new DefaultConfigurationBuilder(), url, use);

        SearchFilter searchFilter = new SearchFilter();

        searchFilter.setParam("excludeInternalTypesAndReferences","true");
        searchFilter.setSortBy("name");
        AtlasTypesDef atlasTypesDef = atlasClientV2.getAllTypeDefs(searchFilter);

        List list=new ArrayList();

        for (int i=0;i<atlasTypesDef.getEntityDefs().size();i++){
            list.add(atlasTypesDef.getEntityDefs().get(i).getName());
        }
        return list;
    }

    /**
     * updateProcessInstance data source
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/basicSearch")
    @ResponseStatus(HttpStatus.OK)
    public Map getAtlasLineage(@RequestParam(value = "type") String type,
                               @RequestParam(value = "pageSize") int pageSize,
                               @RequestParam(value = "pageNo") int pageNo,
                               @RequestParam(value = "table",required = false) String table
                               ){

//        String[] url = {ConfUtils.get("atlas_url")};
//        String[] use = ConfUtils.get("atlas_PW").split(",");
        String[] url = {ConfigurationManager.getProperty(Constants.ATLAS_URL)};
        String[] use = ConfigurationManager.getProperty(Constants.ATLAS_PW).split(",");

        AtlasSearchResult basic=null;
        AtlasClientV2 atlasClientV2 =new AtlasClientV2(new DefaultConfigurationBuilder(), url, use);

        Map map = new HashMap();
        try {
            AtlasSearchResult atlasSearchResult = atlasClientV2.basicSearch(type,null,table, true,10000,0);
            List<AtlasEntityHeader> entities = atlasSearchResult.getEntities();
            for(int i=0;i<entities.size();i++){
                Map<String, Object> attributes = entities.get(i).getAttributes();


                String base = attributes.get("qualifiedName").toString();
                String s = base.split("\\.")[0];
                String tblName=attributes.get("name").toString();
                List<Map> name = modelDesignMapper.getName(s, tblName);
                if(name.size()==1){
                    entities.get(i).setDisplayText((String) name.get(0).get("tabledescribe"));
                }else {
                    entities.get(i).setDisplayText(" ");
                }

            }

//            List<AtlasEntityHeader> entities = atlasSearchResult.getEntities();
//            List<AtlasEntityHeader> newlist=new ArrayList<>();
//            for (int i = 0; i < entities.size(); i++) {
//                String guid = entities.get(i).getGuid();
//
//                AtlasEntity.AtlasEntitiesWithExtInfo entitiesByGuids = atlasClientV2.getEntitiesByGuids(Collections.singletonList(guid));
//                AtlasEntity atlasEntity = entitiesByGuids.getEntities().get(0);
//                Object o = ((Map) atlasEntity.getRelationshipAttribute("db")).get("relationshipStatus");
//                String DELETED=o.toString();
//
//
////                String DELETED = (String) ((Map) atlasClientV2.getEntitiesByGuids(Collections.singletonList(guid)).getEntities().get(0).getRelationshipAttribute("db")).get("relationshipStatus");
//
//                AtlasEntityHeader atlasEntityHeader = entities.get(i);
//
//                if (!DELETED.equals("DELETED")){
//                    newlist.add(atlasEntityHeader);
//                }else {
//                    atlasClientV2.deleteEntityByGuid(guid);
//                }
//
//            }
            if(null !=entities){
                map.put("pageSize",pageSize);

                int count = entities.size();
                map.put("count",count);
                int totalPage = count / pageSize;
                if (count % pageSize > 0) {
                    totalPage++;
                }
                map.put("totalPages",totalPage);
                int end = (pageNo)*pageSize;
                if(end>count){
                    end = count;
                }
                map.put("data",entities.subList((pageNo-1)*pageSize,end));

            }

        } catch (AtlasServiceException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * updateProcessInstance data source
     *
     * @param type
     * @return
     */
    @GetMapping(value = "/synchro")
    @ResponseStatus(HttpStatus.OK)
    public int synchro(@RequestParam(value = "type") String type) throws AtlasServiceException {

//        String[] url = {ConfUtils.get("atlas_url")};
//        String[] use = ConfUtils.get("atlas_PW").split(",");
        String[] url = {ConfigurationManager.getProperty(Constants.ATLAS_URL)};
        String[] use = ConfigurationManager.getProperty(Constants.ATLAS_PW).split(",");


        AtlasClientV2 atlasClientV2 = new AtlasClientV2(new DefaultConfigurationBuilder(), url, use);

        AtlasSearchResult atlasSearchResult = atlasClientV2.basicSearch(type,null,null, true,10000,0);

        List<AtlasEntityHeader> entities = atlasSearchResult.getEntities();

        for (int i = 0; i < entities.size(); i++) {
                String guid = entities.get(i).getGuid();

                AtlasEntity.AtlasEntitiesWithExtInfo entitiesByGuids = atlasClientV2.getEntitiesByGuids(Collections.singletonList(guid));
                AtlasEntity atlasEntity = entitiesByGuids.getEntities().get(0);
                Object o = ((Map) atlasEntity.getRelationshipAttribute("db")).get("relationshipStatus");
                String DELETED=o.toString();


//                String DELETED = (String) ((Map) atlasClientV2.getEntitiesByGuids(Collections.singletonList(guid)).getEntities().get(0).getRelationshipAttribute("db")).get("relationshipStatus");


                if (DELETED.equals("DELETED")){
                    atlasClientV2.deleteEntityByGuid(guid);
                }
            }
        return 1;
    }

}
