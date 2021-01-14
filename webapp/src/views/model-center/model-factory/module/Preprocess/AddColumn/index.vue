<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
    <a-form-item label="类型选择:">
      <a-select v-model="params.typeSelect"  placeholder="请选择列" >
        <a-select-option  v-for="c in params.typeList"  :key="c.value" :value="c.value"  > {{ c.name}} </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="常数:" v-show=" params.typeSelect == 'constant' ">
      <a-input v-model="params.value"   />
    </a-form-item>

    <a-form-item label="选择列:" v-show=" params.typeSelect == 'order' ">
      <a-select v-model="params.orderColumn"  placeholder="请选择列" show-search optionFilterProp="children">
        <a-select-option v-for="d in params.columnListAgo" :key="d.name"> {{ d.comment == ''||d.comment==undefined?d.name:d.comment }} </a-select-option>
      </a-select>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import FieldSelection from "@/components/FieldSelection"

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          columnList:[],
          columnListAgo: [],
          datasource: 16,
          table: "",
          showType: "TABLE",
          newTable: "",
          dataStorage: getDataStorage(),
          //类型选择表
          typeList: [{name: "新增常数列", value: "constant"}, {name: "新增序号列", value: "order"}],
          typeSelect: "",
          value: 0,
          orderColumn: null,
        },
        customParamsStaus: false,
        initState: false
      };
    },
    components: {FieldSelection},
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
      /*页面初始化 按需加载，具体方法查看module.vue*/
      initPage() {
        debugger
        //#####################通用写法###################
        //上次本组件保存的参数
        if(JSON.stringify(this.tasks)!='{}'){
          this.params = this.tasks
        }else{
          //如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable=this.$parent.getLocalNodeTableName()
        }
        //////////////******************************//自己需要清理的数据
        //判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空
        let preNodeTableName = this.$parent.getPreNodeTableName()
        if(preNodeTableName != this.params.table){
          this.params.typeSelect = ""
          this.params.value = null
          this.params.orderColumn = null
        }
        //#####################通用写法###################
        //获取上一节点的datasource
        this.params.datasource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
        //####################组件按需加载####################
        /*获取上游节点字段信息*/
        this.getProNodeColumList()
        //####################组件按需加载####################
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

      addPinjie(){
        this.params.columnList = JSON.parse(JSON.stringify(this.params.columnListAgo));
        //新增列
        let list=[];
        let comment=""
        for(let i=0;i<this.params.columnList.length;i++){
          list.push(this.params.columnList[i].name)
          if(this.params.orderColumn == this.params.columnList[i].name) {
            comment = this.params.columnList[i].comment == "" ? this.params.columnList[i].name : this.params.columnList[i].comment
          }
        }
        if (list.indexOf("addcolumn") > 0) {
        } else {
          if (this.params.typeSelect == "constant") {
            this.params.columnList.push({
              name: "addcolumn",
              type: 'string',
              comment: '常数列'
            });
          }
          else if (this.params.typeSelect == "order") {            this.params.columnList.push({
              name: "addcolumn",
              type: 'int',
              comment: '序号列_'+comment
            });
          }
        }
      }
    },
    watch: {
      "params.value": function() {
        this.addPinjie()
        debugger
      },
      "params.orderColumn": function() {
        this.addPinjie()
        debugger
      },
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