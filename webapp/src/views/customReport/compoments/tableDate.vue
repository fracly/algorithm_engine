<style lang="less" scoped>
    @import url("../bigData.less");
    @import url("../reportPreview/index.less");
</style>
<template>
  <div></div>
</template>
<script>
    import 'ant-design-vue/dist/antd.css';
    import zhCN from 'ant-design-vue/lib/locale-provider/zh_CN'
    let colums = []

    export default {
        name: "TableDate",
        props: {
            reportData: {
                type: Object, //指定传入的类型
                default: function () {
                    return {};
                }
            }
        },
        data() {
            return {

                tableDatas:[]
            }
        },
        methods: {
            loadReport() {
                debugger
                    //报表基本信息
                    this.reportInfo = this.reportData.reportInfo
                    this.tableDatas = this.reportData.reportData.reportData
                    //表头部分
                    let text = JSON.parse(this.reportData.reportHead)
                    //如果表头分为两部分 则第一部分为统计维度信息，列做特殊处理 位置固定 否则按正常处理
                    if (text.reportData.length > 1) {
                        //表头 统计列
                        this.headName1 = text.reportData[0].headName1
                        this.headName2 = text.reportData[0].headName2
                        colums[0] = text.reportData[0];
                        colums[1] = text.reportData[1];
                        this.showTableFirstColum = true;
                    } else {
                        colums[0] = text.reportData[0];
                        this.showTableFirstColum = false;
                    }
            },
        },
        watch:{
            reportData:{
                handler(newName,oldName){
                  debugger
                    this.loadReport();
                }
            }
        },
        computed: {
        },
        components: {},
        mounted() {

        }


    };
</script>

