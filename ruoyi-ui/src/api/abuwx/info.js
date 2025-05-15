import request from '@/utils/request'

// 查询用户基本信息列表
export function listInfo(query) {
  return request({
    url: '/abuwx/info/list',
    method: 'get',
    params: query
  })
}

// 查询用户基本信息详细
export function getInfo(id) {
  return request({
    url: '/abuwx/info/' + id,
    method: 'get'
  })
}

// 新增用户基本信息
export function addInfo(data) {
  return request({
    url: '/abuwx/info',
    method: 'post',
    data: data
  })
}

// 修改用户基本信息
export function updateInfo(data) {
  return request({
    url: '/abuwx/info',
    method: 'put',
    data: data
  })
}

// 删除用户基本信息
export function delInfo(id) {
  return request({
    url: '/abuwx/info/' + id,
    method: 'delete'
  })
}
