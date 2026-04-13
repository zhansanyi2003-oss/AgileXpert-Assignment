import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 60000,
})

request.interceptors.response.use(
  (response) => {
    if (response.status === 204) {
      return null
    }

    const result = response.data

    if (result && typeof result === 'object' && 'code' in result) {
      if (result.code === 1) {
        return result.data ?? null
      }

      return Promise.reject(new Error(result.msg || 'Request failed.'))
    }

    return result
  },
  (error) => {
    const response = error?.response
    const data = response?.data

    if (typeof data === 'string' && data.trim()) {
      return Promise.reject(new Error(data))
    }

    if (data?.msg) {
      return Promise.reject(new Error(data.msg))
    }

    if (data?.message) {
      return Promise.reject(new Error(data.message))
    }

    if (error?.message) {
      return Promise.reject(new Error(error.message))
    }

    return Promise.reject(new Error('Request failed.'))
  },
)

export default request
