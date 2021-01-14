<template>
  <div class="detailpannel" :style="styleObject">
    <div>
      <!--组件参数设置-->
      <div v-if="status=='node-selected'" style="overflow-y: auto;" :style="styleObject" class="pannel">
        <a-tabs type="card" :tabBarGutter="0" size="small">
          <a-tab-pane :forceRender="true" key="1" tab="组件参数" style="padding: 0 10px 0px 10px">
            <module :node="node" ref="moduleParams" :graph="graph" :key="menuKey" :module="module"></module>
            <div style="height: 41px"></div>
          </a-tab-pane>
          <a-tab-pane :forceRender="true" key="2" tab="系统参数" style="padding: 0 10px 0px 10px">
            <system-param :node="node" :graph="graph" ref="systemParams" :key="menuKey"></system-param>
            <div style="height: 41px"></div>
          </a-tab-pane>
        </a-tabs>
        <!--组件说明-->
        <a-row class="help">
          <a-col :span="24">
            <div v-show="showHelp" style="background-color: white;margin: -10px  -15px  0px  -15px;">
              <p v-if="node.desc" v-html=" node.desc">
              </p>
              <p v-if="!node.desc">
                暂无组件说明！
              </p>
            </div>
          </a-col>
          <a-col :span="20">
            {{ node.componmentLabel?node.componmentLabel:node.label}}
          </a-col>
          <a-col :span="4" style="text-align: right">
            <a-icon type="up" v-show="!showHelp" @click="() => {this.showHelp=!this.showHelp}"
                    :style="{ fontSize: '16px', color: '#1890FF' }"/>
            <a-icon type="down" v-show="showHelp" @click="() => {this.showHelp=!this.showHelp}"
                    :style="{ fontSize: '16px', color: '#1890FF' }"/>
          </a-col>
        </a-row>
      </div>
      <!--模型参数设置-->
      <div v-show="status==='canvas-selected'" style="overflow-y: auto;" :style="styleObject" class="pannel">
        <div class="pannel-title">模型参数</div>
        <a-form-model ref="ruleForm" :rules="rules" layout="vertical" class="ant-advanced-search-form" :model="module"
                      style="padding: 10px">
          <a-form-model-item label="模型名称:" prop="name">
            <a-input v-model="module.name"/>
          </a-form-model-item>
          <a-form-item label="模型描述:">
            <a-input v-model="module.desc" type="textarea"/>
          </a-form-item>
          <a-form-model-item label="模型组:" prop="workGroup">
            <a-select placeholder="请选择所属工作组" v-model="module.workGroup">
              <a-select-option v-for="item in AllGroup" :key="item.id" :value="item.name">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-divider orientation="left">
            运行参数信息
          </a-divider>
          <work-param ref="workParams" :definitionId="module.id" :module="module"></work-param>
        </a-form-model>
      </div>
    </div>
  </div>
</template>

