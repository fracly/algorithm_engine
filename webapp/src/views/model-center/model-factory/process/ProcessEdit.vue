<template>
  <!--新增修改模型组-->
  <a-modal :visible="visible" :title="type=='add'?'创建模型':'修改模型'" @cancel="() => { this.$emit('hideProcess',0) }" width="400px">
    <template slot="footer">
      <a-button key="back" @click="() => { this.$emit('hideProcess',0) }">
        返回
      </a-button>
      <a-button key="submit" type="primary" @click="handleOk">
        保存
      </a-button>
    </template>
    <a-form-model
      ref="ruleForm"
      :rules="rules"
      layout="vertical"
      class="ant-advanced-search-form"
      :model="params"
      style="text-align: left">
      <a-form-model-item label="模型名称:" prop="name">
        <a-input v-model="params.name"/>
      </a-form-model-item>
      <a-form-item label="模型描述:">
        <a-input v-model="params.desc" type="textarea"/>
      </a-form-item>
    </a-form-model>

  </a-modal>
</template>
<script>
  import { createProcess, editProcess } from '@/api/process'
  import { clone } from '@/utils/global'

  export default {
    data () {
      return {
        type: 'add',
        visible: true,
        labelCol: { lg: { span: 6 }, sm: { span: 6 } },
        wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
        params: {
          processDefinitionJson: { 'globalParams': [], 'tasks': [], 'timeout': 0 },
          name: '',
          desc: '',
          locations: {},
          connects: [],
          workGroup: '',
          id: ''
        },
        rules: {
          name: [
            { required: true, message: '模型名称不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    props: {
      process: {
        type: Object,
        default: () => {
          return {
            id: ''
          }
        }
      }
    },
    created () {
      if ('id' in this.process) {
        this.params = this.process
        this.type = 'update'
      } else {
        this.type = 'add'
      }
      this.params.workGroup = this.process.projectName
    },
    methods: {
      handleOk () {
        if (!this.verifyParams()) {
          return
        }
        if (this.type === 'add') {
          this.add()
        } else {
          this.update()
        }
      },
      /* 校验模型参数 */
      verifyParams () {
        let res = false
        this.$refs.ruleForm.validate(valid => {
          if (valid) {
            res = true
          } else {
            res = false
          }
        })
        return res
      },
      add () {
        const _that = this
        createProcess(this.params).then((res) => {
          if (res.code === 0) {
            _that.$message.success('创建模型成功')
            this.$emit('hideProcess', 1, res.data.id)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('创建模型失败！')
          this.$emit('hideProcess', 1)
        })
      },
      update () {
        const _that = this
        editProcess(this.params).then((res) => {
          if (res.code === 0) {
            _that.$message.success('更新模型成功')
            this.$emit('hideProcess', 1, res.data)
            this.$emit('childLoadTopo');
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('更新模型失败！')
          this.$emit('hideProcess', 1)
        })
      }
    }

  }
</script>
