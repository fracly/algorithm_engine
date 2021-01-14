<template>
  <div id="mountNode" :style="{width:width}" style="">
    <div class="editor">
      <context-menu/>
      <a-spin size="large" :spinning="loading" :tip="tip">

        <div class="bottom-container">
          <a-row>
            <a-col :span="4">
              <!--左侧菜单栏-->
              <item-panel :height="height"
                          ref="panelChild"
                          @loadTopo="loadTopo"
                          @resetTopo="resetTopo"
                          :module="module"/>
            </a-col>
            <a-col :span="15">
              <!--工具栏-->
              <toolbar :width="width"
                       :editor="editor"
                       :command="command"
                       :AllowStop="AllowStop"
                       :AllowSaving="AllowSaving"
                       :AllowRunning="AllowRunning"
                       :readOnly="readOnly"
                       @initTreeData="initTreeData"
                       @saveTopo="saveTopo"
                       @stopTask="stopTask"
                       ref="toolbarChild"
              />
              <!--画布页-->
              <page :height="height"
                    :width="width"
                    :data="data"
                    ref="pageChild"
                    @initInstanceId="initInstanceId"
                    @changeLoading="changeLoading"
                    @saveTopo="saveTopo"
                    @setLog="setLog"
                    @allowToolbarKey="allowToolbarKey"
                    :editor="editor"
                    :command="command"
                    :module="module"
                    :AllowRunning="AllowRunning"
                    :readOnly="readOnly"
                    :processInstanceId="processInstanceId"
              />
            </a-col>
            <a-col :span="5">
              <!--组件参数编辑栏-->
              <detail-panel :height="height"
                            ref="paramChild"
                            :editor="editor"
                            :command="command"
                            :module="module"
                            @changeLoading="changeLoading"
                            @changeTree="changeTree"
                            @runTask="runTask"
                            @allowToolbarKey="allowToolbarKey"
              />
              <!--miniMap-->
              <!-- <minimap :editor="editor" :command="command"/>-->
            </a-col>
          </a-row>
        </div>
      </a-spin>
    </div>
    <Flow/>
  </div>
</template>

<script>
  import Toolbar from "@/components/G6Editor/components/Toolbar";
  import ItemPanel from "@/components/G6Editor/components/ItemPanel";
  import DetailPanel from "@/components/G6Editor/components/DetailPanel";
  import Minimap from "@/components/G6Editor/components/Minimap";
  import Page from "@/components/G6Editor/components/Page";
  import Flow from "@/components/G6Editor/components/Flow"
  import ContextMenu from "@/components/G6Editor/components/ContextMenu";
  import Editor from "@/components/G6Editor/components/Base/Editor";
  import command from "@/components/G6Editor/command";
  import {RandomNumber} from "@/utils/global";

  export default {
    name: "G6Editor",
    components: {
      Toolbar,
      ItemPanel,
      DetailPanel,
      Minimap,
      Page,
      ContextMenu,
      Flow
    },
    props: {
      height: {
        type: Number,
        default: document.documentElement.clientHeight-10
      },
      width: {
        type: Number,
        default: document.documentElement.clientWidth
      },
      data: {
        type: Object,
        default: () => {
          return {
            nodes: [],
            edges: [],
          }
        }
      }
    },
    created() {
      this.init();
    },
    data() {
      return {
        readOnly:false,
        AllowStop:false,
        AllowSaving:false,
        AllowRunning:false,
        tip: "",
        loading: false,
        editor: {},
        command: null,
        module: {
          id: 0,
          workId: "",
          name: "",
          desc: "",
          workGroup: "",
          workGroupId: "",
          failureStrategy: "",
        },
        processInstanceId: null,
      };
    },
    methods: {
      /*清空画布*/
      resetTopo(processId){
        if(processId == this.module.id){
          this.module= {
            id: 0,
            workId: RandomNumber("work", 6),
            name: "",
            desc: "",
            workGroup: "",
            workGroupId: "",
            failureStrategy: "",
          }
          this.data.nodes=[]
          this.data.edges=[]
        }
      },
      /*停止任务*/
      stopTask() {
        this.changeLoading(true, "任务停止中，请稍后");
        this.$refs.pageChild.stopTask();
      },
      /*展示日志*/
      setLog(log) {
        this.$refs.toolbarChild.setLog(log)
      },
      /*任务运行实例Id*/
      initInstanceId(id) {
        this.processInstanceId = id;
      },
      /*流程图保存方法*/
      saveTopo(params) {
        /*此时需要清空*/
        if (params.type == "runTask") {
          //启动任务中不允许重复运行和保存
          this.allowToolbarKey("AllowSaving",true);
          this.allowToolbarKey("AllowRunning",true)
          this.$refs.toolbarChild.cleanLog()
        }
        this.$refs.paramChild.saveTopo(params);
      },
      changeTree(workType) {
        this.$refs.panelChild.changeTree(workType);
      },
      /*画布全局loading事件*/
      changeLoading(state, tip) {
        this.tip = tip
        this.loading = state
      },
      loadTopo(data,readOnly) {
        this.readOnly = readOnly
        this.$refs.toolbarChild.cleanLog()
        //处理从树中加载的参数信息
        let edges = JSON.parse(data.connects)
        let process = JSON.parse(data.processDefinitionJson);
        let location = JSON.parse(data.locations);
        let nodes = []
        process.tasks.forEach(function (value, key) {
          let locationElement = location[value.id];
          locationElement.tasks = value;
          nodes.push(locationElement)
        });
        /*初始化流程图信息和流程参数*/
        this.module.id = data.id
        if("workId" in process){
          this.module.workId = process.workId
        }else{
          this.module.workId = RandomNumber("work", 6)
        }
        this.module.workGroup = data.projectName
        this.module.workGroupId = data.projectId
        this.module.name = data.name
        this.module.desc = data.desc
        this.data.nodes = nodes;
        this.data.edges = edges;
      },
      init() {
        this.editor = new Editor();
        this.command = new command(this.editor);
        this.module.workId = RandomNumber("work", 6)
      },
      initTreeData() {
        this.$refs.panelChild.loadTree();
      },
      /*操作工具栏的按钮 入参分别是按钮的key 和状态*/
      allowToolbarKey(key, statu) {
        this[key] = statu;
      },
      runTask(param) {
        this.$refs.pageChild.runTask(this.$refs.paramChild.getWorkInfo(),param);
      }
    }
  };
</script>

<style scoped>


  /deep/ .ant-tabs-tab {
    font-size: 14px !important;
  }

  /deep/ .ant-input {
    font-size: 12px;
  }

  text[font-size~="16"] {
    font-size: 14px !important;;
  }

  svg {
    font-size: 16px !important;
  }

  .editor {
    width: 100%;
    user-select: none;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
  }

  .bottom-container {
    position: relative;
  }

  .ant-layout-content {
    margin: 24px 10px 10px !important;;
    padding-top: 0px;
  }
</style>
