// 统一请求路径前缀在libs/axios.js中修改
import { getRequest, postRequest, putRequest, deleteRequest } from '@/libs/axios';

// 获取模型列表数据
export const getModelList = (params) => {
    return getRequest('/taskModel/getByCondition', params)
}

// 获取全部已发布模型
export const getAllModelList = () => {
    return getRequest('/taskModel/getAllList')
}

// 获取ModelXML
export const getXml = (id) => {
    return getRequest(`/taskModel/getXml/${id}`)
}

// 保存ModelXML
export const saveXml = (id, params) => {
    return postRequest(`/taskModel/saveXml/${id}`, params)
}

// 模型发布
export const releaseModel = (id) => {
    return postRequest(`/taskModel/release/${id}`)
}

// 添加模型
export const addModel = (params) => {
    return postRequest('/taskModel/add', params)
}

// 启用或者禁用模型
export const updateStatus = (id) => {
    return postRequest(`/taskModel/status/${id}`)
}

// 批量通过ids删除
export const deleteModelByIds = (ids) => {
    return deleteRequest(`/taskModel/delAllByIds/${ids}`)
}


/**********************************************************************/
// 获取任务实例列表数据
export const getInstanceList = (params) => {
    return getRequest('/taskInstance/getByCondition', params)
}

// 开启任务
export const startTask = (id) => {
    return postRequest(`/taskInstance/start/${id}`)
}

// 暂停任务
export const suspend = (id) => {
    return postRequest(`/taskInstance/suspend/${id}`)
}

// 获取任务实例列表数据
export const queryPhase = (id) => {
    return getRequest(`/taskInstance/queryPhase/${id}`)
}

// 添加任务实例
export const addInstance = (params) => {
    return postRequest('/taskInstance/add', params)
}

// 更新任务实例
export const updateInstance = (params) => {
    return postRequest('/taskInstance/update', params)
}

// 批量通过ids删除
export const deleteInstanceByIds = (ids) => {
    return deleteRequest(`/taskInstance/delAllByIds/${ids}`)
}


/**********************************************************************/
// 获取任务流程列表数据
export const getProcessList = (params) => {
    return getRequest('/taskProcess/getByCondition', params)
}

// 更新流程
export const updateProcess = (params) => {
    return postRequest('/taskProcess/update', params)
}
