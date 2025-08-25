package com.first.challenge.model.state;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class State {
    private UUID stateId;
    private String name;
    private String description;
}
