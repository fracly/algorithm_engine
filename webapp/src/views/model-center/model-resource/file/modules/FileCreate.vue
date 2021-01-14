<template>
  <a-modal
    centered
    v-model="visible"
    @cancel="handleCancel"
    @ok="handleOk"
    :width="800"
    :maskClosable="false"
    :title="title"
    :zIndex="1"
  >
    <a-form-model :model="file" :rules="rules" ref="fileForm">
      <a-form-model-item
        label="文件名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="fileName"
      >
        <a-input
          placeholder="请输入文件名称"
          v-model="file.fileName"
          :disabled="!isAdd"
        />
      </a-form-model-item>
      <a-form-model-item
        label="文件格式"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        prop="suffix"
      >
        <a-select
          placeholder="请选择文件格式"
          v-model="file.suffix"
          @change="changeSuffix"
          :disabled="!isAdd"
        >
          <a-select-option
            v-for="(item) in fillTypeArr"
            :key="item"
            :value="item">{{ item }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item
        label="描述"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-textarea
          placeholder="请输入描述"
          v-model="file.desc"
          :disabled="!isAdd"
          :auto-size="{ minRows: 2 }"/>
      </a-form-model-item>
      <a-form-model-item
        label="文件内容"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        prop="content"
      >
        <div class="code-mirror-model">
          <a-textarea v-model="file.content" id="code-create-mirror" name="code-create-mirror"/>
        </div>
      </a-form-model-item>
    </a-form-model>

  </a-modal>
</template>

<script>
  import { createResourceFile, getViewResources, updateContent } from '@/api/resources'
  import codemirror from '@/utils/codemirror'
  let editor
  const handlerSuffix = {
    '.txt': 'textile',
    '.log': 'textile',
    '.sh': 'shell',
    '.conf': 'textile',
    '.cfg': 'textile',
    '.py': 'python',
    '.java': 'textile',
    '.sql': 'sql',
    '.hql': 'sql',
    '.xml': 'xml'
  }

  export default {
    name: 'FileCreate',
    data () {
      const checkContent = (rule, value, callback) => {
        value = editor.getValue()
        if (value === '') {
          callback(new Error('文件内容不能为空'))
        } else {
          this.$refs.fileForm.validateField('checkPass')
          callback()
        }
      }
      return {
        stepLoading: false,
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        fileList: [],
        visible: false,
        title: '',
        fillTypeArr: ['txt', 'log', 'sh', 'conf', 'cfg', 'py', 'java', 'sql', 'xml', 'hql'],
        file: {
          type: 'FILE',
          content: '',
          fileName: '',
          suffix: undefined,
          desc: ''
        },
        rules: {
          fileName: [{ required: true, message: '文件名称不能为空', whitespace: true }],
          suffix: [{ required: true, message: '文件格式必须选择' }],
          content: [{ required: true, validator: checkContent }]
        },
        isAdd: false,
        name: '',
        item: {},
        mode: 'python'
      }
    },
    methods: {
      add () {
        this.visible = true
        this.isAdd = true
        this.title = '创建文件'
        this.file = {
          type: 'FILE',
            content: '',
            fileName: '',
            suffix: undefined,
            desc: ''
        }
        this.$nextTick(() => {
          setTimeout(() => {
            this.handlerEditor()
          }, 10)
        })
      },
      edit (item) {
        this.visible = true
        this.isAdd = false
        this.title = '编辑文件内容'
        const self = this
        this.item = item
        const i = item.fileName.lastIndexOf('.')
        const a = item.fileName.substring(i, item.fileName.length)
        this.mode = handlerSuffix[a]
        this.file = item
        this.file.suffix = this.mode
        getViewResources({
          id: item.id,
          skipLineNum: 0,
          limit: 2000
        }).then(res => {
          if (res.code === 0) {
            self.name = res.data.alias.split('.')[0]
            this.$nextTick(() => {
              const content = res.data.content ? res.data.content + '\n' : ''
              setTimeout(() => {
                self.handlerEditor().setValue(content)
                editor.refresh()
              }, 100)
            })
          } else {
            self.$message.error(res.msg)
          }
        }).catch(e => {
          self.$message.error(e)
        })
      },
      handleOk () {
        const self = this
        this.stepLoading = true
        this.file.content = editor.getValue()
        this.$refs.fileForm.validate(valid => {
          if (valid) {
            if (self.isAdd) {
              createResourceFile(self.file).then(res => {
                if (res.code === 0) {
                  self.$message.success('文件创建成功')
                  self.handleCancel()
                  self.$emit('refresh')
                } else {
                  self.$message.error(res.msg)
                }
              }).catch(e => {
                self.$message.error(e)
              })
            } else {
              updateContent({
                id: self.item.id,
                content: editor.getValue()
              }).then(res => {
                if (res.code === 0) {
                  self.$message.success('文件编辑成功')
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
        this.destroyed()
      },
      handlerEditor () {
        // editor
        editor = codemirror('code-create-mirror', {
          mode: this.mode,
          readOnly: false
        })

        this.keypress = () => {
          if (!editor.getOption('readOnly')) {
            editor.showHint({
              completeSingle: false
            })
          }
        }
        // Monitor keyboard
        editor.on('keypress', this.keypress)
        return editor
      },
      destroyed () {
        editor.toTextArea() // 卸载
        editor.off(('.code-create-mirror'), 'keypress', this.keypress)
      },
      changeSuffix (val) {
        editor.setOption('mode', handlerSuffix['.' + val])
      }
    },
    mounted () {
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
