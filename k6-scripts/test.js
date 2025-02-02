import http from "k6/http";
import { check } from "k6";

/*export let options = {
    stages: [
        // Ramp-up from 1 to 30 VUs in 30s
        { duration: "10s", target: 5 },

        // Stay on 30 VUs for 60s
        { duration: `${__ENV.DURATION || "60s"}`, target: `${__ENV.VUS || 30}` },

        // Ramp-down from 30 to 0 VUs in 10s
        { duration: "10s", target: 0 }
    ]
};*/

export let options = {
    scenarios: {
        constant_rps: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS || 100, // Número de requisições por segundo (RPS)
            timeUnit: "1s", // Unidade de tempo para o rate
            duration: __ENV.DURATION || "60s", // Duração total do teste
            preAllocatedVUs: __ENV.VUS || 4, // Número de VUs pré-alocados
            maxVUs: 50, // Número máximo de VUs
        },
    },
};

export default function() {
//    let res = http.get("http://httpbin.org/");
    let baseUrl = __ENV.BASE_URL || "http://java-app:8080/api/v1/customers/123";
    let res = http.get(baseUrl);
    check(res, { "status is 200": (r) => r.status === 200 });
}