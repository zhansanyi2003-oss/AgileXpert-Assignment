import request from './request'

export function uploadWallpaper(file, name) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('name', name)

  return request.post('/wallpapers', formData)
}
