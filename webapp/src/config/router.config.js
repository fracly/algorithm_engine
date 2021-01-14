import { UserLayout, BasicLayout, RouteView, BlankLayout, PageView } from '@/layouts'
import { bxAnaalyse } from '@/core/icons'

export const asyncRouterMap = [

  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '首页' },
    redirect: '/index',
    children: [
      // 首页
      {
        path: '/index',
        name: 'Index',
        component: () => import('@/views/data-monitor/busi-monitor/BusiDataView'),
        meta: { title: '首页', keepAlive: true, icon: bxAnaalyse, permission: [ 'index' ] }
      },

      // 数据管理
      {
        name: 'Manager',
        path: '/manager',
        redirect: '/manager/datasource',
        component: PageView,
        meta: { title: '数据管理', icon: 'dashboard', permission: [ 'manager' ] },
        children: [
          {
            path: '/manager/datasource',
            name: 'DataSource',
            component: () => import('@/views/data-manager/datasource/DatasourceList'),
            meta: { title: '数据源', keepAlive: true, permission: [ 'datasource' ] }
          },
          {
            path: '/manager/design/table',
            name: 'TableDesign',
            component: () => import('@/views/data-manager/table-manager/TableList'),
            meta: { title: '数据资产', keepAlive: true, permission: [ 'dataasset' ] }
          },
          {
            path: '/manager/design/label',
             name: 'LabelDesign',
            component: () => import('@/views/data-manager/label-design/LabelList'),
             meta: { title: '行业标签', keepAlive: true, permission: [ 'industry-label' ] }
           },
          {
            path: '/manager/kylin/cube',
            name: 'Kylin',
            component: () => import('@/views/data-manager/kylin/index'),
            meta: { title: 'CUBE调度', keepAlive: true, permission: [ 'cube' ] }
          },
          {
            path: '/manager/kylin/log/:name',
            name: 'KylinLog',
            component: () => import('@/views/data-manager/kylin/modules/LogList'),
            hidden: true,
            meta: { title: 'CUBE日志', keepAlive: true, permission: [ 'cube' ] }
          }
        ]
      },

      // 数据治理
      {
        path: 'governance',
        name: 'Governance',
        redirect: '/governance/quality-rule',
        component: PageView,
        meta: { title: '数据治理', keepAlive: true, icon: bxAnaalyse, permission: [ 'governance' ] },
        children: [
          {
            path: '/governance/quality-rule',
            name: 'QualityRule',
            component: () => import('@/views/data-governance/quality-rule/TableList'),
            meta: { title: '质量规则', keepAlive: false, permission: [ 'quality-rule' ] }
          },
          {
            path: '/governance/metadata/search',
            name: 'MetadataSearch',
            component: () => import('@/views/data-governance/metadata/search/MetadataList'),
            meta: { title: '元数据检索', keepAlive: false, permission: [ 'metadata-search' ] }
          }
        ]
      },

      // 模型中心
      {
        path: '/model',
        name: 'Model',
        component: PageView,
        redirect: '/model/factory',
        meta: { title: '数据建模', icon: 'upload', permission: [ 'model' ] },
        children: [
          {
            path: '/model/factory/exploit',
            name: 'Exploit',
            component: () => import('@/views/model-center/model-factory/process/main'),
            meta: { title: '模型开发', keepAlive: false, permission: [ 'model-design' ], hiddenHeaderContent: true }
          },
          {
            path: '/model/factory/schedule',
            name: 'Schedule',
            component: () => import('@/views/model-center/model-factory/schedule/ScheduleList'),
            meta: { title: '定时管理', keepAlive: false, permission: [ 'schedule-manage' ] }
          },
          {
            path: '/model/factory/log',
            name: 'Log',
            component: () => import('@/views/model-center/model-factory/log/InstanceList'),
            meta: { title: '日志查询', keepAlive: false, permission: [ 'log-search' ] }
          },
          {
            path: '/model/resource',
            name: 'File',
            component: RouteView,
            children: [
              {
                path: 'file',
                name: 'FileList',
                component: () => import('@/views/model-center/model-resource/file/FileList'),
                meta: { title: '文件管理', keepAlive: false, permission: [ 'file-manage' ] }
              },
              {
                path: 'func',
                name: 'Func',
                component: () => import('@/views/model-center/model-resource/func/FuncList'),
                meta: { title: '函数管理', keepAlive: false, permission: [ 'function-manage' ] }
              }
            ],
            meta: { title: '资源管理', keepAlive: false, permission: [ 'model' ] }
          }
        ]
      },

      // 数据服务
      {
        path: '/resource',
        name: 'Resource',
        component: PageView,
        redirect: '/resource/package',
        meta: { title: '服务引擎', icon: 'tool', permission: [ 'service' ] },
        children: [
          {
            path: '/resource/service',
            name: 'ServicePackage',
            component: () => import('@/views/service-manager/service-package/ServiceList'),
            meta: { title: '服务开发', permission: [ 'service-develop' ] }
          },
          {
            path: '/resource/service/add',
            name: 'ServiceAdd',
            component: () => import('@/views/service-manager/service-package/modules/ServiceEdit'),
            hidden: true,
            meta: { title: '新增服务', permission: [ 'service' ] }
          },
          {
            path: '/resource/service/verify/:id',
            name: 'ServiceVerify',
            component: () => import('@/views/service-manager/service-package/modules/ServiceVerify'),
            hidden: true,
            meta: { title: '测试服务', permission: [ 'service' ] }
          },
          {
            path: '/resource/service/edit/:id',
            name: 'ServiceEdit',
            component: () => import('@/views/service-manager/service-package/modules/ServiceEdit'),
            hidden: true,
            meta: { title: '编辑服务', permission: [ 'service' ] }
          },
          {
            path: '/resource/service/view/:id',
            name: 'ServiceView',
            component: () => import('@/views/service-manager/service-package/modules/ServiceView'),
            hidden: true,
            meta: { title: '服务详情', permission: [ 'service' ] }
          },
          {
            path: '/resource/application',
            name: 'Application',
            component: () => import('@/views/service-manager/application/Main'),
            meta: { title: '应用管理', permission: [ 'application-manage' ] }
          },
          {
            path: '/resource/application/list',
            name: 'ApplicationList',
            hidden: true,
            component: () => import('@/views/service-manager/application/modules/AppList'),
            meta: { title: '应用列表', permission: [ 'service' ] }
          },
          {
            path: '/resource/application/add',
            name: 'AppAdd',
            hidden: true,
            component: () => import('@/views/service-manager/application/modules/AppEdit'),
            meta: { title: '新增应用', permission: [ 'service' ] }
          },
          {
            path: '/resource/application/edit/:id',
            name: 'AppEdit',
            hidden: true,
            component: () => import('@/views/service-manager/application/modules/AppEdit'),
            meta: { title: '编辑应用', permission: [ 'service' ] }
          },
          {
            path: '/resource/application/log',
            name: 'AppLog',
            hidden: true,
            component: () => import('@/views/service-manager/application/modules/AppLog'),
            meta: { title: '日志监控', permission: [ 'service' ] }
          },
          {
            path: '/resource/flow/control',
            name: 'FlowControl',
            component: () => import('@/views/service-manager/flow-control/FlowControlList'),
            meta: { title: '流控规则', permission: [ 'flow-control-rule' ] }
          }
        ]
      },
      // 自定义报表
      {
        path: '/report',
        name: 'Report',
        component: PageView,
        redirect: '/report/custom',
        meta: { title: '报表引擎', icon: 'profile', permission: [ 'report' ] },
        children: [
          {
            path: '/report/custom',
            name: 'ReportList',
            component: () => import('@/views/report/ReportList'),
            meta: { title: 'excel报表开发', permission: [ 'excel-report-develop' ] }
          },
          {
            path: '/report/group',
            name: 'ReportGroupList',
            component: () => import('@/views/report/ReportGroupList'),
            meta: { title: '报表分组', permission: [ 'report-group' ] }
          },
          {
            path: '/report/custom/add',
            name: 'ReportMain',
            component: () => import('@/views/report/Main'),
            hidden: true,
            meta: { title: '新增报表', permission: [ 'report' ] }
          },
          {
            path: '/report/custom/edit/:id',
            name: 'ReportEdit',
            hidden: true,
            component: () => import('@/views/report/Main'),
            meta: { title: '编辑报表', permission: [ 'report' ] }
          },
          {
            path: '/report/custom/view/:id',
            name: 'ReportView',
            hidden: true,
            component: () => import('@/views/report/View'),
            meta: { title: '查看报表', permission: [ 'report' ] }
          },
          {
            path: '/report/custom/preview/:code',
            name: 'ReportPreview',
            hidden: true,
            component: () => import('@/views/report/ReportPreview'),
            meta: { title: '预览报表', permission: [ 'report' ] }
          }
        ]
      },
      // Account
      {
        path: '/account',
        component: RouteView,
        redirect: '/account/center',
        name: 'account',
        meta: { title: '个人页', icon: 'user', keepAlive: true, permission: [ 'account' ] },
        hidden: true,
        children: [
          {
            path: '/account/settings',
            name: 'settings',
            component: () => import('@/views/account/settings/Index'),
            meta: { title: '个人设置', hideHeader: true, permission: [ 'account' ] },
            redirect: '/account/settings/base',
            hideChildrenInMenu: true,
            hidden: true,
            children: [
              {
                path: '/account/settings/base',
                name: 'BaseSettings',
                component: () => import('@/views/account/settings/BaseSetting'),
                meta: { title: '基本设置', hidden: true, permission: [ 'account' ] }
              }
            ]
          }
        ]
      },
      // 监控管理
      {
        path: '/monitor',
        name: 'Monitor',
        component: RouteView,
        redirect: '/monitor/cluster',
        meta: { title: '监控管理', icon: 'monitor', permission: [ 'monitor' ] },
        children: [
          {
            path: '/monitor/yarn',
            name: 'MonitorYarn',
            component: () => import('@/views/data-monitor/yarn-monitor/YarnMonitor'),
            meta: { title: 'YARN监控', permission: [ 'yarn-monitor' ] }
          },
          {
            path: '/monitor/cluster',
            name: 'MonitorCluster',
            component: () => import('@/views/data-monitor/cluster-monitor/ClusterMonitor'),
            meta: { title: '集群监控', permission: [ 'cluster-monitor' ] }
          }
        ]
      },
      // 系统管理
      {
        path: '/system',
        name: 'System',
        component: PageView,
        redirect: '/system/user',
        meta: { title: '系统管理', icon: 'tool', permission: [ 'system' ] },
        children: [
          {
            path: '/system/worker-group',
            name: 'WorkerGroup',
            component: () => import('@/views/system-manager/WorkerGroup'),
            meta: { title: 'worker分组', keepAlive: false, permission: [ 'worker-group' ] }
          },
          {
            path: '/system/tenant',
            name: 'Tenant',
            component: () => import('@/views/system-manager/Tenant'),
            meta: { title: '租户管理', keepAlive: false, permission: [ 'tenant-manage' ] }
          },
          {
            path: '/system/queue',
            name: 'Queue',
            component: () => import('@/views/system-manager/Queue'),
            meta: { title: '队列管理', keepAlive: false, permission: [ 'queue-manage' ] }
          },
          {
            path: '/system/user',
            name: 'SystemUser',
            component: () => import('@/views/system-manager/SystemUser'),
            meta: { title: '用户管理', keepAlive: false, permission: [ 'user-manage' ] }
          },
          {
            path: '/system/role',
            name: 'SystemRole',
            component: () => import('@/views/system-manager/SystemRole'),
            meta: { title: '角色分配', keepAlive: false, permission: [ 'role-assign' ] }
          },
          {
            path: '/system/menu',
            name: 'SystemMenu',
            component: () => import('@/views/system-manager/SystemMenu'),
            meta: { title: '菜单管理', keepAlive: false, permission: [ 'menu-manage' ] }
          }
        ]
      }
    ]
  },
  {
    path: '*', redirect: '/404'
  }
]

export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import('@/views/user/Login')
      },
      {
        path: 'recover',
        name: 'recover',
        component: undefined
      }
    ]
  },
  {
    path: '/test',
    component: BlankLayout,
    redirect: '/test/home',
    children: [
      {
        path: 'home',
        name: 'TestHome',
        component: () => import('@/views/Home')
      }
    ]
  },
  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  },
  {
    path: '/customReport/:reportCode',
    component: () => import('@/views/customReport/index')
  },
  {
    path: '/reportPreview/:reportCode',
    component: () => import('@/views/customReport/reportPreview/index')
  }
]
