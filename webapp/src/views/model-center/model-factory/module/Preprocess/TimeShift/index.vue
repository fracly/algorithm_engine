<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="时间列:">
      <span v-if="params.columnList.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-select  v-model="params.timeColumn"  placeholder="请选择时间列" show-search optionFilterProp="children">
          <a-select-option v-for="d in params.columnList" :key="d.name">{{ d.comment == ''||d.comment==undefined?d.name:d.comment }}  </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="时间转换格式:">
      <a-select v-model="params.formatold"  placeholder="请选择时间格式">
        <a-select-option v-for="f in params.formatList" :key="f">{{ f }} </a-select-option>
      </a-select>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import TagSelectOption from '../../../../../../components/TagSelect/TagSelectOption'

  const formatList = ["yyyy","yyyy-MM","yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH","yyyy-MM-dd HH:mm"]
  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          databaseData: [],
          tableData: [],
          datasource: "",
          table: "",
          showType: "TABLE",
          newTable: "",
          dataStorage: getDataStorage(),
          columnList: [],
          timeColumn: undefined,
          format: "",
          formatold: undefined,
          formatList
        },
        customParamsStaus: false,
        initState: false
      };
    },
    components: { TagSelectOption },
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
        // debugger
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
          this.params.timeColumn = undefined
          this.params.format = undefined
        }

        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()

        /*获取上游节点字段信息*/
        this.getProNodeColumList()
      },
      /*获取上游节点的字段值*/
      getProNodeColumList(){
        /*获取上游节点字段信息数据*/
        let _that = this
        let data = this.$parent.getPreNodeColumList()
        if("point0List" in data){
          this.params.columnList = data.point0List;
        }
      },
    },
    watch: {
      "params.formatold": function () {
        this.params.format=this.params.formatold.replace(/ /g, '!!');
        //debugger
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