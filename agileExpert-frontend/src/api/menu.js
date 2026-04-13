import request from './request'

export function createSubmenu(userId, payload) {
  return request.post(`/users/${userId}/menu/submenus`, payload)
}

export function addRootApp(userId, appId) {
  return request.post(`/users/${userId}/menu/root-apps/${appId}`)
}

export function addSubmenuApp(userId, submenuId, appId) {
  return request.post(`/users/${userId}/menu/submenu-apps`, { submenuId, appId })
}

export function renameMenuItem(userId, itemId, name) {
  return request.patch(`/users/${userId}/menu/items/${itemId}`, { name })
}

export function deleteMenuItem(userId, itemId) {
  return request.delete(`/users/${userId}/menu/items/${itemId}`)
}

export function launchMenuItem(userId, itemId) {
  return request.post(`/users/${userId}/menu/items/${itemId}/launch`)
}
