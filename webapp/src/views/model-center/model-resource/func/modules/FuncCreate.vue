<template>
  <a-modal
    centered
    v-model="visible"
    @cancel="handleCancel"
    @ok="handleOk"
    :width="800"
    :maskClosable="false"
    :title="title"
  >
    <a-form-model :model="func" :rules="rules" ref="funcForm">
      <a-form-model-item
        label="类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="type"
      >
        <a-radio-group v-model="func.type">
          <a-radio value="HIVE">HIVE UDF</a-radio>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item
        label="函数名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="funcName"
      >
        <a-input
          placeholder="请输入函数名称"
          v-model="func.funcName"
        />
      </a-form-model-item>
      <a-form-model-item
        label="包名类名"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="className"
      >
        <a-input
          placeholder="请输入包名类名"
          v-model="func.className"
        />
      </a-form-model-item>
      <a-form-model-item
        label="参数"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-input
          placeholder="请输入参数"
          v-model="func.argTypes"
        />
      </a-form-model-item>
      <a-form-model-item
        label="UDF资源"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="resourceId"
      >
        <a-select
          placeholder="请选择UDF"
          v-model="func.resourceId"
        >
          <a-select-option
            v-for="(item) in udfResourceList"
            :key="item.id"
            :value="item.id">{{ item.alias }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item
        label="使用说明"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-textarea
          placeholder="请输入使用说明"
          v-model="func.desc"
          :auto-size="{ minRows: 2 }"/>
      </a-form-model-item>
    </a-form-model>

  </a-modal>
</template>

<script>
  import { createUdfFunc, updateUdfFunc, getResourcesList } from '@/api/resources'

  export default {
    name: 'FileCreate',
    data () {
      return {
        stepLoading: false,
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        visible: false,
        title: '',
        udfResourceList: [],
        func: {
          type: 'HIVE',
          className: '',
          funcName: '',
          desc: '',
          argTypes: '',
          resourceId: undefined,
          database: ''
        },
        rules: {
          type: [{ required: true, message: '类型不能为空' }],
          funcName: [{ required: true, message: '函数不能为空' }],
          className: [{ required: true, message: '包名类名不能为空' }],
          resourceId: [{ required: true, message: 'UDF资源不能为空' }]

        },
        isAdd: false,
        name: ''
      }
    },
    methods: {
      add () {
        this.visible = true
        this.isAdd = true
        this.title = '创建函数'
        this.func = {
          type: 'HIVE',
            className: '',
            funcName: '',
            desc: '',
            argTypes: '',
            resourceId: undefined,
            database: ''
        }
      },
      edit (item) {
        this.visible = true
        this.isAdd = false
        this.title = '编辑函数'
        this.func = item
      },
      handleOk () {
        const self = this
        this.stepLoading = true
        this.$refs.funcForm.validate(valid => {
          if (valid) {
            if (self.isAdd) {
              createUdfFunc(self.func).then(res => {
                if (res.code === 0) {
                  self.$message.success('函数创建成功')
                  self.handleCancel()
                  self.$emit('refresh')
                } else {
                  self.$message.error(res.msg)
                }
              }).catch(e => {
                self.$message.error(e)
              })
            } else {
              updateUdfFunc(self.func).then(res => {
                if (res.code === 0) {
                  self.$message.success('函数编辑成功')
                  self.handleCancel()
                  self.$emit('refresh')
                } else {
                  self.$message.error(res.msg)
                }
              }).catch(e => {
                self.$message.error(e)
              })
            }
          } else {
            console.log('error submit!!')
            return false
          }
        })
      },
      handleCancel () {
        this.visible = false
        this.$emit('cancel')
      },
      getUdfList () {
        getResourcesList({ type: 'UDF' }).then(res => {
        this.udfResourceList = res.data
        }).catch(e => {
          self.$message.error(e)
        })
      }
    },
    created () {
     this.getUdfList()
    }
  }
</script>
<style lang="less">
  .CodeMirror {
    border:1px solid #DDDEDD;
    border-radius: 3px;
  }
  .name {
      text-align: center;
      position: relative;
      font-size: 30px;
    }
</style>
