<template>
  <div>
    <a-form>
      <result :title="title" :isShow="false" style="max-width: 660px; margin: 0px auto 0;padding: 0px;">
        <div class="information" >
          <a-row>
            <a-col :sm="8" :xs="24">报表名称：</a-col>
            <a-col :sm="16" :xs="24">{{ reportInfo.name }}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">报表编码：</a-col>
            <a-col :sm="16" :xs="24">{{ reportInfo.code }}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">报表分组：</a-col>
            <a-col :sm="16" :xs="24">{{ groupName }}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">查询权限：</a-col>
            <a-col :sm="16" :xs="24">{{ roleName }}</a-col>
          </a-row>
          <a-row>
            <a-col :sm="8" :xs="24">报表地址：</a-col>
            <a-col :sm="16" :xs="24">{{ url }}</a-col>
          </a-row>
        </div>
        <div style="margin-top: 50px;margin-left: 30%">
          <a-button style="margin-left: 8px" @click="toOrderList">返回</a-button>
        </div>
      </result>
    </a-form>
  </div>
</template>

<script>
  import { Result } from '@/components'
  import { getGroupsList, getRolesList, editStatusReport } from '@/api/report'
    export default {
        name: 'Step4',
        data () {
            return {
                loading: false,
                groupName: '',
                roleName: '',
                url: '',
                title: '恭喜您，发布成功',
                icon: 'success'
            }
        },
        props: [
            'reportInfo'
        ],
        components: {
        Result
      },
        methods: {
            finish () {
                this.$emit('finish')
            },
            toOrderList () {
                this.$router.push('/report/custom')
            },
            groupList () {
                const self = this
                getGroupsList().then(res => {
                    for (const i in res.data) {
                        if (res.data[i].id === self.reportInfo.groupId) {
                            self.groupName = res.data[i].name
                        }
                    }
                })
            },
            roleList () {
                const self = this
                getRolesList().then(res => {
                    const role = []
                    for (const i in res.data) {
                        if (self.reportInfo.roleId.indexOf(res.data[i].id) !== -1) {
                            role.push(res.data[i].name)
                        }
                    }
                    self.roleName = role.toString()
                })
            },
            // 上线
           handleEnable (id) {
            editStatusReport({ 'id': id, status: 1 }).then(res => {
              if (res.code === 0) {
              } else {
                this.title = '报表发布失败'
                this.icon = 'error'
              }
            })
          }
        },
        created () {
            this.handleEnable(this.reportInfo.id)
            this.groupList()
            this.roleList()
            this.url = Glod.dsjmh + '/#/customReport/' + this.reportInfo.code
        }
    }
</script>
<style lang="less" >
  .information {
    line-height: 22px;
    .ant-row:not(:last-child) {
      margin-bottom: 24px;
    }
  }
  .result {
    svg { font-size: 30px}
    .description{
      margin-bottom: 0px;
    }
    .extra{
      margin-top: -30px;
    }

  }
</style>
