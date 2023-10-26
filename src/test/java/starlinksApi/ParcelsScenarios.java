package starlinksApi;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;

public class ParcelsScenarios {

    private ScenarioBuilder createParcelScenario = CoreDsl.scenario("Create new parcel")
            .exec(HttpDsl.http("create parcel api call")
                    .post("/parcels")
                    .body(CoreDsl.ElFileBody("bodies/newParcel.json")).asJson()
                    .check(CoreDsl.jmesPath("tracking_number").saveAs("track_number")));

    public ScenarioBuilder getCreateParcelScenario(){
        return createParcelScenario;
    }
}
