/**
 * author：AbuCoder
 * date:2023年4月7日13:53:23
 * 正式上线，baseUrl请换成线上地址
 */

// export const baseUrl = 'https://abucoder.rasmall.cn/prod-api/wxapi/';//上线时使用https地址
// export const uploadUrl = baseUrl+'/upload'//图片上传地址,上线时使用https地址
export const baseUrl = 'http://localhost:8083/wxapi/';//本地调试用该地址
// export const baseUrl = 'http://10.168.7.96:8083/wxapi/';//本地调试用该地址
export const uploadUrl = baseUrl+'upload'//图片上传地址
//获取验证码接口
export const getVerify = baseUrl + "getVerify"
//验证通过，验证验证码
export const checkVerify = baseUrl + "checkVerify?vaibleStr="
//登录接口
export const fetchLogin = baseUrl + "login"