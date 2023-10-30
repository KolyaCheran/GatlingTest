package starlinksApi;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;

public class ParcelsScenarios {

    private ChainBuilder createParcel = CoreDsl.exec(HttpDsl.http("create parcel api call")
                    .post("/parcels")
                    .body(CoreDsl.ElFileBody("bodies/newParcel.json")).asJson()
                    .check(CoreDsl.jmesPath("tracking_number").saveAs("track_number")));

    private ChainBuilder cancelParcel = CoreDsl.exec(
                HttpDsl.http("cancel parcel api call")
                .post("/cancel")
                .body(CoreDsl.ElFileBody("bodies/cancelParcel.json")).asJson()
                .check(HttpDsl.status().is(200),
                        CoreDsl.substring("\"Canceled\""),
                        CoreDsl.substring("\"#{track_number}\"")));

    private ChainBuilder trackParcel = CoreDsl.exec(
                HttpDsl.http("get tracking of parcel")
                        .post("/history")
                        .body(CoreDsl.ElFileBody("bodies/getTrackingForParcel.json")).asJson()
                        .check(CoreDsl.substring("\"#{track_number}\""),
                                CoreDsl.substring("Parcel created"),
                                CoreDsl.substring("Parcel canceled")));

    private ScenarioBuilder createParcelScenario = CoreDsl.scenario("Create new parcel")
            .exec(createParcel);
    private ScenarioBuilder cancelParcelScenario = CoreDsl.scenario("Cancel parcel")
            .exec(createParcel).pause(2).exec(cancelParcel);
    private ScenarioBuilder createCancelTrackParcel = CoreDsl.scenario("Create - Cancel - Tracking")
            .exec(createParcel).pause(2).exec(cancelParcel).pause(2).exec(trackParcel);

    public ScenarioBuilder getCreateParcelScenario(){
        return createParcelScenario;
    }

    public ScenarioBuilder getCancelParcelScenario(){
        return cancelParcelScenario;
    }

    public ScenarioBuilder getCreateCancelTrackParcel(){
        return createCancelTrackParcel;
    }
}
