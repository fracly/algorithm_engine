<template>
  <a-modal
    :visible="visible"
    @cancel="handleCancel" :width="width" cancelText="关闭" :closable="closable" :centered="centered">
    <template slot="title">
      <a-row>
        <a-col :span="12" style="font-size: 16px">
          编辑
        </a-col>
        <a-col :span="12" style="text-align: right;">
          <a-tooltip style="margin-left: 15px;margin-right: 10px" v-show="!FullScreen">
            <template slot="title">
              进入全屏
            </template>
            <a-icon @click="fullScreen" type="fullscreen" style="color: #4AABE5;"/>
          </a-tooltip>
          <a-tooltip style="margin-left: 15px;margin-right: 10px" v-show="FullScreen">
            <template slot="title">
              取消全屏
            </template>
            <a-icon @click="fullScreen" type="fullscreen-exit" style="color: #4AABE5;"/>
          </a-tooltip>

        </a-col>
      </a-row>
    </template>
    <template slot="footer">
      <a-button key="submit" type="primary" @click="saveCode">
        保存
      </a-button>
      <a-button key="back" @click="handleCancel">
        关闭
      </a-button>
    </template>

    <div class="from-mirror" :style="{height: height}"
         style=";overflow-y: auto; ;font-size: 14px;">
      <a-input
        type="hidden"
        v-model="code"
      ></a-input>
      <a-textarea
        id="code-mirror"
        name="code-mirror"
        style="opacity: 0;" />
    </div>
  </a-modal>


</template>

<script>
  import codemirror from '@/utils/codemirror'
  import {clone} from '@/utils/global'

  let editor

  export default {
    data() {
      return {
        code:"",
        visible: true,
        centered: true,
        closable: false,
        title: ['<div><img className={styles.picture} src={zy}></img>警告信息</div>'],
        node: {},
        visible: true,
        log: "",
        width: '700px',
        height: '400px',
        FullScreen: false,
      };
    },
    components: {},
    props: {
      Code: {
        type: String,
        default: () => {
          return ""
        }
      },
      type: {
        type: String,
        default: () => {
          return ""
        }
      }
    },
    created() {

    },
    mounted() {
      this.code=clone(this.Code);
      setTimeout(() => {
        this.handlerEditorForChild().refresh();
      }, 500)

    },
    methods: {
      saveCode(){
        this.$emit("saveCode",this.code)
      },
      handleCancel() {
        this.$emit("cancel")
      },
      handlerEditorForChild() {
        let _that = this
        // editor
        editor = codemirror("code-mirror", {
          mode: _that.type,
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
        editor.on("change", function () {
          _that.code = editor.getValue()
        });
        editor.setValue(this.code)
        return editor
      },
      fullScreen() {
        this.FullScreen = !this.FullScreen
        if (this.FullScreen) {
          this.width = (document.body.clientWidth - 80) + "px"    // 屏幕宽
          this.height = (document.body.clientHeight - 150) + "px"    // 屏幕高\
        } else {
          this.width = "700px"    // 屏幕宽
          this.height = "400px"
        }
      },

    },
    destroyed() {
      /**
       * 销毁编辑器实例
       */
      if (editor) {
        editor.toTextArea() // Uninstall
        editor.off(('.code-mirror'), 'keypress', this.keypress)
      }
    },
  };
</script>
<style lang="less">
  .from-mirror {
    width: 100%;
    position: relative;
    z-index: 0;
    display: flex;
      .CodeMirror {
        height: auto;
        min-height: 100%;
        max-height: 100%;
        width: 110%;
        border: 1px solid #ddd!important;
        border-radius: 3px;
      }
      .CodeMirror-scroll {
        height: auto;
        min-height: 100%;
        max-height: 100%;
        width: 100%;
      }
      .CodeMirror-gutters {
        border-left: none;
      }
  }
</style>

<style scoped>
  svg {
    font-size: 16px;
  }

  .ant-modal-header {
    padding: 10px 10px !important;
  }

  .ant-modal-close-x {

    width: 20px;
    height: 20px;
    line-height: 40px;
    padding-right: 40px;
  }

  /deep/ .ant-table-pagination.ant-pagination {
    float: right;
    margin: 5px 0;
  }

  /deep/ .ant-modal-footer {
    padding: 5px !important;
  }

  /deep/ .ant-modal-body {
    padding: 5px !important;
  }
</style>

