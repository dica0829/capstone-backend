package com.zoopick.server.dto.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.entity.MatchType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchConfirmResponse {
    @JsonProperty("match_id")
    @NotNull
    Long matchId;

    @JsonProperty("match_type")
    @NotNull
    MatchType matchType;

    @JsonProperty("found_item_id")
    @NotNull
    Long foundItemId;

    @JsonProperty("counterpart_id")
    Long counterpartId;
}
