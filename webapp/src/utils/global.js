/*默认支持数据库引擎*/
const dsTypeListS = [
  {
    id: 0,
    code: 'MYSQL',
    name:'MYSQL',
    disabled: false
  },
  /* {
     id: 1,
     code: 'POSTGRESQL',
     disabled: false
   },*/
  {
    id: 2,
    code: 'HIVE',
    name:'HIVE/IMPALA',
    disabled: false
  },
  /* {
     id: 3,
     code: 'SPARK',
     disabled: false
   },*/
  /*{
    id: 4,
    code: 'CLICKHOUSE',
    disabled: false
  },*/
  /* {
     id: 5,
     code: 'ORACLE',
     disabled: false
   },
   {
     id: 6,
     code: 'SQLSERVER',
     disabled: false
   },*/
 /* {
    id: 7,
    code: 'KAFKA',
    name:'KAFKA',
    disabled: false
  },
  {
    id: 8,
    code: 'ELASTICSEARCH',
    name:'ELASTICSEARCH',
    disabled: false
  }*/
]
const wirtedsTypeListS = [
  /*{
    id: 0,
    code: 'MYSQL',
    name:'MYSQL',
    disabled: false
  },*/
  /* {
     id: 1,
     code: 'POSTGRESQL',
     disabled: false
   },*/
  {
    id: 2,
    code: 'HIVE',
    name:'HIVE/IMPALA',
    disabled: false
  },
  /* {
     id: 3,
     code: 'SPARK',
     disabled: false
   },*/
  /*{
    id: 4,
    code: 'CLICKHOUSE',
    disabled: false
  },*/
  /* {
     id: 5,
     code: 'ORACLE',
     disabled: false
   },
   {
     id: 6,
     code: 'SQLSERVER',
     disabled: false
   },*/
  /* {
     id: 7,
     code: 'KAFKA',
     name:'KAFKA',
     disabled: false
   },
   {
     id: 8,
     code: 'ELASTICSEARCH',
     name:'ELASTICSEARCH',
     disabled: false
   }*/
]
/*默认运行任务数据源*/
const dataStorage = 16

function getDataSouceData() {
  return dsTypeListS
}
function getWirteDataSouceData() {
  return wirtedsTypeListS
}

function getDataStorage() {
  return dataStorage
}

/*生成唯一ID*/
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

/*通过关键词 生成随机数*/
function RandomNumber(keyword,number){
  return keyword+"_"+ Math.ceil(Math.random()*Math.pow(10,number))
}

/*数组 数据去重 对象*/
function uniqueForKey(arr, key) {
  const res = new Map();
  return arr.filter((arr) => !res.has(arr[key]) && res.set(arr[key], 1))
}

/*数组 数据去重 数据*/
function uniqueForItem(arr) {
  const res = new Map();
  return arr.filter((arr) => !res.has(arr) && res.set(arr, 1))
}
/*两个集合取差值*/
function getSubtractForKey(dataA,dataB,key) {
  let dataC = clone(dataA);
  for (let i = dataC.length - 1; i >= 0; i--) {
    for (let j = 0; j  < dataB.length; j++) {
      if (dataC[i][key] === dataB[j][key]) {
        dataC.splice(i, 1);
        break;
      }
    }
  }
  return dataC;
}
/*两个集合取差值*/
function getSubtractForItem(dataA,dataB) {
  let dataC = clone(dataA);
  for (let i = dataC.length - 1; i >= 0; i--) {
    for (let j = 0; j  < dataB.length; j++) {
      if (dataC[i] === dataB[j]) {
        dataC.splice(i, 1);
        break;
      }
    }
  }
  return dataC;
}

function formatDateTime (time){
  var date = new Date(time);
  var Y = date.getFullYear() + '-';
  var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
  var D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + ' ';
  var h = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours()) + ':';
  var m = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes()) + ':';
  var s = (date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds());

  let strDate = Y+M+D+h+m+s;
  return strDate;
}

function clone(obj){
  let temp = null;
  if(obj instanceof Array){
    temp = JSON.parse(JSON.stringify(obj))
  }else if(obj instanceof Function){
    //函数是共享的是无所谓的，js也没有什么办法可以在定义后再修改函数内容
    temp = obj;
  }else if(obj instanceof Object){
    temp = new Object();
    for(let item in obj){
      let val = obj[item];
      temp[item] = typeof val == 'object'?clone(val):val; //这里也没有判断是否为函数，因为对于函数，我们将它和一般值一样处理
    }
  } else{
    return obj
  }
  return temp;
}

export {
  getDataSouceData,
  getDataStorage,
  uuid,
  uniqueForKey,
  uniqueForItem,
  getSubtractForKey,
  getSubtractForItem,
  RandomNumber,
  clone,
  getWirteDataSouceData,
  formatDateTime
}
