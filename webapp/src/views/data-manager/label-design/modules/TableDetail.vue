<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :closable="false"
  >
    <a-spin :spinning="confirmLoading">
      <a-table
        ref="table"
        :columns="columns"
        rowKey="id"
        :key="id"
        :dataSource="columnList"
        style="overflow-x:auto;overflow-y:auto;height:600px;"
        :pagination="false"
        bordered>
        <template slot="name" slot-scope="text, record">
          <a-input v-if="record.editable" :value="text" style="margin: -5px -5px -5px -5px" @change="e => handleChange(e.target.value, record.name, 'name', record.id)"></a-input>
          <span v-else> {{ text }}</span>
        </template>
        <template slot="comment" slot-scope="text, record">
          <a-input v-if="record.editable" :value="text" style="margin: -5px -5px -5px -5px" @change="e => handleChange(e.target.value, record.comment, 'comment', record.id)"></a-input>
          <a-popover title="" :content="text" v-else-if="text.length > 5">
            {{ text.substr(0, 4) + '...' }}
          </a-popover>
          <span v-else>
            {{ text }}
          </span>
        </template>
        <template slot="type" slot-scope="text, record">
          <a-select v-if="record.editable" style="width:100px;" @change="handleChange($event, '', 'type', record.id)" v-model="record.type">
            <a-select-option :key=item v-for="item in columnTypeList" :value="item" :name="item">
              {{ item }}
            </a-select-option>
          </a-select>
          <span v-else> {{ text }}</span>
        </template>
        <template slot="operation" slot-scope="text, record">
          <div class="editable-row-operations">
              <span v-if="record.editable">
                <a @click="() => save(record)">保存</a> |
                <a-popconfirm title="确认取消?" @confirm="() => cancel(record.id)">
                  <a>取消</a>
                </a-popconfirm>
              </span>
            <span v-else>
                <a :disabled="editingKey !== null" @click="handleEdit(record.id)">编辑</a>
              </span>
          </div>
        </template>
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
      </a-table>
    </a-spin>
    <a-button style="margin-top: 10px" @click="handleAdd" type="primary">添加字段</a-button>
    <template slot="footer">
      <a-button type="default" @click="handleCancel">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>

    import { addTableColumn, updateTableColumn, queryTableColumn } from '@/api/model'
    import { testContainsHZ } from '@/utils/util'

    const columnTypeList = ['string', 'bigint', 'int', 'float', 'double', 'decimal', 'timestamp', 'date']

    export default {
        name: 'TableDetail',
        data () {
            return {
                title: '',
                visible: false,
                adding: false,
                oldName: '',
                confirmLoading: false,
                columnTypeList,
                editingKey: null,
                cacheData: [],
                // 查询条件
                queryParam: {
                    base: '',
                    table: ''
                },
                // 表头
                columns: [
                    {
                        title: '#',
                        scopedSlots: { customRender: 'serial' },
                        width: '8%'
                    },
                    {
                        title: '字段名',
                        dataIndex: 'name',
                        scopedSlots: { customRender: 'name' },
                        width: '15%'
                    },
                    {
                        title: '字段类型',
                        dataIndex: 'type',
                        scopedSlots: { customRender: 'type' },
                        width: '15%'
                    },
                    {
                        title: '字段描述',
                        dataIndex: 'comment',
                        scopedSlots: { customRender: 'comment' },
                        width: '15%'
                    },
                    {
                        title: '数据质量',
                        dataIndex: 'colQuality'
                    },
                    {
                        title: '告警信息',
                        dataIndex: 'colWarn'
                    },
                    {
                        title: '操作',
                        dataIndex: 'operation',
                        scopedSlots: { customRender: 'operation' },
                        width: '15%'
                    }
                ],
                columnList: []
            }
        },
        created () {
            this.cacheData = this.columnList.map(item => ({ ...item }))
        },
        methods: {
            show (item) {
                this.visible = true
                this.confirmLoading = true
                queryTableColumn(item).then(res => {
                    this.columnList = res.data
                    this.queryParam.base = item.base
                    this.queryParam.table = item.tblName
                    this.title = '表 ' + item.tblName + ' 详情'
                    this.confirmLoading = false
                })
            },
            handleCancel () {
                this.visible = false
            },
            handleAdd () {
                if (this.editingKey === '') {
                   this.$message.warn('请先保存编辑的数据')
                   return
                }
                this.adding = true
                this.editingKey = ''
                this.columnList.push({ 'id': this.columnList.length + 1, 'editable': true, 'name': '', 'type': '', 'comment': '' })
            },
            handleChange (value, key, column, id) {
                // change 改变数组最后一个对象的值
                if (this.adding === true) {
                    const length = this.columnList.length
                    if (column === 'type') {
                        this.columnList[length - 1].type = value
                    } else if (column === 'name') {
                        this.columnList[length - 1].name = value
                    } else if (column === 'comment') {
                        this.columnList[length - 1].comment = value
                    }
                } else {
                    const newData = [...this.columnList]
                    const target = newData.filter(item => id === item.id)[0]
                    if (column === 'type') {
                        target.type = value
                    } else if (column === 'name') {
                        target.name = value
                    } else if (column === 'comment') {
                        target.comment = value
                    }
                }
            },
            handleDelete (i) {
                this.column.splice(i, 1)
            },
            handleEdit (key) {
                const newData = [...this.columnList]
                const target = newData.filter(item => key === item.id)[0]
                this.oldName = target.name
                this.editingKey = key
                if (target) {
                    target.editable = true
                    this.columnList = newData
                }
            },
            save (record) {
                const params = { ...this.queryParam }
                if (record.name === null || record.name === '') {
                    this.$message.error('字段名不能为空')
                    return
                }
                if (testContainsHZ(record.name)) {
                    this.$message.error('字段名不能包含中文')
                    return
                }
                if (record.type === null || record.type === '') {
                    this.$message.error('类型不能为空')
                    return
                }
                params.name = record.name
                params.type = record.type
                params.comment = record.comment
                if (this.adding === false) {
                    params.oldName = this.oldName
                    // update
                    const that = this
                    updateTableColumn(params).then(res => {
                        if (res.code === 0) {
                            that.$message.success(res.msg)
                            that.editingKey = null
                            that.$set(record, 'editable', false)
                        } else {
                            this.$message.error(res.msg)
                        }
                    })
                } else {
                    // add
                    addTableColumn(params).then(res => {
                        if (res.code === 0) {
                            this.$message.success('添加字段成功')
                            this.editingKey = null
                            record.editable = false
                            this.adding = false
                        } else {
                            this.$message.error('添加字段失败')
                        }
                    })
                }
            },
            cancel (key) {
                if (this.adding === true) {
                    this.columnList.pop()
                    this.editingKey = null
                } else {
                    const newData = [...this.columnList]
                    const target = newData.filter(item => key === item.id)[0]
                    this.editingKey = null
                    if (target) {
                        target.editable = false
                        this.columnList = newData
                    }
                }
            }
        }
    }
</script>
