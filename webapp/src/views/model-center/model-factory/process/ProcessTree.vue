<template>
  <div class="menu">
    <a-input-search style="margin-bottom: 8px" placeholder="请输入关键字" @change="onChange"/>
    <!--工作组右键菜单-->
    <v-contextmenu ref="workGroupContextmenu" style="width: 150px">
      <v-contextmenu-item @click="addProcess">
        <a-icon type="plus-square" style="font-size: larger;color: #00CDEA"/>&nbsp;创建模型
      </v-contextmenu-item>
      <!--@click="renameWork"-->
      <v-contextmenu-item @click="renameWork">
        <a-icon type="edit" style="font-size: larger;color: #00CDEA"/>&nbsp;修改组名
      </v-contextmenu-item>
      <v-contextmenu-item @click="deleteWork">
        <a-icon type="delete" style="font-size: larger;color: #00CDEA"/> 删除
      </v-contextmenu-item>
    </v-contextmenu>
    <!--任务右键菜单-->
    <v-contextmenu ref="workContextmenu" style="width: 150px">
      <v-contextmenu-item @click="shareProcess">
        <a-icon type="share-alt" style="font-size: larger;color: #00CDEA"/>&nbsp;共享
      </v-contextmenu-item>
      <v-contextmenu-item @click="updateProcess">
        <a-icon type="edit" style="font-size: larger;color: #00CDEA"/>&nbsp;修改
      </v-contextmenu-item>
      <v-contextmenu-item @click="recycleProcess">
        <a-icon type="delete" style="font-size: larger;color: #00CDEA"/>&nbsp;删除
      </v-contextmenu-item>
    </v-contextmenu>
    <!--我的工作区右键菜单-->
    <v-contextmenu ref="workSpaceContextmenu" style="width: 150px">
      <v-contextmenu-item @click="() => { project={};showProject=true }">
        <a-icon type="plus-square" style="font-size: larger;color: #00CDEA"/>&nbsp;新增模型组
      </v-contextmenu-item>
    </v-contextmenu>
    <!--我的回收站任务右键菜单-->
    <v-contextmenu ref="workRecycleContextmenu" style="width: 150px">
      <v-contextmenu-item @click="recoverProcess">
        <a-icon type="reload" style="font-size: larger;color: #00CDEA"/>&nbsp;恢复
      </v-contextmenu-item>
      <v-contextmenu-item @click="deleteProcess">
        <a-icon type="delete" style="font-size: larger;color: #00CDEA"/>&nbsp;删除
      </v-contextmenu-item>
    </v-contextmenu>
    <!--我的共享区任务右键菜单（当前用户的）-->
    <v-contextmenu ref="workSharedContextmenu" style="width: 150px">
      <v-contextmenu-item @click="cancelShareProcess">
        <a-icon type="share-alt" style="font-size: larger;color: #00CDEA"/>&nbsp;取消分享
      </v-contextmenu-item>
    </v-contextmenu>
    <!--我的共享区任务右键菜单(不是当前用户的)-->
    <v-contextmenu ref="workSharedContextmenu1" style="width: 150px">
      <v-contextmenu-item @click="saveAs">
        <a-icon type="share-alt" style="font-size: larger;color: #00CDEA"/>&nbsp;另存为
      </v-contextmenu-item>
    </v-contextmenu>
    <a-tree
      :tree-data="workDatas"
      show-icon
      @select="loadTopo"
      @expand="expend"
      @rightClick="onRightClick"
      :load-data="onLoadData"
      :loadedKeys="expandedKeys"
      :style="styleObject"
      style="overflow-y: auto;overflow-x: hidden;"
      :expanded-keys="expandedKeys"
      :autoExpandParent="autoExpandParent"
    >
      <template slot="custom" slot-scope="item">
        <a-icon :type="item.icons" style="font-size: larger;color: #00CDEA"/>
        <a-icon
          v-if="item.type != 'work' && item.open"
          :type="item.iconsOpen"
          theme="filled"
          style="font-size: larger;color: #00CDEA"/>
        <a-icon
          v-if="item.type !='work' && !item.open"
          :type="item.iconsClose"
          theme="filled"
          style="font-size: larger;color: #00CDEA"/>
        <span
          v-if="item.title.indexOf(searchValue) > -1">{{ item.title.substr(0, item.title.indexOf(searchValue)) }}<span
          style="color: #f50">{{ searchValue }}</span>
          <ellipsis :length="18" :tooltip="true">{{ item.title.substr(item.title.indexOf(searchValue) + searchValue.length) }}
          </ellipsis>
        </span>
        <span v-else>
          <ellipsis :length="18" :tooltip="true">{{ item.title }}</ellipsis>
        </span>
      </template>
    </a-tree>
    <!--模型组操作子组件-->
    <edit-project
      v-if="showProject"
      :project="project"
      @hideProject="hideProject"></edit-project>
    <!--模型操作子组件-->
    <process-edit
      v-if="showProcess"
      :process="process"
      @childLoadTopo="childLoadTopo"
      @hideProcess="hideProcess"></process-edit>
    <!--共享区保存子组件-->
    <process-share
      v-if="showShare"
      :process="process"
      @hideShare="hideShare"></process-share>
    <Confirm title="确认删除" @confirm="deleteWork1" content="确认删除该模型组吗？" ref="workConfirm" :isShow="false"></Confirm>
    <Confirm title="确认删除" @confirm="deleteProcess1" content="确认删除该模型吗？" ref="processConfirm" :isShow="false"></Confirm>
    <Confirm title="确认回收" @confirm="recycleProcess1" content="确认回收该模型吗？" ref="recycleConfirm" :isShow="false"></Confirm>

  </div>

