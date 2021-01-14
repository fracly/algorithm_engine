<template>
  <a-modal
    :title="title"
    class="data-modal"
    :width="800"
    :visible="visible"
    :confirmLoading="submitLoading"
    :maskClosable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-form :form="form">
      <a-form-item
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        label="服务名"
      >
        <a-select
          placeholder="请选择服务/编码"
          v-decorator="['resource', { rules: [{required: true, message: '服务必须选择'}] }]"
          :show-search="true"
          :disabled="!isAdd"
        >
          <a-select-option
            v-for="(item) in serversList"
            :key="item.service_code"
            :value="item.service_code">{{ item.service_name+"/"+item.service_code }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :required="true"
        label="阈值类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-radio-group v-decorator="['grade', {initialValue: 1, rules: [{required: true, message: '阈值类型必须选择'}] }]">
          <a-radio :value="1">QPS</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        :required="true"
        label="阈值"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-input
          placeholder="请输入阈值"
          v-decorator="['count', {rules: [{required: true, message: '阈值不能为空'}] }]"/>
      </a-form-item>
      <a-form-item
        :required="true"
        label="流控效果"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-radio-group v-decorator="['controlBehavior', {initialValue: 0, rules: [{required: true, message: '流控效果必须选择'}] }]">
          <a-radio :value="0">快速失败</a-radio>
<!--          <a-radio :value="1">Warm Up</a-radio>-->
<!--          <a-radio :value="2">排队等待</a-radio>-->
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
  import { interList, getToken, addRule, updateRule } from '@/api/service'

  export default {
    name: 'FlowControlEdit',
    components: { },
    data () {
      return {
        visible: false,
        submitLoading: false,
        isCreate: true,
        title: '',
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        form: this.$form.createForm(this),
        params: {},
        serversList: [],
        isAdd: false,
        reqData: {
          app: 'cloudconsumer', // 默认参数、
          clusterMode: false, // 默认参数
          limitApp: 'default', // 默认参数
          strategy: 0// 默认参数
        }
      }
    },
    created () {
      this.getInterList()
    },
    mounted () {
    },
    methods: {
      add () {
        this.form.resetFields()
        this.visible = true
        this.isCreate = true
        this.title = '新建规则'
        this.isAdd = true
      },
      edit (params) {
        this.form.resetFields()
        this.visible = true
        this.isCreate = false
        this.title = '编辑规则'
        this.isAdd = false
        this.$nextTick(() => {
          this.form.setFieldsValue({
            resource: params.resource,
            grade: params.grade,
            count: params.count,
            controlBehavior: params.controlBehavior
          })
        })
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
            getToken().then(res => {
                if (res) {
                  for (const key in values) {
                    that.reqData[key] = values[key]
                  }
                  that.reqData.count = parseInt(that.reqData.count)
                  const params = {}
                  params.seqId = Math.floor(Math.random() * 10000)
                  params.timeStamp = new Date().getTime()
                  params.reqData = that.reqData
                  params.token = res.access_token
                  if (that.isCreate) {
                    addRule(params).then((res) => {
                      if (res.code === 0) {
                        that.$message.success('新建流控规则成功')
                        that.$emit('ok')
                        that.$emit('refresh')
                      } else {
                        that.$message.error('新建流控规则失败:' + res.msg)
                      }
                      that.submitLoading = false
                      that.close()
                    }).catch(err => {
                      that.$message.error('新建流控规则失败:' + err)
                      that.submitLoading = false
                    })
                  } else {
                    updateRule(params).then((res) => {
                      if (res.code === 0) {
                        that.$message.success('编辑流控规则成功')
                        that.$emit('ok')
                        that.$emit('refresh')
                      } else {
                        that.$message.error('编辑流控规则失败:' + res.msg)
                      }
                      that.submitLoading = false
                      that.close()
                    }).catch(err => {
                      that.$message.error('编辑流控规则失败:' + err)
                      that.submitLoading = false
                    })
                  }
                } else {
                  this.$message.error(res.msg)
                }
              }).catch(e => {
                this.$message.error(e.msg || '')
              })
          }
        })
      },

      handleCancel () {
        this.close()
      },
      getInterList () {
        interList().then(res => {
          this.serversList = res.data
        })
      }
    }
  }
</script>
