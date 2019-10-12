// 统一请求路径前缀在libs/axios.js中修改
import { getRequest, postRequest, putRequest, deleteRequest, importRequest, uploadFileRequest } from '@/libs/axios';

// 获取数据源配置列表数据
export const getDatabaseConfigList = (params) => {
    return postRequest('/databaseConfig/getByCondition', params)
}
// 添加数据源配置
export const addDatabaseConfig = (params) => {
    return postRequest('/databaseConfig/add', params)
}
// 编辑数据源配置
export const editDatabaseConfig = (params) => {
    return postRequest('/databaseConfig/edit', params)
}
// 删除数据源配置
export const deleteDatabaseConfig = (id, params) => {
    return deleteRequest(`/databaseConfig/delById/${id}`, params)
}
// 批量删除数据源配置
export const deleteDatabaseConfigs = (ids, params) => {
    return deleteRequest(`/databaseConfig/delByIds/${ids}`, params)
}
// 批量删除数据源配置
export const mydeleteDatabaseConfigs = (ids, params) => {
    return deleteRequest(`/databaseConfig/mydelByIds/${ids}`, params)
}
// 初始化数据源
export const initDatabaseConfig = (params) => {
    return postRequest('/databaseConfig/initpool', params)
}
// 关闭数据源配置
export const closeDatabaseConfig = (params) => {
    return postRequest('/databaseConfig/closepool', params)
}
// 启用数据源配置
export const restartDatabaseConfig = (params) => {
    return postRequest('/databaseConfig/restartpool', params)
}

