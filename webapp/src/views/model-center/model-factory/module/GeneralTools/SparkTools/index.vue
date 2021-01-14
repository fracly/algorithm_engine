<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
    <a-form-item label="程序类型:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-select v-model="params.programType" :defaultValue="params.programType" style="width: 100px">
        <a-select-option key="SCALA" value="SCALA">SCALA</a-select-option>
        <a-select-option key="JAVA" value="JAVA">JAVA</a-select-option>
        <a-select-option key="PYTHON" value="PYTHON">PYTHON</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="主函数的class:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.mainClass" type="textarea" autosize="{ minRows: 2, maxRows: 6 }"/>
    </a-form-item>

    <a-form-item label="主jar包:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-select
        show-search
        placeholder="请选择主jar包"
        v-model="params.mainJar.res"
        dropdownClassName="menuStyle"
        :dropdownStyle="{overflow: 'scroll'}"
      >
        <a-select-option
          v-for="(item,index) in resources"
          :title="item.alias"
          :key="index.id"
          :value="item.alias">
          {{ item.alias }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item
      label="部署方式:"
      :labelCol="labelCol"
      :wrapperCol="wrapperCol">
      <a-radio-group v-model="params.deployMode" >
        <a-radio value="cluster">
          cluster
        </a-radio>
        <a-radio value="client">
          client
        </a-radio>
        <a-radio value="local">
          local
        </a-radio>
      </a-radio-group>
    </a-form-item>

    <a-form-item label="Driver内核数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.driverCores" />
    </a-form-item>
    <a-form-item label="Driver内存数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.driverMemory"/>
    </a-form-item>
    <a-form-item label="Executor数量:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.executorCores"/>
    </a-form-item>
    <a-form-item label="Executor内存数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.executorMemory"/>
    </a-form-item>
    <a-form-item label="Executor内核数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-input v-model="params.numExecutors"/>
    </a-form-item>
    <a-form-item label="命令行参数:" type="textarea" autosize="{ minRows: 2, maxRows: 6 }">
      <a-input v-model="params.mainArgs"/>
    </a-form-item>
    <a-form-item label="其他参数:" type="textarea" autosize="{ minRows: 2, maxRows: 6 }">
      <a-input v-model="params.others"/>
    </a-form-item>

    <a-form-item label="资源:">
      <a-select
        show-search
        mode="multiple"
        placeholder="请选择资源"
        v-model="params.resourceList">
        <a-select-option v-for="(item,index) in resources" :key="item.alias">
          {{ item.alias }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="自定义参数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div @click="showCustomParams">
        <a-icon type="plus-circle" style="color:#429FE1"/><span v-if="params.localParams.length!=0">已添加{{ params.localParams.length }}条自定义参数</span>
      </div>
      <a-modal
        v-if="customParamsStaus"
        :visible="customParamsStaus"
        destroyOnClose
        title="自定义参数"
        @cancel="showCustomParams"
        width="650px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveCustomParams">
            保存
          </a-button>
          <a-button key="back" @click="showCustomParams">
            返回
          </a-button>
        </template>
        <custom-params :CustomParams="params.localParams" ref="customParams" ></custom-params>
      </a-modal>

    </a-form-item>

  </a-form-model>
</template>

<script>
  import customParams from '@/views/model-center/model-factory/module/GeneralTools/SparkTools/customParams'
  import codemirror from '@/utils/codemirror'
  import { getResources } from '@/api/process'
  import { clone } from '@/utils/global'

  let editor
  export default {
    data () {
      return {
        labelCol: { span: 7 },
        wrapperCol: { span: 17 },
        resources: [],
        params: {
          mainClass: '',
          mainJar: { res: '' },
          deployMode: '',
          resourceList: [],
          localParams: [],
          driverCores: 1,
          driverMemory: '512M',
          numExecutors: 2,
          executorMemory: '2G',
          executorCores: 2,
          mainArgs: '',
          others: '',
          programType: 'SCALA'
        },
        customParamsStaus: false
      }
    },
    components: {
      customParams
    },
    props: {
      tasks: {
        type: Object,
        default: () => {
          return {
            id: 'default',
            label: ''
          }
        }
      }
    },
    /* 初始化参数 */
    created () {
      if (JSON.stringify(this.tasks) != '{}') {
        this.params = this.tasks
      }
      const _that = this
      getResources({ 'type': 'FILE' }).then(res => {
        _that.resources = res.data
      })
    },
    mounted () {
      this.handlerEditor()
    },
    methods: {
      test () {
        debugger
      },
      saveCustomParams () {
        const localParams = this.$refs.customParams.LocalParams
        for (var i = 0; i < localParams.length; i++) {
          if ((localParams[i].prop == '' || localParams[i].prop == null) && this.customParamsStaus) {
            this.$message.error('props参数不能为空')
            return
          }
        }
        this.params.localParams = clone(localParams)
        this.customParamsStaus = !this.customParamsStaus
      },
      showCustomParams () {
        this.customParamsStaus = !this.customParamsStaus
      },
      handlerEditor () {
        const _that = this
        // editor
        editor = codemirror('code-shell-mirror', {
          mode: 'shell',
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
        editor.on('change', function () {
          _that.params.rawScript = editor.getValue()
        })
        editor.setValue(this.params.rawScript)
        return editor
      }
    }
  }

  function uuid () {
    var s = []
    var hexDigits = '0123456789abcdef'
    for (var i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1)
    }
    s[14] = '4' // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1) // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = '-'

    var uuid = s.join('')
    return uuid
  }
</script>
<style lang="less">
  .ant-form-item {
    margin-bottom: 4px!important;
  }

  /deep/.ant-modal-body {
    padding: 5px!important;
  }
  .menuStyle {
  .ant-select-dropdown-menu-item {
    overflow: unset!important;
  }
  }
</style>
