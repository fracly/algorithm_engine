<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
    <a-form-item label="数据源:">
      <a-select placeholder="请选择数据源" v-model="params.type">
        <a-select-option v-for="item in dataSourceData" :key="item.id" :value="item.code">
          {{ item.code }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="选择库:">
      <a-select placeholder="请选择库" v-model="params.datasource" :defaultValue="params.datasource">
        <a-select-option v-for="item in databaseData" :key="item.id">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="请输入表:" type="textarea" autosize="{ minRows: 2, maxRows: 6 }">
      <a-input v-model="params.table"/>
    </a-form-item>

  </a-form-model>
</template>

<script>
  import {getWirteDataSouceData} from "@/utils/global";
  import {getDataBaseData,getTableData,getColumData} from '@/api/process'


  export default {
    data() {
      return {
        dataSourceData:getWirteDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        databaseData:[],
        tableData:[],
        params:{
          type: "",
          datasource: "",
          table: "",
          lastTable: "",
          typeDatasource: "",

        },
        customParamsStaus:false,
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
      }
      this.$watch('params.type', this.watchType)
      this.$watch('params.datasource', this.watchDataSource)
      this.$watch('params.table', this.watchTable)

      this.initPage();
    },
    methods: {

      initPage() {
        debugger

        //#####################通用写法###################
        //获取上一节点的datasource
        this.params.typeDatasource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.lastTable = this.$parent.getPreNodeTableName()

      },

      showCustomParams(){
        this.customParamsStaus=!this.customParamsStaus
      },
      /*重新加载库*/
      reloadDatabaseData(){
        //this.$parent.updateLocalNodeParam("name","写入"+this.params.type)
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
        this.params.newTable=this.params.table
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

