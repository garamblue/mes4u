import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent
    if (getToken()) {
      // the server (AuthTokenFilter) reads the JWT from the 'Authorization: Bearer' header
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config
  },
  error => {
    // do something with request error
    // console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    return res
  },
  error => {
    // console.log(error.response) // for debug
    const response = error.response || {}
    const data = response.data
    let message = typeof data === 'string' ? data : (data && (data.message || data.error)) || error.message
    if (response.status === 403) {
      message = 'You do not have permission to perform this action.'
    }
    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
