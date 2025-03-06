import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    insecureSkipTLSVerify: true,
    scenarios: {
        progressive_rps: {
            executor: 'ramping-arrival-rate',  // Define um crescimento de RPS
            startRate: 10,  // Começa com 10 RPS
            timeUnit: '1s', // Medido por segundo
            preAllocatedVUs: 100,
            maxVUs: 300,
            stages: [
                { duration: '1m', target: 1000 },
                { duration: '2m', target: 2000 },
                { duration: '1m', target: 0 }
            ],
        }
    },
    thresholds: {
        'http_req_duration': [`p(95)<${__ENV.P95_TARGET}`],  // 95% das requisições devem ser menores que 500ms
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
