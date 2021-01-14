<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16">
      <a-col :md="24" :lg="16">
        <a-form :form="form" layout="vertical">
          <a-form-item label="姓名">
            <a-input placeholder="请输入你的姓名" v-decorator="['userCNName', { rules: [{required: true, message: '姓名必须填写'}] }]"/>
          </a-form-item>
          <a-form-item label="账号" style="display: none">
            <a-input type="hidden" v-decorator="['id']"/>
            <a-input type="hidden" v-decorator="['tenantId']"/>
            <a-input type="hidden" v-decorator="['queue']"/>
            <a-input placeholder="请输入你的账号" v-decorator="['userName', { rules: [{required: true, message: '账号必须填写'}] }]"/>
          </a-form-item>
          <a-form-item label="描述">
            <a-textarea rows="4" placeholder="个人描述" v-decorator="['desc']"/>
          </a-form-item>
          <a-form-item
            label="电子邮件"
          >
            <a-input placeholder="example@163.com" v-decorator="['email']"/>
          </a-form-item>
          <a-form-item
            label="手机"
          >
            <a-input placeholder="139xxxx8917" v-decorator="['phone', { rules: [{required: true, message: '手机必须填写'}] }]"/>
          </a-form-item>
          <a-form-item
            label="上次更新时间"
          >
            <a-input v-decorator="['updateTime']" disabled></a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleOk(null)">保存</a-button>
            <a-button type="primary" style="margin-left: 8px" @click="show">修改密码</a-button>
          </a-form-item>
        </a-form>

      </a-col>
      <a-col :md="24" :lg="8" :style="{ minHeight: '180px' }">
        <div class="ant-upload-preview">
          <!--          <a-icon type="cloud-upload-o" class="upload-icon"/>-->
          <div class="mask">
            <a-icon type="plus"/>
          </div>
          <img src="@/assets/default.jpg"/>
        </div>
      </a-col>

    </a-row>
    <modify-password-modal
      ref="modifyModal"
      :visible="visible"
      :loading="loading"
      @cancel="handleCancel"
      @ok="handlePasswordOk"
    ></modify-password-modal>

  </div>
</template>

<script>
  import { getInfo } from '@/api/login'
  import { systemUserUpdate } from '@/api/system'
  import md5 from 'md5'
  import moment from 'moment'
  import ModifyPasswordModal from './ModifyPasswordModal'

  export default {
    components: { ModifyPasswordModal },
    data () {
      return {
        form: this.$form.createForm(this),
        record: {},
        visible: false,
        loading: false
      }
    },
    methods: {
      handlePasswordOk () {
        const form = this.$refs.modifyModal.form
        this.loading = true
        form.validateFields((errors, values) => {
          if (!errors) {
            if (values.password1 !== values.password2) {
              this.$message.error('两次输入的密码不一致')
              this.loading = false
              this.visible = true
            } else {
              this.visible = false
              this.handleOk(values.password1)
            }
            this.loading = false
          } else {
            this.loading = false
          }
        })
      },
      handleOk (pass) {
        this.form.validateFields((errors, values) => {
          if (!errors) {
            if (pass) {
              values.userPassword = md5(pass)
            }
            systemUserUpdate(values).then(res => {
              if (res.code === 0) {
                this.$message.success(res.msg)
              } else {
                this.$message.error(res.msg)
              }
            }).catch(err => {
              console.log(err)
              this.$message.error('更新用户信息失败')
            })
          }
        })
      },
      handleCancel () {
        this.visible = false
      },
      show () {
        this.visible = true
      },
      getUserInfo () {
        const self = this
        getInfo().then(res => {
          self.record = res.data
          const timeStamp = self.record.updateTime
          self.record.updateTime = moment(timeStamp).format('YYYY-MM-DD HH:mm:ss')
          const user = {
            userCNName: self.record.userCNName,
            userName: self.record.userName,
            userPassword: self.record.userPassword,
            id: self.record.id,
            desc: self.record.desc,
            email: self.record.email,
            phone: self.record.phone,
            tenantId: self.record.tenantId,
            queue: self.record.queue,
            updateTime: self.record.updateTime

          }
          self.$nextTick(() => {
            this.form.setFieldsValue(user)
          })
        })
      },
      modifyPassword () {
        this.$router.push('/account/settings/security')
      }
    },
    mounted () {
      this.form.resetFields()
      this.getUserInfo()
    }
  }
</script>

<style lang="less" scoped>

  .avatar-upload-wrapper {
    height: 200px;
    width: 100%;
  }

  .ant-upload-preview {
    position: relative;
    margin: 0 auto;
    width: 100%;
    max-width: 180px;
    border-radius: 50%;
    box-shadow: 0 0 4px #ccc;

    .upload-icon {
      position: absolute;
      top: 0;
      right: 10px;
      font-size: 1.4rem;
      padding: 0.5rem;
      background: rgba(222, 221, 221, 0.7);
      border-radius: 50%;
      border: 1px solid rgba(0, 0, 0, 0.2);
    }

    .mask {
      opacity: 0;
      position: absolute;
      background: rgba(0, 0, 0, 0.4);
      cursor: pointer;
      transition: opacity 0.4s;

      i {
        font-size: 2rem;
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -1rem;
        margin-top: -1rem;
        color: #d6d6d6;
      }
    }

    img, .mask {
      width: 100%;
      max-width: 180px;
      height: 100%;
      border-radius: 50%;
      overflow: hidden;
    }
  }
</style>
