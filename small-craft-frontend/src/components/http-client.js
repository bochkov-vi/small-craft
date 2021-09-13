import axios from "axios";

export const AXIOS = axios.create({
    baseURL: `http://localhost:8080/small-craft/rest`,
    headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'POST,GET',
        'Access-Control-Allow-Headers': '*'
    }
    /*baseURL: `http://localhost:8080/api`,
    headers: {
        'Access-Control-Allow-Origin': 'http://localhost:8080',
        'Access-Control-Allow-Methods': 'POST,GET',
        'Access-Control-Allow-Headers': '*'
      }*/
})