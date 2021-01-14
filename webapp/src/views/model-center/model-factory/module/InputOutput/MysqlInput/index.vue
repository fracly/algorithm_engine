<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
      <!--<a-form-item label="数据源:">
        &lt;!&ndash;<a-select placeholder="请选择数据源" >
          <a-select-option value="HIVE" key="HIVE">HIVE</a-select-option>
        </a-select>&ndash;&gt;
      </a-form-item>-->
    <a-form-item label="选择库:">
      <a-select placeholder="请选择库" v-model="params.datasource" :defaultValue="params.datasource">
        <a-select-option v-for="item in databaseData" :key="item.id">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item :label="params.type == 'KAFKA' ? 'Topic名:' : '选择表:'">
      <a-select show-search placeholder="请选择表" v-model="params.table" :defaultValue="params.table" :filter-option="filterOption"  option-filter-prop="children">
        <a-select-option v-for="(item,index) in tableData" :key="index" :value="item.name">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="输入过滤条件:" type="textarea" autosize="{ minRows: 2, maxRows: 6 }">
      <a-input v-model="params.cond" placeholder=" 例如 : where 1=1" />
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
            datasource: null,
            dataStorage: getDataStorage(),
            table: "",
            udfs: "",
            sqlType: 0,
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
            cond: "",
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
        getTableData({ 'datasourceId': this.params.datasource,"type":this.params.type }).then(res => {
          this.tableData=res.data;
        })
      }else {
        this.reloadDatabaseData();
        this.params.newTable=this.$parent.getLocalNodeTableName()
      }
      this.$watch('params.type', this.watchType)
      this.$watch('params.datasource', this.watchDataSource)
      this.$watch('params.table', this.watchTable)
    },
    methods: {
      /*重新加载库*/
      reloadDatabaseData(){
        debugger
        getDataBaseData({ 'type': this.params.type }).then(res => {
            this.databaseData=res.data;
            this.tableData=[];
            this.$set(this.params, 'datasource',"");
            this.$set(this.params, 'table',"");
        })
      },
      /*重新加载表*/
        reloadTableData(){
        getTableData({ 'datasourceId': this.params.datasource,"type":this.params.type }).then(res => {
          this.tableData=res.data;
          this.$set(this.params, 'table',"");
        })
      },
      /*重新加载字段信息*/
      reloadTableCloumData(){
        /*this.params.newTable=this.params.table*/
        getColumData({ 'datasourceId': this.params.datasource,"type":this.params.type,'table':this.params.table }).then(res => {
          this.$set(this.params, "columnList", res.data);
        })
      },
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
      watchTable(){
        if(this.params.table!="" && this.params.table!=undefined && this.params.table!=null){
          this.reloadTableCloumData()
        }
      }
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

