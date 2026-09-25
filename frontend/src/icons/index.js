import 'virtual:svg-icons-register' // svg sprite (vite-plugin-svg-icons)
import SvgIcon from '@/components/SvgIcon' // svg component

export default {
  install(app) {
    // register globally
    app.component('SvgIcon', SvgIcon)
  }
}
