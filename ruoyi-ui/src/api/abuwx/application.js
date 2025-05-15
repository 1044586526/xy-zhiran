import request from '@/utils/request'

// 查询灾害救助申报列表
export function listApplication(query) {
  return request({
    url: '/abuwx/application/list',
    method: 'get',
    params: query
  })
}

// 查询灾害救助申报详细
export function getApplication(id) {
  return request({
    url: '/abuwx/application/' + id,
    method: 'get'
  })
}

// 新增灾害救助申报
export function addApplication(data) {
  return request({
    url: '/abuwx/application',
    method: 'post',
    data: data
  })
}

// 修改灾害救助申报
export function updateApplication(data) {
  return request({
    url: '/abuwx/application',
    method: 'put',
    data: data
  })
}

// 删除灾害救助申报
export function delApplication(id) {
  return request({
    url: '/abuwx/application/' + id,
    method: 'delete'
  })
}
