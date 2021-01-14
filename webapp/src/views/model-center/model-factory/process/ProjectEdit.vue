<template>
  <!--新增修改模型组-->
  <a-modal :visible="visible" :title="type=='add'?'模型组新增':'模型组修改'" @cancel="() => { this.$emit('hideProject',0) }" width="400px">
    <template slot="footer">
      <a-button key="back" @click="() => { this.$emit('hideProject',0) }">
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
      <a-form-model-item
        label="模型组名称"
        prop="projectName">
        <a-input v-model="params.projectName"/>
      </a-form-model-item>
      <a-form-model-item
        label="描述"
      >
        <a-input v-model="params.desc"/>
      </a-form-model-item>
    </a-form-model>

  </a-modal>
</template>
<script>
  import { createProject, updateProject } from '@/api/process'
  import { clone } from '@/utils/global'

  export default {
    data () {
      return {
        type: 'add',
        visible: true,
        labelCol: { lg: { span: 6 }, sm: { span: 6 } },
        wrapperCol: { lg: { span: 19 }, sm: { span: 19 } },
        params: {
          projectId: '',
          projectName: '',
          desc: ''
        },
        rules: {
          projectName: [
            { required: true, message: '模型组名称不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    props: {
      project: {
        type: Object,
        default: () => {
          return {
            id: ''
          }
        }
      }
    },
    created () {
      if (JSON.stringify(this.project) != '{}') {
        this.params = clone(this.project)
        this.type = 'update'
      } else {
        this.type = 'add'
      }
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
        createProject(this.params).then((res) => {
          if (res.code === 0) {
            _that.$message.success('创建模型组成功')
            this.$emit('hideProject', 1)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('创建模型组失败！')
          this.$emit('hideProject', 1)
        })
      },
      update () {
        const _that = this
        updateProject(this.params).then((res) => {
          if (res.code === 0) {
            _that.$message.success('更新模型组成功')
            this.$emit('hideProject', 1)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('更新模型组失败！')
          this.$emit('hideProject', 1)
        })
      }
    }

  }
</script>
