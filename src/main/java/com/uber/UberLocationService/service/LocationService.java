package com.uber.UberLocationService.service;

import com.uber.UberLocationService.dtos.DriverLocationDTO;

import java.util.List;

public interface LocationService {

    Boolean saveDriverLocation(String driverId, Double latitude, Double longitude);
    List<DriverLocationDTO>  getNearByDrivers(Double latitude, Double longitude);

}
