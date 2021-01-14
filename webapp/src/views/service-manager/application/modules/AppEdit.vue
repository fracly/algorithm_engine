<template>
  <a-card>
    <a-form :form="form">
      <a-form-item label="应用名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-input
          placeholder="请输入应用名称，例如:科技处"
          v-decorator="['name', {rules: [{required: true, message: '应用名称不能为空，且长度为1-16个字符',min:1,max:16,whitespace:true}] }]"/>
      </a-form-item>
      <a-form-item label="应用类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-select
          show-search
          optionFilterProp="children"
          placeholder="请选择应用类型"
          v-decorator="['type',{rules: [{required: true, message: '应用类型必须选择'}]}]"
        >
          <a-select-option
            v-for="(item) in typeList"
            :key="item.code"
            :value="item.code">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="接口选择" :labelCol="labelCol" :wrapperCol="wrapperCol" >
        <a-button style="width: 100%;" @click="showChooseFiled">
          已选择<span class="filedChoose">{{ interfaces.length }}</span>个接口
        </a-button>
        <span v-show="interfaces.length>0">选择的接口:{{ inter.toString() }}</span>
        <a-input
          v-show="false"
          v-decorator="['interface', {rules: [{required: true, message: '选择的接口不能为空'}] }]"/>
        <!--字段选择模态框-->
        <a-modal :visible="interfacesChoose" title="接口选择" @cancel="showChooseFiled" width="850px">
          <template slot="footer">
            <a-button key="submit" type="primary" @click="saveChooseFiled">
              保存
            </a-button>
            <a-button key="back" @click="showChooseFiled">
              返回
            </a-button>
          </template>
          <field-selection v-if="interfacesChoose" :chooseField="interfaces" :FiledList="serversList" :typeList="serverList" ref="filedSelect"></field-selection>
        </a-modal>
      </a-form-item>
      <a-form-item label="应用描述" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <a-textarea
          placeholder="请输入应用描述"
          v-decorator="['desc']"/>
      </a-form-item>
      <a-form-item :wrapperCol="{span: 8, offset: 10}">
        <a-button @click="_close">返回</a-button>
        <a-button style="margin-left: 8px" type="primary" @click="_create">{{ info.id?'修改':'创建' }}</a-button>
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script>
  import FieldSelection from './FieldSelect'
  // import FieldSelection from '@/components/FieldSelection'
  import { createApp, interList, getAppInfo, getTypeList } from '@/api/service'

  export default {
    name: 'AppEdit',
    data () {
      return {
        typeList: [],
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 10 }, sm: { span: 10 } },
        form: this.$form.createForm(this),
        interfacesChoose: false,
        serversList: [],
        interfaces: [],
        inter: [],
        serverList: [],
        info: {
          status: 1,
          id: ''
        }
      }
    },
    components: { FieldSelection },
    methods: {
      showChooseFiled () {
        this.interfacesChoose = !this.interfacesChoose
      },
      saveChooseFiled () {
        this.interfaces = this.$refs.filedSelect.chooseFileds
        this.inter = []
        for (let i = 0; i < this.serversList.length; i++) {
          for (let j = 0; j < this.interfaces.length; j++) {
            if (this.serversList[i].id === this.interfaces[j]) {
              this.inter.push(this.serversList[i].name)
            }
          }
        }
        this.interfacesChoose = !this.interfacesChoose
      },
      _close () {
        this.$router.push('/resource/application/list')
      },
      // 新建应用
      _create () {
        const that = this
        const { form: { validateFields } } = this
        this.form.setFieldsValue({ interface: this.interfaces.toString() })
        validateFields((err, values) => {
          if (!err) {
            that.info.interfaces = that.interfaces.toString()
            for (const key in values) {
              that.info[key] = values[key]
            }

            createApp(that.info).then(res => {
              if (that.info.id) {
                that.$message.success('修改成功')
              } else {
                that.$message.success('创建成功')
              }
              this._close()
            }).catch(e => {
              this.$message.error(e || '')
            })
          }
        })
      },
      getInterList () {
        const that = this
        interList().then(res => {
          const result = res.data
          that.serversList = result.map(option => {
            return {
              name: option.service_name,
              type: option.service_code,
              comment: option.des,
              id: option.service_id.toString(),
              serverType: option.service_type
            }
          })
          if (this.$route.params.id) {
            getAppInfo({ applicationId: this.$route.params.id }).then(res => {
              that.info.id = this.$route.params.id
              const info = {
                name: res.data.name,
                type: res.data.type,
                desc: res.data.desc,
                interfaces: res.data.interfaces
              }
              this.form.setFieldsValue({ ...info })
              const interfaces = res.data.interfaces.split(',')
              this.interfaces = res.data.interfaces.split(',')
              for (let i = 0; i < that.serversList.length; i++) {
                for (let j = 0; j < interfaces.length; j++) {
                  if (that.serversList[i].id === interfaces[j]) {
                    that.inter.push(that.serversList[i].name)
                  }
                }
              }
            }).catch(e => {
              this.$message.error(e || '')
            })
          }
        })
      },
      getType () {
        getTypeList({ code: '02' }).then(res => {
          this.typeList = res.data
        })
      },
      getServiceType () {
        getTypeList({ code: '01' }).then(res => {
          this.serverList = res.data
        })
      }

    },
    mounted () {
    },
    created () {
      this.getInterList()
      this.getType()
      this.getServiceType()
    }
  }
</script>
<style lang="less" scoped>

</style>
