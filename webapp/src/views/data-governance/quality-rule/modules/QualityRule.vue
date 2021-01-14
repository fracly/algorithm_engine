<template>
  <a-modal
    title="质量规则配置"
    :width="1000"
    :visible="visible"
    :confirmLoading="submitLoading"
    :closable="false">
    <template slot="footer">
      <a-button type="default" @click="handleCancel">关闭</a-button>
    </template>
    <a-row style="overflow-x:auto;overflow-y:auto;height:400px;">
    <a-descriptions title="表信息描述">
      <a-descriptions-item label="名称">
        {{ info.tblName }}
      </a-descriptions-item>
      <a-descriptions-item label="描述">
        {{ info.tblDesc }}
      </a-descriptions-item>
    </a-descriptions>
    <a-descriptions title="表规则配置" >
      <a-descriptions-item label="">
        <a-tabs>
          <a-tab-pane key="1" tab="表规则配置">
            <a-table
              :columns="tableHeader"
              :data-source="tableRules"
              :pagination="false"
              row-key="key"
              bordered>
              <span slot="serial" slot-scope="text, record, index">
                {{ index + 1 }}
              </span>
              <span slot="type" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable" :default-value="record.type" style="width: 100px;" v-model="record.type" @change="handleTableRuleTypeChange">
                  <a-select-option :value="item.type" :key="item.type" :name="item.type" v-for="item in cacheTableRuleTypeList"> {{ item.type }}</a-select-option>
                </a-select>
                <span v-else> {{ text }}</span>
              </span>
              <span slot="condi" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable" :default-value="record.condi" style="width: 100px;" v-model="record.condi" @change="handleTableRuleCondiChange($event, record)">
                  <a-select-option :value="item.condi" :key="item.condi" :name="item.condi" v-for="item in cacheTableRuleTypeCondiList"> {{ item.condi }}</a-select-option>
                </a-select>
                <span v-else> {{ text }}</span>
              </span>
              <span slot="param" slot-scope="text, record">
                <a-input v-if="record.editable && cacheTableRuleTypeCondiParamList.length === 0" style="width:100%" :default-value="text" v-model="record.param"></a-input>
                <a-select show-search optionFilterProp="children" v-else-if="record.editable && cacheTableRuleTypeCondiParamList.length > 0" :default-value="record.param" style="width: 100px;" v-model="record.param">
                  <a-select-option :value="item.param" :key="item.param" :name="item.param" v-for="item in cacheTableRuleTypeCondiParamList"> {{ item.param }}</a-select-option>
                </a-select>
                <span v-else> {{ text }}</span>
              </span>
              <span slot="action" slot-scope="text, record">
                <template v-if="tableEditKey !== record.key && tableEditKey === ''">
                  <a @click="handleTableRuleEdit(record)"><a-icon type="edit"></a-icon></a>
                  <a-divider type="vertical"/>
                  <a-popconfirm title="确认删除规则？" @confirm="handleTableRuleDelete(record)">
                    <a href="javascript:;"><a-icon type="delete"></a-icon></a>
                  </a-popconfirm>
                </template>
                <template v-else-if="tableEditKey !== record.key && tableEditKey !== ''">
                  <a @click="handleTableRuleEdit(record)" disabled="true"><a-icon type="edit"></a-icon></a>
                  <a-divider type="vertical"/>
                  <a @click="handleTableRuleDelete(record)" disabled="true"><a-icon type="delete"></a-icon></a>
                </template>
                <template v-else>
                  <a @click="handleTableRuleSave(record)">保存</a>
                  <a-divider type="vertical"/>
                  <a-popconfirm title="确认取消？" @confirm="handleTableRuleCancel(record)">
                    <a href="javascript:;">取消</a>
                  </a-popconfirm>
                </template>
              </span>
            </a-table>
            <a-button v-show="userInfo.administrator" type="primary" style="margin-top:5px;" @click="handleTableRuleAdd"><a-icon type="plus"></a-icon></a-button>
          </a-tab-pane>
          <a-tab-pane key="2" tab="字段规则配置">
            <a-table
              :columns="columnHeader"
              :data-source="columnRules"
              bordered
              :pagination="false"
            >
              <span slot="serial" slot-scope="text, record, index">
                {{ index + 1 }}
              </span>
              <span slot="name" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable" :default-value="record.name" style="margin: -5px -25px -5px -5px;width:100%" @change="handleColumnNameChange($event, record)">
                  <a-select-option v-for="item in cacheColumnList" :key="item.name" :name="item.name" :value="item.name">{{ item.name }}</a-select-option>
                </a-select>
                <span v-else> {{ text }} </span>
              </span>
              <span slot="type" slot-scope="text">
                <span> {{ text }} </span>
              </span>
              <template slot="comment" slot-scope="text">
                <span> {{ text }} </span>
              </template>
              <span slot="colRule" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable" :default-value="record.colRule" v-model="record.colRule" style="margin: -5px -25px -5px -5px;width:100%" @change="handleColumnRuleTypeChange($event, record)">
                  <a-select-option v-for="item in cacheColumnRuleTypeList" :key="item.type" :name="item.type" :value="item.type"> {{ item.type }}</a-select-option>
                </a-select>
                <span v-else> {{ text }} </span>
              </span>
              <span slot="colRuleCondi" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable" :default-value="record.colRuleCondi" v-model="record.colRuleCondi" style="margin: -5px -25px -5px -5px;width:100%" @change="handleColumnRuleCondiChange($event, record.colRule)">
                  <a-select-option v-for="item in cacheColumnRuleTypeCondiList" :key="item.condi" :name="item.condi" :value="item.condi"> {{ item.condi }}</a-select-option>
                </a-select>
                <span v-else> {{ text }} </span>
              </span>
              <span slot="colRuleParam" slot-scope="text, record">
                <a-select show-search optionFilterProp="children" v-if="record.editable && cacheColumnRuleTypeCondiParamList.length > 0" :default-value="record.colRuleParam" v-model="record.colRuleParam" style="margin: -5px -25px -5px -5px;width:100%">
                  <a-select-option v-for="item in cacheColumnRuleTypeCondiParamList" :key="item.param" :name="item.param" :value="item.param"> {{ item.param }}</a-select-option>
                </a-select>
                <a-input v-else-if="record.editable" :default-value="text" v-model="record.colRuleParam"></a-input>
                <span v-else> {{ text }}</span>
              </span>
              <span slot="action" slot-scope="text, record">
                <template v-if="columnEditKey !== record.key && columnEditKey === ''">
                  <a @click="handleColumnRuleEdit(record)"><a-icon type="edit"></a-icon></a>
                  <a-divider type="vertical"/>
                  <a-popconfirm title="确认删除规则？" @confirm="handleColumnRuleDelete(record)">
                    <a href="javascript:;"><a-icon type="delete"></a-icon></a>
                  </a-popconfirm>
                </template>
                <template v-else-if="columnEditKey !== record.key && columnEditKey !== ''">
                  <a @click="handleColumnRuleEdit(record)" disabled="true"><a-icon type="edit"></a-icon></a>
                  <a-divider type="vertical"/>
                  <a @click="handleColumnRuleDelete(record)" disabled="true"><a-icon type="delete"></a-icon></a>
                </template>
                <template v-else>
                  <a @click="handleColumnRuleSave(record)">保存</a>
                  <a-divider type="vertical"/>
                  <a-popconfirm title="确认取消？" @confirm="handleColumnRuleCancel(record)">
                    <a href="javascript:;">取消</a>
                  </a-popconfirm>
                </template>
              </span>
            </a-table>
            <a-button v-show="userInfo.administrator"  type="primary" style="margin-top:5px;" @click="handleColumnRuleAdd"><a-icon type="plus"></a-icon></a-button>
          </a-tab-pane>
          <a-button  slot="tabBarExtraContent" v-show="userInfo.administrator">
            <a style="color:green" href="javascript:;" @click="runRule"><a-icon type="play-circle"></a-icon> 运行规则 </a>
          </a-button>
        </a-tabs>
      </a-descriptions-item>
    </a-descriptions>
    </a-row>
  </a-modal>

