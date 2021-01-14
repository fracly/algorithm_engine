<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
      <!--<a-form-item label="数据源:">
        &lt;!&ndash;<a-select placeholder="请选择数据源" >
          <a-select-option value="HIVE" key="HIVE">HIVE</a-select-option>
        </a-select>&ndash;&gt;
      </a-form-item>-->
    <a-form-item label="选择库:">
      <a-select placeholder="请选择库" v-model="params.dataStorage" :defaultValue="params.dataStorage">
        <a-select-option v-for="item in databaseData" :key="item.id">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item :label="params.type == 'KAFKA' ? 'Topic名:' : '选择表:'">
      <a-select show-search placeholder="请选择表" v-model="params.newTable" :defaultValue="params.newTable" :filter-option="filterOption"  option-filter-prop="children">
        <a-select-option v-for="(item,index) in tableData" :key="index" :value="item.name">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="请选择保存方式">
      <a-select show-search placeholder="请选择保存方式" v-model="params.save" style="width: 100px">
        <a-select-option key="W" value="W">覆盖</a-select-option>
        <a-select-option key="I" value="I">追加</a-select-option>
      </a-select>
    </a-form-item>

  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import {getDataBaseData,getTableData,getColumData} from '@/api/process'
  import {getUpdateById} from '@/api/datasource'



  export default {
    data() {
      return {
        dataSourceData:getDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        databaseData:[],
        tableData:[],
        params:{
            type: "MYSQL",
            datasource:null,
            dataStorage: null,
            table: "", // 上游节点 输出表名
            udfs: "",
            save: "I",
            title: "",
            receivers: "",
            receiversCc: "",
            columnList:[],
            showType: "TABLE",
            localParams: [],
            connParams: "",
            newTable:"",
            preStatements: [],
            postStatements: [],
        },
        initState:false
      };
    },
    components:{
    },
          props:{
            tasks: {
              type: Object,
            default: () => {
                return {
                  id:"default",
                  label:""
                }
        }
      }
    },
    created() {
      if(JSON.stringify(this.tasks)!='{}') {
        this.params = this.tasks
        getDataBaseData({ 'type': this.params.type }).then(res => {
          this.databaseData=res.data;
        })
        getTableData({ 'datasourceId': this.params.dataStorage,"type":this.params.type }).then(res => {
          this.tableData=res.data;
        })
      }else {
        this.reloadDatabaseData();
        // this.params.newTable=this.$parent.getLocalNodeTableName()
      }
      this.$watch('params.type', this.watchType)
      this.$watch('params.dataStorage', this.watchDataSource)
      this.$watch('params.newTable', this.watchTable)
      this.initPage()
    },
    methods: {

      /*页面初始化 按需加载，具体方法查看module.vue*/
      initPage() {

        //////////////******************************//自己需要清理的数据
        //判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空
        let preNodeTableName = this.$parent.getPreNodeTableName()
        if(preNodeTableName != this.params.table){
          this.params.fraction = ""
        }
        // //#####################通用写法###################
        // //获取上一节点的datasource
        this.params.datasource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
        //####################组件按需加载####################
        // /*获取上游节点字段信息*/
        // this.getProNodeColumList()
        //####################组件按需加载####################
      },
      /*重新加载库*/
      reloadDatabaseData(){
        getDataBaseData({ 'type': this.params.type }).then(res => {
            this.databaseData=res.data;
            this.tableData=[];
            this.$set(this.params, 'dataStorage',"");
            this.$set(this.params, 'newTable',"");
        })
      },
      /*重新加载表*/
        reloadTableData(){
        getTableData({ 'datasourceId': this.params.dataStorage,"type":this.params.type }).then(res => {
          this.tableData=res.data;
          this.$set(this.params, 'newTable',"");
        })
      },
      /*重新加载字段信息*/
      // reloadTableCloumData(){
      //   /*this.params.newTable=this.params.table*/
      //   getColumData({ 'datasourceId': this.params.datasource,"type":this.params.type,'table':this.params.table }).then(res => {
      //     this.$set(this.params, "columnList", res.data);
      //   })
      // },
      /*表查询*/
      filterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        );
      },
      /*数据源监听*/
      watchType(){
        if(this.params.type!="" && this.params.type!=undefined && this.params.type!=null){
          this.reloadDatabaseData()
        }
      },
      /*库监听*/
      watchDataSource(){
        if(this.params.datasource!="" && this.params.datasource!=undefined && this.params.datasource!=null){
          this.reloadTableData()
        }
      },
      /*表监听*/
      // watchTable(){
      //   if(this.params.table!="" && this.params.table!=undefined && this.params.table!=null){
      //     this.reloadTableCloumData()
      //   }
      // }
    }
  };
</script>
<style scoped>
  .ant-form-item {
     margin-bottom: 4px!important;
  }

   /deep/.ant-modal-body {
     padding: 5px!important;
   }

</style>

