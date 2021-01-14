<template>
  <div style="text-align: right;height: 350px">
    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>
    <a-divider/>
    <div v-for="item in CustomParams" :key="item.id" style="text-align: left;padding-bottom: 5px">
      <a-input v-model="item.prop" style="width: 180px" placeholder="prop必填"/>&nbsp;&nbsp;
      <a-input v-model="item.value" style="width: 120px" placeholder="value选填"/>&nbsp;&nbsp;
      <a-select placeholder="系统时间格式" v-model="item.time" style="width: 160px">
          <a-select-option placeholder="系统时间格式" v-for="item in timeFormat" :value="item">
                {{item}}
          </a-select-option>
      </a-select>
      &nbsp;&nbsp;
      <a-input style="width: 80px" v-model="item.n" placeholder="前推数值"/>&nbsp;&nbsp;&nbsp;&nbsp;
      <a-icon type="delete" style="font-size: large;color: #FF498C;text-align: center" @click="deteleItem(item)"/>
    </div>
  </div>
</template>

<script>
  const timeFormat=["yyyyMMdd","yyyy-MM-dd","yyyyMM","yyyy-MM","yyyy","yyyy-MM-dd HH:mm:ss"]

  export default {
    data() {
      return {
        timeFormat
      };
    },
    props:{
      CustomParams: {
        type: Array,
        default: () => {
        }
      }
    },
    methods: {
      addParm(){
        var item={
          id:uuid(),
          prop:null,
          value:null,
          time:null,
          n:null
        }
        this.CustomParams.push(item)
      },
      deteleItem(node){
        const index1 = this.CustomParams.findIndex(item => item.id === node.id);
        this.CustomParams.splice(index1, 1);
      }
    }
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

