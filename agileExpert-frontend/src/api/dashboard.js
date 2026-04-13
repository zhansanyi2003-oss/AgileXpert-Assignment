import request from './request'

export function getDashboardData() {
  return request.get('/dashboard')
}

export function runSimulation() {
  return request.post('/dashboard/simulation')
}
