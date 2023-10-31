package starlinksApi;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;


public class ShipmentsScenarios {


    private ChainBuilder createShipment = CoreDsl
            .exec(HttpDsl.http("create shipment api call")
                    .post("/shipments")
                    .body(CoreDsl.ElFileBody("bodies/newShipmentWithTwoParcels.json")).asJson()
                    .check(CoreDsl.jmesPath("track_number").saveAs("track_number")));

    private ChainBuilder cancelShipment = CoreDsl
            .exec(HttpDsl.http("cancel shipment")
                    .post("/shipments/#{track_number}/cancel")
                    .queryParam("api_key", "test2022")
                    .check(HttpDsl.status().is(200),
                            CoreDsl.substring("\"Canceled\"")));

    private ChainBuilder shipmentTracking = CoreDsl
            .exec(HttpDsl.http("tracking of shipment")
                    .get("/shipment/history")
                    .queryParam("api_key", "test2022")
                    .queryParam("tracking_number", "#{track_number}")
                    .check(CoreDsl.substring("created"),
                            CoreDsl.substring("canceled")));


    private ScenarioBuilder createShipmentScenario = CoreDsl.scenario("Create shipment")
            .exec(createShipment);

    private ScenarioBuilder cancelShipmentScenario = CoreDsl.scenario("Cancel shipment")
            .exec(createShipment).pause(2).exec(cancelShipment);


    private ScenarioBuilder shipmentTrackingScenario = CoreDsl.scenario("Get shipment tracking")
            .exec(createShipment).pause(2).exec(cancelShipment).pause(2).exec(shipmentTracking);

    public ScenarioBuilder getCreateShipmentScenario(){
        return createShipmentScenario;
    }

    public ScenarioBuilder getShipmentTrackingScenario(){
        return shipmentTrackingScenario;
    }

    public ScenarioBuilder getCancelShipmentScenario(){
        return cancelShipmentScenario;
    }
}
