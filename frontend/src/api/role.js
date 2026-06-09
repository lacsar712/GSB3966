import request from '@/utils/request'

export function getRoleList() {
  return request({
    url: '/role',
    method: 'get'
  })
}
