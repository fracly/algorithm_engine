<!--
<template>
  <div style="text-align: right;height: 350px">
    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>
    <a-divider/>
    <div v-for="item in CustomParams" :key="item.id" style="text-align: left;padding-bottom: 10px">
      <a-input v-model="item.nonup" style="width: 32%" placeholder="请输入手动区间上限"/>&nbsp;&nbsp;
      <a-input v-model="item.nondown" style="width: 32%" placeholder="请输入手动区间下限"/>&nbsp;&nbsp;&nbsp;&nbsp;
      <a-input v-model="item.nonnum" style="width: 32%" placeholder="请输入手动类别"/>&nbsp;&nbsp;&nbsp;&nbsp;
      <a-icon type="delete" style="font-size: large;color: #FF498C;text-align: center" @click="deteleItem(item)"/>
    </div>
  </div>
</template>

<script>

  export default {
    data() {
      return {
        choseList: []
      };
    },
    props:{
      CustomParams: {
        type: Array,
        default: () => {
        }
      },

    },
    methods: {
      addParm(){
        var item={
          id:uuid(),
          nonup:"",
          nondown:"",
          nonnum:""
        }
        this.CustomParams.push(item)
      },
      deteleItem(node){
        const index1 = this.CustomParams.findIndex(item => item.id === node.id);
        this.CustomParams.splice(index1, 1);
      }
    },
    /*初始化参数*/
    created() {

    },
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

-->
