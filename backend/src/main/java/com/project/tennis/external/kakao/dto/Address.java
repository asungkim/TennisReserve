package com.project.tennis.external.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(
        @JsonProperty("address_name")
        String addressName,

        @JsonProperty("region_1depth_name")
        String region1depthName,

        @JsonProperty("region_2depth_name")
        String region2depthName,

        @JsonProperty("region_3depth_name")
        String region3depthName,

        @JsonProperty("mountain_yn")
        String mountainYn,

        @JsonProperty("main_address_no")
        String mainAddressNo,

        @JsonProperty("sub_address_no")
        String subAddressNo,

        @JsonProperty("zip_code")
        String zipCode
) {
}
