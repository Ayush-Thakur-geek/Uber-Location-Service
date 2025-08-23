package com.uber.UberLocationService.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverLocationDTO {

    String driverId;
    Double latitude;
    Double longitude;

}
