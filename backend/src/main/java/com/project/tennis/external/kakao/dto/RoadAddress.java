package com.project.tennis.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoadAddress(
        @JsonProperty("address_name")
        String addressName,

        @JsonProperty("region_1depth_name")
        String region1depthName,

        @JsonProperty("region_2depth_name")
        String region2depthName,

        @JsonProperty("region_3depth_name")
        String region3depthName,

        @JsonProperty("road_name")
        String roadName,

        @JsonProperty("underground_yn")
        String undergroundYn,

        @JsonProperty("main_building_no")
        String mainBuildingNo,

        @JsonProperty("sub_building_no")
        String subBuildingNo,

        @JsonProperty("building_name")
        String buildingName,

        @JsonProperty("zone_no")
        String zoneNo
) {
}
