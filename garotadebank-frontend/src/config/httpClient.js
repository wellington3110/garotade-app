import * as axios from 'axios';

export default axios.default.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
});
