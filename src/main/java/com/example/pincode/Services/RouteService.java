package com.example.pincode.Services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.example.pincode.Entity.Route;
import com.example.pincode.Repository.RouteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    // HERE API key
    private static final String HERE_API_KEY = "sbW3lEMk18idy0BEXtnc5etNJjBVw-x9vLo5OOrm2yo";

    public Route getRoute(String fromPincode, String toPincode) {
        
        Optional<Route> cachedRoute = routeRepository.findByFromPincodeAndToPincode(fromPincode, toPincode);
        if (cachedRoute.isPresent()) {
            return cachedRoute.get();
        }

        
        String fromCoordinates = getCoordinatesFromPincode(fromPincode);
        String toCoordinates = getCoordinatesFromPincode(toPincode);

        
        Route route = fetchRouteFromAPI(fromCoordinates, toCoordinates);

        
        routeRepository.save(route);
        return route;
    }

    
    private Route fetchRouteFromAPI(String fromCoordinates, String toCoordinates) {
        String url = "https://router.hereapi.com/v8/routes?transportMode=car&origin="
                + fromCoordinates + "&destination=" + toCoordinates + "&return=summary&apiKey=" + HERE_API_KEY;

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            
            System.out.println("HERE Routing API Response: " + response.getBody());

            
            double distance = parseDistanceFromResponse(response.getBody());
            String duration = parseDurationFromResponse(response.getBody());
            String routeInfo = parseRouteInfo(response.getBody());

            return new Route(fromCoordinates, toCoordinates, distance, duration, routeInfo);

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error fetching route from HERE API: " + e.getMessage());
        }
    }

    private String getCoordinatesFromPincode(String pincode) {
        
        String url = "https://geocode.search.hereapi.com/v1/geocode?q=" + pincode + "&apiKey=" + HERE_API_KEY;

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            
            System.out.println("HERE Geocoding API Response: " + response.getBody());

            
            String jsonResponse = response.getBody();
            return parseCoordinatesFromResponse(jsonResponse);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error fetching coordinates from HERE API: " + e.getMessage());
        }
    }

    
    private String parseCoordinatesFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode itemsNode = root.path("items");
            if (itemsNode.isArray() && itemsNode.size() > 0) {
                JsonNode positionNode = itemsNode.get(0).path("position");
                String latitude = positionNode.path("lat").asText();
                String longitude = positionNode.path("lng").asText();
                return latitude + "," + longitude;  // Latitude first, then Longitude
            } else {
                throw new RuntimeException("Coordinates not found for pincode.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error parsing coordinates from HERE Geocoding API response: " + e.getMessage());
        }
    }

    
    private double parseDistanceFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode routesNode = root.path("routes");
            if (routesNode.isArray() && routesNode.size() > 0) {
                JsonNode sectionsNode = routesNode.get(0).path("sections");
                if (sectionsNode.isArray() && sectionsNode.size() > 0) {
                    JsonNode summaryNode = sectionsNode.get(0).path("summary");
                    JsonNode lengthNode = summaryNode.path("length");

                    if (!lengthNode.isMissingNode()) {
                        return lengthNode.asDouble() / 1000;  // Convert meters to kilometers
                    }
                }
            }
            return 0.0;

        } catch (Exception e) {
            throw new RuntimeException("Error parsing distance from HERE Routing API response: " + e.getMessage());
        }
    }

    
    private String parseDurationFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode routesNode = root.path("routes");
            if (routesNode.isArray() && routesNode.size() > 0) {
                JsonNode sectionsNode = routesNode.get(0).path("sections");
                if (sectionsNode.isArray() && sectionsNode.size() > 0) {
                    JsonNode summaryNode = sectionsNode.get(0).path("summary");
                    JsonNode durationNode = summaryNode.path("duration");

                    if (!durationNode.isMissingNode()) {
                        int durationInSeconds = durationNode.asInt();
                        int hours = durationInSeconds / 3600;
                        int minutes = (durationInSeconds % 3600) / 60;

                        return hours + "h " + minutes + "m";
                    }
                }
            }
            return "Duration not available";

        } catch (Exception e) {
            throw new RuntimeException("Error parsing duration from HERE Routing API response: " + e.getMessage());
        }
    }

    
    private String parseRouteInfo(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode routesNode = root.path("routes");
            if (routesNode.isArray() && routesNode.size() > 0) {
                JsonNode sectionsNode = routesNode.get(0).path("sections");
                if (sectionsNode.isArray() && sectionsNode.size() > 0) {
                    return "Route found with valid sections.";
                }
            }
            return "No valid route found between the specified locations.";

        } catch (Exception e) {
            throw new RuntimeException("Error parsing route info from HERE Routing API response: " + e.getMessage());
        }
    }
}
