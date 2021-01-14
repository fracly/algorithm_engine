<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
    <a-form-item label="常数项:">
        <a-input v-model="params.absolute"  />
      </a-form-item>
      <a-form-item label="自定义参数:" :label-col="labelCol" :wrapper-col="wrapperCol">
        <div @click="openmodel()">
          <a-icon type="plus-circle" style="color:#429FE1"/><span v-if="params.localParams.length!=0">已添加{{params.localParams.length}}条自定义参数</span>
        </div>
        <a-modal v-if="customParamsStaus" :visible="customParamsStaus" destroyOnClose title="自定义参数" @cancel="showCustomParams" width="600px">
          <template slot="footer">
            <a-button type="primary" @click="saveCustomParams">
              保存
            </a-button>
            <a-button key="back" @click="showCustomParams">
              返回
            </a-button>
          </template>
          <custom-params :CustomParams="params.localParams"  :pageList="params.columnListAgo"  ref="CustomParams" ></custom-params>
        </a-modal>
      </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import customParams from "@/views//model-center/model-factory/module/Preprocess/AccumulationCalculator/customParams"
  import TagSelectOption from '../../../../../../components/TagSelect/TagSelectOption'

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
          columnListAgo: [],
          localParams: [],
          absolute: "1",
          list: [],
          coefficientList: []
        },
        customParamsStaus: false,
        initState: false
      };
    },
    components: { TagSelectOption, customParams },
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
          this.params.localParams = []
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
          this.params.columnListAgo = data.point0List;
        }
      },
      openmodel(){
        this.customParamsStaus=!this.customParamsStaus
      },
      saveCustomParams(){
        let localParams =this.$refs.CustomParams.localParams
        for(var i=0;i<localParams.length;i++){
          if((localParams[i].column=="" ||localParams[i].column==null) ){
            this.$message.error("列不能为空");
            return
          }else if(localParams[i].coefficient=="" ||localParams[i].coefficient==null){
            this.$message.error("系数不能为空");
            return
          }
        }
        this.params.localParams = localParams
        this.customParamsStaus=!this.customParamsStaus
        //取消的时候通过选中的列配置传给下一个节点的参数list 和 coefficientList
        this.params.list = []
        this.params.coefficientList = []
        for (let i=0;i < this.params.localParams.length;i++){
           let  name = this.params.localParams[i].column
           let  effic = this.params.localParams[i].coefficient
           this.params.list.push(name)
           this.params.coefficientList.push(effic)
        }
        debugger
        //新增累加列
        this.params.columnList = JSON.parse(JSON.stringify(this.params.columnListAgo));
        let list=[];
        for(let i=0;i<this.params.columnList.length;i++){
          list.push(this.params.columnList[i].name)
        }
        if (list.indexOf("accumulation_col") > 0) {
        }
        else {
          this.params.columnList.push({
            name:"accumulation_col",
            type:'double',
            comment:'累加列'
          });
        }
      },
      showCustomParams(){
        this.customParamsStaus=!this.customParamsStaus
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