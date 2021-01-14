<template>
  <div class="menu">
    <a-input-search style="margin-bottom: 8px" v-model="serach" placeholder="请输入关键字" @change="serachComponment"/>
    <a-menu mode="inline" :style="styleObject" style="overflow-y: auto;overflow-x: hidden;"  :openKeys="openKeys" @openChange="onOpenChange">
      <a-sub-menu v-for="item in treeDatas" :key="item.key">
        <span slot="title">
          <i class="iconfont" :class="item.iconPath"></i>&nbsp;&nbsp;&nbsp;
          <span
            v-if="item.label.indexOf(serach) > -1">{{ item.label.substr(0, item.label.indexOf(serach)) }}<span
            style="color: #f50">{{ serach }}</span>
          {{ item.label.substr(item.label.indexOf(serach) + serach.length) }}
          </span>
          <span v-else>{{ item.label }}</span></span>
          <a-menu-item v-for="child in item.children"
                       :key="child.key"
                       class="getItem"
                       :data-shape="child.shape"
                       :data-type="child.type"
                       :data-size="child.size"
                       draggable
                       @dragstart="handleDragstart"
                       @dragend="handleDragEnd($event,child)">
           <span>
              <i class="iconfont" :class="child.iconPath"></i>&nbsp;&nbsp;&nbsp;
             <span
               v-if="child.label.indexOf(serach) > -1">{{ child.label.substr(0, child.label.indexOf(serach)) }}<span
               style="color: #f50">{{ serach }}</span>
            {{ child.label.substr(child.label.indexOf(serach) + serach.length) }}
            </span>
            <span v-else>{{ child.label }}</span>
             </span>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </div>
</template>

<script>
  import eventBus from "@/components/G6Editor/utils/eventBus";
  import "@/components/G6Editor/components/Base";
  import {getData} from "@/utils/Module";
  import { uniqueId } from '@/components/G6Editor/utils'
  import {clone} from '@/utils/global'
  import Util from '@antv/g6/src/util'

  const getParentKey = (key, tree) => {
    let parentKey
    for (let i = 0; i < tree.length; i++) {
      const node = tree[i]
      if (node.children) {
        if (node.children.some(item => item.key === key)) {
          parentKey = node.key
        } else if (getParentKey(key, node.children)) {
          parentKey = getParentKey(key, node.children)
        }
      } else {
        if (node.label.indexOf(key) > -1) {
          parentKey = node.key
        }
      }
    }
    return parentKey
  }

  export default {
    data() {
      return {
        serach:"",
        openKeys:[],
        treeDatas:getData(),
        offsetX: 0,
        offsetY: 0,
        searchValue: '',
        /*page:null,
        command:null,*/
        styleObject: {
          height : this.height - 131+"px"
        }
      };
    },
    props: {
      page: {
        type: Object,
        default: () => {
        }
      },
      command: {
        type: Object,
        default: () => {
        }
      },
      height: {
        type: Number,
        default: 0
      },
    },
    created(){
      this.bindEvent()
    },
    methods: {
      serachComponment(){
        let serach = this.serach;
        serach = serach.replace(/^\s*|\s*$/g,"");
        if(serach=="" || serach==null){
          this.openKeys=[]
          return
        }
        const dataList = [];
        this.generateList(getData(), dataList)
        const expandedKeys = dataList.map(item => {
          if (item.label.indexOf(serach) > -1) {
            return getParentKey(item.key, getData())
          }
          return null
        }).filter((item, i, self) => item && self.indexOf(item) === i)
        Object.assign(this, {
          openKeys:expandedKeys,
          serach: serach,
        })
      },
      generateList (data, dataList) {
        for (let i = 0; i < data.length; i++) {
          const node = data[i]
          const key = node.key
          dataList.push({ key, label: node.label })
          if (node.children) {
            this.generateList(node.children, dataList)
          }
        }
      },

      handleDragstart(e) {
        this.offsetX = e.offsetX;
        this.offsetY = e.offsetY;
      },
      handleDragEnd(e, item) {
        let data = {};
        Object.assign(data, item);
        data.offsetX = this.offsetX;
        data.offsetY = this.offsetY;
        if (this.page) {
          let taskNodeId = getTaskNodeId();
          const graph = this.page.graph;
          // const size = e.target.dataset.size.split("*");
          const xy = graph.getPointByClient(e.x, e.y);
          data.x = xy.x;
          data.y = xy.y;
          data.size = item.size.split("*");
          data.type = "node";
          data = this.addAuthPoint(data);
          data.id = taskNodeId
          data.componmentLabel=data.label
          data.label = this.createNodeName(data.label,data.label,0)
          //初始化节点公共参数参数 防止报错
          data.tasks ={
            "id":data.id,
            "type":data.nodeType,
            "name":data.label,
            "desc": "",
            "dependence": {},
            "preTasks": [],
            "runFlag": "NORMAL",
            "maxRetryTimes": "0",
            "retryInterval": "1",
            "taskInstancePriority": "MEDIUM",
            "params": {
            },
            "workerGroupId": -1,
            "timeout": {
              "strategy": "",
              "enable": false
            },
          }
          this.command.executeCommand("add", [data]);
          let nodeTemp = graph.findById(taskNodeId)
          //节点默认点击事件
          const autoPaint = graph.get('autoPaint');
          graph.setAutoPaint(false);
          const selectedEdges = graph.findAllByState('edge', 'selected');
          Util.each(selectedEdges, edge => {
            graph.setItemState(edge, 'selected', false);
          });
          const selected = graph.findAllByState('node', 'selected');
          Util.each(selected, node => {
            if (node !== item) {
              graph.setItemState(node, 'selected', false);
            }
          });
          graph.setItemState(nodeTemp, 'selected', true);
          eventBus.$emit('nodeselectchange', { target: nodeTemp, select: true });
          graph.setAutoPaint(autoPaint);
          graph.paint();
        }


      },
      onOpenChange(openKeys){
          this.openKeys = openKeys;
      },
      /*节点名称必须唯一 且必须为下一个节点的preTaskName赋值*/
      createNodeName(NodeName,NodeNameTemp,i){;
        if(!this.findNodeName(NodeNameTemp)){
          ++i;
          NodeNameTemp  = this.createNodeName(NodeName,NodeName+"-"+i,i);
        }
        return NodeNameTemp
      },
      findNodeName(NodeName){
        let nodes = this.page.graph.getNodes();
        for(var i=0;i<nodes.length;i++){
          if(nodes[i].getModel().label==NodeName){
            return false;
          }
        }
        return true
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
      bindEvent() {
        /*debugger
        eventBus.$on("afterAddPage", page => {

          this.page = page;
          this.command = page.command;
        });*/
      }
    }
  };


  /*给节点生成唯一标志*/
  function getTaskNodeId(){
    return "tasks_"+ Math.ceil(Math.random()*100000)
  }

</script>
<style scoped>
  /deep/.ant-menu-inline, .ant-menu-vertical, .ant-menu-vertical-left {
    border-right: 0px solid #e8e8e8;
  }

  .menu{
    padding: 10px 10px 0px 10px;
  }
</style>