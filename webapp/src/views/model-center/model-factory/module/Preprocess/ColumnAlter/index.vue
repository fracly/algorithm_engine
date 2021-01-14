<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="列名修改:">
      <span v-if="columnList.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="columnList.length!=0" style="width: 100%;" @click="showChooseFiled">已修改<span class="filedChoose">{{ params.characterColumns.length }}</span>个字段</a-button>
      <!--字段选择模态框-->
      <a-modal :visible="FiledChoose" title="字段选择" @cancel="showChooseFiled" width="950px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseFiled">
            保存
          </a-button>
          <a-button key="back" @click="showChooseFiled">
            返回
          </a-button>
        </template>
        <field-update
          v-if="FiledChoose"
          :savechooseFileds="params.savechooseFileds"
          :chooseField="params.characterColumns"
          :FiledList="params.columnListcopy1"
          :beforeColumnList="params.beforeColumnList"
          ref="filedUpdate"></field-update>
      </a-modal>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import { getDataSouceData, getDataStorage } from '@/utils/global'
  import FieldUpdate from '@/components/FieldUpdate'
  import cloneDeep from 'lodash.clonedeep'

  export default {
    data () {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        FiledChoose: false,
        columnList: [],
        params: {
          columnListcopy1: [],
          savechooseFileds: [],
          databaseData: [],
          tableData: [],
          datasource: '',
          table: '',
          showType: 'TABLE',
          characterColumns: [],
          newTable: '',
          dataStorage: getDataStorage(),
          beforeColumnList: [],
          afterColumnList: [],
          columnList: []
        },
        customParamsStaus: false,
        initState: false
      }
    },
    components: { FieldUpdate },
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
      /* 页面初始化 */
      initPage () {
        // 上次本组件保存的参数
        if (JSON.stringify(this.tasks) != '{}') {
          this.params = this.tasks
        } else {
          // 如果上次没有保存过，则生成新table名字
          this.params.newTable = this.$parent.getLocalNodeTableName()
        }
        // 判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空
        const preNodeTableName = this.$parent.getPreNodeTableName()
        if (preNodeTableName !== this.params.table) {
          this.params.characterColumns = []
        }
        // 获取上一节点的datasource
          this.params.datasource = this.$parent.getPreNodeDataSourceId()
        // 保存本次的上一节点的表名称
        this.params.table = preNodeTableName
        /* 获取上游节点字段信息数据 */
        const data = this.$parent.getPreNodeColumList()
        if ('point0List' in data) {
          this.columnList = data.point0List
          this.params.columnListcopy1 = cloneDeep(this.columnList)
        }
      },
      /* 展开显示字段选择的弹窗 */
      showChooseFiled () {
        this.FiledChoose = !this.FiledChoose
      },
      saveChooseFiled () {
        debugger
         /* refs是父页面调用子页面的方法参数 */
        // this.params.characterColumns = this.$refs.filedUpdate.chooseFileds;
        // this.FiledChoose = !this.FiledChoose
        const filedListTemp = this.$refs.filedUpdate.FiledListTemp
        const chooseFileds = this.$refs.filedUpdate.chooseFileds
        this.params.savechooseFileds = chooseFileds
        // 构造需要的参数，包括传给下一个节点的参数
          this.getDataFromUpdate(filedListTemp)
      },
      getDataFromUpdate (filedListTemp) {
        let a
        const _that = this
        const beforeColumnList = []
        const afterColumnList = []
        for (a = 0; a < filedListTemp.length; a++) {
          const name = filedListTemp[a].name
          const updateName = filedListTemp[a].updateName
          if (updateName === '' || updateName == null) {
            this.$message.error('修改列名不能为空')
            return
          }
          beforeColumnList.push(name)
          afterColumnList.push(updateName)
        }
        _that.params.characterColumns = filedListTemp
        _that.params.beforeColumnList = beforeColumnList
        _that.params.afterColumnList = afterColumnList
        // 构建传给下一个节点的columnList
         const columnListcopy = cloneDeep(_that.columnList)
        for (let a = 0; a < columnListcopy.length; a++) {
           const name = columnListcopy[a].name
           if (beforeColumnList.includes(name)) {
             const number = beforeColumnList.indexOf(name)
             const aftername = afterColumnList[number]
             columnListcopy[a].name = aftername
             columnListcopy[a].comment = filedListTemp[number].comment
           }
        }
        this.params.columnList = columnListcopy
        debugger
        this.FiledChoose = !this.FiledChoose
      }
    }
  }

</script>
<style scoped>
  /deep/.ant-modal-body {
    padding: 15px!important;
  }
</style>

<style>
  .filedChoose{
    color:#4FAEEC;
    padding-left: 3px;
    padding-right:3px
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
