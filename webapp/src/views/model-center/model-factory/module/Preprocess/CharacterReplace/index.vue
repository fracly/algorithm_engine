<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>

    <a-form-item label="替换列:">
      <a-select v-model="params.column"  placeholder="请选择列" show-search optionFilterProp="children">
        <a-select-option v-for="d in params.columnListAgo" :key="d.name"> {{ d.comment == ''||d.comment==undefined?d.name:d.comment }} </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="类型选择:" >
      <a-select v-model="params.rownull">
        <a-select-option :value="a.value" v-for=" a in params.typeList " :key="a.value"> {{a.name}}</a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="原字符串:" v-if=" params.rownull == '1'  ">
      <a-input v-model="params.str_old"   />
    </a-form-item>

    <a-form-item label="新字符串:"  v-if=" params.rownull == '1' || params.rownull == '2' ">
      <a-input v-model="params.str_new"   />
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
        params: {
          columnList:[],
          columnListAgo: [],
          datasource: 16,
          table: "",
          showType: "TABLE",
          newTable: "",
          dataStorage: getDataStorage(),
          column: "",
          typeList: [{name: "删除空值", value: "0"}, {name: "非空字符串替换", value: "1"}, {name: "空值替换", value: "2"}],
          rownull: "",
          str_old: "",
          str_new: ""
        },
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
          this.params.column = ""
          this.params.rownull = ""
          this.params.str_old = ""
          this.params.str_new = ""
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
      //增加新列
      addPinjie(){
        this.params.columnList = JSON.parse(JSON.stringify(this.params.columnListAgo));
        //新增列
        let list=[];
        for(let i=0;i<this.params.columnList.length;i++){
          list.push(this.params.columnList[i].name)
        }
        if (list.indexOf("addcolumn") > 0) {
        } else {
          this.params.columnList.push({
            "name": "replace"+this.params.column,
            "type": "string",
            "comment": "替换列"
          })
        }
      }
    },
    watch: {
      "params.column": function () {
        this.addPinjie()
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