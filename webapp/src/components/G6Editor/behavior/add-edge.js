import eventBus from "@/components/G6Editor/utils/eventBus";
import {uniqueId} from '@/components/G6Editor/utils'
import Util from "@antv/g6/src/util";

let startPoint = null
let startItem = null
let endPoint = {}
let activeItem = null
let curInPoint = null
let sourcePointIndex = 0;
let endPointIndex = 0;
export default {
  getEvents() {
    return {
      mousemove: 'onMousemove',
      mouseup: 'onMouseup',
      'node:mouseover': 'onMouseover',
      'node:mouseleave': 'onMouseleave',
      'node:click': 'onNodeClick',
      'canvas:click': 'onCanvasClick',
    };
  },
  onMouseup(e) {
    const item = e.item
    if (item && item.getType() === 'node') {
      const group = item.getContainer()
      if (e.target._attrs.isInPoint) {
        const children = group._cfg.children
        children.map(child => {
          if (child._attrs.isInPointOut && child._attrs.parent === e.target._attrs.id) {
            activeItem = child
          }
        })
        curInPoint = e.target
      } else if (e.target._attrs.isInPointOut) {
        activeItem = e.target
        const children = group._cfg.children
        children.map(child => {
          if (child._attrs.isInPoint && child._attrs.id === e.target._attrs.parent) {
            curInPoint = child
          }
        })
      }
      if (activeItem) {
        const endX = parseInt(curInPoint._attrs.x)
        const endY = parseInt(curInPoint._attrs.y)
        endPoint = {x: endX, y: endY};
        if (this.edge) {
          let sourcePointIndex = this.edge.getModel().sourcePointIndex;
          this.graph.removeItem(this.edge);
          //查找输入锚点的下标
          for (var i = 0; i < item.getModel().InAnchorPoints.length; i++) {
            if (item.getModel().InAnchorPoints[i].x == endX && item.getModel().InAnchorPoints[i].y == endY) {
              endPointIndex = item.getModel().InAnchorPoints[i].index
            }
          }
          if(!allowedAnchorPonit(item,curInPoint) || IsSelf(startItem,item)){
              return
          }
          const model = {
            id: 'edge' + uniqueId(),
            source: startItem,
            target: item,
            sourceId: startItem._cfg.id,
            targetId: item._cfg.id,
            endPointSourceId: startItem._cfg.id,
            endPointTargetId: item._cfg.id,
            start: startPoint,
            end: endPoint,
            shape: 'customEdge',
            type: 'edge',
            endPointIndex: endPointIndex,
            sourcePointIndex: sourcePointIndex
          }
          eventBus.$emit('addItem', model)
        }
      } else {
        if (this.edge)
          this.graph.removeItem(this.edge);
      }
    } else {
      if (this.edge)
        this.graph.removeItem(this.edge);
    }
    this.graph.find("node", node => {
      const group = node.get('group')
      const children = group._cfg.children
      children.map(child => {
        if (child._attrs.isInPointOut) {
          child.attr("opacity", "0")
        }
        if (child._attrs.isInPoint) {
          child.attr("opacity", "0")
        }
        if (child._attrs.isOutPoint) {
            child.attr("opacity", "0")
        }
      })
    })
    if (startItem) {
      this.graph.setItemState(startItem, 'hover', false);
    }
    this.graph.paint()
    startPoint = null
    startItem = null
    endPoint = {}
    activeItem = null
    curInPoint = null
    this.graph.setMode('default');
    this.onCanvasClick(item)
  },
  onMousemove(e) {
    const item = e.item
    if (!startPoint) {
      this.graph.find("node", node => {
        const group = node.get('group')
        const children = group._cfg.children
        children.map(child => {
          let allowAnchor = allowedAnchorPonit(node,child);
          let isSelf = IsSelf(item,node);
          if (child._attrs.isInPointOut && allowAnchor && !isSelf) {
            child.attr("opacity", "0.4")
          }
          if (child._attrs.isInPoint   && allowAnchor && !isSelf) {
            child.attr("opacity", "1")
          }
          this.graph.paint()
        })
      })
      const startX = parseInt(e.target._attrs.x)
      const startY = parseInt(e.target._attrs.y)
      startPoint = {x: startX, y: startY};
      startItem = item
      //查找输出锚点的下标
      for (var i = 0; i < startItem.getModel().OutAnchorPoints.length; i++) {
        if (startItem.getModel().OutAnchorPoints[i].x == startX && startItem.getModel().OutAnchorPoints[i].y == startY) {
          sourcePointIndex = startItem.getModel().OutAnchorPoints[i].index
        }
      }
      this.edge = this.graph.addItem('edge', {
        source: item,
        target: item,
        start: startPoint,
        end: startPoint,
        shape: 'link-edge',
        sourcePointIndex: sourcePointIndex,
      });
    } else {
      const point = {x: e.x, y: e.y};
      if (this.edge) {
        // 增加边的过程中，移动时边跟着移动
        this.graph.updateItem(this.edge, {
          //  start: startPoint,
          target: point
        });
      }
    }
  },
  onMouseover(e) {
    const item = e.item
    if (item && item.getType() === 'node') {
      if (e.target._attrs.isInPointOut && !this.hasTran) {
        this.hasTran = true
        e.target.transform([
          ['t', 0, 3],
          ['s', 1.2, 1.2],
        ])
      }
      this.graph.paint()
    }
  },
  onMouseleave() {
    this.graph.find("node", node => {
      const group = node.get('group')
      const children = group._cfg.children
      children.map(child => {
        if (child._attrs.isInPointOut) {
          child.resetMatrix()
        }
      })
    })
    this.hasTran = false
    this.graph.paint()
  },
  onCanvasClick(item) {

    if(this.graph==null || item==null){
      return
    }
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
    this.onNodeClick(item)
  },
  onNodeClick(item) {

    if(this.graph==null || item==null){
      return
    }
    const self = this;
    const graph = self.graph;
    const autoPaint = graph.get('autoPaint');
    graph.setAutoPaint(false);
    graph.setItemState(item, 'selected', true);
    eventBus.$emit('nodeselectchange', { target: item, select: true });
    graph.setAutoPaint(autoPaint);
    graph.paint();
  },
}

/*判断节点锚点是否允许被连接*/
function allowedAnchorPonit (node,circle){
  let edges = node.getInEdges();
  let model = node.getModel();

  if(edges.length==0 || model.nodeType=='SHELL' || model.nodeType=='SQL' || model.nodeType=='SPARK' || model.nodeType=='PYTHON'){ //如果目标节点没有连线或者为通用工具 则输入锚点允许连接
    return true
  }
  //不允许连接的锚点集合
  var endList=[]
  for(var i=0;i<edges.length;i++){
    endList.push(edges[i].getModel().end)
  }
  for(var i=0;i<endList.length;i++){
    if(endList[i].x==circle._attrs.x && endList[i].y==circle._attrs.y){
      return false
    }
  }
  return true;
}

/*判断是否是当前节点*/
function IsSelf(item,node){
  if(item.getModel().id == node.getModel().id){
    return true
  }
  return false
}
