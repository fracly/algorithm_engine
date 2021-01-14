<template>
  <a-modal title="运行结果"
           :visible="visible"
           @cancel="handleCancel" width="1000px" :footer="null">

    <a-spin tip="结果请求中" :spinning="spinning">
      <a-table bordered="true" :pagination="pagination" :columns="columns" :data-source="data" size="small" :scroll="{ x: 680, y: 420 }" style="max-height: 460px"></a-table>
    </a-spin>
  </a-modal>
</template>

<script>
  import {opreaResult} from '@/api/process'

  export default {
    data() {
      return {
        pagination:false,
        columns:[],
        data:[],
        visible: true,
        spinning:false,
      };
    },
    props: {
      processInstanceId: {
        type: Number,
        default: () => 0
      },
      projectName: {
        type: String,
        default: () => ""
      },
      resultName: {
        type: String,
        default: () => ""
      }
    },
    created() {
      if(this.processInstanceId != 0){
        this.loadResult()
      }
    },
    methods: {
      loadResult(){
        this.spinning=true;
        let _that=this
        opreaResult({"projectName":_that.projectName,"processInstanceId":_that.processInstanceId,"name":_that.resultName,"skipLineNum":0,"limit":100}).then(res => {
            if(res.code == 0 && res.data.length!=0){
              debugger
              var coulmn = res.data[0]
              this.columns =[];
              for(var i=0;i<coulmn.length;i++){
                var item  = {
                    dataIndex: coulmn[i],
                    key: coulmn[i],
                    title:coulmn[i],
                    width:100
                  };
                this.columns.push(item);
              }
              this.data=[];
              for(var i=1;i<res.data.length;i++){
                var item = {
                  key: i,
                }
                for(var j=0;j<coulmn.length;j++){
                  item[coulmn[j]]=res.data[i][j]
                }
                this.data.push(item);
              }
            }
            this.spinning=false;
        }).catch(() => {
          this.spinning=false;
          //this.$message.error("日志请求失败")
        })
      },

      handleCancel() {
        this.$emit("handleCancel")
      }
    }
  };
</script>

<style scoped>
  svg{
    font-size: 16px;
  }

  .ant-modal-header{
    padding: 10px 10px!important;
  }

  .ant-modal-close-x {

    width: 20px;
    height: 20px;
    line-height: 40px;
    padding-right: 40px;
  }

  /deep/.ant-table-pagination.ant-pagination {
    float: right;
    margin: 5px 0;
  }

  /deep/.ant-modal-footer{
    padding: 5px!important;
  }
  /deep/.ant-modal-body{
    padding: 5px!important;
  }
</style>
