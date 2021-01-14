<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :closable="false"
    :confirm-loading="loading"
  >
    <a-spin :spinning="loading">
    <a-row :gutter="5" style="overflow-x:auto;overflow-y:auto;height:400px;">
      <a-col :sm="6">
        <a-card title="分支信息" style="min-height: 600px;">
          <ul style="margin:0px 0px 0px -24px">
            <li v-for="item in branchList" :key="item.name" style="font-size: 14px;color: #1890ff">
              {{item.name}} <br/>
              描述: {{item.desc }}
            </li>
          </ul>
        </a-card>
      </a-col>
      <a-col  :sm="18">
        <a-card title="版本信息" style="min-height: 600px;">
          <a-descriptions title="基础信息">
            <a-descriptions-item label="版本号">
              <a-select show-search optionFilterProp="children" style="width: 130px;" v-model="versionObj.id" @change="handleVersionChange">
                <a-select-option v-for="item in versionList" :key="item.id" :value="item.id"> {{ item.name }} </a-select-option>
              </a-select>
            </a-descriptions-item>
            <a-descriptions-item label="状态" :span="2">
              <a-icon v-if='versionObj.state === 1' type="check-circle" style="color: green" />
              <a-icon v-else type="stop" style="color: red"/>
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
              {{ versionObj.createTime | moment  }}
            </a-descriptions-item>
            <a-descriptions-item label="更新时间" :span="2">
              {{ versionObj.updateTime | moment  }}
            </a-descriptions-item>
            <a-descriptions-item label="描述">
              {{ versionObj.desc }}
            </a-descriptions-item>
          </a-descriptions>
          <br/>
          <a-descriptions title="字段信息">
            <a-descriptions-item label="">
              <a-table :columns="columns" :data-source="versionObj.columns" :pagination="false" style="height:300px;overflow-y:auto">
                <span slot="description" slot-scope="text">
                  <a-popover title="" :content="text" v-if="text.length > 15">
                    {{ text.substr(0, 15) + '...' }}
                  </a-popover>
                  <span v-else>
                    {{ text }}
                  </span>
                </span>
              </a-table>
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
    </a-row>
    </a-spin>
    <template slot="footer">
      <a-button type="default" @click="handleOk">关闭</a-button>
    </template>
  </a-modal>
</template>

<script>
import { getBranchAndVersionList } from '@/api/metadata'
    export default {
        name: 'TableDetail',
        data () {
            return {
                title: '',
                visible: false,
                loading: false,
                branchList: [],
                versionList: [],
                info: {},
                versionObj: {},
                columns: [
                    {
                        title: '字段名',
                        dataIndex: 'name'
                    },
                    {
                        title: '字段类型',
                        dataIndex: 'type'
                    },
                    {
                        title: '注释',
                        dataIndex: 'comment',
                        scopedSlots: { customRender: 'description' }
                    }
                ]
            }
        },
        created () {
        },
        methods: {
            show (item) {
                this.loading = true
                this.visible = true
                this.info = item
                this.title = '表 【' + item.tablename + '】的元数据详情'
                const _this = this
                getBranchAndVersionList(_this.info).then(res => {
                    _this.branchList = res.data.branch
                    _this.versionList = res.data.version
                    _this.versionObj = res.data.version[0]
                    _this.versionObj.columns = JSON.parse(res.data.version[0].schemaText).columns
                    this.loading = false
                })
            },
            handleOk () {
                this.visible = false
            },
            getBranchAndVersionList () {
            },
            handleVersionChange (value) {
                const newData = [...this.versionList]
                const target = newData.filter(item => value === item.id)[0]
                this.versionObj = target
            }
        }
    }
</script>
