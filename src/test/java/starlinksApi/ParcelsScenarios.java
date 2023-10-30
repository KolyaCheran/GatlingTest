package starlinksApi;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParcelsScenarios {

    private LocalDateTime now = LocalDateTime.now();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    private ScenarioBuilder getTrackingByDateRange = CoreDsl.scenario("Get tracking by date range")
            .exec(HttpDsl.http("get tracking by date range")
                    .post("/date-range-history")
                    .body(CoreDsl.StringBody("{\n" +
                            "  \"api_key\": \"test2022\",\n" +
                            "  \"date_from\": \"" + now.minusDays(5).format(formatter) + " 00:00:00\",\n" +
                            "  \"date_to\": \"" + now.format(formatter) + " 23:59:59\"\n" +
                            "}"))
                    .check(CoreDsl.substring("Parcel created"),
                            CoreDsl.substring("total_pages_count")));


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

    public ScenarioBuilder getCreateCancelTrackParcelScenario(){
        return createCancelTrackParcel;
    }

    public ScenarioBuilder getGetTrackingByDateRangeScenario(){
        return getTrackingByDateRange;
    }
}
