<template>
  <div>
    <v-contextmenu ref="dataContextmenu" style="width: 150px;padding: 5px">
      <v-contextmenu-item @click="copyNode">
        <a-icon type="copy" style="height: 15px;color: #FDD455"/>&nbsp;复制节点
      </v-contextmenu-item>
      <v-contextmenu-item divider></v-contextmenu-item>
      <v-contextmenu-item @click="runCurrentNode" :disabled="AllowRunning">
        <a-icon type="play-circle" style="height: 15px;color: #70CD79"/>&nbsp;执行当前节点
      </v-contextmenu-item>
      <!-- <v-contextmenu-item @click="stopCurrentNode">
           <a-icon type="play-circle" style="height: 15px;color: #E82C1D"/>&nbsp;停止节点
       </v-contextmenu-item>-->
      <v-contextmenu-item @click="executeFromNode" :disabled="AllowRunning">
        <a-icon type="double-right" style="height: 15px;"/>&nbsp;从该节点开始执行
      </v-contextmenu-item>
      <v-contextmenu-item @click="executeToNode" :disabled="AllowRunning">
        <a-icon type="double-left" style="height: 15px;"/>&nbsp;执行到该节点
      </v-contextmenu-item>
      <v-contextmenu-item divider></v-contextmenu-item>
      <v-contextmenu-item @click="showNodeResult"  :disabled="projectName=='' || taskInstId==0">
        <a-icon type="read" style="height: 15px;color: #DBC8D4"/>&nbsp;查看结果
      </v-contextmenu-item>
      <v-contextmenu-item @click="showNodeLog" :disabled="projectName=='' || processInstanceId==0">
        <a-icon type="code" style="height: 15px;color: #333333"/>&nbsp;查看日志
      </v-contextmenu-item>
      <!--<v-contextmenu-item @click="deleteNode">
          <a-icon type="delete" style="height: 15px;color: #F15B54"/>&nbsp;删除节点
      </v-contextmenu-item>-->
    </v-contextmenu>
    <result v-if="showResult" @handleCancel="handleCancel" :processInstanceId="taskInstId" :projectName="projectName" :resultName="resultName"></result>
    <node-log v-if="showLog" :taskInstId="taskInstId" @handleCancelLog="handleCancelLog"></node-log>
  </div>
</template>

