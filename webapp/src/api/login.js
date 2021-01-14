import { axios } from '@/utils/request'

export function login (parameter) {
  return axios({
    url: '/login',
    method: 'post',
    params: parameter
  })
}

export function getInfo () {
  return axios({
    url: '/user/info',
    method: 'get',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function getCurrentUserNav (token) {
  return axios({
    url: '/user/nav',
    method: 'get'
  })
}

export function logout () {
  return axios({
    url: '/signOut',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
