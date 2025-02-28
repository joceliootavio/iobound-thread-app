import http from "k6/http";
import { check } from "k6";

export let options = {
    scenarios: {
        warmup_phase: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS / 2, // Começa com metade do RPS
            timeUnit: "1s",
            duration: "4m", // Warm-up por 4 minutos
            preAllocatedVUs: __ENV.VUS,
            maxVUs: __ENV.VUS,
        },
        full_load: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS, // RPS completo
            timeUnit: "1s",
            startTime: "4m", // Começa após a fase de aquecimento
            duration: __ENV.DURATION,
            preAllocatedVUs: __ENV.VUS,
            maxVUs: __ENV.MAX_VUS,
        },
    },
};

export default function() {
    let baseUrl = "http://" + __ENV.APP_HOST + ":" + __ENV.APP_PORT + __ENV.APP_PATH;
    let res = http.get(baseUrl);
    check(res, { "status is 200": (r) => r.status === 200 });
}
