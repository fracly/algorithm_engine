<template>
  <a-modal
    :title="title"
    :width="600"
    :visible="visible"
    :confirmLoading="loading"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }">
        <a-form-item  label="姓名">
          <a-input v-decorator="['userCNName', {rules: [{required: true, message: '请输入姓名'}]}]" />
          <a-input v-decorator="['id']" v-show="false"/>
        </a-form-item>
        <a-form-item v-show="isCreate" label="账号">
          <a-input v-decorator="['userName', {rules: [{required: true, message: '请输入账号'}]}]" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-if="isCreate" type="password" v-decorator="['userPassword', {rules: [{required: true, message: '请输入密码'}]}]" />
          <a-input v-else type="password" v-decorator="['userPassword', {rules: [{required: false, message: '请输入密码'}]}]" />
        </a-form-item>
        <a-form-item label="租户">
          <a-select show-search optionFilterProp="children" placeholder="请选择租户" v-decorator="['tenantId', {rules: [{required: true, message: '请选择租户'}]}]">
            <a-select-option
              :key="tenant.id"
              v-for="(tenant) in tenantList"
              :value="tenant.id">{{ tenant.tenantName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="队列">
          <a-select show-search optionFilterProp="children" placeholder="请选择队列" v-decorator="['queue', {rules: [{required: true, message: '请选择队列'}]}]">
            <a-select-option
              :key="queue.id"
              v-for="(queue) in queueList"
              :value="queue.queueName">{{ queue.queueName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="手机">
          <a-input :max-length="11" v-decorator="['phone', {rules: [{required: true, message: '请输入手机'}]}]" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-decorator="['email']" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-decorator="['desc', {rules: [{required: true, message: '请输入用户描述'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { systemUserCreate, systemUserUpdate, queryTenantList, queryQueueList } from '@/api/system'
  import md5 from 'md5'

// 表单字段
const fields = ['id', 'userCNName', 'userName', 'phone', 'email', 'desc', 'tenantId', 'queue']

export default {
  data () {
    return {
        visible: false,
        loading: false,
        title: '',
        isCreate: true,
        form: this.$form.createForm(this),
        tenantList: [],
        queueList: []
    }
  },
  methods: {
      show () {
          this.isCreate = true
          this.title = '新建用户'
          this.visible = true
          this.form.resetFields()
      },
      edit (item) {
          this.isCreate = false
          this.title = '编辑用户'
          this.visible = true
          const that = this
          that.$nextTick(() => {
              that.form.setFieldsValue(item)
          })
      },
      handleOk () {
          this.loading = true
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  if (values.userPassword !== null && values.userPassword !== undefined) {
                      const userPassword = values.userPassword
                      values.userPassword = md5(userPassword)
                  }
                  if (values.id > 0) {
                      systemUserUpdate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success(res.msg)
                              this.$emit('refresh')
                              this.visible = false
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      systemUserCreate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success(res.msg)
                              this.$emit('refresh')
                              this.visible = false
                          } else {
                              this.$message.error(res.msg)
                          }
                      })
                  }
                  this.loading = false
              } else {
                  this.loading = false
              }
          })
      },
      handleCancel () {
          this.visible = false
      }
  },
  created () {
    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))
      const that = this
      queryTenantList({ 'pageNo': 1, 'pageSize': 10000 }).then(res => {
          that.tenantList = res.data.totalList
      })
      queryQueueList({ 'pageNo': 1, 'pageSize': 10000 }).then(res => {
          that.queueList = res.data.totalList
      })
  }
}
</script>
