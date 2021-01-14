<style lang="less" scoped>
  @import url("../reportPreview/index.less");
  /deep/.ant-input[disabled]{
   cursor:pointer
}
  /deep/.ant-input-affix-wrapper .ant-input-disabled~.ant-input-suffix .anticon {
    cursor:pointer
  }
</style>
<template>
  <div class="filter-box">
    <a-row>
      <!--:span="(item.paramType=='time'||item.paramType=='tree')?12:12"-->
      <a-col
        v-for="(item,index) in reportParam"
        :span="12"
        class="search-row-unit"
        :key="index"
        style="text-align: left"
        v-if="item.paramType!='merge'"
        >
        <a-row >
          <a-col :span="6" style="text-align: right">
            <!--查询条件名称-->
            <div style="height: 32px;margin-top: 5px">
              <span class="title">{{ item.paramDesc }}：</span>
            </div>
          </a-col>
          <a-col :span="18" style="text-align: left" v-if="item.paramType=='input'">
            <!--输入框-->
            <a-input
              v-model="searchForm[item.paramName]"
              :placeholder="item.paramDesc"
              style="width: 200px"/>
          </a-col>
          <a-col :span="18" style="text-align: left" v-if="item.paramType=='select'" >
            <!--选择框-->
            <a-select style="width: 200px" v-model="searchForm[item.paramName]">
              <a-select-option v-for="(option,index) in item.child" :value="option.dictCode" :key="index">
                {{ option.dictDesc }}
              </a-select-option>
            </a-select>
          </a-col>
          <!--时间控件 单个时间格式-->
          <!--时间粒度选择框 和时间选择框-->
          <a-col :span="18" style="text-align: left" v-if="item.paramType=='time'">
            <!--时间粒度选择框-->
            <a-select
              style="width: 120px"
              v-model="searchForm[item.paramName+'$Grain']"
              @change="timeSelect(item)"
              placeholder="请选择时间粒度">
              <a-select-option v-for="(option,index) in item.child" :value="option.value" :key="index">
                {{ option.label }}
              </a-select-option>
            </a-select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <!--年份时间选择框-->
            <a-select
              style="width: 160px"
              v-model="searchForm[item.paramName][0]"
              v-if="item.timeFormat=='YYYY'"
              placeholder="请选择年份">
              <a-select-option v-for="(option,index) in year" :value="option" :key="index">
                {{ option }}
              </a-select-option>
            </a-select>
            <span v-if="item.timeFormat=='YYYY' && item.type==2">～</span>
            <a-select
              style="width: 160px"
              v-model="searchForm[item.paramName][1]"
              v-if="item.timeFormat=='YYYY' && item.type==2"
              placeholder="请选择结束年份">
              <a-select-option v-for="(option,index) in year" :value="option" :key="index">
                {{ option }}
              </a-select-option>
            </a-select>
            <!--月份时间选择框-->
            <a-month-picker
              style="width: 160px"
              placeholder="请选择月份"
              v-if="(item.timeFormat=='YYYYMM' || item.timeFormat=='YYYY-MM' || item.timeFormat=='YYYY/MM')"
              v-model="searchForm[item.paramName][0]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"/>
            <span
              v-if="(item.timeFormat=='YYYYMM' || item.timeFormat=='YYYY-MM' || item.timeFormat=='YYYY/MM') && item.type==2">～</span>
            <a-month-picker
              style="width: 160px"
              placeholder="请选择结束月份"
              v-if="(item.timeFormat=='YYYYMM' || item.timeFormat=='YYYY-MM' || item.timeFormat=='YYYY/MM') && item.type==2"
              v-model="searchForm[item.paramName][1]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"/>
            <!--天数时间选择框-->
            <a-date-picker
              style="width: 160px"
              placeholder="请选择日"
              v-if="(item.timeFormat=='YYYYMMDD' || item.timeFormat=='YYYY-MM-DD' || item.timeFormat=='YYYY/MM/DD')"
              v-model="searchForm[item.paramName][0]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"/>
            <span
              v-if="(item.timeFormat=='YYYYMMDD' || item.timeFormat=='YYYY-MM-DD' || item.timeFormat=='YYYY/MM/DD') && item.type==2">～</span>
            <a-date-picker
              style="width: 160px"
              placeholder="请选择结束日"
              v-if="(item.timeFormat=='YYYYMMDD' || item.timeFormat=='YYYY-MM-DD' || item.timeFormat=='YYYY/MM/DD') && item.type==2"
              v-model="searchForm[item.paramName][1]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"/>
            <!--时间选择框-->
            <a-date-picker
              :show-time="{ format: item.HourFormat }"
              style="width: 160px"
              v-if="item.timeFormat!='YYYY' && item.timeFormat!='YYYYMM' && item.timeFormat!='YYYY-MM'
                && item.timeFormat!='YYYY/MM' && item.timeFormat!='YYYYMMDD' && item.timeFormat!='YYYY-MM-DD' && item.timeFormat!='YYYY/MM/DD'"
              v-model="searchForm[item.paramName][0]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"
              placeholder="请选择时间"/>
            <span
              v-if="(item.timeFormat!='YYYY' && item.timeFormat!='YYYYMM' && item.timeFormat!='YYYY-MM'
                && item.timeFormat!='YYYY/MM' && item.timeFormat!='YYYYMMDD' && item.timeFormat!='YYYY-MM-DD' && item.timeFormat!='YYYY/MM/DD') && item.type==2">～</span>
            <a-date-picker
              :show-time="{ format: item.HourFormat }"
              style="width: 160px"
              v-if="item.timeFormat!='YYYY' && item.timeFormat!='YYYYMM' && item.timeFormat!='YYYY-MM'
                && item.timeFormat!='YYYY/MM' && item.timeFormat!='YYYYMMDD' && item.timeFormat!='YYYY-MM-DD' && item.timeFormat!='YYYY/MM/DD' && item.type==2"
              v-model="searchForm[item.paramName][1]"
              :valueFormat="item.timeFormat"
              :format="item.timeFormat"
              placeholder="请选择结束时间"/>
          </a-col>
          <!--树形控件-->
          <a-col :span="18" style="text-align: left" v-if="item.paramType=='tree'">
            <a-tree-select
              v-model="searchForm[item.paramName]"
              style="width: 200px;height: 30px"
              :tree-data="item.child"
              tree-checkable
              :showCheckedStrategy="showCheckedStrategy"
              placeholder="请选择"
              :maxTagCount="maxTagCount">
              <a-icon slot="suffixIcon" type="smile"/>
            </a-tree-select>
          </a-col>
          <!--加强型选择框 弹窗形式展开 通过传递参数进行展示-->
          <a-col @click="() => setModalVisible(true,item)" :span="18" style="text-align: left" v-if="item.paramType=='s-select' || item.paramType=='department'">
            <a-input
              style="width: 200px"
              v-model="searchForm[item.paramName+'$Temp']"
              class="illegalInput"
              :disabled="disabled"
              placeholder="请选择">
              <a-icon slot="suffix" type="down"/>
            </a-input>
          </a-col>
          <!--车牌-->
          <a-col :span="18" style="text-align: left" v-if="item.paramType=='carplate'">
            <its-reporthphmInput
              :paramName="item.paramName"
              :parentmessage="searchForm[item.paramName]"
              :glbm="sysInfo.pre_hphm"
              @onChange="chanageHphm"></its-reporthphmInput>
          </a-col>
        </a-row>
      </a-col>
    </a-row>
    <a-row t class="search-row-unit" style="text-align: center">
      <a-col :span="24">
        <a-button class="search" @click="submitQueryForChild" icon="search">查询</a-button>
        <a-button class="printing" @click="resetParam" icon="sync">重置</a-button>
        <a-button class="download" @click="exportExcelForChild"><i class="icon iconfont" style="margin: 1px 5px 0 0;">&#xe628;</i>导出报表</a-button>
      </a-col>
    </a-row>
    <!--公共弹窗部分-->
    <a-modal
      :title="modal.title"
      width="40%"
      centered
      :visible="modal.visible"
      v-if="modal.visible"
      ok-text="确认"
      cancel-text="取消"
      @ok="saveModalVal()"
      @cancel="() => setModalVisible(false,{})">
      <div class="illbox">
        <!-- <h3>{{modal.title}}</h3>-->
        <a-divider></a-divider>
        <div style="height: 300px;overflow:auto;">
          <a-checkbox :indeterminate="indeterminate" :checked="checkedAll" @change="checkAll()">全选
          </a-checkbox>
          <div style="padding: 10px 20px 10px 20px">
            <div v-for="(item,index) in modal.dataSelect" :key="index">
              <a-checkbox
                :indeterminate="checkList[item.key].indeterminate"
                :checked="checkList[item.key].checkAll"
                @change="firstCheckChange(item)">{{ item.label }}
              </a-checkbox>
              <div style="padding: 10px 20px 10px 20px">
                <a-checkbox-group
                  v-model="checkList[item.key].checkList"
                  :options="item.children"
                  @change="onChange(item)"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
  import echarts from 'echarts'
  import 'ant-design-vue/dist/antd.css'
  import moment from 'moment'
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
  import { TreeSelect } from 'ant-design-vue'

  const SHOW_PARENT = TreeSelect.SHOW_PARENT
  const colums = []

  export default {
    name: 'SearFrom',
    props: {
      reportParam: {
        type: Array, // 指定传入的类型
        default: function () {
          return []
        }
      },
      exportExcel: {
        type: Function,
        default: null
      },
      submitQuery: {
        type: Function,
        default: null
      }
    },
    data () {
      return {
        disabled: true,
        showCheckedStrategy: 'SHOW_ALL',
        maxTagCount: 1,
        indeterminate: false,
        checkedAll: false,
        checkList: {},
        checkListTemp: {},
        // 全选按钮参数
        modal: {
          title: '',
          visible: false,
          paramName: '',
          params: {}
        }, // 弹窗参数设置
        searchForm: {}, // 查询参数
        SHOW_PARENT: SHOW_PARENT, // 树插
        status: 0,
        year: [],
        department: []
      }
    },
    methods: {
      // 时间粒度选择回填方法
      timeSelect (item) {
        const _this = this
        const searchFormElement = this.searchForm[item.paramName + '$Grain']
        // 切换后 重置时间参数
        this.$set(this.searchForm, item.paramName, [])
        var child = item.child
        for (var i = 0; i < child.length; i++) {
          if (searchFormElement == child[i].value) {
            item.timeFormat = child[i].dataFormat
            item.type = child[i].type
            item.HourFormat = child[i].HourFormat
          }
        }
      },
      // 车牌选择回填方法
      chanageHphm (result, paramName) {
        this.searchForm[paramName] = result
      },
      // 点击多选框 判断事件
      onChange (res) {
        if (this.checkList[res.key].checkList.length == 0) {
          this.checkList[res.key].indeterminate = false
          this.checkList[res.key].checkAll = false
        }
        if (this.checkList[res.key].checkList.length != 0 && this.checkList[res.key].checkList.length < this.checkList[res.key].checkLength) {
          this.checkList[res.key].indeterminate = true
          this.checkList[res.key].checkAll = false
        }
        if (this.checkList[res.key].checkList.length != 0 && this.checkList[res.key].checkList.length == this.checkList[res.key].checkLength) {
          this.checkList[res.key].indeterminate = false
          this.checkList[res.key].checkAll = true
        }
        if (this.checkList[res.key].checkList.length != 0 && this.checkList[res.key].checkList.length > this.checkList[res.key].checkLength) {
          this.checkList[res.key].indeterminate = false
          this.checkList[res.key].checkAll = false
        }
        this.checkAllBox()// 判断总check状态
      },
      // 判断二级菜单是否已经全选
      firstCheckChange (res) {
        if (this.checkList[res.key].checkAll) {
          this.checkList[res.key].indeterminate = false
          this.checkList[res.key].checkAll = false
          this.checkList[res.key].checkList = []
        } else {
          this.checkList[res.key].indeterminate = false
          this.checkList[res.key].checkAll = true
          this.checkList[res.key].checkList.push(res.value)
          if ('children' in res) {
            const children = res.children
            for (var i = 0; i < children.length; i++) {
              this.checkList[res.key].checkList.push(children[i].value)
            }
          }
        }
        this.checkAllBox()// 判断总check状态
      },
      // 判断总check状态
      checkAllBox () {
        var checkAll = false
        this.indeterminate = false
        for (var i = 0; i < this.modal.dataSelect.length; i++) { // 获取key
          const length = this.checkList[this.modal.dataSelect[i].key].checkList.length
          const checkLength = this.checkList[this.modal.dataSelect[i].key].checkLength
          if (length == 0) { // 说明其中一个节点为空
            this.checkedAll = false
            checkAll = true
          } else {
            if (length != checkLength) { // 说明节点没有全选
              this.indeterminate = true
              this.checkedAll = false
              return
            }
          }
        }
        if (checkAll) {
          this.indeterminate = true
          this.checkedAll = false
        } else {
          this.indeterminate = false
          this.checkedAll = true
        }
      },
      // 所有全选
      checkAll () {
        if (this.checkedAll) {
          this.indeterminate = false
          this.checkedAll = false
          for (var key in this.checkList) { // 将所有选择框取消掉
            this.checkList[key].indeterminate = false
            this.checkList[key].checkAll = false
            this.checkList[key].checkList = []
          }
        } else {
          this.indeterminate = false
          this.checkedAll = true
          for (var i = 0; i < this.modal.dataSelect.length; i++) { // 全选所有选择框
            this.checkList[this.modal.dataSelect[i].key].indeterminate = false
            this.checkList[this.modal.dataSelect[i].key].checkAll = true
            // 塞入值
            this.checkList[this.modal.dataSelect[i].key].checkList.push(this.modal.dataSelect[i].value)
            if ('children' in this.modal.dataSelect[i]) {
              const children = this.modal.dataSelect[i].children
              for (var j = 0; j < children.length; j++) {
                this.checkList[this.modal.dataSelect[i].key].checkList.push(children[j].value)
              }
            }
          }
        }
      },
      // 是否展示弹窗 并传参
      setModalVisible (modalVisible, parms) {
        this.checkListTemp = this.checkList
        this.modal.visible = modalVisible
        this.modal.params = parms
        if (modalVisible) {
          this.modal.title = parms.paramDesc
          this.modal.paramName = parms.paramName // 这里记录当前弹窗所对应的参数名 以便后面做填充
          this.modal.dataSelect = parms.child // 传入选择数据
          // 设置参数
          var select = parms.child
          const _this = this
          select.forEach((items, indexs) => {
            var ch = {
              indeterminate: false, // 是否部分选中
              checkAll: false, // 是否全选
              checkLength: 0,
              id: items.key,
              checkList: []
            }
            if ('children' in items) {
              ch.checkLength = items.children.length
            }
            _this.$set(_this.checkList, items.key, ch)
          })
          if (this.searchForm[parms.paramName + '$Param'] != '') {
            this.modal = this.searchForm[parms.paramName + '$Param']
            this.checkList = this.searchForm[parms.paramName + '$CheckList']
            this.checkedAll = this.searchForm[parms.paramName + '$checkedAll']
          }
        } else {
          this.modal.title = ''
          this.modal.paramName = '' // 这里记录当前弹窗所对应的参数名 以便后面做填充
          this.modal.dataSelect = [] // 传入选择数据
          this.modal.params = {}
          this.modal.visible = false
          this.indeterminate = false
          this.checkedAll = false
          this.checkList = {}
        }
      },
      // 保存模态框参数
      saveModalVal () {
        this.searchForm[this.modal.paramName] = []
        this.searchForm[this.modal.paramName + '$Temp'] = []
        this.searchForm[this.modal.paramName + '$Param'] = this.modal
        this.searchForm[this.modal.paramName + '$CheckList'] = this.checkList
        this.searchForm[this.modal.paramName + '$checkedAll'] = this.checkedAll
        var arrList = []
        for (var i = 0; i < this.modal.dataSelect.length; i++) { // 全选所有选择框
          const checkList = this.checkList[this.modal.dataSelect[i].key].checkList
          for (var j = 0; j < checkList.length; j++) {
            this.searchForm[this.modal.paramName].push(checkList[j])
            this.findModelName(this.modal.params.child, checkList[j], this.modal.paramName, this)
          }
        }
        const _this = this
        if (this.searchForm[this.modal.paramName].length == 0) {
          this.$confirm({
            title: '提示',
            content: '您没有选择任何参数，确认继续吗？',
            onOk () {
              _this.modal.title = ''
              _this.modal.paramName = '' // 这里记录当前弹窗所对应的参数名 以便后面做填充
              _this.modal.dataSelect = [] // 传入选择数据
              _this.modal.params = {}
              _this.indeterminate = false
              _this.checkedAll = false
              _this.checkList = {} // 清空选择框的参数
              _this.modal.visible = false
            },
            onCancel () {
            }
          })
        } else {
          _this.modal.title = ''
          _this.modal.paramName = '' // 这里记录当前弹窗所对应的参数名 以便后面做填充
          _this.modal.dataSelect = [] // 传入选择数据
          _this.modal.params = {}
          _this.indeterminate = false
          _this.checkedAll = false
          _this.checkList = {} // 清空选择框的参数
          _this.modal.visible = false
        }
      },
      findModelName (params, val, param, _this) {
        for (var i = 0; i < params.length; i++) {
          if (params[i].key === val) {
            _this.searchForm[param + '$Temp'].push(params[i].label)
          }
          if (Object.prototype.hasOwnProperty.call(params[i], 'children')) {
            _this.findModelName(params[i].children, val, param, _this)
          }
        }
      },
      // 加载参数
      loadParms () {
        const l_this = this
        this.reportParam.forEach((item, index) => {
          // 使用$set为对象添加响应式 property
          l_this.$set(l_this.searchForm, item.paramName, '')
          // 时间粒度不算具体参数 所以key值有时间参数动态生成
          if (item.paramType === 'time') {
            this.$set(this.searchForm, item.paramName, [])
            this.$set(this.searchForm, item.paramName + '$timeFormat', item.timeFormat)
            this.$set(l_this.searchForm, item.paramName + '$Grain', null)
          }
          if (item.paramType === 'tree') {
            this.$set(l_this.searchForm, item.paramName, [])
          }
          if (item.paramType === 's-select') {
            // 保存中文显示
            this.$set(l_this.searchForm, item.paramName + '$Temp', [])
            // 保存页面存储的值
            this.$set(l_this.searchForm, item.paramName + '$Param', '')
            // checkList
            this.$set(l_this.searchForm, item.paramName + '$CheckList', '')
            // checkAll
            this.$set(l_this.searchForm, item.paramName + '$checkedAll', '')
          }
          if (item.paramType === 'department') {
            this.$set(l_this.searchForm, item.paramName + '$Temp', [])
            // 保存页面存储的值
            this.$set(l_this.searchForm, item.paramName + '$Param', '')
            // checkList
            this.$set(l_this.searchForm, item.paramName + '$CheckList', '')
            // checkAll
            this.$set(l_this.searchForm, item.paramName + '$checkedAll', '')
            this.department.push(item.paramName)
          }
          // item.type="";
        })
        this.loading = false
      },
      // 重置所有参数
      resetParam () {
        this.searchForm = {}
        const l_this = this
        /* this.reportParam.forEach((item, index) => {
          // 使用$set为对象添加响应式 property
          this.$set(this.searchForm, item.paramName, "");
          //时间粒度不算具体参数 所以key值有时间参数动态生成
          if (item.paramType == "time") {
            this.$set(this.searchForm, item.paramName + "$timeFormat", item.timeFormat);
            this.$set(l_this.searchForm, item.paramName + "$Grain", null);
            this.$set(this.searchForm, item.paramName, []);
          }
          if (item.paramType == "tree") {
            this.$set(this.searchForm, item.paramName, []);
          }
        }); */
        this.loadParms()
        // this.submitQueryForChild();
      },
      // 子组件调用父组件方法
      exportExcelForChild () {
        this.exportExcel()
      },
      submitQueryForChild () {
        for (var i = 0; i < this.department.length; i++) {
          if (this.searchForm[this.department[i]] === '' || this.searchForm[this.department[i]] === undefined || this.searchForm[this.department[i]] == null) {
            this.$message.error('机构参数为必选参数')
            return
          }
        }
        this.submitQuery()
      }
    },
    computed: {
      authInfo: function () {
        return this.$store.getters.authInfo
      },
      sysInfo: function () {
        return this.$store.getters.sysInfo
      }
    },
    watch: {
      reportParam: {
        handler (newName, oldName) {
          this.loadParms()
        }
      }
    },
    components: {},
    mounted () {
      var myDate = new Date()
      var tYear = myDate.getFullYear()
      for (var i = 0; i < 5; i++) {
        this.year.push(tYear--)
      }
    }
  }
</script>
