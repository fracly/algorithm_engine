<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-row>
      <a-col :md="24" :sm="24">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" tab="左表">
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>
    <a-form-item label="号牌号码:">
      <a-select style="width: 100%" v-model="params.vehpass[0]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList0" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="号牌种类:">
      <a-select style="width: 100%" v-model="params.vehpass[1]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList0" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="经过时间:">
      <a-select style="width: 100%" v-model="params.vehpass[2]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList0" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="设备编号:">
      <a-select style="width: 100%" v-model="params.vehpass[3]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList0" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="车道号:">
      <a-select style="width: 100%" v-model="params.vehpass[4]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList0" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-row>
      <a-col :md="24" :sm="24">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" tab="右表">
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>
    <a-form-item label="相机编号:">
      <a-select style="width: 100%" v-model="params.lane[0]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="相机监控车道:">
      <a-select style="width: 100%" v-model="params.lane[1]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="所属路口:">
      <a-select style="width: 100%" v-model="params.lane[2]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="所属车道:">
      <a-select style="width: 100%" v-model="params.lane[3]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="车道通行能力:">
      <a-select style="width: 100%" v-model="params.lane[4]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="红绿灯周期时常:">
      <a-select style="width: 100%" v-model="params.lane[5]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="绿信比:">
      <a-select style="width: 100%" v-model="params.lane[6]" show-search optionFilterProp="children">
        <a-select-option v-for="col in params.columnList1" :value="col.name"> {{ col.name }}</a-select-option>
      </a-select>
    </a-form-item>
    <a-row>
      <a-col :md="24" :sm="24">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" tab="时间参数">
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>
    <a-form-item label="时间类型">
      <a-radio-group style="width: 100%" v-model="params.timeParam" >
        <a-radio value="1">起止时间</a-radio>
        <a-radio value="2">系统时间</a-radio>
      </a-radio-group>
      <a-range-picker
        v-show="params.timeParam==='1'"
        v-model="timeRange"
        @change="handleRangePickerChange"
        showTime
        format="YYYY-MM-DD HH:mm:ss"
        :style="{width: '320px','margin-top': '10px'}"/>
      <a-select
        v-show="params.timeParam==='2'"
        style="width: 50%;margin-top: 10px"
        v-model="params.timeType"
        placeholder="请选择时间类型">
        <a-select-option value="D">天</a-select-option>
        <a-select-option value="H">时</a-select-option>
        <a-select-option value="M">分</a-select-option>
      </a-select>
      <a-input
        v-show="params.timeParam==='2'"
        style="width: 45%;margin-left: 8px;margin-top: 10px"
        v-model="params.value"
        placeholder="请输入前推数值"></a-input>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import { getDataSouceData, getDataStorage } from '@/utils/global'
  import FieldSelection from '@/components/FieldSelection'

  export default {
    data () {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        timeRange: [],
        params: {
          columnList: [],
          columnList0: [],
          columnList1: [],
          datasource: 0,
          datasource2: 0,
          leftTable: '',
          rightTable: '',
          showType: 'TABLE',
          newTable: '',
          dataStorage: getDataStorage(),
          vehpass: [],
          lane: [],
          start_time: '',
          end_time: '',
          timeParam: '',
          timeType: '',
          value: ''
        }
      }
    },
    components: { FieldSelection },
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
        debugger
        // #####################通用写法###################
        // 上次本组件保存的参数
        if (JSON.stringify(this.tasks) !== '{}') {
          this.params = this.tasks
          this.timeRange = [this.params.start_time, this.params.end_time]
        } else {
          // 如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable = this.$parent.getLocalNodeTableName()
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
        // #####################通用写法###################

        // ####################组件按需加载####################
        /* 获取上游节点字段信息 */
        this.getProNodeColumList()
        // ####################组件按需加载####################
        this.saveTable()
      },

      /* 获取上游节点的字段值 */
      getProNodeColumList () {
        /* 获取上游节点字段信息数据 */
        // const _that = this
        const data = this.$parent.getPreNodeColumList()
        if ('point0List' in data) {
          this.$set(this.params, 'columnList0', data.point0List)
        } else {
          this.$set(this.params, 'columnList0', [])
        }
        if ('point1List' in data) {
          this.$set(this.params, 'columnList1', data.point1List)
        } else {
          this.$set(this.params, 'columnList1', [])
        }
      },
      saveTable () {
        const columnList = [
          { name: 'inter_id', type: 'string', comment: '路口代码' },
          { name: 'lane_id', type: 'string', comment: '车道代码' },
          { name: 'pdcd', type: 'double', comment: '排队长度' },
          { name: 'start_time', type: 'string', comment: '查询开始时间' },
          { name: 'end_time', type: 'string', comment: '查询结束时间' },
          { name: 'update_time', type: 'string', comment: '写入时间' }
        ]
        this.params.columnList = columnList
      },
      handleRangePickerChange (val) {
        if (val.length === 0) {
          this.params.start_time = null
          this.params.end_time = null
        } else {
          this.params.start_time = val[0].format('YYYY-MM-DD HH:mm:ss')
          this.params.end_time = val[1].format('YYYY-MM-DD HH:mm:ss')
        }
      }
    }
  }

</script>
<style scoped>
  /deep/ .ant-modal-body {
    padding: 15px !important;
  }
</style>

<style>
  .filedChoose {
    color: #4FAEEC;
    padding-left: 3px;
    padding-right: 3px
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
