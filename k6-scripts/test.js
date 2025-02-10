import http from "k6/http";
import { check } from "k6";

export let options = {
    scenarios: {
        constant_rps: {
            executor: "constant-arrival-rate",
            rate: __ENV.RPS || 100, // Número de requisições por segundo (RPS)
            timeUnit: "1s", // Unidade de tempo para o rate
            duration: __ENV.DURATION || "60s", // Duração total do teste
            preAllocatedVUs: __ENV.VUS || 4, // Número de VUs pré-alocados
            maxVUs: __ENV.MAX_VUS || 50, // Número máximo de VUs
        },
    },
};

export default function() {
    let baseUrl = __ENV.BASE_URL || "http://java-app:8080/api/v1/customers/123";
    let res = http.get(baseUrl);
    check(res, { "status is 200": (r) => r.status === 200 });
}