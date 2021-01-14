<style lang="less" scoped>
  // 深色样式定制
  @import url("./bigData.less");
  @import url("./index.less");
</style>

<template>
  <div class="cutControls">
    <a-spin :spinning="loading">
      <!--报表查询区域-->
      <sear-form :reportParam="reportParam" :exportExcel="reportInfo.reportType=='2'?exportExcelNoPagination:exportExcel" :submitQuery="reportInfo.reportType=='2'?changePage:submitQuery" ref="searchForm"></sear-form>
      <!--报表展示区域  高度暂时写死 后续修改为自适应-->
      <div class="cutControl" style="height: 740px">
        <div class="bigData-examples">
          <div class="data-box">
            <div class="data-row">
              <div class="data-col col1">
                <div class="data-tit">
                  <i class="iconfont">&#xeb2c;</i><h5>{{ reportInfo.reportName }}</h5>
                </div>
                <div class="data-cont">
                  <div class="tableChart">
                    <a-table
                      :columns="columns"
                      :data-source="tableData"
                      bordered
                      size="middle"
                      :pagination="reportInfo.reportType=='2'?pagination : false"
                      showPagination="auto"
                      :scroll="{ x: gridWidth, y: gridHeight }">
                      <template slot="customTitle" v-if="showTableFirstColum">
                        <b>{{ headName1 }}</b>
                        <div class="slopingside"></div>
                        <em>{{ headName2 }}</em>
                      </template>
                    </a-table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script>
  import 'ant-design-vue/dist/antd.css'
  import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
  import { TreeSelect } from 'ant-design-vue'
  import searForm from './compoments/searForm.vue'
  import { loadReport, queryList, exportExcel,exportExcelNoPagination } from '@/api/report'

  const SHOW_PARENT = TreeSelect.SHOW_PARENT

  const colums = []
  export default {
    name: 'CutControl',
    data () {
      return {
        loading: false,
        reportParam: [], // 报表查询参数
        columList: [], // 字段信息
        reportHeadMid: [], // 中间表头
        status: 1,
        reportData: {},
        reportCode: '',
        gridHeight: 560, // 报表大小
        gridWidth: 1200, // 报表大小
        columns: colums, // 表头数据
        locale: zhCN, // 定义语言 - 项目中只有中文简体语言包
        headName1: '', // 表头第一列
        headName2: '', // 表头第一列
        showTableFirstColum: false,
        reportInfo: {}, // 报表基本信息
        tableData: [],
        glbmbm: '',
        class: '',
        pagination:{
          showSizeChanger:true,
          showQuickJumper:true,
          position:"bottom",
          total: 0,
          pageSizeOptions: ["10", "20", "50", "100"],//每页中显示的数据
          pageSize: 10,
          current: 1, // 页面回退，主要是这个参数
          showTotal: (total) => `共${total}条`,
          onShowSizeChange: this.onShowSizeChange,
          onChange: current => this.changeCurrent(current),
        },
        mergeColumList: [],//合并字段集合 只有非清单才会使用
      }
    },
    methods: {
      // 合并单元格，这里我抽出了一个函数，调用的时候只需要将dataIndex作为参数传入即可
      rowSpan(key) {
        let arr = this.tableData
          .reduce((result, item) => {
            if (result.indexOf(item[key]) < 0) {
              result.push(item[key]);
            }
            return result;
          }, [])
          .reduce((result, keys) => {
            const children = this.tableData.filter((item) => item[key] === keys);
            result = result.concat(
              children.map((item, index) => ({
                ...item,
                [`${key}RowSpan`]: index === 0 ? children.length : 0,
              }))
            );
            return result;
          }, []);
        debugger
        this.tableData = arr;
      },
      /*分页方法*/
      onShowSizeChange(current, pageSize) {
        this.pagination.pageSize = pageSize;
        this.pagination.current = 1;
        this.changePage();
      },
      /*分页方法*/
      changeCurrent(current){
        this.pagination.current=current;
        this.changePage();
      },
      /*分页跳转*/
      changePage(){
        let searchForm = this.$refs.searchForm.searchForm;
        this.loading = true
        /* 查询时增加报表状态 */
        searchForm.status = this.status
        searchForm.reportCode = this.reportCode
        searchForm.pageSize=this.pagination.pageSize;
        searchForm.pageNum=this.pagination.current;
        queryList(searchForm).then(res => {
          const data = res
          if (data.code !== 1) {
            alert(data.msg)
            this.loading = false
            return
          }
          // 重新加载数据
          this.tableData = data.data.reportData.reportData
          for (var i = 0; i < this.mergeColumList.length; i++) {
            this.rowSpan(this.mergeColumList[i]["paramName"]);
          }
          this.loading = false
          this.pagination.total=data.data.reportData.pagination.totalCount;
          this.pagination.pageSize=data.data.reportData.pagination.pageSize;
          this.pagination.current=data.data.reportData.pagination.pageNum;
        }).catch(err => {
          this.loading = false
          console.log(err)
          this.$message.error('报表加载超时，请检查报表相关配置')
        })

      },
      // 加载报表信息
      loadReport () {
        loadReport({ 'status': this.status, 'glbmbm': this.glbmbm, reportCode: this.reportCode }).then(res => {
          const data = res
          if (data.code !== 1) {
            alert(data.msg)
            this.loading = false
            return
          }
          // 报表数据
          // this.tableData = data.data.reportData.reportData
          this.columList = data.data.reportData.columName
          // 中间表头
          this.reportHeadMid = JSON.parse(data.data.reportInfo.reportHeadMid)
          // 报表基本信息
          this.reportInfo = data.data.reportInfo
          // 表头部分
          const text = JSON.parse(data.data.reportHead)
          // 加载查询条件
          this.reportParam = data.data.reportParam
          //获取合并字段
          this.mergeColumList = this.reportParam.filter(item => item.paramType == "merge")
          //进行列合并
          this.findChildMerge(text.reportData);
          // 如果表头分为两部分 则第一部分为统计维度信息，列做特殊处理 位置固定 否则按正常处理
          if (text.reportData.length > 1) {
            // 表头 统计列
            this.headName1 = text.reportData[0].headName1
            this.headName2 = text.reportData[0].headName2
            colums[0] = text.reportData[0]
            colums[1] = text.reportData[1]
            this.showTableFirstColum = true
          } else {
            colums[0] = text.reportData[0]
            this.showTableFirstColum = false
          }
          this.loading = false
        }).catch(err => {
          this.loading = false
          console.log(err)
          this.$message.error('报表加载超时，请检查报表相关配置')
        })
      },
      // 报表查询方法
      submitQuery () {
        let searchForm = this.$refs.searchForm.searchForm;
        this.loading = true
        /* 查询时增加报表状态 */
        searchForm.status = this.status
        searchForm.reportCode = this.reportCode
        queryList(searchForm).then(res => {
          const data = res
          if (data.code !== 1) {
            alert(data.msg)
            this.loading = false
            return
          }
          // 重新加载数据
          this.tableData = data.data.reportData.reportData
          for (var i = 0; i < this.mergeColumList.length; i++) {
            this.rowSpan(this.mergeColumList[i]["paramName"]);
          }
          this.loading = false
        }).catch(err => {
          this.loading = false
          console.log(err)
          this.$message.error('报表加载超时，请检查报表相关配置')
        })
      },
      // excel 导出通用方法
      exportExcel () {
        var params = {
          'tableData': this.tableData,
          'reportData': colums,
          'cloumList': this.columList,
          'reportHeadMid': this.reportHeadMid,
          reportCode: this.reportCode
        }
        exportExcel(params).then(res => {
            const blob = res
            const fileName = this.reportInfo.reportName + (new Date()).valueOf() + '.xls'
            if ('download' in document.createElement('a')) {
              // 非IE下载
              const elink = document.createElement('a')
              elink.download = fileName
              elink.style.display = 'none'
              elink.href = URL.createObjectURL(blob)
              document.body.appendChild(elink)
              elink.click()
              URL.revokeObjectURL(elink.href) // 释放URL 对象
              document.body.removeChild(elink)
            } else {
              // IE10+下载
              navigator.msSaveBlob(blob, fileName)
            }
            this.$message.success('生成文件成功')
          })
          .catch(err => {
            console.log(err)
            this.$message.error('服务器出现问题,请稍后再试')
          })
      },
      /*分页导出sql*/
      exportExcelNoPagination () {
        var params = {
          'tableData': this.tableData,
          'reportData': colums,
          'cloumList': this.columList,
          'reportHeadMid': this.reportHeadMid,
          reportCode: this.reportCode
        }
        let searchForm = this.$refs.searchForm.searchForm;
        let paramss = Object.assign(searchForm, params);
        exportExcelNoPagination(paramss)
          .then(res => {
            const blob = res
            const fileName = this.reportInfo.reportName + (new Date()).valueOf() + '.xls'
            if ('download' in document.createElement('a')) {
              // 非IE下载
              const elink = document.createElement('a')
              elink.download = fileName
              elink.style.display = 'none'
              elink.href = URL.createObjectURL(blob)
              document.body.appendChild(elink)
              elink.click()
              URL.revokeObjectURL(elink.href) // 释放URL 对象
              document.body.removeChild(elink)
            } else {
              // IE10+下载
              navigator.msSaveBlob(blob, fileName)
            }
            this.$message.success('生成文件成功')
          })
          .catch(err => {
            console.log(err)
            this.$message.error('服务器出现问题,请稍后再试')
          })
      },

      //递归查询字段是否需要合并
      findChildMerge(list) {

        for (var i = 0; i < list.length; i++) {
          if (list[i].dataIndex) {//如果字段有dataIndex属性 则进行下一班判断
            var alist = this.mergeColumList.filter(item => item.paramName == list[i].dataIndex)
            if (alist != null && alist.length > 0) {
              var paramName = alist[0]["paramName"];
              list[i]["customRender"] = (text, row, index)  => {
                return {
                  children: text,
                  attrs: {
                    rowSpan: row[paramName+"RowSpan"],
                  }
                };
              }
            }
          }
          if (list[i].children) {
            this.findChildMerge(list[i].children)
          }
        }
      },
    },
    computed: {
      authInfo: function () {
        return this.$store.getters.authInfo
      },
      sysInfo: function () {
        return this.$store.getters.sysInfo
      }
    },
    components: { searForm },
    mounted () {
      this.class = this.$route.query.class
      document.documentElement.classList.add('mode-default')
      if (this.class === 'dark') {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
      // 获取用户部门信息
      this.glbmbm = this.$route.query.glbmbh
      this.loading = true
      // 初始化报表和参数
      this.reportCode = this.$route.params.reportCode
      this.status = this.$route.query.status
      this.loadReport()
    }
  }
</script>
