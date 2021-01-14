<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="选择列:">
      <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-select  v-model="params.column"  placeholder="请选择列" @change="addColumn()" show-search optionFilterProp="children">
        <a-select-option v-for="d in params.columnListAgo" :key="d.name">{{ d.comment == ''||d.comment==undefined?d.name:d.comment }}  </a-select-option>
      </a-select>
    </a-form-item>


    <a-form-item label="离散化方式:">
      <a-select  v-model="params.way"  placeholder="请选择列" >
        <a-select-option v-for="d in params.WayList" :key="d.value">{{ d.name }} </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item  label="类别数:" >
      <a-input  v-model="params.autonum" />
    </a-form-item>
<!--    <a-form-item v-if="params.way != '0' " label="手动参数:" :label-col="labelCol" :wrapper-col="wrapperCol">-->
<!--      <div @click="openmodel()">-->
<!--        <a-icon type="plus-circle" style="color:#429FE1"/><span v-if="params.localParams.length!=0">已添加{{params.localParams.length}}条自定义参数</span>-->
<!--      </div>-->
<!--      <a-modal v-if="customParamsStaus" :visible="customParamsStaus" destroyOnClose title="自定义参数" @cancel="showCustomParams" width="650px">-->
<!--        <template slot="footer">-->
<!--          <a-button key="back" @click="showCustomParams">-->
<!--            返回-->
<!--          </a-button>-->
<!--        </template>-->
<!--        <custom-params :CustomParams="params.localParams"  ></custom-params>-->
<!--      </a-modal>-->
<!--    </a-form-item>-->
<!--    <a-form-item v-if="params.way != '0' " label="离散范围:"  >-->
<!--      <a-input  v-model="params.fanwei" />-->
<!--    </a-form-item>-->

  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import customParams from "@/views/model-center/model-factory/module/Preprocess/DataDiscretization/customParams"

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        customParamsStaus: false,
        params: {
          sqlType: 0,
          databaseData: [],
          tableData: [],
          datasource: "",
          table: "",
          showType: "TABLE",
          newTable: "",
          dataStorage: getDataStorage(),
          columnList: [],
          columnListAgo: [],
          column: "",
          way: "",
          WayList: [{name: "等宽离散", value: "0"}, {name: "等频离散", value: '1'}],
          autonum: "",
          localParams: [],
          nonup: [],
          nondown: [],
          nonnum: [],
          fanwei:""
        },
        customParamsStaus: false,
        initState: false
      };
    },
    components: { customParams },
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
          this.params.nonup = []
          this.params.nondown = []
          this.params.nonnum = []
          this.params.column = ""
          this.params.way = ""
          this.params.autonum = ""
          this.params.localParams = []
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
          this.params.columnListAgo = data.point0List;
        }
      },

      showCustomParams(){
        this.customParamsStaus = !this.customParamsStaus
        //取消的时候通过选中的列配置传给下一个节点的参数list 和 coefficientList
        this.params.nonup = []
        this.params.nondown = []
        this.params.nonnum = []
        for (let i=0;i < this.params.localParams.length;i++){
          let  nonup = this.params.localParams[i].nonup
          let  nondown = this.params.localParams[i].nondown
          let  nonnum = this.params.localParams[i].nonnum
          this.params.nonup.push(nonup)
          this.params.nondown.push(nondown)
          this.params.nonnum.push(nonnum)
        }
      },
      openmodel(){
        this.customParamsStaus = !this.customParamsStaus
      },

      //增加离散列字段到columnlist里面
      addColumn(){
        this.params.columnList = JSON.parse(JSON.stringify(this.params.columnListAgo));
        let list=[];
        for(let i=0;i<this.params.columnList.length;i++){
          list.push(this.params.columnList[i].name)
        }
        if (list.indexOf("discretization_" + this.params.column) > 0) {
        }
        else {
          this.params.columnList.push({
            name:"discretization_" + this.params.column,
            type:'string',
            comment:'离散列'
          });
        }
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