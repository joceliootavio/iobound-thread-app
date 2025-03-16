import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    insecureSkipTLSVerify: true,
    scenarios: {
        progressive_rps: {
            executor: 'ramping-arrival-rate',  // Define um crescimento de RPS
            startRate: 10,  // Começa com 10 RPS
            timeUnit: '1s', // Medido por segundo
            preAllocatedVUs: 20,
            maxVUs: 50,
            stages: [
                { duration: '1m', target: 500 },
                { duration: '2m', target: 1000 },
//                { duration: '2m', target: 1000 },
            ],
//            stages: [ otimized
//                { duration: '2m', target: 50 },
//                { duration: '4m', target: 200 },
//                { duration: '4m', target: 300 },
//                { duration: '2m', target: 80 }
//            ],
//            stages: [ // reactive
//                { duration: '1m', target: 50 },
//                { duration: '2m', target: 200 },
//                { duration: '2m', target: 300 },
//                { duration: '2m', target: 500 },
//                { duration: '1m', target: 500 },
//                { duration: '1m', target: 80 }
//            ],
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
                threshold: `p(95)<${__ENV.P95_TARGET}`,
                abortOnFail: true,
                delayAbortEval: '30s'
            }
        ]
    }
};

export default function () {
    let uri = __ENV.APP_CONTEXT + __ENV.APP_PATH;
    let res = http.get(uri);

    check(res, {
        'status é 200': (r) => r.status === 200,
        'tempo de resposta < 500ms': (r) => r.timings.duration < 500,
    });
}