</template>
<script>
    import { queryTableColumn, cudTableRule, cudColumnRule, queryColumnRule, queryTableRuleType, queryTableRuleTypeCondi, queryTableRuleTypeCondiParam,
        queryColumnRuleType, queryColumnRuleTypeCondi, queryColumnRuleTypeCondiParam } from '@/api/model'
    import TagSelectOption from '../../../../components/TagSelect/TagSelectOption'
    import { mapGetters } from 'vuex'
    const columnTypeList = ['string', 'bigint', 'int', 'float', 'double', 'decimal', 'timestamp', 'date']
    export default {
        name: 'QualityRule',
        components: { TagSelectOption },
        data () {
            return {
                columnTypeList,
                visible: false,
                submitLoading: false,
                tableAdding: false,
                columnAdding: false,
                tableEditKey: '',
                columnEditKey: '',
                info: {
                    base: '',
                    tblName: '',
                    tblDesc: ''
                },
                editingBack: {}, // 当前正在操作的对象的备份，取消时，便于还原
                form: this.$form.createForm(this),
                tableHeader: [
                    {
                        title: '编号',
                        scopedSlots: { customRender: 'serial' }
                    },
                    {
                        title: '规则类型',
                        dataIndex: 'type',
                        scopedSlots: { customRender: 'type' }
                    },
                    {
                        title: '规则条件',
                        dataIndex: 'condi',
                        scopedSlots: { customRender: 'condi' }
                    },
                    {
                        title: '规则参数',
                        dataIndex: 'param',
                        scopedSlots: { customRender: 'param' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                columnHeader: [
                    {
                        title: '编号',
                        scopedSlots: { customRender: 'serial' },
                        width: '8%'
                    },
                    {
                        title: '字段名称',
                        dataIndex: 'name',
                        scopedSlots: { customRender: 'name' },
                        width: '12%'
                    },
                    {
                        title: '字段类型',
                        dataIndex: 'type',
                        scopedSlots: { customRender: 'type' }
                    },
                    {
                        title: '字段描述',
                        dataIndex: 'comment',
                        scopedSlots: { customRender: 'comment' },
                        width: '15%'
                    },
                    {
                        title: '规则类型',
                        dataIndex: 'colRule',
                        scopedSlots: { customRender: 'colRule' },
                        width: '12%'
                    },
                    {
                        title: '规则条件',
                        dataIndex: 'colRuleCondi',
                        scopedSlots: { customRender: 'colRuleCondi' },
                        width: '15%'
                    },
                    {
                        title: '规则参数',
                        dataIndex: 'colRuleParam',
                        scopedSlots: { customRender: 'colRuleParam' },
                        width: '15%'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        scopedSlots: { customRender: 'action' },
                        width: '15%'
                    }
                ],
                tableRules: [],
                columnRules: [],
                cacheColumnList: [],
                cacheTableRuleTypeList: [],
                cacheTableRuleTypeCondiList: [],
                cacheTableRuleTypeCondiParamList: [],
                cacheColumnRuleTypeList: [],
                cacheColumnRuleTypeCondiList: [],
                cacheColumnRuleTypeCondiParamList: []
            }
        },
        created () {
            // get table rule type
            queryTableRuleType().then(res => {
                this.cacheTableRuleTypeList = res.data
            })

            // get table column type
            queryColumnRuleType().then(res => {
                this.cacheColumnRuleTypeList = res.data
            })
        },
        computed: {
        ...mapGetters(['userInfo'])
        },
        mounted () {
        },
        methods: {
            show (params, info) {
                this.tableRules = []
                this.visible = true
                this.info.tblName = params.tblName
                this.info.tblDesc = params.tblDescribe
                this.info.base = info.base
                JSON.parse(params.tabRule).forEach((item, index) => {
                    item.key = index
                    item.editable = false
                    this.tableRules.push(item)
                })
                queryTableColumn(this.info).then(res => {
                    this.cacheColumnList = res.data
                })
                queryColumnRule(this.info).then(res => {
                    const that = this
                    that.columnRules = []
                    res.data.forEach(item => {
                        if (item.colRule !== '' && item.colRule !== undefined) {
                            that.columnRules.push(item)
                        }
                    })
                })
            },
            handleCancel () {
                this.visible = false
                this.tableEditKey = ''
                this.tableAdding = false
                this.columnEditKey = ''
                this.columnAdding = false
            },
            handleTableRuleAdd () {
                this.tableAdding = true
                if (this.tableEditKey !== '') {
                    this.$message.warn('请先完成当前行的编辑')
                    return
                }
                const newData = [...this.tableRules]
                const newKey = newData.length
                const obj = { 'key': newKey, 'editable': true, 'condi': '', 'type': '', 'param': '' }
                newData.push(obj)
                this.tableRules = newData
                this.tableEditKey = newKey
            },
            handleTableRuleEdit (item) {
                this.editingBack = JSON.parse(JSON.stringify(item))
                const newData = [...this.tableRules]
                const target = newData.filter(item2 => item.key === item2.key)[0]
                this.tableEditKey = item.key
                if (target) {
                    this.$set(target, 'editable', true)
                    this.tableRules = newData
                }
            },
            handleTableRuleDelete (item) {
                const newData = [...this.tableRules]
                this.tableRules = newData.filter(item2 => item2.key !== item.key)
                this.handleTableRuleSave(item)
            },
            handleTableRuleSave (item) {
                if (item.type === '' || item.type === undefined) {
                    this.$message.error('请选择规则类型')
                    return
                } else if (item.condi === '' || item.condi === undefined) {
                    this.$message.error('请选择条件类型')
                    return
                } else if (item.param === '' || item.param === undefined) {
                    this.$message.error('请填写参数值')
                    return
                }
                const that = this
                this.tableEditKey = ''
                this.$set(item, 'editable', false)
                this.$set(item, 'param', item.param)
                const params = { ...that.info }
                params.tableRule = JSON.stringify(that.tableRules)
                cudTableRule(params).then(res => {
                    if (res.code === 0) {
                        that.$message.success(res.msg)
                    } else {
                        that.$message.error(res.msg)
                    }
                    that.$emit('refresh')
                    that.tableAdding = false
                })
            },
            handleTableRuleCancel (obj) {
                if (this.tableAdding === true) {
                    this.tableRules.pop()
                    this.tableEditKey = ''
                    this.tableAdding = false
                } else {
                    this.tableEditKey = ''
                    this.$set(obj, 'editable', false)
                    this.$set(obj, 'type', this.editingBack.type)
                    this.$set(obj, 'condi', this.editingBack.condi)
                    this.$set(obj, 'param', this.editingBack.param)
                }
            },

            handleColumnRuleAdd () {
                this.columnAdding = true
                if (this.columnEditKey !== '') {
                    this.$message.warn('请先完成当前行的编辑')
                    return
                }
                const newData = [...this.columnRules]
                const newKey = newData.length
                const obj = { 'key': newKey, 'editable': true, 'condi': '', 'type': '', 'param': '', 'column': '' }
                newData.push(obj)
                this.columnRules = newData
                this.columnEditKey = newKey
            },
            handleColumnRuleEdit (key) {
                const newData = [...this.columnRules]
                const target = newData.filter(item => key.key === item.key)[0]
                this.columnEditKey = key.key
                if (target) {
                    target.editable = true
                    this.columnRules = newData
                }
            },
            handleColumnRuleDelete (record) {
                const newData = [...this.columnRules]
                this.columnRules = newData.filter(item2 => item2.key !== record.key)
                this.handleColumnRuleSave(record)
            },
            handleColumnRuleCancel (obj) {
                if (this.columnAdding === true) {
                    this.columnRules.pop()
                    this.columnEditKey = ''
                    this.columnAdding = false
                } else {
                    this.columnEditKey = ''
                    this.$set(obj, 'editable', false)
                }
            },
            handleColumnRuleSave (item) {
                if (item.name === '' || item.name === undefined) {
                    this.$message.error('请选择规则字段')
                    return
                } else if (item.colRule === '' || item.colRule === undefined) {
                    this.$message.error('请选择规则类型')
                    return
                } else if (item.colRuleCondi === '' || item.colRuleCondi === undefined) {
                    this.$message.error('请选择规则条件')
                    return
                } else if (item.colRuleParam === '' || item.colRuleParam === undefined) {
                    this.$message.error('请输入规则参数')
                    return
                }
                const that = this
                this.columnEditKey = ''
                this.$set(item, 'editable', false)
                this.$set(item, 'param', item.param)
                const params = { ...that.info }
                params.columnRule = JSON.stringify(that.columnRules)
                cudColumnRule(params).then(res => {
                    if (res.code === 0) {
                        that.$message.success(res.msg)
                    } else {
                        that.$message.error(res.msg)
                    }
                    that.$emit('refresh')
                    that.tableAdding = false
                })
            },

            handleTableRuleTypeChange (type) {
                const that = this
                queryTableRuleTypeCondi({ 'type': type }).then(res => {
                    that.cacheTableRuleTypeCondiList = res.data
                })
            },
            handleTableRuleCondiChange (condi, record) {
                const that = this
                queryTableRuleTypeCondiParam({ 'type': record.type, 'condi': condi }).then(res => {
                    that.cacheTableRuleTypeCondiParamList = res.data
                })
            },
            handleColumnNameChange (name, record) {
                const newData = [...this.cacheColumnList]
                const target = newData.filter(item => name === item.name)[0]
                this.$set(record, 'type', target.type)
                this.$set(record, 'comment', target.comment)
                this.$set(record, 'name', target.name)
            },
            handleColumnRuleTypeChange (type) {
                const that = this
                queryColumnRuleTypeCondi({ 'type': type }).then(res => {
                    that.cacheColumnRuleTypeCondiList = res.data
                })
            },
            handleColumnRuleCondiChange (condi, type) {
                const that = this
                queryColumnRuleTypeCondiParam({ 'type': type, 'condi': condi }).then(res => {
                    that.cacheColumnRuleTypeCondiParamList = res.data
                })
            },

            runRule () {
                this.$message.success('规则调度成功')
                // runTableRule(this.info)
                // runColumnRule(this.info).then(res => {
                // })
            }
        }
    }
</script>