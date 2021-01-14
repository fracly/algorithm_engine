<template>
  <a-modal
    :title="title"
    class="data-modal"
    :width="800"
    :visible="visible"
    :confirmLoading="submitLoading"
    :maskClosable="false"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-form :form="form">
      <a-form-item
        :required="true"
        label="表层级"
      >
        <a-input type="hidden" v-decorator="['layer', {}]" disabled></a-input>
        <a-input v-decorator="['tableCategory', {rules: [{required: true}]}]" disabled></a-input>
      </a-form-item>
      <a-form-item
        :required="true"
        label="业务主题"
      >
        <a-select show-search optionFilterProp="children" placeholder="请选择业务主题" v-decorator="['topic', {rules: [{required: true, message: '请选择业务主题'}]}]">
          <a-select-option
            :key="topic.id"
            v-for="(topic) in topicList"
            :value="topic.layer">{{ topic.layer }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表中文名"
      >
        <a-input
          :required="true"
          v-decorator="['name', {rules: [{required: true, min: 1, message: '请输入至少1个字符的名称！'}]}]"/>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表描述"
      >
        <a-textarea
          v-decorator="['description', {rules: [{required: true, min: 1, message: '请输入至少1个字符的描述！'}]}]"
          placeholder="请输入表描述"
          rows="2"
        >
        </a-textarea>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表类型"
      >
          <a-checkbox-group  v-model="tableTypes">
                <a-checkbox value="hive">
                  hive
                </a-checkbox>
                <a-checkbox value="mysql">
                  mysql
                </a-checkbox>
                <a-checkbox value="es">
                  es
                </a-checkbox>
          </a-checkbox-group>
      </a-form-item>
      <a-form-item
        :required="true"
        label="表创建方式"
        v-show="isCreate"
      >
        <a-radio-group
          name="radioGroup"
          v-decorator="['createMethod', {}]">
          <a-radio value="1" :disabled="tableTypes.length > 1 || tableTypes.includes('es')" >建表语句</a-radio>
          <a-radio value="2"  :disabled="false" >字段信息</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item>
        <a-textarea
          v-show="form.getFieldValue('createMethod') == '1' && isCreate"
          rows="4"
          placeholder="请输入建表语句"
          v-decorator="['createSql',
                        {rules: [{ message: '请输入建表语句，示例：<br /> create table xx(col string, ...)' }]}
          ]"></a-textarea>
        <div v-show="form.getFieldValue('createMethod') == '2' && isCreate">
          <a-input
            style="width:50%"
            v-decorator="['tableName', {}]"
            placeholder="请输入表名(英文)"></a-input>
          <a-input-group
            v-for="(item, index) in columnList"
            :key="index"
            compact>
            <a-input style="width:30%" placeholder="请输入字段名(英文)" v-model="item.name"></a-input>
            <a-select show-search optionFilterProp="children" style="width:30%;margin-left: 5px" placeholder="请选择字段类型" v-model="item.type">
              <a-select-option
                :key="index"
                v-for="(columnType, index) in columnTypeList"
                :value="columnType.value">{{ columnType.name }}
              </a-select-option>
            </a-select>
            <a-input
              style="width:30%;margin-left: 5px"
              placeholder="请输入字段描述"
              v-model="item.comment"></a-input>
            <a-button style="margin-left: 5px" icon="delete" @click="handleDelete(index)"/>
          </a-input-group>
          <a-row >
            <a-button style="margin-left: 0px" @click="handleAddColumn" type="primary">添加字段
              <a-icon type="plus-circle"/>
            </a-button>
            <a-button style="margin-left: 10px" @click="openMysqlmodel" type="primary" v-if="tableTypes.includes('mysql')">mysql字段配置
              <a-icon type="plus-circle"/>
            </a-button>
            <a-button style="margin-left: 10px" @click="openhivemodel" type="primary" v-if="tableTypes.includes('hive')">hive字段配置
              <a-icon type="plus-circle"/>
            </a-button>
            <a-button style="margin-left: 10px" @click="openEsmodel" type="primary" v-if="tableTypes.includes('es')">es字段配置
              <a-icon type="plus-circle"/>
            </a-button>
          </a-row>
        </div>
        <!--hive字段配置弹出框-->
        <a-modal v-if="customParamsStaus" :visible="customParamsStaus" destroyOnClose title="hive字段配置" :closable="false" width="600px">
          <template slot="footer">
            <a-button key="submit" type="primary" @click="saveHiveCustomParams">
              保存
            </a-button>
            <a-button key="back" @click="openhivemodel">
              返回
            </a-button>
          </template>
          <custom-params :CustomParams="hivecolumnList"   ref="CustomParams"></custom-params>
        </a-modal>

        <!--mysql 字段配置弹出框-->
        <a-modal v-if="customParamsStausMysql" :visible="customParamsStausMysql" destroyOnClose title="mysql字段配置" :closable="false" width="600px">
          <template slot="footer">
            <a-button key="submit" type="primary" @click="saveMysqlCustomParams">
              保存
            </a-button>
            <a-button key="back" @click="openMysqlmodel">
              返回
            </a-button>
          </template>
          <custom-params-mysql :CustomParams="mysqlcolumnList"   ref="CustomParamsMysql"></custom-params-mysql>
        </a-modal>

        <!--es 字段配置弹出框-->
        <a-modal v-if="customParamsStausEs" :visible="customParamsStausEs" destroyOnClose title="es字段配置" :closable="false" width="600px">
          <template slot="footer">
            <a-button key="submit" type="primary" @click="saveEsCustomParams">
              保存
            </a-button>
            <a-button key="back" @click="openEsmodel">
              返回
            </a-button>
          </template>
          <custom-params-es :CustomParams="escolumnList"   ref="CustomParamsEs"></custom-params-es>
        </a-modal>
      </a-form-item>


    </a-form>
  </a-modal>
