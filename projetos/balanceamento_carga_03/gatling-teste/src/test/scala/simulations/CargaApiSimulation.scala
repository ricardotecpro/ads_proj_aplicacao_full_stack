// CargaApiSimulation.scala
package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CargaApiSimulation extends Simulation {

  // 1. Configuração do Protocolo HTTP
  val httpProtocol = http
    .baseUrl("http://localhost:8081") // URL base do Serviço A
    .acceptHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // 2. Definição do Cenário
  val scn = scenario("Cenário de Carga API Pedidos")
    .exec(
      http("Req: Fazer Pedido")
        .get("/fazer-pedido")
        .check(status.is(200)) // Verifica se a resposta é HTTP 200
    )

  // 3. Configuração da Carga (Injeção de Usuários)
  setUp(
    scn.inject(
      // rampUsersPerSec(10).to(100).during(10.seconds), // Rampa de subida
      // nothingFor(5.seconds), // Pausa
      constantUsersPerSec(100).during(30.seconds) // Carga constante
    )
  ).protocols(httpProtocol)
}