<script>
  import Util from '@antv/g6/src/util'
  import eventBus from "@/components/G6Editor/utils/eventBus";
  import Grid from "@antv/g6/build/grid";
  import Module from "@/views/model-center/model-factory/process/module";
  import SystemParam from "@/components/G6Editor/components/SystemParam";
  import WorkParam from "@/components/G6Editor/components/WorkParam";
  import {getAllWorkGroup, createProcess, updateProcess} from '@/api/process'
  import {clone} from '@/utils/global'
  import ACol from "ant-design-vue/es/grid/Col";

  export default {
    data() {
      return {
        SaveStatus: false,
        showHelp: false,
        status: "canvas-selected",
        showGrid: false,
        validate: false,
        page: {},
        graph: {},
        item: {},
        node: {},
        grid: null,
        menuKey: 1,
        AllGroup: [],
        styleObject: {
          height: this.height - 40 + "px"
        },
        rules: {
          name: [
            {required: true, message: '模型名称不能为空', trigger: 'blur'},
          ],
          workGroup: [
            {required: true, message: '模型组不能为空', trigger: 'change'},
          ],
        }
      };
    },
    created() {
      this.bindEvent();
      this.initPage();
    },
    props: {
      height: {
        type: Number,
        default: 0
      },
      module: {
        type: Object,
        default: () => {
        }
      },
    },
    components: {
      ACol,
      Module,
      SystemParam,
      WorkParam
    },
    watch: {
      //当节点发生变化时 重新渲染整个页面 防止相同组件之间的参数互相影响
      'node.id': {
        handler(newVal, oldVal) {
          console.info("出发了节点事件："+newVal+"   "+oldVal);
          this.showHelp = false;
          ++this.menuKey
        }
      },
      'module.id':{
        handler(newVal, oldVal) {
          this.status = "canvas-selected"
        }
      }
    },
    methods: {
      getWorkInfo() {
        return this.$refs.workParams.params
      },
      saveTopo(param) {
        this.$emit('changeLoading', true, "流程保存中，请稍后");
        let saveNode1 = this.saveNode();
        if (!saveNode1) {
          this.$message.error('流程节点参数保存失败');
          this.$emit('changeLoading', false, "");
          this.$emit("allowToolbarKey","AllowSaving",false)
          this.$emit("allowToolbarKey","AllowRunning",false)
          return false
        }
        if (!this.verifyParams()) {
          this.$message.error('流程参数校验未通过');
          this.$emit('changeLoading', false, "");
          this.$emit("allowToolbarKey","AllowSaving",false)
          this.$emit("allowToolbarKey","AllowRunning",false)
          return false
        }
        let saveProcess1 = this.saveProcess();
        if (this.module.id != "") {
          this.$refs.workParams.saveWorkInfo();
          updateProcess(saveProcess1).then(res => {
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            if (res.code != 0) {
              this.$message.error("流程更新失败：" + res.msg);
              this.$emit('changeLoading', false, "");
            } else {
              this.$message.success("流程更新成功");
              this.$emit('changeLoading', false, "");
              //this.$emit('changeTree', "workSpace");
              if (param.type == "runTask") {
                this.$emit('runTask',param);
              }
            }
          }).catch(res => {
            this.$message.error("流程更新报错：" + res);
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            this.$emit('changeLoading', false, "");
          })
        } else {
          createProcess(saveProcess1).then(res => {
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            if (res.code != 0) {
              this.$message.error("流程新增失败：" + res.msg);
            } else {
              this.module.id = res.data.id
              this.$refs.workParams.saveWorkInfo();
              this.$message.success("流程新增成功");
              this.$emit('changeLoading', false, "");
             // this.$emit('changeTree', "workSpace");
              if (param.type == "runTask") {
                this.$emit('runTask',param);
              }
            }
          }).catch(res => {
            this.$message.error("流程新增报错：" + res);
            this.$emit("allowToolbarKey","AllowSaving",false)
            this.$emit("allowToolbarKey","AllowRunning",false)
            this.$emit('changeLoading', false, "");
          })
        }
      },
      /*封装流程参数*/
      saveProcess() {
        let reqData = {
          "processDefinitionJson": {
            "workId": "", //流程Id 在页面初始化生成 用于生成组件表名
            "globalParams": [], //流程参数
            "tasks": [], //流程节点参数
            "timeout": 0
          },
          "locations": {},
          "workGroup": "",
          "connects": [],
          "name": "",
          "desc": ""
        }
        reqData.id = this.module.id
        reqData.name = this.module.name
        reqData.desc = this.module.desc
        reqData.workGroup = this.module.workGroup
        let tasks = []
        /*节点信息*/
        let nodes = this.graph.getNodes();
        /*流程信息*/
        let edges = this.graph.getEdges();
        for (var i = 0; i < nodes.length; i++) {
          let model = clone(nodes[i].getModel());
          if (JSON.stringify(model.tasks.timeout.interval) == '{}') {
            model.tasks.timeout.interval = null
          }
          //保存的时候 封装preTasks参数
          let inEdges = nodes[i].getInEdges();
          model.tasks.preTasks = [];
          for (var j = 0; j < inEdges.length; j++) {
            model.tasks.preTasks.push(inEdges[j].getSource().getModel().tasks.name)
          }
          tasks.push(model.tasks)
          model.tasks = {};
          reqData.locations[model.id] = model
        }
        for (var i = 0; i < edges.length; i++) {
          let edge = edges[i].getModel();
          reqData.connects.push(edge)
        }
        reqData.processDefinitionJson.workId = this.module.workId;
        reqData.processDefinitionJson.tasks = tasks;
        return reqData;
      },
      /*初始化页面参数*/
      initPage() {
        getAllWorkGroup().then(res => {
          this.AllGroup = res.data
        })
      },
      /*绑定事件*/
      bindEvent() {
        let self = this;
        //设置eventBus 事件
        eventBus.$on("afterAddPage", page => {
          self.page = page;
          self.graph = self.page.graph;
          /*默认显示网格线*/
          /*this.grid = new Grid();
          this.graph.addPlugin(this.grid);*/
          eventBus.$on("nodeselectchange", item => {
            console.info("调用了节点点击事件");
            if (item.select === true && item.target.getType() === "node") {
              let model = item.target.getModel();
              if (self.node == null || self.node.id == undefined || self.node == null || self.node.id == model.id || self.node.id == "") {
                self.status = "node-selected";
                self.item = item.target;
                self.node = model
                //修复连接锚点后 不自动获取上游节点数据的bug
                ++this.menuKey
                console.info("点击了节点："+self.node.label);
              } else {
                if (this.saveNodeParams()) {
                  self.status = "node-selected";
                  self.item = item.target;
                  self.node = model
                }
              }
            } else {
              if (this.saveNodeParams()) {
                self.status = "canvas-selected";
                self.item = null;
                self.node = {
                  id:"",
                };
                console.info("点击了画布")
              }
            }
          });
        });
      },
      //保存节点数据
      saveNodeParams() {
        if (this.$refs.moduleParams != undefined && this.$refs.systemParams != undefined) {
          let moduleParams = this.$refs.moduleParams.getChildParams();
          let systemParams = this.$refs.systemParams.getChildParams();
          let assign = Object.assign({}, moduleParams, systemParams);
          //合并两个对象的属性 封装任务节点参数
          var label=""
          if(assign.name.length>10){
            label =  assign.name.substring(0,10)+"..."
          }else{
            label =  assign.name
          }
          let model = {
            label: label,
            tasks: assign
          }
          this.graph.update(this.item, model);
        }
        return true;
      },
      /*保存时候需要对参数进行页面保存后在进行后台请求 所以先模拟点击画布出发参数保护事件 并进行模型参数的校验*/
      onCanvasClick() {

        const graph = this.graph;
        const autoPaint = graph.get('autoPaint');
        graph.setAutoPaint(false);
        const selected = graph.findAllByState('node', 'selected');
        Util.each(selected, node => {
          graph.setItemState(node, 'selected', false);
          eventBus.$emit('nodeselectchange', {target: node, select: false});
        });

        const selectedEdges = graph.findAllByState('edge', 'selected');
        Util.each(selectedEdges, edge => {
          graph.setItemState(edge, 'selected', false);
          eventBus.$emit('nodeselectchange', {target: edge, select: false});
        })
        graph.paint();
        graph.setAutoPaint(autoPaint);
      },
      /*在流程保存之前 先进行节点参数的保存*/
      saveNode() {
        if (this.status == "node-selected") {
          if (this.saveNodeParams()) {
            this.onCanvasClick()
            this.status = "canvas-selected";
            this.item = null;
            this.node = null;
            return true
          } else {
            return false
          }
        } else {
          return true
        }
      },
      /*校验模型参数*/
      verifyParams() {
        let res = false;
        this.$refs.ruleForm.validate(valid => {
          if (valid) {
            res = true
          } else {
            res = false
          }
        });
        return res
      }
    }
  };
</script>

<style scoped>
  /deep/ .ant-form label {
    font-size: 12px;
  }

  .detailpannel {
    border-bottom: 1px solid #e8e8e8;
    position: relative;
    right: 0px;
    z-index: 2;
    background: #f7f9fb;
    border-left: 1px solid #e6e9ed;
  }

  .detailpannel .block-container {
    padding: 16px 8px;
  }

  .block-container .el-col {
    height: 28px;
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }

  .pannel-title {
    height: 40px;
    border-top: 1px solid #dce3e8;
    border-bottom: 1px solid #dce3e8;
    background: #ebeef2;
    color: #000;
    line-height: 40px;
    padding-left: 12px;
    font-size: 14px !important;

  }

  .help {
    z-index: 999;
    width: 100%;
    position: absolute;
    bottom: 5px;
    border: 1px solid #d9d9d9;
    border-radius: 1px;
    padding: 10px 15px 10px 15px;
    background-color: #EFF7FF;
  }

  /deep/ svg {
    font-size: 16px
  }
</style>
