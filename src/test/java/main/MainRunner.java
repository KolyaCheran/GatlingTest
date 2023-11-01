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
    private final int defaultDurationInSec = 300;

    private HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(Config.getBaseUrl());

    {
        setUp(
                shipmentsScenarios.getCreateShipmentScenario().injectOpen(constantUsersPerSec(1).during(defaultDurationInSec)),
                parcelsScenarios.getCreateParcelScenario().injectOpen(constantUsersPerSec(1).during(defaultDurationInSec)),
                parcelsScenarios.getCancelParcelScenario().injectOpen(rampUsers(50).during(defaultDurationInSec)),
                parcelsScenarios.getCreateCancelTrackParcelScenario().injectOpen(rampUsers(50).during(defaultDurationInSec)),
                parcelsScenarios.getTrackingByDateRangeScenario().injectOpen(rampUsers(25).during(defaultDurationInSec)),
                shipmentsScenarios.getCancelShipmentScenario().injectOpen(rampUsers(50).during(defaultDurationInSec)),
                shipmentsScenarios.getShipmentTrackingScenario().injectOpen(rampUsers(50).during(defaultDurationInSec))
        ).protocols(httpProtocol);
    }
}
