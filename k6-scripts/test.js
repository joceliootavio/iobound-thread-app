import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let options = {
    insecureSkipTLSVerify: true,
    scenarios: {
       progressive_rps: {
           executor: 'ramping-arrival-rate',  // Define um crescimento de RPS
           startRate: 10,  // Começa com 10 RPS
           timeUnit: '1s', // Medido por segundo
           preAllocatedVUs: 10,
           maxVUs: 20,
           stages: [
               { duration: '2m', target: 200 },
               { duration: '1m', target: 300 },
           ],
        // progressive_vus: {
        //     executor: 'ramping-vus',
        //     stages: [
        //         { duration: '1m', target: 50 },
        //         { duration: '2m', target: 200 },
        //         { duration: '1m', target: 200 },
        //     ],
        }
    },
    thresholds: {
        'http_req_failed': [
            {
                threshold: 'rate<0.05',
                abortOnFail: true
            }
        ],
        'http_req_duration': [
            {
                threshold: `p(95)<2000`,
                abortOnFail: true,
                delayAbortEval: '10s'
            }
        ]
    }
};

export default function () {
//    let uri = __ENV.APP_CONTEXT + "/api/coroutine/from-api/user?delay=200&times=2&async=true"
//    let uri = __ENV.APP_CONTEXT + "/api/reactive/from-api/user?delay=200&times=2"
//    let uri = __ENV.APP_CONTEXT + "/mock/user?sleep=200"

    let uri = __ENV.APP_CONTEXT + "/api/blocking/from-api/user?anyParam=1"
                    + "&memoryOps=quadratic"
                    + "&userFromRds=random"
//                    + "&async=true"
                    + "&times=1&delay=200"

    let res = http.get(uri);

    check(res, {
        'status é 200': (r) => r.status === 200,
        'tempo de resposta < 500ms': (r) => r.timings.duration < 500,
    });
}
