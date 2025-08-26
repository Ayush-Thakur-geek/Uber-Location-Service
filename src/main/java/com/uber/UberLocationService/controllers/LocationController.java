package com.uber.UberLocationService.controllers;

import com.uber.UberLocationService.dtos.DriverLocationDTO;
import com.uber.UberLocationService.dtos.NearByDriverRequestDTO;
import com.uber.UberLocationService.dtos.SaveDriverLocationRequestDTO;
import com.uber.UberLocationService.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService redisLocationService;

    LocationController(LocationService redisLocationService) {
        this.redisLocationService = redisLocationService;
    }

    @PostMapping("/drivers")
    public ResponseEntity<Boolean> saveDriverLocation(
            @RequestBody SaveDriverLocationRequestDTO saveDriverLocationRequestDto
    ) {
        if (redisLocationService.saveDriverLocation(
                saveDriverLocationRequestDto.getDriverId(),
                saveDriverLocationRequestDto.getLatitude(),
                saveDriverLocationRequestDto.getLongitude())
        ) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/nearby/drivers")
    public ResponseEntity<List<DriverLocationDTO>> getNearByDrivers(@RequestBody NearByDriverRequestDTO nearByDriverRequestDTO) {
        List<DriverLocationDTO> drivers = redisLocationService.getNearByDrivers(
                nearByDriverRequestDTO.getLatitude(),
                nearByDriverRequestDTO.getLongitude()
        );
        if  (drivers.isEmpty()) {
            return new ResponseEntity<>(drivers, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }
}
