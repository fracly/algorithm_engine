<template>
  <div>
    <a-row>
      <a-col :md="6" :sm="12">
        <a-form-item>
          <a-select
            show-search
            optionFilterProp="children"
            placeholder="请选择服务类型"
            v-model="serviceType"
            style="width: 90%"
          >
            <a-select-option
              v-for="(item) in typeList"
              :key="item.id"
              :value="item.id">{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-col>
      <a-col :md="12" :sm="12">
        <a-form-item label="">
          <a-input v-model="searchKey" placeholder="输入关键字搜索服务" />
        </a-form-item>
      </a-col>
      <a-col :md="6" :sm="12">
        <a-form-item label="">
          <a-button type="primary" icon="search" style="width: 120px;margin-left:8px;" @click="handleSearch()">查询</a-button>
          <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>

        </a-form-item>
      </a-col>

    </a-row>
    <a-divider />
    <a-row :gutter="[16,16]" >
      <a-col :span="12">
        <a-table
          :columns="columns"
          :data-source="pageList"
          :pagination="false"
          size="small"
          :row-selection="rowSelection"
          bordered
          :scroll="{y:400}"></a-table>
      </a-col>
      <a-col :span="12">
        <a-table
          :columns="columnsTemp"
          :data-source="FiledListTemp"
          :pagination="false"
          size="small"
          bordered
          :scroll="{y:400}">
          <a slot="action" slot-scope="text,record" @click="deleteItem(record)" style="text-align: center">
            <a-icon type="delete" style="color:#FF498C;font-size: larger" theme="outlined"/>
          </a>
        </a-table>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import { uniqueForKey, getSubtractForKey } from '@/utils/global'

  const columns = [
    {
      title: '服务名称',
      dataIndex: 'name',
      key: 'name',
      scopedSlots: { customRender: 'name' },
      sorter: (a, b) => a.name.length - b.name.length
    },
    {
      title: '服务编码',
      dataIndex: 'type',
      key: 'type',
      sorter: (a, b) => a.type.length - b.type.length
    },
    {
      title: '服务描述',
      dataIndex: 'comment',
      key: 'comment'
    }
  ]

  const columnsTemp = [
    {
      title: '',
      dataIndex: 'name',
      key: 'operation',
      width: 50,
      scopedSlots: { customRender: 'action' }
    },
    {
      title: '服务名称',
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.length - b.name.length
    },
    {
      title: '服务编码',
      dataIndex: 'type',
      key: 'type',
      sorter: (a, b) => a.type.length - b.type.length
    },
    {
      title: '服务描述',
      dataIndex: 'comment',
      key: 'comment'
    }
  ]

  export default {
    name: 'FieldSelection',
    components: {},
    data () {
      return {
        searchKey: '',
        serviceType: undefined,
        columns,
        columnsTemp,
        FiledListTemp: [],
        pageList: [],
        chooseFileds: []
      }
    },
    computed: {
      rowSelection () {
        const _that = this
        return {
          selectedRowKeys: _that.chooseFileds,
          onChange: (selectedRowKeys, selectedRows) => {
            _that.changeRow(selectedRowKeys, selectedRows)
          },
          getCheckboxProps: record => ({
            props: {
              defaultChecked: _that.chooseField.includes(record.id)
            }
          })
        }
      }
    },
    props: {
      /* 已选择的字段 */
      chooseField: {
        type: Array,
        required: true
      },
      /* 选择字段集合 */
      FiledList: {
        type: Array,
        required: true
      },
      // 类型列表
      typeList: {
        type: Array,
        required: true
      }
    },
    watch: {
    },
    created () {
      this.initPage()
    },
    methods: {
      initPage () {
        this.pageList = this.FiledList
        this.chooseFileds = this.chooseField
        for (let i = 0; i < this.chooseFileds.length; i++) {
          this.getItem(this.chooseFileds[i])
        }
        this.setKey()
      },
      getItem (name) {
        for (let i = 0; i < this.pageList.length; i++) {
          if (name === this.pageList[i].id) {
            this.FiledListTemp.push(this.FiledList[i])
          }
        }
      },
      setKey () {
        for (let i = 0; i < this.pageList.length; i++) {
          this.pageList[i].key = this.pageList[i].id
        }
      },
      deleteItem (items) {
        const index = this.chooseFileds.findIndex(item => item === items.id)
        this.chooseFileds.splice(index, 1)
        const index1 = this.FiledListTemp.findIndex(item => item.id === items.id)
        this.FiledListTemp.splice(index1, 1)
      },
      changeRow (selectedRowKeys, selectedRows) {
        // 获取当前展示table 未选择的在右边临时table中去掉
        const subtractForKey = getSubtractForKey(this.pageList, selectedRows, 'id')
        const filedTemp = this.FiledListTemp.concat(selectedRows)
        this.FiledListTemp = uniqueForKey(filedTemp, 'id')
        this.FiledListTemp = getSubtractForKey(this.FiledListTemp, subtractForKey, 'id')
        const chooseFiledsTemp = []
        for (var i = 0; i < this.FiledListTemp.length; i++) {
          chooseFiledsTemp.push(this.FiledListTemp[i].id)
        }
        this.chooseFileds = chooseFiledsTemp
      },
      handleSearch () {
        const name = this.searchKey.trim()
        const type = this.serviceType
        if (name !== '' && type !== undefined) {
          this.pageList = this.FiledList.filter(function (pageList) {
            return Object.keys(pageList).some(function (key) {
              return String(pageList[key]).indexOf(name) > -1 && pageList['serverType'] === type
            })
          })
        } else if (name !== '' && type === undefined) {
          this.pageList = this.FiledList.filter(function (pageList) {
            return Object.keys(pageList).some(function (key) {
              return String(pageList[key]).indexOf(name) > -1
            })
          })
        } else if (name === '' && type !== undefined) {
          this.pageList = this.FiledList.filter(function (pageList) {
            return Object.keys(pageList).some(function (key) {
              return pageList['serverType'] === type
            })
          })
        } else {
          this.pageList = this.FiledList
        }
        console.log(this.pageList)
      },
      resetSearchForm () {
        this.searchKey = ''
        this.serviceType = undefined
        this.handleSearch()
      }
    }
  }
</script>
<style>
  /deep/.ant-modal-body {
    padding: 5px!important;
  }
</style>
