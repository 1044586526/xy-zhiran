import request from '@/utils/request'

// 查询社保民主评议记录列表
export function listEvaluation(query) {
  return request({
    url: '/abuwx/evaluation/list',
    method: 'get',
    params: query
  })
}

// 查询社保民主评议记录详细
export function getEvaluation(id) {
  return request({
    url: '/abuwx/evaluation/' + id,
    method: 'get'
  })
}

// 新增社保民主评议记录
export function addEvaluation(data) {
  return request({
    url: '/abuwx/evaluation',
    method: 'post',
    data: data
  })
}

// 修改社保民主评议记录
export function updateEvaluation(data) {
  return request({
    url: '/abuwx/evaluation',
    method: 'put',
    data: data
  })
}

// 删除社保民主评议记录
export function delEvaluation(id) {
  return request({
    url: '/abuwx/evaluation/' + id,
    method: 'delete'
  })
}
