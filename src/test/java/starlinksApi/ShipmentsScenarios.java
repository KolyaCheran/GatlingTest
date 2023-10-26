package starlinksApi;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;


public class ShipmentsScenarios {


    private ScenarioBuilder createShipmentScenario = CoreDsl.scenario("Create new shipment")
            .exec(HttpDsl.http("create shipment api call")
                    .post("/shipments")
                    .body(CoreDsl.ElFileBody("bodies/newShipmentWithTwoParcels.json")).asJson()
                    .check(CoreDsl.jmesPath("track_number").saveAs("track_number")));

    public ScenarioBuilder getCreateShipmentScenario(){
        return createShipmentScenario;
    }
}
