import request from '../utils/request'

export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const getActivities = () => {
  return request({
    url: '/activities',
    method: 'get'
  })
}

export const getPublicActivities = () => {
  return request({
    url: '/activities/public',
    method: 'get'
  })
}

export const getActivityTimeSlots = (activityId) => {
  return request({
    url: `/activities/${activityId}/time-slots`,
    method: 'get'
  })
}

export const getAppointments = (params) => {
  return request({
    url: '/appointments',
    method: 'get',
    params
  })
}

export const getAppointmentDetail = (id) => {
  return request({
    url: `/appointments/${id}`,
    method: 'get'
  })
}

export const createAppointment = (data) => {
  return request({
    url: '/appointments/public',
    method: 'post',
    data
  })
}

export const checkIn = (id) => {
  return request({
    url: `/appointments/${id}/check-in`,
    method: 'post'
  })
}

export const assignBarber = (id, volunteerId) => {
  return request({
    url: `/appointments/${id}/assign-barber`,
    method: 'post',
    params: { volunteerId }
  })
}

export const completeAppointment = (id) => {
  return request({
    url: `/appointments/${id}/complete`,
    method: 'post'
  })
}

export const cancelAppointment = (id) => {
  return request({
    url: `/appointments/${id}/cancel`,
    method: 'post'
  })
}

export const getBuildings = () => {
  return request({
    url: '/buildings',
    method: 'get'
  })
}

export const getVolunteers = () => {
  return request({
    url: '/volunteers',
    method: 'get'
  })
}

export const getAvailableVolunteers = () => {
  return request({
    url: '/volunteers/available',
    method: 'get'
  })
}

export const getTools = (params) => {
  return request({
    url: '/tools',
    method: 'get',
    params
  })
}

export const getToolTypes = () => {
  return request({
    url: '/tool-types',
    method: 'get'
  })
}

export const useTool = (toolId, volunteerId, appointmentId) => {
  return request({
    url: `/tool-disinfestations/${toolId}/use`,
    method: 'post',
    params: { volunteerId, appointmentId }
  })
}

export const returnTool = (recordId) => {
  return request({
    url: `/tool-disinfestations/usage/${recordId}/return`,
    method: 'post'
  })
}

export const getDisinfestations = (params) => {
  return request({
    url: '/tool-disinfestations/disinfestations',
    method: 'get',
    params
  })
}

export const completeDisinfestation = (id) => {
  return request({
    url: `/tool-disinfestations/disinfestations/${id}/complete`,
    method: 'post'
  })
}

export const getHomeServices = (params) => {
  return request({
    url: '/home-services',
    method: 'get',
    params
  })
}

export const scheduleHomeService = (id, data) => {
  return request({
    url: `/home-services/${id}/schedule`,
    method: 'post',
    data
  })
}

export const startHomeService = (id) => {
  return request({
    url: `/home-services/${id}/start`,
    method: 'post'
  })
}

export const completeHomeService = (id, feedback) => {
  return request({
    url: `/home-services/${id}/complete`,
    method: 'post',
    params: { feedback }
  })
}

export const getActivityStatistics = (id) => {
  return request({
    url: `/activity-reports/${id}/statistics`,
    method: 'get'
  })
}

export const exportAppointments = (id) => {
  window.open(`/api/activity-reports/${id}/export`, '_blank')
}