</template>

<script>

    import { createTable, updateTable, queryTopic } from '@/api/model'
    import { testContainsHZ } from '@/utils/util'
    import { mapGetters } from 'vuex'
    import customParams from "../modules/customParams"
    import customParamsMysql from "../modules/customParamsMysql"
    import customParamsEs from "../modules/customParamsEs"

    const columnTypeList = [ {name: "整形", value: "int"},
        {name: "长整型", value: "bingint"},
        {name: "单精度浮点型", value: "float"},
        {name: "双精度浮点型", value: "double"},
        {name: "字符串", value: "string"},
        {name: "日期型", value: "date"},
        {name: "时间戳", value: "timestamp"},
        {name: "布尔", value: "bool"}]
    export default {
        name: 'TableEdit',
        components: { customParams, customParamsMysql, customParamsEs},
        data () {
            return {
                title: '',
                visible: false,
                submitLoading: false,
                isCreate: true,
                columnList: [],
                hivecolumnList: [],
                mysqlcolumnList: [],
                escolumnList: [],
                tableName: '',
                id: '',
                // 进度条控制
                form: this.$form.createForm(this),
                params: {},
                datasourceId: 0,
                columnTypeList,
                topicList: [],
                tableTypes:[],
                customParamsStaus: false,
                customParamsStausMysql: false,
                customParamsStausEs: false
            }
        },
        created () {
        },
        mounted () {
        },
        computed: {
            ...mapGetters(['nickname', 'userId'])
        },
        methods: {
            add (params) {
                this.title = '新建数据资产'
                this.form.resetFields()
                this.visible = true
                this.isCreate = true
                this.datasourceId = params.datasourceId
                this.$nextTick(() => {
                    this.form.setFieldsValue({ 'layer': params.layer, 'topic': params.topic, 'tableCategory': params.tableCategory })
                })
                const that = this
                queryTopic({ 'layer': params.layer }).then(res => {
                    that.topicList = res.data
                })
            },
            edit (params) {
                this.title = '编辑数据资产'
                this.form.resetFields()
                this.visible = true
                this.isCreate = false
                this.datasourceId = params.datasourceId
                this.$nextTick(() => {
                    this.form.setFieldsValue({ 'tableCategory': params.layerCn,
                        'layer': params.layer,
                        'topic': params.topic,
                        'description': params.area,
                        'name': params.tblDescribe,
                        'tableName': params.tblName })
                })
                const that = this
                queryTopic({ 'layer': params.layer }).then(res => {
                    that.topicList = res.data
                })
            },
            handleDelete (i) {
                this.columnList.splice(i, 1)
                this.hivecolumnList.splice(i, 1)
                this.mysqlcolumnList.splice(i, 1)
                this.escolumnList.splice(i, 1)
            },
            close () {
                // this.$emit('ok')
                this.visible = false
            },
            handleOk () {
                const that = this
                this.form.validateFields((err, values) => {
                    if (!err) {
                        if (that.isCreate) {
                            if (values.createMethod === null || values.createMethod === undefined) {
                                that.$message.warn('请选择创建方式')
                                return
                            }
                            if (values.createMethod === '2' && (values.tableName === null || values.tableName === undefined)) {
                                that.$message.warn('请填写表名')
                                return
                            }
                            if (values.createMethod === '1' && (values.createSql === null || values.createSql === undefined)) {
                                that.$message.warn('请填写建表语句')
                                return
                            }
                        }
                        that.submitLoading = true
                        if (that.isCreate) {
                            if (testContainsHZ(values.tableName)) {
                                that.$message.error('表名不能包含中文')
                                that.submitLoading = false
                                return
                            }
                            for (let i = 0; i < that.columnList.length; i++) {
                                if (testContainsHZ(that.columnList[i].name)) {
                                    that.$message.error('字段名不能包含中文')
                                    that.submitLoading = false
                                    return
                                }
                            }
                            const formData = new FormData()
                            formData.append('levelName', values.layer + '_' + values.topic)
                            // 根据字段信息拼出建表语句
                            if (values.createMethod === '2') {
                                values.createSql = 'CREATE TABLE IF NOT EXISTS ' + values.tableName + '('
                                for (let i = 0; i < this.hivecolumnList.length; i++) {
                                    const obj = this.hivecolumnList[i]
                                    if (i === this.hivecolumnList.length - 1) {
                                        values.createSql = values.createSql + obj.name + ' ' + obj.type + ' comment \'' + obj.comment + '\''
                                    } else {
                                        values.createSql = values.createSql + obj.name + ' ' + obj.type + ' comment \'' + obj.comment + '\','
                                    }
                                }
                                values.createSql = values.createSql + ') '
                                formData.append('tableName', values.tableName)

                              //之间填写的建表语句
                            } else {
                                const tableName = values.createSql.toLowerCase().split(' table')[1].trim().split('(')[0].trim()
                                formData.append('tableName', tableName)
                                if (tableName.indexOf('.') !== -1) {
                                    that.submitLoading = false
                                    that.$message.error('建表语句表名不允许有库名前缀')
                                    return
                                }
                            }
                            formData.append('tableStatement', values.createSql)
                            formData.append('applicationName', values.name)
                            formData.append('tableComment', values.description)
                            formData.append('dataSourceId', that.datasourceId)
                            formData.append('userId', that.userId)
                            formData.append('userName', that.nickname)
                            formData.append('tableTypes',that.tableTypes.toString())
                            formData.append('mysqlcolumnList',JSON.stringify(that.mysqlcolumnList))
                            formData.append('escolumnList',JSON.stringify(that.escolumnList))
                            formData.append('createMethod',values.createMethod)
                            createTable(formData).then((res) => {

                                if (res.code === 0) {
                                    that.$message.success('创建表成功')
                                    that.close()
                                    that.$emit('ok')
                                } else {
                                    that.$message.error(res.msg)
                                }
                                that.submitLoading = false
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('创建表失败！')
                                that.submitLoading = false
                            })
                        } else {
                            // 更新表信息
                            const params = {}
                            params.area = values.name
                            params.tblName = values.tableName
                            params.tblDescribe = values.description
                            params.layer = values.layer
                            params.topic = values.topic
                            updateTable(params).then(res => {
                                if (res !== 0) {
                                    that.$message.success('更新表信息成功')
                                } else {
                                    that.$message.error('更新表信息失败')
                                }
                                that.submitLoading = false
                                that.close()
                                that.$emit('ok')
                            }).catch(err => {
                                console.log(err)
                                that.$message.error('更新表失败！')
                                that.submitLoading = false
                            })
                        }
                    }
                })
            },
            handleCancel () {
                this.close()
            },
            handleAddColumn: function () {
                this.columnList.push({ 'name': '', 'type': '', 'comment': '' })
            },
            openhivemodel(){

                if (this.hivecolumnList.length < this.columnList.length){

                    this.hivecolumnList = this.columnList
                }

                this.customParamsStaus=!this.customParamsStaus
            },
            openMysqlmodel(){
                if (this.mysqlcolumnList.length < this.columnList.length){
                    this.mysqlcolumnList = this.columnList
                }
                this.customParamsStausMysql=!this.customParamsStausMysql
            },
            openEsmodel(){
                if (this.escolumnList.length < this.columnList.length){
                    this.escolumnList = this.columnList
                }
                this.customParamsStausEs=!this.customParamsStausEs
            },
            // showCustomParams(){
            //     this.customParamsStaus=!this.customParamsStaus
            // },
            saveHiveCustomParams(){
                this.hivecolumnList =this.$refs.CustomParams.localParams
                //保存hive的字段信息

                this.customParamsStaus=!this.customParamsStaus
            },
            saveMysqlCustomParams(){
                this.mysqlcolumnList =this.$refs.CustomParamsMysql.localParams
                //保存hive的字段信息

                this.customParamsStausMysql=!this.customParamsStausMysql
            },
            saveEsCustomParams(){
                this.escolumnList =this.$refs.CustomParamsEs.localParams
                //保存hive的字段信息

                this.customParamsStausEs=!this.customParamsStausEs
            },
            //表类型
            // TbleTypeonChange(checkedValues){
            //     console.log('checked = ', checkedValues);
            //     let a=this.tableTypes
            //     debugger
            // }
        }
    }
</script>
<style>
  .data-modal .ant-form-item {
    margin-bottom: 0px;
  }
</style>