<script>
  import eventBus from "@/components/G6Editor/utils/eventBus";
  import okSvg from "@/assets/icons/ok.svg";
  import loadingSvg from "@/assets/icons/loading.gif";
  import errorSvg from "@/assets/icons/error.svg";
  import Result from "@/components/G6Editor/components/Result"
  import { uniqueId } from '@/components/G6Editor/utils'
  import {clone} from '@/utils/global'
  /**/
  import NodeLog from "@/components/G6Editor/components/NodeLog"
  import "@/components/G6Editor/components/Base";


  export default {
    components: {
      Result,
      NodeLog
    },
    data() {
      return {
        taskInstId: 0,
        node: {},
        page: null,
        command: null,
        showResult: false,
        showLog: false,
        resultName:"",

      };
    },
    props: {
      processInstanceId:{
        type: Number,
        default: () => 0
      },
      projectName:{
        type: String,
        default: () => ''
      },
      graph: {
        type: Object,
        default: () => {
        }
      },
      AllowRunning:{
        type:Boolean
      },
      module:{
        type: Object,
        default: () => {
        }
      }
    },
    created() {
      this.bindEvent();
    },
    methods: {
      handleCancelLog() {
        if (this.showLog) {
          this.showLog = false
        }
      },
      handleCancel() {
        if (this.showResult) {
          this.showResult = false
        }
      },
      deleteNode() {

      },
      /*复制节点*/
      copyNode() {
        if (this.page) {
          const graph = this.page.graph;
          var data = clone(this.node.getModel());
          data.offsetX = data.offsetX+ 100;
          data.offsetY = data.offsetY+ 30;
          data.x = data.x+ 100;
          data.y = data.y+ 30;
          data.id = getTaskNodeId();
          data.state="",
          data = this.addAuthPoint(data);
          data.label = this.createNodeName(data.label, data.label, 0)
          //初始化节点公共参数参数 防止报错
          data.tasks.id = data.id
          data.tasks.name = data.label
          data.stateImage=""
          //重新生成newtable 名称
          data.tasks.params.newTable=this.module.workId + "_" + data.id
        }
        this.command.executeCommand("add", [data]);
        this.node = {}
      },

      showNodeLog() {
        this.showLog = true
      },
      /*查看节点结果*/
      showNodeResult() {
        this.showResult = true
      },

      runCurrentNode() {
        var item={
          type:"runTask",
          startNodeList:this.resultName,
          taskDependType: "TASK_ONLY"
        }
        this.$emit("runTaskForChild",item);
      },
      /*从当前节点执行*/
      executeFromNode() {
        var item={
          type:"runTask",
          startNodeList:this.resultName,
          taskDependType: "TASK_POST"
        }
        this.$emit("runTaskForChild",item);
      },
      /*执行到当前节点*/
      executeToNode() {
        var item={
          type:"runTask",
          startNodeList:this.resultName,
          taskDependType: "TASK_PRE"
        }
        this.$emit("runTaskForChild",item);
      },
      /*停止当前节点*/
      stopCurrentNode() {
      },
      /*清空所有节点状态*/
      runTaskForLast(node) {
        let _that = this
        let Node = this.getNodes(node._cfg.id);
        let edges = node.getEdges();
        if (edges == null || edges.length == 0) {
          const model = {
            stateImage: errorSvg,
            state: "failed"
          };
          this.graph.update(Node, model);
          return
        }
        if (edges.length == 1 && edges[0].getTarget()._cfg.id == node._cfg.id) {
          let target = edges[0].getTarget();
          let targetNode = this.getNodes(target._cfg.id);
          const models = {
            stateImage: okSvg,
            isDoingStart: false,
            isDoingEnd: false,
            state: "complete"
          };
          setTimeout(function () {
            _that.graph.update(targetNode, models);
          }, 3000);
          return;
        }
        for (var i = 0; i < edges.length; i++) {
          if (edges[i].getSource()._cfg.id == node._cfg.id) {
            let source = edges[i].getSource();
            let sourceNode = this.getNodes(source._cfg.id);
            const Sourcemodels = {
              stateImage: okSvg,
              isDoingStart: true,
              isDoingEnd: false,
              state: "complete"
            };
            this.graph.update(sourceNode, Sourcemodels);
            let target = edges[i].getTarget();
            let targetNode = this.getNodes(target._cfg.id);
            const models = {
              stateImage: loadingSvg,
              isDoingStart: false,
              isDoingEnd: true,
              state: "running"
            };
            this.graph.update(targetNode, models);

            setTimeout(function () {
              _that.runTaskForLast(targetNode)
            }, 3000);
          }
        }

      },
      getNodes(key) {
        let nodes = this.graph.getNodes();
        for (var i = 0; i < nodes.length; i++) {
          if (nodes[i]._cfg.id == key) {
            return nodes[i];
          }
        }
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
            isDoingStart: false
          };
          this.graph.update(nodes[i], model);
        }

      },
      bindEvent() {
        let _that = this
        eventBus.$on("contextmenuClick", e => {
          _that.node = e.item
          _that.resultName = _that.node.getModel().tasks.name
          _that.taskInstId = _that.node.getModel().taskInstId
          /**先清除之前的弹窗**/
          _that.NodeTreeItem = null;
          if (e.item.getModel() == null) {
            _that.$refs.dataContextmenu.hide()
            return;
          };
          _that.NodeTreeItem = {
            id: this.node.id,
            title: this.node.label,
            parentId: null
          };
          const x = e.clientX
          // 因为我放在页面上的box有滚动条，所以需要减掉该盒子的scrollTop
          //const y = event.currentTarget.offsetTop - document.getElementById('left').scrollTop;
          const y = e.clientY
          const postition = {
            top: y - 8,
            left: x + 50
          };
          _that.$refs.dataContextmenu.show(postition);
        });
        eventBus.$on("mousedown", () => {
          this.NodeTreeItem = null;
        });
        eventBus.$on("afterAddPage", page => {
          this.page = page;
          this.command = page.command;
        });
      },
      /*节点名称必须唯一 且必须为下一个节点的preTaskName赋值*/
      createNodeName(NodeName, NodeNameTemp, i) {
        if (!this.findNodeName(NodeNameTemp)) {
          ++i;
          NodeNameTemp = this.createNodeName(NodeName, NodeName+"-COPY"+i, i);
        }
        return NodeNameTemp
      },
      /*给节点新增节点位置*/
      addAuthPoint(data){
        let size = data.size;
        if (!size) {
          size = [170, 34]
        }
        // 此处必须是NUMBER 不然bbox不正常
        const width = parseInt(size[0]);
        const height = parseInt(size[1]);
        // 此处必须有偏移 不然drag-node错位
        const offsetX = -width / 2
        const offsetY = -height / 2
        if (data.inPoints) {
          var inPoints=[]
          for (let i = 0; i < data.inPoints.length; i++) {
            let x, y = 0;
            //0为顶 1为底
            if (data.inPoints[i][0] === 0) {
              y = 0;
            } else {
              y = height;
            }
            x = width * data.inPoints[i][1];
            const id = 'inPoint' + uniqueId()
            var item =  {
              id: id,
              parent: id,
              x: x + offsetX,
              y: y + offsetY,
              isInPointOut: true,
              index:i
            }
            inPoints[i] = item
          }
          data.InAnchorPoints=inPoints
        }
        if (data.outPoints) {
          var outPoints=[]
          for (let i = 0; i < data.outPoints.length; i++) {
            let x, y = 0;
            //0为顶 1为底
            if (data.outPoints[i][0] === 0) {
              y = 0;
            } else {
              y = height;
            }
            x = width * data.outPoints[i][1];
            const id = 'outPoint' + uniqueId()
            var item =  {
              id: id,
              parent: id,
              x: x + offsetX,
              y: y + offsetY,
              isOutPoints: true,
              index:i
            }
            outPoints[i] = item
          }
          data.OutAnchorPoints=outPoints
        }
        return data;
      },
      /*查找节点名称*/
      findNodeName(NodeName) {
        let nodes = this.page.graph.getNodes();
        for (var i = 0; i < nodes.length; i++) {
          if (nodes[i].getModel().tasks.name == NodeName) {
            return false;
          }
        }
        return true
      }
    }
  };

  /*给节点生成唯一标志*/
  function getTaskNodeId(){
    return "tasks_"+ Math.ceil(Math.random()*100000)
  }
</script>

<style>
  .v-contextmenu-item--disabled.v-contextmenu-item--hover{
    color: #ccc;
    cursor: not-allowed;
    background: transparent !important;
  }

  .context-menu li {
    cursor: pointer;
    font-size: 12px;
    height: 28px;
    line-height: 28px;
  }

  .context-menu li:hover {
    background-color: #f5f7fa;
  }
</style>
