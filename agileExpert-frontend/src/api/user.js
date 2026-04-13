import request from './request'

export function getUser(id) {
  return request.get(`/users/${id}`)
}

export function createUser(name) {
  return request.post('/users', { name })
}

export function renameUser(id, name) {
  return request.patch(`/users/${id}`, { name })
}

export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

export function selectTheme(userId, themeId) {
  return request.put(`/users/${userId}/theme/${themeId}`)
}

export function selectWallpaper(userId, wallpaperId) {
  return request.put(`/users/${userId}/wallpaper/${wallpaperId}`)
}
