package com.river.module.affiliate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTypeEnum {

    OAUTH2(1, "OAuth2"),
    BEARER_TOKEN(2, "Bearer Token"),
    API_KEY(3, "API Key"),
    BASIC_AUTH(4, "Basic Auth");

    private final Integer code;
    private final String name;
}
