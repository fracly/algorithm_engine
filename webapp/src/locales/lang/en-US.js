import antdEnUS from 'ant-design-vue/es/locale-provider/en_US'
import momentEU from 'moment/locale/eu'

const components = {
  antLocale: antdEnUS,
  momentName: 'eu',
  momentLocale: momentEU
}

const locale = {
  'message': '-',
  'menu.home': 'Home',
  'menu.dashboard': 'Dashboard',
  'menu.dashboard.analysis': 'Analysis',
  'menu.dashboard.monitor': 'Monitor',
  'menu.dashboard.workplace': 'Workplace',

  'layouts.usermenu.dialog.title': 'Message',
  'layouts.usermenu.dialog.content': 'Do you really log-out.',

  'app.setting.pagestyle': 'Page style setting',
  'app.setting.pagestyle.light': 'Light style',
  'app.setting.pagestyle.dark': 'Dark style',
  'app.setting.pagestyle.realdark': 'RealDark style',
  'app.setting.themecolor': 'Theme Color',
  'app.setting.navigationmode': 'Navigation Mode',
  'app.setting.content-width': 'Content Width',
  'app.setting.fixedheader': 'Fixed Header',
  'app.setting.fixedsidebar': 'Fixed Sidebar',
  'app.setting.sidemenu': 'Side Menu Layout',
  'app.setting.topmenu': 'Top Menu Layout',
  'app.setting.content-width.fixed': 'Fixed',
  'app.setting.content-width.fluid': 'Fluid',
  'app.setting.othersettings': 'Other Settings',
  'app.setting.weakmode': 'Weak Mode',
  'app.setting.copy': 'Copy Setting',
  'app.setting.loading': 'Loading theme',
  'app.setting.copyinfo': 'copy success，please replace defaultSettings in src/models/setting.js',
  'app.setting.production.hint': 'Setting panel shows in development environment only, please manually modify',
  // AI引擎平台
  'algo.data.monitor': '数据监控',
  'algo.data.monitor.busi': '业务监控',
  'algo.data.monitor.cluster': '集群监控',
  'algo.data.monitor.mysql': 'Mysql监控',
  'algo.data.governance': '数据治理',
  'algo.data.governance.datasource': '数据源管理',
  'algo.data.governance.table': '标准模型',
  'algo.data.governance.table.manager': '模型设计',
  'algo.data.governance.label.design': '标签设计',
  'algo.data.governance.metadata': '元数据',
  'algo.data.governance.metadata.search': '元数据检索',
  'algo.data.governance.metadata.kinship': '血缘关系',
  'algo.data.governance.data.asset': '数据资产',
  'algo.data.governance.data.asset.classify': '数据资产分类',
  'algo.data.governance.data.asset.lifecycle': '生命周期管理',
  'algo.model.center': '模型中心',
  'algo.model.center.model.factory': '模型开发管理',
  'algo.model.center.model.factory.exploit': '模型开发',
  'algo.model.center.model.factory.schedule': '定时管理',
  'algo.model.center.model.factory.group': '模型组管理',
  'algo.model.center.model.resource': '模型资源管理',
  'algo.model.center.model.resource.file': '文件管理',
  'algo.model.center.model.resource.jar': 'jar包管理',
  'algo.model.center.model.resource.func': '函数管理',
  'algo.report': '报表管理',
  'algo.report.custom': '自定义报表',
  'algo.service': '资源服务',
  'algo.service.manager': '服务管理',
  'algo.service.package': '服务封装',
  'algo.service.application': '应用管理',
  'algo.service.flow.control': '流控管理'
}

export default {
  ...components,
  ...locale
}
