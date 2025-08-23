package com.uber.UberLocationService.service;

import com.uber.UberLocationService.dtos.DriverLocationDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RedisLocationServiceImpl implements LocationService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String DRIVER_GEO_OPS_KEY = "drivers";

    private static final Double SEARCH_RADIUS = 5.0;

    public RedisLocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {

        try {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            geoOps.add(
                    DRIVER_GEO_OPS_KEY,
                    new RedisGeoCommands.GeoLocation<>(
                            driverId,
                            new Point(
                                    latitude,
                                    longitude
                            )
                    )
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public List<DriverLocationDTO> getNearByDrivers(Double latitude, Double longitude) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Distance distance = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
        Circle within = new Circle(
                new Point(latitude, longitude),
                distance
        );
        try {
            GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY, within);
            assert results != null;
            return results.getContent()
                    .stream()
                    .map(GeoResult -> {
                        Point point = geoOps.position(DRIVER_GEO_OPS_KEY, GeoResult.getContent().getName()).get(0);
                        return DriverLocationDTO.builder()
                                    .driverId(GeoResult.getContent().getName())
                                    .latitude(point.getX())
                                    .longitude(point.getY())
                                    .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            List<DriverLocationDTO> errorList = new ArrayList<>();
            log.error(e.getMessage());
            return errorList;
        }
    }
}
