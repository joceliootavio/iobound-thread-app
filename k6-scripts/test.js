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
           maxVUs: 60,
           stages: [
               { duration: '1m', target: 1000 },
               { duration: '1m', target: 1000 },
           ],
//            stages: [ otimized
//                { duration: '2m', target: 50 },
//                { duration: '4m', target: 200 },
//                { duration: '4m', target: 300 },
//                { duration: '2m', target: 80 }
//            ],
        // progressive_vus: {
        //     executor: 'ramping-vus',  // Define um crescimento de RPS
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
    let uri = __ENV.APP_CONTEXT + "/mock/user?sleep=200"

//    let uri = __ENV.APP_CONTEXT + "/api/blocking/from-api/user?anyParam=1"
//                    + "&memoryOps=quadratic"
//                    + "&userFromRds=e07d7927-6659-4668-9b95-54f7ef91334b"
//                    + "&times=2&delay=600"

    let res = http.get(uri);

    check(res, {
        'status é 200': (r) => r.status === 200,
        'tempo de resposta < 500ms': (r) => r.timings.duration < 500,
    });
}
