<template>
  <div style="text-align: right;height: 350px;overflow-y:auto">
    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>
    <a-divider/>
    <div v-for="(item,index) in LocalParams" :key="item.id" style="text-align: left;padding-bottom: 5px">
      <a-input v-model="item.prop" style="width: 180px" placeholder="prop必填"/>&nbsp;&nbsp;
      <a-input v-model="item.value" style="width: 120px" placeholder="value选填"/>&nbsp;&nbsp;
      <a-select placeholder="系统时间格式" v-model="item.time" style="width: 160px" @change="changeTimeFormat(item)">
        <a-select-option placeholder="系统时间格式" v-for="item in timeFormat" :value="item">
          {{item}}
        </a-select-option>
      </a-select>
      &nbsp;&nbsp;
      <a-input style="width: 80px" v-model="item.n" placeholder="前推数值" @change="changeN(index)"/>&nbsp;&nbsp;&nbsp;&nbsp;
      <a-icon type="delete" style="font-size: large;color: #FF498C;text-align: center" @click="deteleItem(item)"/>
    </div>
  </div>
</template>

<script>
  import {clone} from "@/utils/global";
  const timeFormat=["yyyy","yyyyMM","yyyy-MM","yyyyMMdd","yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"]

  export default {
    data() {
      return {
        timeFormat,
        LocalParams:[]
      };
    },
    props:{
      CustomParams: {
        type: Array,
        default: () => {
        }
      }
    },
    created() {
      this.initPage()
    },
    methods: {
      initPage(){
        let clone1 = clone(this.CustomParams);
        this.LocalParams = clone1
      },
      changeN(i){
        debugger;
        let time = this.LocalParams[i].time;
        let value = this.LocalParams[i].value;
        let n=this.LocalParams[i].n;
        var now = new Date();
        if (time != null) {
          if (time.endsWith("dd")) {
            value =now.getTime()-n*24*3600*1000
          } else if (time.endsWith("MM")) {
            value =now.getTime()-n*24*3600*1000*30
          } else if (time == "yyyy") {
            value =now.getTime()-n*24*3600*1000*365
          } else {
            value =now.getTime()-n*1000
          }
          value=new Date(value);
          this.LocalParams[i].value = this.dateFtt(time, value);
        }
      },

      /**
       * 格式化当前时间
       * @param fmt
       * @param date
       * @returns {*}
       */
      dateFtt(fmt, date) {
        var o = {
          // "Y":date.getFullYear(),//年
          "M+": date.getMonth() + 1, //月份
          "d+": date.getDate(), //日
          "H+": date.getHours(), //小时
          "m+": date.getMinutes(), //分
          "s+": date.getSeconds(), //秒
          "q+": Math.floor((date.getMonth() + 3) / 3), //季度
          "S": date.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt))
          fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
          if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
      },

      changeTimeFormat(item){
        let time = item.time;
        item.value = this.dateFtt(time, new Date());
        item.time = time;
        item="";
        var value =  new Date().format(item.time)
        item.value=value
        item.n=""
      },
      addParm(){
        var item={
          id:uuid(),
          prop:null,
          value:null,
          time:null,
          type:"VARCHAR",
          n:null
        }
        this.LocalParams.push(item)
      },
      deteleItem(node){
        const index1 = this.LocalParams.findIndex(item => item.id === node.id);
        this.LocalParams.splice(index1, 1);
      }
    }
  };

  Date.prototype.format = function (format) {
    var args = {
      "M+": this.getMonth() + 1,
      "d+": this.getDate(),
      "h+": this.getHours(),
      "m+": this.getMinutes(),
      "s+": this.getSeconds(),
      "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
      "S": this.getMilliseconds()
    };
    if (/(y+)/.test(format))
      format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var i in args) {
      var n = args[i];
      if (new RegExp("(" + i + ")").test(format))
        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? n : ("00" + n).substr(("" + n).length));
    }
    return format;

  };

  function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
  }
</script>
<style scoped>
  /deep/.ant-divider-horizontal {
    margin: 5px 0;
  }
</style>

