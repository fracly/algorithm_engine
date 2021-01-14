<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="loading"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-spin :spinning="loading">
      <a-form :form="form">
        <a-form-item  label="角色名称">
          <a-input v-decorator="['name', {rules: [{required: true, message: '请输入角色名称'}]}]" />
          <a-input v-decorator="['id', {rules: [{required: false}]}]" v-show="false" />
        </a-form-item>
        <a-form-item v-show="isCreate" label="角色代码">
          <a-input v-decorator="['code', {rules: [{required: true, message: '请输入角色代码'}]}]" />
        </a-form-item>
        <a-form-item label="角色描述">
          <a-textarea v-decorator="['desc', {rules: [{required: true, message: '请输入角色描述'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
    import { systemRoleCreate, systemRoleUpdate } from '@/api/system'

    // 表单字段
    const fields = ['name', 'code', 'desc']

    export default {
        data () {
            return {
                title: '',
                visible: false,
                loading: false,
                isCreate: false,
                form: this.$form.createForm(this)
            }
        },
        methods: {
            show () {
                this.isCreate = true
                this.title = '新建角色'
                this.form.resetFields()
                this.visible = true
            },
            edit (item) {
                this.isCreate = false
                this.title = '编辑角色'
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
                        if (values.id > 0) {
                            systemRoleUpdate(values).then(res => {
                                if (res.code === 0) {
                                    this.$message.success(res.msg)
                                    this.visible = false
                                    this.$emit('refresh')
                                } else {
                                    this.$message.error(res.msg)
                                }
                                this.$emit('refresh')
                            })
                        } else {
                            systemRoleCreate(values).then(res => {
                                if (res.code === 0) {
                                    this.$message.success(res.msg)
                                    this.visible = false
                                    this.$emit('refresh')
                                } else {
                                    this.$message.error(res.msg)
                                }
                            })
                        }
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
        }
    }
</script>