</template>

<script>
  import eventBus from '@/components/G6Editor/utils/eventBus'
  import { getAllWorkGroup, getAllInstance, loadTopo, deleteProject,
    deleteProcess, releaseProcess, getAllFlagInstance, sharedProcess } from '@/api/process'
  import editProject from '@/views/model-center/model-factory/process/ProjectEdit'
  import processEdit from '@/views/model-center/model-factory/process/ProcessEdit'
  import processShare from '@/views/model-center/model-factory/process/ProcessShare'
  import Confirm from '@/components/Notification/Confirm'
  import _ from 'lodash'
  import { mapGetters } from 'vuex'
  import Ellipsis from '@/components/Ellipsis'
  export const workData = [
    {
      title: '我的工作区',
      key: '0',
      type: 'workSpace',
      open: false,
      iconsOpen: 'folder-open',
      iconsClose: 'folder',
      scopedSlots: { title: 'custom' },
      selectable: false,
      children: []
    },
    {
      title: '我的回收站',
      key: '1',
      type: 'recycle',
      open: false,
      iconsOpen: 'folder-open',
      iconsClose: 'folder',
      scopedSlots: { title: 'custom' },
      selectable: false,
      children: undefined
    },
    {
      title: '共享区',
      key: '2',
      type: 'shared',
      open: false,
      iconsOpen: 'folder-open',
      iconsClose: 'folder',
      scopedSlots: { title: 'custom' },
      selectable: false,
      children: undefined
    }
  ]
  // const getParentKey = (key, tree) => {
  //   let parentKey
  //   for (let i = 0; i < tree.length; i++) {
  //     const node = tree[i]
  //     if (node.children) {
  //       if (node.children.some(item => item.key === key)) {
  //         parentKey = node.key
  //       } else if (getParentKey(key, node.children)) {
  //         parentKey = getParentKey(key, node.children)
  //       }
  //     } else {
  //       if (node.title.indexOf(key) > -1) {
  //         parentKey = node.key
  //       }
  //     }
  //   }
  //   return parentKey
  // }

  export default {
    data () {
      return {
        showProcess: false,
        process: {},
        project: {},
        processEvent: {},
        showProject: false,
        workDatas: workData,
        page: null,
        autoExpandParent: false,
        command: null,
        offsetX: 0,
        offsetY: 0,
        list: [],
        node: null,
        expandedKeys: ['0'],
        searchValue: '',
        styleObject: {
          height: this.height - 141 + 'px'
        },
        event: {},
        treeNode: {},
        showShare: false
      }
    },
    components: {
      editProject,
      processEdit,
      processShare,
      Confirm,
      Ellipsis
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
      }
    },
    created () {
      this.bindEvent()
      this.initPage()
    },
    computed: {
      ...mapGetters(['nickname', 'userId'])
    },
    methods: {
      /* 操作编辑模型组方法 */
      hideProject (type) {
        this.showProject = false
        if (type === 1) {
          const key = ['0', this.project.key]
          this.initPage(key)
        }
      },
      /* 操作编辑模型方法 */
      hideProcess (type, id) {
        this.showProcess = false
        if (type === 1) {
          // this.expandedKeys.push(id)
          this.onLoadData(this.event, id)
        }
      },
      /* 初始化页面 */
      initPage (keys) {
        getAllWorkGroup().then(res => {
          this.AllGroup = res.data
          // 初始化我的工作区
          this.initWorkSpace(keys)
          // 初始化我的回收站
          // this.initRecycle()
          // 初始化共享区
          // this.initShared()
        })
      },
      /* 获取所有工作组 */
      getWorkGroup () {
        var children = []
        for (var i = 0; i < this.AllGroup.length; i++) {
          var child = {
            title: this.AllGroup[i].name,
            key: this.AllGroup[i].id,
            type: 'workGroup',
            open: false,
            iconsOpen: 'folder-open',
            iconsClose: 'folder',
            scopedSlots: { title: 'custom' },
            selectable: false,
            projectId: this.AllGroup[i].id,
            projectName: this.AllGroup[i].name,
            desc: this.AllGroup[i].desc,
            isLeaf: false
          }
          children.push(child)
        }
        return children
      },
      // 初始化工作区
      initWork (data, parentName, parentId) {
        const children = []
        for (let i = 0; i < data.length; i++) {
          const child = {
            title: data[i].name,
            id: data[i].id,
            key: data[i].id,
            type: 'work',
            open: false,
            icons: 'apartment',
            isLeaf: true,
            parentName: parentName,
            scopedSlots: { title: 'custom' },
            projectName: parentName,
            parentId: parentId,
            name: data[i].name,
            desc: data[i].desc
          }
          children.push(child)
        }
        return children
      },
      // 初始化回收站
      getRecycleData (data, parentName) {
        const children = []
        for (let i = 0; i < data.length; i++) {
          const child = {
            title: data[i].name,
            id: data[i].id,
            key: data[i].id,
            type: 'recycle',
            open: false,
            icons: 'apartment',
            isLeaf: true,
            parentName: data[i].projectName,
            scopedSlots: { title: 'custom' },
            projectName: data[i].projectName,
            name: data[i].name,
            desc: data[i].desc
          }
          children.push(child)
        }
        return children
      },
      // 获取共享区数据
      getSharedData (data, parentName) {
        const children = []
        for (let i = 0; i < data.length; i++) {
          const child = {
            title: data[i].name,
            id: data[i].id,
            key: data[i].id,
            type: 'shared',
            open: false,
            icons: this.userId === data[i].userId ? 'heart' : 'apartment',
            isLeaf: true,
            parentName: data[i].projectName,
            scopedSlots: { title: 'custom' },
            projectName: data[i].projectName,
            name: data[i].name,
            desc: data[i].desc
          }
          children.push(child)
        }
        return children
      },
      // 树的展开事件
      onLoadData (treeNode, id) {
        const self = this
        return new Promise(resolve => {
          // 如果文件夹关闭 则不加载
          if (!treeNode.dataRef.open) {
            resolve()
            return
          }
          // 加载任务
          if (treeNode.dataRef.type === 'workGroup') {
            getAllInstance({ 'projectId': treeNode.dataRef.projectId, state: 1 }).then(res => {
              treeNode.dataRef.children = self.initWork(res.data, treeNode.dataRef.title, treeNode.dataRef.projectId)
              treeNode.dataRef.open = true
              self.workDatas = [...self.workDatas]
              if (id) {
                this.loadTopo(id, _.find(self.event.dataRef.children, { id: id }))
              }
              resolve()
            })
          } else if (treeNode.dataRef.type === 'recycle') {
            getAllInstance({ 'projectId': 0, state: 0 }).then(res => {
              self.workDatas[1].children = self.getRecycleData(res.data, treeNode.dataRef.title)
              self.workDatas = [...self.workDatas]
              resolve()
            })
          } else if (treeNode.dataRef.type === 'shared') {
            getAllFlagInstance().then(res => {
              self.workDatas[2].children = self.getSharedData(res.data, treeNode.dataRef.title)
              self.workDatas = [...self.workDatas]
              resolve()
            })
          }
        })
      },
      // 搜索事件
      onChange (e) {
        let value = e.target.value
        value = value.replace(/^\s*|\s*$/g, '')
        const dataList = []
        this.generateList(this.workDatas[0].children, dataList)
        // const expandedKeys = dataList.map(item => {
        //     if (item.title.indexOf(value) > -1) {
        //       return getParentKey(item.key, this.workDatas[0].children)
        //     }
        //     return null
        //   }).filter((item, i, self) => item && self.indexOf(item) === i)
        const expandedKeys = this.expandedKeys
          console.log(expandedKeys)
          Object.assign(this, {
          expandedKeys,
          searchValue: value,
          autoExpandParent: true
        })
      },
      generateList (data, dataList) {
        for (let i = 0; i < data.length; i++) {
          const node = data[i]
          const key = node.title
          dataList.push({ key, title: key })
          if (node.children) {
            this.generateList(node.children, dataList)
          }
        }
      },
      /* 更新模型 */
      updateProcess (vm, event) {
        this.process = this.node
        this.showProcess = true
      },
      /* 更新模型 */
      editTaskName (vm, event) {
        const node = this.node
        if (node.type === 'work') {
          this.$message.info('重命名的是work节点为' + node.title + ' key为：' + node.key)
        } else {
          this.$message.info('重命名的是task节点为' + node.label + ' key为：' + node.key)
        }
      },
      /* 加入回收站模型 */
      recycleProcess (vm, event) {
        this.$refs.recycleConfirm.handleClick()
      },
      recycleProcess1 () {
        const _that = this
        releaseProcess({ 'releaseState': 0, 'processId': _that.node.id, 'workGroup': _that.node.projectName }).then((res) => {
          if (res.code === 0) {
            _that.$message.success('加入我的回收站成功')
            _that.onLoadData(_that.event)
            // 清空画布
            _that.$emit('resetTopo', _that.node.id)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('加入我的回收站失败！')
        })
      },
      // 恢复
      recoverProcess (vm, event) {
        const _that = this
        releaseProcess({ 'releaseState': 1, 'processId': _that.node.id, 'workGroup': _that.node.projectName }).then((res) => {
          if (res.code === 0) {
            _that.$message.success('恢复成功')
            _that.onLoadData(_that.event)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('恢复失败！')
        })
      },
      /* 删除模型 */
      deleteProcess (vm, event) {
        this.$refs.processConfirm.handleClick()
      },
      /* 删除模型 */
      deleteProcess1 () {
        const _that = this
        deleteProcess({ 'workGroup': _that.node.projectName, 'processDefinitionId': _that.node.id }).then((res) => {
          if (res.code === 0) {
            _that.$message.success('删除模型成功')
            _that.onLoadData(_that.event)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('删除模型失败！')
        })
      },
      /* 新增模型 */
      addProcess (vm, event) {
        this.process = { 'projectName': this.node.projectName }
        this.showProcess = true
      },
      // 修改模型组信息
      renameWork (vm, event) {
        this.project = this.node
        this.showProject = true
      },
      // 删除模型组
      deleteWork (vm, event) {
        this.$refs.workConfirm.handleClick()
      },
      deleteWork1 () {
        const _that = this
        deleteProject({ 'projectId': _that.node.projectId }).then((res) => {
          if (res.code === 0) {
            _that.$message.success('删除模型组成功')
            _that.initPage()
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('删除模型组失败！')
        })
      },
      // 共享模型
      shareProcess () {
        const _that = this
        this.$confirm({
          title: '提示',
          content: '您确定共享这个模型吗？',
          onOk () {
            sharedProcess({ 'flag': 0, 'processId': _that.node.id, 'workGroup': _that.node.projectName }).then((res) => {
              if (res.code === 0) {
                _that.$message.success('共享模型成功')
                _that.onLoadData(_that.event)
              } else {
                _that.$message.error(res.msg)
              }
            }).catch(err => {
              console.log(err)
              _that.$message.error('共享模型失败！')
            })
          },
          onCancel () {
          }
        })
      },
      // 取消共享模型
      cancelShareProcess () {
        const _that = this
        this.$confirm({
          title: '提示',
          content: '您确定取消共享这个模型吗？',
          onOk () {
            sharedProcess({ 'flag': 1, 'processId': _that.node.id, 'workGroup': _that.node.projectName }).then((res) => {
              if (res.code === 0) {
                _that.$message.success('取消共享模型成功')
                _that.onLoadData(_that.event)
              } else {
                _that.$message.error(res.msg)
              }
            }).catch(err => {
              console.log(err)
              _that.$message.error('取消共享模型失败！')
            })
          },
          onCancel () {
          }
        })
      },
      // 另存为
      saveAs () {
        this.process = this.node
        this.showShare = true
      },
      /* 操作编辑模型方法 */
      hideShare (id, name) {
        this.showShare = false
        if (id) {
          const event = _.cloneDeep(this.event)
          event.eventKey = id
          event.expanded = true
          event.dataRef = {
            title: name,
            key: id,
            type: 'workGroup',
            open: true,
            iconsOpen: 'folder-open',
            iconsClose: 'folder',
            scopedSlots: { title: 'custom' },
            selectable: false,
            projectId: id,
            projectName: name,
            desc: ''
          }
          this.onLoadData(event)
          // this.expend(this.expandedKeys, this.treeNode)
        }
      },
      childLoadTopo () {
        let id = ''
        let groupId = ''
        const isOperate = false
        /* //从树中获取topo信息 */
        if (this.node) {
          id = this.node.id
          groupId = this.node.parentName
          loadTopo({ 'processId': id, 'workGroup': groupId }).then(res => {
            this.$emit('loadTopo', res.data, isOperate)
          })
        }
      },
      /* tree右击事件 */
      onRightClick ({ event, node }) {
        this.node = node._props.dataRef
        if (this.node.type === 'work') {
          this.event = node.$parent._props
        } else {
          this.event = node._props
          this.event.dataRef.open = true
          this.treeNode = node
        }

        /* 先清除之前的弹窗 */
        this.NodeTreeItem = null
        const answer = node._props.dataRef.anwer
        if (answer) {
          this.$refs.contextmenu.hide()
          this.answer = answer
          return
        }
        this.NodeTreeItem = {
          id: node._props.eventKey,
          title: node._props.title,
          parentId: node._props.dataRef.parentId || null
        }
        const x = event.x
        // 因为我放在页面上的box有滚动条，所以需要减掉该盒子的scrollTop
        // const y = event.currentTarget.offsetTop - document.getElementById('left').scrollTop;
        const y = event.y
        const postition = {
          top: y - 10,
          left: x + 30
        }
        if (node._props.dataRef.type === 'work') { // 任务右键事件
          this.$refs.workContextmenu.show(postition)
        }
        if (node._props.dataRef.type === 'workGroup') { // 工作组右击事件
          this.$refs.workGroupContextmenu.show(postition)
        }
        if (node._props.dataRef.type === 'workSpace') { // 我的工作区右击事件
          this.$refs.workSpaceContextmenu.show(postition)
        }
        if (node._props.dataRef.type === 'recycle' && node._props.dataRef.key !== '1') { // 回收站事件
          this.$refs.workRecycleContextmenu.show(postition)
        }
        if (node._props.dataRef.type === 'shared' && node._props.dataRef.key !== '2' && node._props.dataRef.icons === 'heart') { // 共享区右击事件
          this.$refs.workSharedContextmenu.show(postition)
        }
        if (node._props.dataRef.type === 'shared' && node._props.dataRef.key !== '2' && node._props.dataRef.icons !== 'heart') { // 共享区右击事件
          this.$refs.workSharedContextmenu1.show(postition)
        }
      },
      // 用于点击空白处隐藏增删改菜单
      clearMenu () {
        this.NodeTreeItem = null
      },
      /* 左侧树点击效果切换事件 */
      expend (keys, event) {
        // const _this = this
        this.expandedKeys = keys
        this.autoExpandParent = false
        if (event.node) {
          event.node.dataRef.open = event.expanded
        } else {
          this.onLoadData(event)
        }
      },
      /* 加载topo图 */
      loadTopo (keys, event) {
        let id = ''
        let groupId = ''
        let isOperate = true
        /* //从树中获取topo信息 */
        if (event.node) {
          id = event.node.dataRef.id
          groupId = event.node.dataRef.parentName
        } else {
          id = event.id
          groupId = event.parentName
        }
        if (event.node.dataRef.type === 'shared' && event.node.dataRef.icons !== 'heart') {
           isOperate = true
        } else {
          isOperate = false
        }
        loadTopo({ 'processId': id, 'workGroup': groupId }).then(res => {
          this.$emit('loadTopo', res.data, isOperate)
        })
      },
      /* 加载树 */
      loadTree () {
        this.initPage()
      },
      /* 加载eventBus数据 */
      bindEvent () {
        const _that = this
        eventBus.$on('afterAddPage', page => {
          _that.page = page
          _that.command = page.command
          _that.loadPage()
        })
      },
      /* 更新topo视图 */
      loadPage () {
        this.$emit('loadPage', this.page)
      },
      /* 初始化我的工作区 */
      initWorkSpace (keys) {
        const workGroup = this.getWorkGroup()
        this.workDatas[0].children = workGroup
        if (keys) {
          this.expend(keys, this.treeNode)
        }
      }
    }
  }
</script>

<style scoped>

  .menu {

    padding: 10px 10px 10px 12px;
  }

  .ant-tree-iconEle .ant-tree-icon__customize {
    display: none
  }

  .itempannel .pannel-type-icon {
    width: 16px;
    height: 16px;
    display: inline-block;
    vertical-align: middle;
    margin-right: 8px;
  }
</style>
