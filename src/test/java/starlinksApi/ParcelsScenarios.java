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

    private ScenarioBuilder createParcelScenario = CoreDsl.scenario("Create new parcel")
            .exec(createParcel);
    private ScenarioBuilder cancelParcelScenario = CoreDsl.scenario("Cancel parcel")
            .exec(createParcel).pause(5).exec(cancelParcel) ;

    public ScenarioBuilder getCreateParcelScenario(){
        return createParcelScenario;
    }

    public ScenarioBuilder getCancelParcelScenario(){
        return cancelParcelScenario;
    }
}
