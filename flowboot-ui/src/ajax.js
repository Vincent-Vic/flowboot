import axios from 'axios'
import qs from 'qs'
import {Message} from "element-ui";
import router from "./router";
import {getToken} from "@/utils/auth";
import el from "element-ui/src/locale/lang/el";
import store from "@/store";

const TIME_OUT = 30000 // 超时时间30秒
axios.defaults.baseURL = "/api"

// 请求数据拦截处理
axios.interceptors.request.use(config => {

    let token = getToken();
    if (token){
        config.headers['Authorization'] = 'Bearer '+token
    }


    return config
}, error => {
    return Promise.reject(error)
})

// 返回数据拦截处理
axios.interceptors.response.use(response => {
    //code... 你的逻辑
    let res = response.data;
    if (res.code != 200){
        let errMsg = res=== undefined ? "系统异常": res.data ?res.data:res.msg?res.msg:"系统异常"
        Message.error(errMsg)
        return Promise.reject(res)
    }
    return response.data.data //直接返回后台返回的json object
}, error => {
    var uri = window.location.pathname
    if (error.response.data){
        error.message = error.response.data.msg;
    }

    if (error.response.status === 401){
        if (uri !== '/login') {
            store.dispatch('FedLogOut').then(() => {
                Message.error("登入过期请重新登入");
            }).catch(err => {
                console.log(err)
            })
            router.push("/login")
        }

    } else if (error.response.status === 403){
        router.push("/403")
    } else if (error.response.status === 404){
        router.push("/404")
    } else {
        console.log("ajax err ",error)
        Message.error(error.message);
    }
    return Promise.reject(error.response);
})

/*
* 封装一个私有的请求方法
*/
const _request = (method, url, data) => {
    const headers = {}
    const configData = {
        url, // 请求的地址
        timeout: TIME_OUT, // 超时时间, 单位毫秒
        headers
    }
    if (method === 'get') {
        configData.method = 'get'
        configData.params = data// get 请求的数据
    } else if (method === 'postForm') {
        configData.method = 'post'
        if (data instanceof FormData) {
            configData.headers['Content-Type'] = 'multipart/form-data; charset=UTF-8'
            configData.data = data
        } else {
            configData.headers['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8'
            configData.data = qs.stringify(data)
        }
    } else if (method === 'postJson') {
        configData.method = 'post'
        configData.headers['Content-Type'] = 'application/json; charset=UTF-8'
        configData.data = data
    }
    return axios(configData)
}

class Ajax {
    get = (url, data = {}) => {
        return _request('get', url, data)
    }

    postForm = (url, data = {}) => {
        return _request('postForm', url, data)
    }

    postJson = (url, data = {}) => {
        return _request('postJson', url, data)
    }

    post = this.postJson // 默认post的Content-Type是application/json
}

export default (Vue) => {
    if (typeof window !== 'undefined' && window.Vue) {
        Vue = window.Vue
    }
    Vue.prototype.$ajax = new Ajax()
}

const ajax=new Ajax();
export {
    ajax
}
