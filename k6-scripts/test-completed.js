import http from "k6/http";
import { check } from "k6";

export let options = {
    insecureSkipTLSVerify: true,
    scenarios: {
        warmup_phase: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS / 2, // Começa com metade do RPS
            timeUnit: "1s",
            duration: "4m", // Warm-up por 4 minutos
            preAllocatedVUs: __ENV.MAX_VUS / 2,
            maxVUs: __ENV.MAX_VUS / 2,
        },
        full_load: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS, // RPS completo
            timeUnit: "1s",
            startTime: "4m", // Começa após a fase de aquecimento
            duration: "5m",
            preAllocatedVUs: __ENV.MAX_VUS / 2,
            maxVUs: __ENV.MAX_VUS,
        },
         peak_load: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS * 1.2, // RPS dobrado
            timeUnit: "1s",
            startTime: "9m",
            duration: "4m",
            preAllocatedVUs: __ENV.MAX_VUS,
            maxVUs: __ENV.MAX_VUS * 1.5,
        },
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

export default function() {
    let baseUrl = __ENV.APP_CONTEXT + __ENV.APP_PATH;
    let res = http.get(baseUrl);
    check(res, { "status is 200": (r) => r.status === 200 });
}
