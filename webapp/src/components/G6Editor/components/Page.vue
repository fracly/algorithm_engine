<template>
  <div class="page">
    <div :id="pageId" class="graph-container" style="position: relative;background-color: white"></div>
    <context-menu ref="menuChild" :AllowRunning="AllowRunning" :module="module" :graph="graph" :projectName="module.workGroup" :processInstanceId="processInstanceId" @runTaskForChild="runTaskForChild"></context-menu>
  </div>
</template>


<script>
  import Toolbar from "@/components/G6Editor/components/Toolbar";
  import ContextMenu from "@/components/G6Editor/components/ContextMenu";
  import G6 from "@antv/g6/build/g6";
  import {initBehavors} from "@/components/G6Editor/behavior";
  import okSvg from "@/assets/icons/ok.svg";
  import loadingSvg from "@/assets/icons/loading.gif";
  import errorSvg from "@/assets/icons/error.svg";
  import stopSvg from "@/assets/icons/stop.svg";
  import pauseSvg from "@/assets/icons/pause.svg";
  import readySvg from "@/assets/icons/ready.svg";


  import {formatDateTime} from "@/utils/global"

  import {startProcess, queryStatus,stopProcess} from "@/api/process"


  let Datas;
  export default {
    data() {
      return {
        pageId: "graph-container",
        graph: null,
        processInstanceId:0,
        Status:"",
      };
    },
    components: {
      Toolbar,
      ContextMenu
    },
    props: {
      height: {
        type: Number,
        default: 0
      },
      width: {
        type: Number,
        default: 0
      },
      data: {
        type: Object,
        default: () => {
          return {
            nodes: [],
            edges: []
          }
        }
      },
      editor: {
        type: Object,
        default: () => {
        }
      },
      module: {
        type: Object,
        default: () => {
        }
      },
      command: {
        type: Object,
        default: () => {
        }
      },
      LogList:{
        type: Array,
        default: () => {
          return []
        }
      },
      AllowRunning:{
        type:Boolean,
        default: () => false
      },
      readOnly:{
        type:Boolean,
        default: () => false
      }
    },
    created() {
      initBehavors();
    },
    mounted() {
      this.$nextTick(() => {
        this.init();
      });
    },
    computed: {
      newNodes() {
        return this.data.nodes;
      }
    },
    watch: {
      newNodes(val) {
        let _that=this
        this.graph.changeData(this.data);
        _that.loadPage()
      },
      'readOnly': {
        handler(newVal, oldVal) {
            if(newVal){
              this.graph.setMode("readOnly")
            }else{
              this.graph.setMode("default")
            }
        }
      }
    },
    methods: {
      stopTask(){
        let _that =this
        stopProcess({"processInstanceId":_that.processInstanceId,"projectName":_that.module.workGroup,"executeType":"STOP"}).then(res => {
          this.$emit("changeLoading",false,"");
          if (res.code != 0) {
            this.$message.error("流程停止失败：" + res.msg);
          } else {
            this.$message.success("流程停止中");
            _that.queryStatus()
          }
        }).catch(res => {
            this.$message.error("流程停止报错：" + res);
        })
      },
      loadPage() {
        /*加载页面先去除所有之前保存的状态 然后重新查询最新的状态*/
        this.cleanAllState();
        this.$emit("allowToolbarKey","AllowSaving",true)
        this.$emit("allowToolbarKey","AllowRunning",true)
        let _that =this
        queryStatus({"processDefinitionId":_that.module.id,"projectName":_that.module.workGroup}).then(res => {
          if(res.data ==null){
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            return
          }
          this.setStatus(res.data.taskList)
          //获取最后一次流程实例
          _that.processInstanceId = res.data.instanceId
          //如果流程正在运行 则获取最后一次的流程运行状态
          if(res.data.processInstanceState=="RUNNING_EXEUTION"){
            this.graph.setMode("readOnly")
            setTimeout(function () {
              _that.queryStatus()
            }, 2000);
          }else{//非运行状态 允许保存和运行
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
          }
        })
      },
      /*画布初始化*/
      init() {
        const height = this.height - 85
        const width = this.width - 400
        this.graph = new G6.Graph({
          container: "graph-container",
          //切换渲染方式 让画布支持GIF
          renderer: "canvas",
          height: height,
          width: width,
          modes: {
            // 支持的 behavior
            default: [
              "drag-canvas",
              "zoom-canvas",
              "hover-node",
              "select-node",
              "hover-edge",
              "keyboard",
              "customer-events",
              "add-menu",
            ],
            mulitSelect: ["mulit-select"],
            addEdge: ["add-edge"],
            moveNode: ["drag-item"],
            readOnly:[ "drag-canvas", "zoom-canvas","select-node", "add-menu"],
          }
        });
        let command = this.command;
        this.editor.emit("afterAddPage", {graph: this.graph, command});
      },
      /*查询流程节点运行状态*/
      queryStatus() {
        let _that =this
        if (this.processInstanceId != 0) {
          queryStatus({"processInstanceId":_that.processInstanceId,"projectName":_that.module.workGroup}).then(res => {
            if(res.data.taskList !=null){
              this.setStatus(res.data.taskList)
            }
            if(res.data.processInstanceState=="RUNNING_EXEUTION" || res.data.processInstanceState=="READY_STOP"){
              setTimeout(function () {
                _that.queryStatus()
              }, 2000);
            }else{
              this.graph.setMode("default")
              this.$emit("allowToolbarKey","AllowSaving",false)
              this.$emit("allowToolbarKey","AllowRunning",false)
            }
          })
        }
      },
      setStatus(listNode){
        let listNodes = listNode.sort(function (a, b) {
          return a.id - b.id;
        });
        this.setLog(listNodes);
        for(var i=0;i<listNodes.length;i++){
          let node = this.getNodes(listNodes[i].name);
          if(node == null){
            continue
          }
          let nodeModel = node.getModel();
          //如果节点状态是运行完成和失败 则不进行下一步判断
          if((nodeModel.state== "complete" || nodeModel.state== "failed") && ("taskInstId" in nodeModel)){
            const model = {
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
            continue;
          }
          /*任务失败*/
          if(listNodes[i].state=="FAILURE" || listNodes[i].state=="NEED_FAULT_TOLERANCE" || listNodes[i].state=="PARAMS_ERROR"){//任務失敗
            const model = {
              isDoingEnd: false,
              stateImage: "errorSvg",
              state: "failed",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
          }
          /*任务成功*/
          if(listNodes[i].state=="SUCCESS"){//任務成功
            const model = {
              stateImage: "okSvg",
              isDoingEnd: false,
              state: "complete",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
          }
          /*运行中 已提交状态*/
          if(listNodes[i].state=="RUNNING_EXEUTION" || listNodes[i].state=="WAITTING_DEPEND" || listNodes[i].state=="SUBMITTED_SUCCESS"){ //任务运行中
            const targetNode = {
              stateImage: "loadingSvg",
              isDoingStart: false,
              isDoingEnd: true,
              state: "running",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, targetNode);
            const sourceNode = {
              isDoingStart: true,
            };
            let source = node.getInEdges()
            for(var j=0;j<source.length;j++){
              let sourceChild =source[j].getSource();
              this.graph.update(sourceChild, sourceNode);
            }
          }
          //任务暂停
          if(listNodes[i].state=="READY_PAUSE" || listNodes[i].state=="PAUSE"){
            const model = {
              isDoingEnd: true,
              stateImage: "pauseSvg",
              state: "running",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
          }
          /*任务停止*/
          if(listNodes[i].state=="STOP"  || listNodes[i].state=="KILL"){
            const model = {
              isDoingEnd: false,
              stateImage: "stopSvg",
              state: "failed",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
          }
          /*等待*/
          if(listNodes[i].state=="WAITTING_THREAD"  || listNodes[i].state=="READY_STOP"){
            const model = {
              isDoingEnd: true,
              stateImage: "readySvg",
              state: "running",
              taskInstId:listNodes[i].id
            };
            this.graph.update(node, model);
          }
        }
      },
      /*日志输出*/
      setLog(listNodes){
        for(var i=0;i<listNodes.length;i++){
          let id = listNodes[i].id;
          let endTime = listNodes[i].endTime;
          let startTime = listNodes[i].startTime;
          if(startTime!=null){
            let map = {
              newId:id+"-startTime",
              time:formatDateTime(startTime,"yyyy-MM-dd HH:mm:ss"),
              nodeName:listNodes[i].name,
              taskSuccess:listNodes[i].taskSuccess,
              type:"start",
              tip:"开始执行",
              state:listNodes[i].state,
            }
            this.$emit("setLog",map)
          }
          if(endTime!=null){
            let map = {
              newId:id+"-endTime",
              time:formatDateTime(endTime,"yyyy-MM-dd HH:mm:ss"),
              nodeName:listNodes[i].name,
              taskSuccess:listNodes[i].taskSuccess,
              type:"end",
              tip:"执行完成",
              state:listNodes[i].state,
              resultTable:listNodes[i].resultTable,
            }
            this.$emit("setLog",map)
          }
        }
      },
      /*运行任务*/
      runTask(workInfo,param) {
        this.graph.setMode("readOnly")
        //1 初始化所有任务状态
        this.cleanAllState();
        this.$emit("changeLoading",true,"启动任务中，请稍后")
        this.$emit("allowToolbarKey","AllowSaving",true)
        this.$emit("allowToolbarKey","AllowRunning",true)
        //2 模拟任务运行 后期修改为从接口获取运行数据
        workInfo.projectName = this.module.workGroup
        if("taskDependType" in param && (param.taskDependType=="TASK_ONLY" || param.taskDependType=="TASK_PRE" || param.taskDependType=="TASK_POST")){
          workInfo.startNodeList=param.startNodeList;
          workInfo.taskDependType=param.taskDependType;
        }
        startProcess(workInfo).then(res => {
          this.$emit("changeLoading",false,"");
          if (res.code != 0) {
            this.graph.setMode("default")
            this.$message.error("流程运行失败：" + res.msg);
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            return false;
          } else {
            this.$emit("initInstanceId",res.data.id)
            this.processInstanceId = res.data.id
            this.queryStatus();
          }
        }).catch(res => {
          this.$message.error("流程运行报错：" + res);
          this.$emit("changeLoading",false,"");
          this.$emit("allowToolbarKey","AllowSaving",false)
          this.$emit("allowToolbarKey","AllowRunning",false)
        })
      },
      /*按钮子组件 运行任务*/
      runTaskForChild(param){
        this.$emit('saveTopo',param);
      },
      /*通过关键词查找状态*/
      getNodes(key) {
        let nodes = this.graph.getNodes();
        for (var i = 0; i < nodes.length; i++) {
          if (nodes[i]._cfg.model.tasks.name == key) {
            return nodes[i];
          }
        }
        return null
      },
      /*清空所有节点状态*/
      cleanAllState() {
        let nodes = this.graph.getNodes();
        if (nodes.length == 0 || nodes == null) {
          //this.$message.error('任务运行失败！您没有设置任何节点');
          return
        }
        for (var i = 0; i < nodes.length; i++) {
          const model = {
            stateImage: null,
            isDoingEnd: false,
            isDoingStart: false,
            state:"wait",
          };
          this.graph.update(nodes[i], model);
        }

      }
    }
  };
</script>

<style scoped>
  .page {
    /*margin-left: 200px;
    margin-right: 200px;*/
    border-bottom: 1px solid #e8e8e8;
  }

  text[font-size~="16"] {
    font-size: 14px;
  }

  /* 提示框的样式 */
  .g6-tooltip {
    border: 1px solid #e2e2e2;
    border-radius: 4px;
    font-size: 12px;
    color: #545454;
    background-color: rgba(255, 255, 255, 0.9);
    padding: 10px 8px;
    box-shadow: rgb(174, 174, 174) 0px 0px 10px;
  }
</style>
