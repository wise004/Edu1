import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://13.53.190.127:7777', // moslashtiring
  timeout: 100000,
});

export default instance;
