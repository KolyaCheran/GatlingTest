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
                    .post("/api/v1/parcels")
                    .body(CoreDsl.ElFileBody("bodies/newParcel.json")).asJson()
                    .check(CoreDsl.jmesPath("tracking_number").saveAs("track_number")));

    private ChainBuilder cancelParcel = CoreDsl.exec(
                HttpDsl.http("cancel parcel api call")
                .post("/api/v1/cancel")
                .body(CoreDsl.ElFileBody("bodies/cancelParcel.json")).asJson()
                .check(HttpDsl.status().is(200),
                        CoreDsl.substring("\"Canceled\""),
                        CoreDsl.substring("\"#{track_number}\"")));

    private ChainBuilder trackParcel = CoreDsl.exec(
                HttpDsl.http("get tracking of parcel")
                        .post("/api/v1/history")
                        .body(CoreDsl.ElFileBody("bodies/getTrackingForParcel.json")).asJson()
                        .check(CoreDsl.substring("\"#{track_number}\""),
                                CoreDsl.substring("Parcel created"),
                                CoreDsl.substring("Parcel canceled")));

    private ChainBuilder scanParcelByDWS = CoreDsl.exec(
            HttpDsl.http("dws scan web hook")
                    .post("/webhook/dws-result-telegram")
                    .header("Authorization", "Bearer b40ae0b2e23575c6b76185054fcfffb86d2c899c0cd4fc0463993934707858f1")
                    .body(CoreDsl.StringBody("RT;00000000;GR;01;j;004;#{track_number};0000000010;kg;0000;0200;0300;0400;0000;;;0"))
                    .check(HttpDsl.status().is(200)));

    private ScenarioBuilder trackingByDateRange = CoreDsl.scenario("Get tracking by date range")
            .exec(HttpDsl.http("get tracking by date range")
                    .post("/api/v1/date-range-history")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(CoreDsl.StringBody("{\n" +
                            "  \"api_key\": \"test2022\",\n" +
                            "  \"date_from\": \"" + now.minusDays(5).format(formatter) + " 00:00:00\",\n" +
                            "  \"date_to\": \"" + now.format(formatter) + " 23:59:59\"\n" +
                            "}"))
                    .check(CoreDsl.substring("Parcel created"),
                            CoreDsl.substring("total_pages_count")));


    private ScenarioBuilder createParcelAndDwsScanScenario = CoreDsl.scenario("Create new parcel")
            .exec(createParcel).pause(1).exec(scanParcelByDWS);
    private ScenarioBuilder cancelParcelScenario = CoreDsl.scenario("Cancel parcel")
            .exec(createParcel).pause(1).exec(cancelParcel);
    private ScenarioBuilder createCancelTrackParcel = CoreDsl.scenario("Create - Cancel - Tracking")
            .exec(createParcel).pause(1).exec(cancelParcel).pause(1).exec(trackParcel);

    public ScenarioBuilder getCreateParcelAndDwsScanScenario(){
        return createParcelAndDwsScanScenario;
    }

    public ScenarioBuilder getCancelParcelScenario(){
        return cancelParcelScenario;
    }

    public ScenarioBuilder getCreateCancelTrackParcelScenario(){
        return createCancelTrackParcel;
    }

    public ScenarioBuilder getTrackingByDateRangeScenario(){
        return trackingByDateRange;
    }
}
