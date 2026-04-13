import request from './request'

export function createApp(app) {
  return request.post('/apps', app)
}

export function updateApp(appId, app) {
  return request.patch(`/apps/${appId}`, app)
}

export function deleteApp(appId) {
  return request.delete(`/apps/${appId}`)
}
