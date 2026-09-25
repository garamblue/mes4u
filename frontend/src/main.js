import { createApp } from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import Cookies from 'js-cookie'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import locale from 'element-plus/es/locale/lang/en' // lang i18n
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import icons from '@/icons' // icon
import '@/permission' // permission control

// map the legacy size names stored in the 'size' cookie to Element Plus sizes
const sizeMap = { medium: 'default', small: 'default', mini: 'small' }
const cookieSize = Cookies.get('size') || 'medium'

const app = createApp(App)

// set ElementPlus lang to EN
app.use(ElementPlus, {
  locale,
  size: sizeMap[cookieSize] || cookieSize // set element-plus default size
})

// register all Element Plus icons globally (used as <el-button icon="Search" />)
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  app.component(key, component)
})

app.use(icons)
app.use(store)
app.use(router)

app.mount('#app')
