import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const taskApi = {
  getAll: () => api.get('/tasks'),
  getToday: () => api.get('/tasks/today'),
  getById: (id) => api.get(`/tasks/${id}`),
  create: (task) => api.post('/tasks', task),
  update: (id, task) => api.put(`/tasks/${id}`, task),
  delete: (id) => api.delete(`/tasks/${id}`)
}

export const recordApi = {
  getToday: () => api.get('/records/today'),
  getByTaskId: (taskId) => api.get(`/records/task/${taskId}`),
  getById: (id) => api.get(`/records/${id}`),
  create: (record) => api.post('/records', record),
  getStatistics: () => api.get('/records/statistics')
}

export default api
