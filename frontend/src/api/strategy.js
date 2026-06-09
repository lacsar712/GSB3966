import request from '@/utils/request'

export function getStrategyList(params) {
  return request({
    url: '/strategy',
    method: 'get',
    params
  })
}

export function getStrategyDetail(id) {
  return request({
    url: `/strategy/${id}`,
    method: 'get'
  })
}

export function createStrategy(data) {
  return request({
    url: '/strategy',
    method: 'post',
    data
  })
}

export function updateStrategy(id, data) {
  return request({
    url: `/strategy/${id}`,
    method: 'put',
    data
  })
}

export function deleteStrategy(id) {
  return request({
    url: `/strategy/${id}`,
    method: 'delete'
  })
}

export function submitForReview(id) {
  return request({
    url: `/strategy/${id}/submit`,
    method: 'post'
  })
}

export function reviewStrategy(id, data) {
  return request({
    url: `/strategy/${id}/review`,
    method: 'post',
    data
  })
}

export function startStrategy(id) {
  return request({
    url: `/strategy/${id}/start`,
    method: 'post'
  })
}

export function pauseStrategy(id) {
  return request({
    url: `/strategy/${id}/pause`,
    method: 'post'
  })
}

export function archiveStrategy(id) {
  return request({
    url: `/strategy/${id}/archive`,
    method: 'post'
  })
}
