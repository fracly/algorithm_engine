<template>
  <a-modal
    title="新增数据资产"
    class="data-modal"
    :width="800"
    :visible="visible"
    :confirmLoading="submitLoading"
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-form :form="form">
      <a-form-item
        :required="true"
        label="表分类"
      >
        <a-input type="hidden" v-decorator="['layer', {}]" disabled></a-input>
        <a-input type="hidden" v-decorator="['topic', {}]"></a-input>
        <a-input v-decorator="['tableCategory', {rules: [{required: true}]}]" disabled></a-input>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表中文名"
      >
        <a-input
          :required="true"
          v-decorator="['name', {rules: [{required: true, min: 1, message: '请输入至少1个字符的名称！'}]}]"/>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表描述"
      >
        <a-textarea
          v-decorator="['description', {rules: [{required: true, min: 1, message: '请输入至少1个字符的描述！'}]}]"
          placeholder="请输入表描述"
          rows="2"
        >
        </a-textarea>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表创建方式"
        v-show="isCreate"
      >
        <a-radio-group
          name="radioGroup"
          v-decorator="['createMethod', {}]">
          <a-radio value="1">建表语句</a-radio>
          <a-radio value="2">字段信息</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item>
        <a-textarea
          v-show="form.getFieldValue('createMethod') == '1' && isCreate"
          rows="4"
          placeholder="请输入建表语句"
          v-decorator="['createSql',
                        {rules: [{ message: '请输入建表语句，示例：<br /> create table xx(col string, ...)' }]}
          ]"></a-textarea>
        <div v-show="form.getFieldValue('createMethod') == '2' && isCreate">
          <a-input
            style="width:50%"
            v-decorator="['tableName', {}]"
            placeholder="请输入表名(英文)"></a-input>
          <a-input-group
            v-for="(item, index) in columnList"
            :key="index"
            compact>
            <a-input style="width:30%" placeholder="请输入字段名(英文)" v-model="item.name"></a-input>
            <a-select style="width:30%;margin-left: 5px" placeholder="请选择字段类型" v-model="item.type">
              <a-select-option
                :key="index"
                v-for="(columnType, index) in columnTypeList"
                :value="columnType">{{ columnType }}
              </a-select-option>
            </a-select>
            <a-input
              style="width:30%;margin-left: 5px"
              placeholder="请输入字段描述"
              v-model="item.comment"></a-input>
            <a-button style="margin-left: 5px" icon="delete" @click="handleDelete(index)"/>
          </a-input-group>
          <a-button style="margin-left: 10px" @click="handleAddColumn" type="primary">添加字段
            <a-icon type="plus-circle"/>
          </a-button>
        </div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>

    import { createTable, updateTable } from '@/api/model'
    import { testContainsHZ } from '@/utils/util'
    import { mapGetters } from 'vuex'

    const columnTypeList = ['STRING', 'BIGINT', 'INT', 'FLOAT', 'DOUBLE', 'DECIMAL', 'TIMESTAMP', 'DATE']

    export default {
        name: 'TableEdit',
        components: { },
        data () {
            return {
                visible: false,
                submitLoading: false,
                isCreate: true,
                columnList: [],
                tableName: '',
                id: '',
                // 进度条控制
                form: this.$form.createForm(this),
                params: {},
                datasourceId: 0,
                columnTypeList
            }
        },
        created () {
        },
        mounted () {
        },
        computed: {
            ...mapGetters(['nickname', 'userId'])
        },
        methods: {
            add (params) {
                this.form.resetFields()
                this.visible = true
                this.isCreate = true
                this.datasourceId = params.datasourceId
                this.$nextTick(() => {
                    this.form.setFieldsValue({ 'layer': params.layer, 'topic': params.topic, 'tableCategory': params.tableCategory })
                })
            },
            edit (params) {
                this.form.resetFields()
                this.visible = true
                this.isCreate = false
                this.datasourceId = params.datasourceId
                this.$nextTick(() => {
                    this.form.setFieldsValue({ 'tableCategory': params.layerCn + '-' + params.topic,
                        'layer': params.layer,
                        'topic': params.topic,
                        'description': params.tblDescribe,
                        'name': params.area,
                        'tableName': params.tblName })
                })
            },
            handleDelete (i) {
                this.columnList.splice(i, 1)
            },
            close () {
                this.$emit('ok')
                this.visible = false
            },
            handleOk () {
                const that = this
                this.form.validateFields((err, values) => {
                    if (!err) {
                        that.submitLoading = true
                        if (that.isCreate) {
                            if (testContainsHZ(values.tableName)) {
                                that.$message.error('表名不能包含中文')
                                that.submitLoading = false
                                return
                            }
                            for (let i = 0; i < that.columnList.length; i++) {
                                if (testContainsHZ(that.columnList[i].name)) {
                                    that.$message.error('字段名不能包含中文')
                                    that.submitLoading = false
                                    return
                                }
                            }
                            const formData = new FormData()
                            formData.append('levelName', values.layer + '_' + values.topic)
                            // 根据字段信息拼出建表语句
                            if (values.createMethod === '2') {
                                values.createSql = 'CREATE TABLE IF NOT EXISTS ' + values.tableName + '('
                                for (let i = 0; i < this.columnList.length; i++) {
                                    const obj = this.columnList[i]
                                    if (i === this.columnList.length - 1) {
                                        values.createSql = values.createSql + obj.name + ' ' + obj.type + ' comment \'' + obj.comment + '\''
                                    } else {
                                        values.createSql = values.createSql + obj.name + ' ' + obj.type + ' comment \'' + obj.comment + '\','
                                    }
                                }
                                values.createSql = values.createSql + ') '
                            }
                            values.createSql = values.createSql + ' STORED AS PARQUET'
                            formData.append('tableStatement', values.createSql)
                            const tmp = values.createSql.split('(')[0].split(' ')
                            formData.append('tableName', tmp[tmp.length - 1])
                            formData.append('applicationName', values.name)
                            formData.append('tableComment', values.description)
                            formData.append('dataSourceId', that.datasourceId)
                            formData.append('userId', that.userId)
                            formData.append('userName', that.nickname)
                            createTable(formData).then((res) => {
                                if (res.code === 0) {
                                    that.$message.success('创建表成功')
                                    that.$emit('ok')
                                } else {
                                    that.$message.error('创建表失败')
                                }
                                that.submitLoading = false
                                that.close()
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('创建表失败！')
                                that.submitLoading = false
                            })
                        } else {
                            // 更新表信息
                            const params = {}
                            params.area = values.name
                            params.tblName = values.tableName
                            params.tblDescribe = values.description
                            params.layer = values.layer
                            params.topic = values.topic
                            updateTable(params).then(res => {
                                if (res !== 0) {
                                    that.$message.success('更新表信息成功')
                                } else {
                                    that.$message.error('更新表信息失败')
                                }
                                that.submitLoading = false
                                that.close()
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('更新表失败！')
                                that.submitLoading = false
                            })
                        }
                    }
                })
            },
            handleCancel () {
                this.close()
            },
            handleAddColumn: function () {
                this.columnList.push({ 'name': '', 'type': '', 'comment': '' })
            }
        }
    }
</script>
<style>
  .data-modal .ant-form-item {
    margin-bottom: 0px;
  }
</style>
