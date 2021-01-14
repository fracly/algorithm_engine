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
        :required="true"
        label="名称"
      >
        <a-input type="hidden" v-decorator="['layer', {}]"></a-input>
        <a-input type="hidden" v-decorator="['topic', {}]"></a-input>
        <a-input v-decorator="['newTopic', {rules: [{required: true}]}]"></a-input>
      </a-form-item>
      <a-form-item
        :required="true"
        label="序号"
      >
        <a-input
          :required="true"
          v-decorator="['sort', {rules: [{required: true}]}]"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>

    import { createTopic, updateTopic } from '@/api/model'

    export default {
        name: 'LayerEdit',
        components: { },
        data () {
            return {
                visible: false,
                submitLoading: false,
                isCreate: true,
                title: '',
                form: this.$form.createForm(this),
                params: {}
            }
        },
        created () {
        },
        mounted () {
        },
        methods: {
            add (params) {
                this.title = '新建行业模型目录'
                this.form.resetFields()
                this.visible = true
                this.isCreate = true
                this.$nextTick(() => {
                    this.form.setFieldsValue({ 'layer': params.key.split('-')[0] })
                })
            },
            edit (params) {
                this.title = '编辑行业模型目录'
                this.form.resetFields()
                this.visible = true
                this.isCreate = false
                this.$nextTick(() => {
                    this.form.setFieldsValue({
                        'layer': params.key.split('-')[0],
                        'topic': params.title,
                        'newTopic': params.title,
                        'sort': params.sort
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
                        if (that.isCreate) {
                            values.topic = values.newTopic
                            createTopic(values).then((res) => {
                                if (res === 0) {
                                    that.$message.error('新建行业模型目录失败')
                                } else {
                                    that.$message.success('新建行业模型目录成功')
                                    that.$emit('ok')
                                    that.$emit('refresh')
                                }
                                that.submitLoading = false
                                that.close()
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('新建行业模型目录失败！')
                                that.submitLoading = false
                            })
                        } else {
                            updateTopic(values).then(res => {
                                if (res.code === 0) {
                                    that.$message.success('更新行业模型目录成功')
                                    that.$emit('refresh')
                                } else {
                                    that.$message.error('更新行业模型目录失败')
                                }
                                that.submitLoading = false
                                that.close()
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('更新行业模型目录失败！')
                                that.submitLoading = false
                            })
                        }
                    }
                })
            },
            handleCancel () {
                this.close()
            }
        }
    }
</script>
