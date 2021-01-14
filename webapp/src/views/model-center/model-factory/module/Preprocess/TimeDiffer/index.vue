<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="被减列:">
      <span v-if="columnList.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-select  v-model="params.column"  placeholder="请选择列" show-search optionFilterProp="children">
        <a-select-option v-for="d in columnList" :key="d.name">{{ d.comment == ''||d.comment==undefined?d.name:d.comment }} </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="减列:">
      <a-select v-model="params.column2"  placeholder="请选择列" show-search optionFilterProp="children">
        <a-select-option v-for="d in columnList" :key="d.name">{{ d.comment == ''||d.comment==undefined?d.name:d.comment }} </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="时间差单位:">
      <a-select v-model="params.unit"  placeholder="请选择单位">
        <a-select-option v-for="d in params.unitList"  :key="d.value">{{ d.name }} </a-select-option>
      </a-select>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import cloneDeep from 'lodash.clonedeep'

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        columnList: [],
        params: {
          databaseData: [],
          tableData: [],
          datasource: "",
          table: "",
          showType: "TABLE",
          newTable: "",
          dataStorage: getDataStorage(),
          columnList: [],
          column: undefined,
          column2: undefined,
          unit: undefined,
          unitList: [{name: "秒", value: "seconds"}, {name: "分钟", value: "minutes"}, {name: "小时", value: "hours"}],
        },
        customParamsStaus: false,
        initState: false
      };
    },
    components: {  },
    props: {
      tasks: {
        type: Object,
        default: () => {
          return {
            id: "default",
            label: ""
          }
        }
      },
    },
    created() {
      this.initPage();
    },
    methods: {
      /*页面初始化 */
      initPage() {
        debugger
        //上次本组件保存的参数
        if(JSON.stringify(this.tasks)!='{}'){
          this.params = this.tasks
        }else{
          //如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable=this.$parent.getLocalNodeTableName()
        }
        //获取上一节点的datasource
        this.params.datasource = this.$parent.getPreNodeDataSourceId()

        //判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空,要写在保存表的上面
        let preNodeTableName = this.$parent.getPreNodeTableName()
        if(preNodeTableName != this.params.table){
          this.params.column = undefined
          this.params.column2 = undefined
          this.params.unit = undefined
          this.params.columnList = []

        }

        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()

        /*获取上游节点字段信息*/
        this.getProNodeColumList()
      },
      /*获取上游节点的字段值*/
      getProNodeColumList(){
        debugger
        /*获取上游节点字段信息数据*/
        let _that = this
        let data = this.$parent.getPreNodeColumList()
        if("point0List" in data){
          _that.columnList = data.point0List;

        }
      },
    },
    watch: {
      "params.column2": function() {
        debugger
        let that = this
        that.params.columnList = cloneDeep(that.columnList);
        let list = [];
        for (let i = 0; i < this.columnList.length; i++) {
          list.push(this.columnList[i].name)
        }
        if (list.indexOf("time_difference") > 0){

        }else {
          that.params.columnList.push(
            {
              key: "time_difference",
              name: "time_difference",
              type: 'string',
              comment: '时间差列'
            })
        }
        console.info(this.params.cloumList)
        debugger
       }
    }
  };


</script>
<style scoped>
  /deep/.ant-modal-body {
    padding: 15px!important;
  }
</style>

<style>
  .filedChoose{
    color:#4FAEEC;
    padding-left: 3px;
    padding-right:3px
  }
  .el-tooltip__popper[x-placement^=top] .popper__arrow {
    border-top-color: #1ab394;
  }
  .el-tooltip__popper[x-placement^=top] .popper__arrow:after {
    border-top-color: #1ab394;
  }
  .atooltip {
    background: #1ab394 !important;
  }


</style>