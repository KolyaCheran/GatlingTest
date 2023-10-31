package main;

import config.Config;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import starlinksApi.ParcelsScenarios;
import starlinksApi.ShipmentsScenarios;

import static io.gatling.javaapi.core.CoreDsl.*;

public class MainRunner extends Simulation {

    ShipmentsScenarios shipmentsScenarios = new ShipmentsScenarios();
    ParcelsScenarios parcelsScenarios = new ParcelsScenarios();

    private HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(Config.getBaseUrl());

    {
        setUp(
                shipmentsScenarios.getCreateShipmentScenario().injectOpen(constantUsersPerSec(2).during(600)),
                parcelsScenarios.getCreateParcelScenario().injectOpen(constantUsersPerSec(2).during(600)),
                parcelsScenarios.getCancelParcelScenario().injectOpen(rampUsers(1).during(1)),
                parcelsScenarios.getCreateCancelTrackParcelScenario().injectOpen(rampUsers(1).during(1)),
                parcelsScenarios.getTrackingByDateRangeScenario().injectOpen(rampUsers(1).during(1)),
                shipmentsScenarios.getCancelShipmentScenario().injectOpen(rampUsers(1).during(1)),
                shipmentsScenarios.getShipmentTrackingScenario().injectOpen(rampUsers(1).during(1))
        ).protocols(httpProtocol);
    }
}
