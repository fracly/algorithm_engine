<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="连接类型:">
      <a-select style="width: 100%" v-model="params.connectionType">
        <a-select-option v-for="a in typeList" :value="a.value"> {{ a.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="自定义参数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div @click="openmodel()">
        <a-icon type="plus-circle" style="color:#429FE1"/>
        <span v-if="params.localParams.length!=0">已添加{{ params.localParams.length }}条自定义参数</span>
      </div>
      <a-modal
        v-if="customParamsStaus"
        :visible="customParamsStaus"
        destroyOnClose
        title="自定义参数"
        @cancel="showCustomParams"
        width="550px">
        <template slot="footer">
          <a-button type="primary" @click="saveCustomParams">
            保存
          </a-button>
          <a-button type="back" @click="showCustomParams">
            返回
          </a-button>
        </template>
        <custom-params
          :CustomParams="params.localParams"
          :pageList1="params.columnList0"
          :pageList2="params.columnList1"
          ref="CustomParams"></custom-params>
      </a-modal>
    </a-form-item>
    <a-form-item label="左表输出字段:">
      <span v-if="params.columnList0.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnList0.length!=0" style="width: 100%;" @click="showChooseFiled">已选择<span
        class="filedChoose">{{ params.leftColumnList.length }}</span>个字段
      </a-button>
      <!--字段选择模态框-->
      <a-modal :visible="FiledChoose" title="字段选择" @cancel="showChooseFiled" width="850px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseFiled">
            保存
          </a-button>
          <a-button key="back" @click="showChooseFiled">
            返回
          </a-button>
        </template>
        <field-selection
          v-if="FiledChoose"
          :chooseField="params.leftColumnList"
          :FiledList="params.columnList0"
          ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>
    <a-form-item label="右表输出字段:">
      <span v-if="params.columnList1.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnList1.length!=0" style="width: 100%;" @click="showChooseFiled1">已选择<span
        class="filedChoose">{{ params.rightColumnList.length }}</span>个字段
      </a-button>
      <a-modal :visible="FiledChoose1" title="字段选择" @cancel="showChooseFiled1" width="850px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseFiled1">
            保存
          </a-button>
          <a-button key="back" @click="showChooseFiled1">
            返回
          </a-button>
        </template>
        <field-selection
          v-if="FiledChoose1"
          :chooseField="params.rightColumnList"
          :FiledList="params.columnList1"
          ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import { getDataSouceData, getDataStorage } from '@/utils/global'
  import FieldSelection from '@/components/FieldSelection'
  import customParams from '@/views/model-center/model-factory/module/Preprocess/TableJoin/customParams'
  import _ from 'lodash'

  export default {
    data () {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        FiledChoose: false,
        FiledChoose1: false,
        customParamsStaus: false,
        typeList: [{ name: '左连接', value: 'left' }, { name: '右连接', value: 'right' }, { name: '内连接', value: 'inner' }],
        params: {
          tableData: [],
          databaseData: [],
          datasource: 0,
          datasource2: 0,
          table: '',
          leftTable: '',
          rightTable: '',
          showType: 'TABLE',
          characterColumns: [],
          characterColumns1: [],
          newTable: '',
          columnList: [],
          columnList0: [],
          columnList1: [],
          dataStorage: getDataStorage(),
          localParams: [],
          connectionType: '',
          rightColumnList: [],
          leftColumnList: [],
          leftJoinColumn: [],
          symbolList: [],
          rightJoinColumn: []
        },
        columnList0: [],
        columnList1: [],
        initState: false
      }
    },
    components: { FieldSelection, customParams },
    props: {
      tasks: {
        type: Object,
        default: () => {
          return {
            id: 'default',
            label: ''
          }
        }
      }
    },
    created () {
      this.initPage()
    },
    methods: {
      /* 页面初始化 按需加载，具体方法查看module.vue */
      initPage () {
        if (JSON.stringify(this.tasks) !== '{}') {
          this.params = this.tasks
        } else {
          // 如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable = this.$parent.getLocalNodeTableName()
        }

        // 判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空,要写在保存表的上面，上面有俩个表的这个要修改
        const preNodeTableName = this.$parent.getPreNodeTableName()
        if (preNodeTableName !== this.params.table) {
          this.params.leftColumnList = []
          this.params.rightColumnList = []
          this.params.localParams = []
          this.params.leftJoinColumn = []
          this.params.rightJoinColumn = []
          this.params.symbolList = []
        }
        // 获取上一节点的datasource
        var datasources = this.$parent.getPreNodeDataSourceIds()
        if ('point0datasource' in datasources) {
          this.$set(this.params, 'datasource', datasources.point0datasource)
        } else {
          this.$set(this.params, 'datasource', 0)
        }
        if ('point1datasource' in datasources) {
          this.$set(this.params, 'datasource2', datasources.point1datasource)
        } else {
          this.$set(this.params, 'datasource2', 0)
        }

        var tables = this.$parent.getPreNodeParams('newTable', 'String', 'table')
        if ('point0table' in tables) {
          this.params.leftTable = tables.point0table
        } else {
          this.params.leftTable = ''
        }
        if ('point1table' in tables) {
          this.params.rightTable = tables.point1table
        } else {
          this.params.rightTable = ''
        }

        // 保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
        /* 获取上游节点字段信息数据 */
        const _that = this
        const data = this.$parent.getPreNodeColumList()
        if ('point0List' in data) {
          _that.$set(_that.params, 'columnList0', data.point0List)
        } else {
          _that.$set(_that.params, 'columnList0', [])
        }
        if ('point1List' in data) {
          _that.$set(_that.params, 'columnList1', data.point1List)
        } else {
          _that.$set(_that.params, 'columnList1', [])
        }
      },
      saveCustomParams () {
        const localParams = this.$refs.CustomParams.localParams
        // 构建返回的三个字段
        this.params.leftJoinColumn = []
        this.params.rightJoinColumn = []
        this.params.symbolList = []
        for (let i = 0; i < localParams.length; i++) {
          const a = localParams[i].column1
          const b = localParams[i].column2
          const c = localParams[i].symbol
          if ((a === '' || a == null)) {
            this.$message.error('左表列不能为空')
            return
          } else if (c === '' || c == null) {
            this.$message.error('关联符号不能为空')
            return
          } else if (b === '' || b == null) {
            this.$message.error('右表列不能为空')
            return
          }
          this.params.leftJoinColumn.push(a)
          this.params.rightJoinColumn.push(b)
          this.params.symbolList.push(c)
          for (let j = 0; j < this.params.columnList0.length; j++) {
            if (this.params.columnList0[j].name === a) {
              this.params.columnList.push(this.params.columnList0[j])
            }
          }
          for (let j = 0; j < this.params.columnList1.length; j++) {
            if (this.params.columnList1[j].name === b) {
              this.params.columnList.push(this.params.columnList1[j])
            }
          }
        }
        debugger
        this.params.columnList = this.removeRepeat(this.params.columnList, 'name')
        this.params.localParams = localParams
        this.customParamsStaus = !this.customParamsStaus
      },
      showCustomParams () {
        this.customParamsStaus = !this.customParamsStaus
      },

      /* 展开显示字段选择的弹窗 */
      showChooseFiled () {
        this.FiledChoose = !this.FiledChoose
      },
      saveChooseFiled () {
        this.params.leftColumnList = this.$refs.filedSelect.chooseFileds
        this.columnList0 = []
        for (let a = 0; a < this.params.columnList0.length; a++) {
          for (let b = 0; b < this.params.leftColumnList.length; b++) {
            if (this.params.columnList0[a].name === this.params.leftColumnList[b]) {
              this.columnList0.push(this.params.columnList0[a])
            }
          }
        }
        this.columnList1 = []
        for (let a = 0; a < this.params.columnList1.length; a++) {
          for (let b = 0; b < this.params.rightColumnList.length; b++) {
            if (this.params.columnList1[a].name === this.params.rightColumnList[b]) {
              this.columnList1.push(this.params.columnList1[a])
            }
          }
        }
        this.params.columnList = _.concat(this.columnList1, this.columnList0)
        this.params.columnList = this.removeRepeat(this.params.columnList, 'name')
        this.FiledChoose = !this.FiledChoose
      },
      /* 展开显示字段选择的弹窗 */
      showChooseFiled1 () {
        this.FiledChoose1 = !this.FiledChoose1
      },
      saveChooseFiled1 () {
        this.params.rightColumnList = this.$refs.filedSelect.chooseFileds
        this.columnList1 = []
        for (let a = 0; a < this.params.columnList1.length; a++) {
          for (let b = 0; b < this.params.rightColumnList.length; b++) {
            if (this.params.columnList1[a].name === this.params.rightColumnList[b]) {
              this.columnList1.push(this.params.columnList1[a])
            }
          }
        }
        this.columnList0 = []
        for (let a = 0; a < this.params.columnList0.length; a++) {
          for (let b = 0; b < this.params.leftColumnList.length; b++) {
            if (this.params.columnList0[a].name === this.params.leftColumnList[b]) {
              this.columnList0.push(this.params.columnList0[a])
            }
          }
        }
        this.params.columnList = _.concat(this.columnList0, this.columnList1)
        this.params.columnList = this.removeRepeat(this.params.columnList, 'name')
        this.FiledChoose1 = !this.FiledChoose1
      },
      removeRepeat (arr, field) {
        const result = {}; const reSet = []
        const s = arr.map(v => v[field])
        for (const i in s) {
          if (result[s[i]] === undefined) {
            result[s[i]] = i
          } else {
            reSet.push(i)
          }
        }

        for (const key in reSet) {
          arr.splice(reSet[key] - key, 1)
        }
        return arr
      },
      openmodel () {
        this.customParamsStaus = !this.customParamsStaus
      }
    }
  }

</script>
<style scoped>
  .ant-form-item {
    margin-bottom: 4px !important;
  }

</style>

<style>
  .el-tooltip__popper[x-placement^=top] .popper__arrow {
    border-top-color: #1ab394;
  }

  .el-tooltip__popper[x-placement^=top] .popper__arrow:after {
    border-top-color: #1ab394;
  }

  .atooltip {
    background: #1ab394 !important;
  }

  /deep/ .ant-modal-body {
    padding: 5px !important;
  }
</style>
