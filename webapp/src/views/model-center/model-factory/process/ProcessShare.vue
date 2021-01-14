<template>
  <!--共享区内存模型-->
  <a-modal :visible="visible" title="另存为" @cancel="() => { this.$emit('hideShare') }" width="400px">
    <template slot="footer">
      <a-button key="back" @click="() => { this.$emit('hideShare') }">
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
      <a-form-model-item label="模型描述:">
        <a-input v-model="params.desc" type="textarea"/>
      </a-form-model-item>
      <a-form-model-item label="模型分组:" prop="workGroup">
        <a-select v-model="params.workGroup" show-search optionFilterProp="children">
          <a-select-option
            v-for="(item) in groupsList"
            :key="item.id"
            :value="item.name">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
    </a-form-model>

  </a-modal>
</template>
<script>
  import { createProcess, getAllWorkGroup, loadTopo } from '@/api/process'

  export default {
    data () {
      return {
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
          name: [{ required: true, message: '模型名称不能为空', trigger: 'blur' }],
          workGroup: [{ required: true, message: '模型分组不能为空' }]
        },
        groupsList: []
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
      this.params.name = this.process.name
      this.params.desc = this.process.name
      this.params.workGroup = ''
      getAllWorkGroup().then(res => {
        this.groupsList = res.data
      })
      loadTopo({ 'processId': this.process.id, 'workGroup': this.process.projectName }).then(res => {
        this.params.processDefinitionJson = JSON.parse(res.data.processDefinitionJson)
        this.params.locations = JSON.parse(res.data.locations)
        this.params.connects = JSON.parse(res.data.connects)
      })
    },
    methods: {
      handleOk () {
        if (!this.verifyParams()) {
          return
        }
        this.add()
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
            _that.$message.success('另存为模型成功')
            let id = ''
            for (let i = 0; i < _that.groupsList.length; i++) {
              if (_that.params.workGroup === _that.groupsList[i].name) {
                id = _that.groupsList[i].id
              }
            }
            this.$emit('hideShare', id, _that.params.workGroup)
          } else {
            _that.$message.error(res.msg)
          }
        }).catch(err => {
          console.log(err)
          _that.$message.error('另存为模型失败！')
          this.$emit('hideShare')
        })
      }
    }

  }
</script>
