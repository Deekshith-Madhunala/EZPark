package io.park.e_z_park.service;

import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class ParkingLLMService {

    private static final String HF_API_URL = "https://router.huggingface.co/v1/chat/completions";
    private static final String HF_API_KEY = "API_KEY";
    private static final String HF_MODEL = "moonshotai/Kimi-K2-Instruct-0905";

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public String askParkingLLM(String userQuery) {
        // 1. Fetch parking lots
        List<ParkingLot> lots = parkingLotRepository.findAll();

        JSONArray lotArray = new JSONArray();
        for (ParkingLot lot : lots) {
            JSONObject obj = new JSONObject();
            obj.put("id", lot.getId());
            obj.put("name", lot.getName());
            obj.put("availableSpots", lot.getAvailableSpots());
            obj.put("totalSpots", lot.getTotalSpots());
            obj.put("pricePerHour", lot.getPricePerHour());
            obj.put("type", lot.getType());
            obj.put("openingTime", lot.getOpeningTime().toString());
            obj.put("closingTime", lot.getClosingTime().toString());

            // Include location details
            if (lot.getLocation() != null) {
                JSONObject loc = new JSONObject();
                loc.put("id", lot.getLocation().getId());
                loc.put("street", lot.getLocation().getStreet());
                loc.put("city", lot.getLocation().getCity());
                loc.put("state", lot.getLocation().getState());
                loc.put("zipCode", lot.getLocation().getZipCode());
                loc.put("country", lot.getLocation().getCountry());
                loc.put("latitude", lot.getLocation().getLatitude());
                loc.put("longitude", lot.getLocation().getLongitude());
                obj.put("location", loc);
            }

            // Include slots
            obj.put("slots", lot.getSlots());

            lotArray.put(obj);
        }

        // 2. Fetch reservations
        List<Reservation> reservations = reservationRepository.findAll();
        JSONArray reservationArray = new JSONArray();
        for (Reservation res : reservations) {
            JSONObject r = new JSONObject();
            r.put("id", res.getId());
            r.put("startTime", res.getStartTime().toString());
            r.put("endTime", res.getEndTime().toString());
            r.put("status", res.getStatus());
            r.put("pricePaid", res.getPricePaid());
            r.put("userId", res.getUser() != null ? res.getUser().getId() : null);
            r.put("parkingLotId", res.getParkingLot() != null ? res.getParkingLot().getId() : null);
            reservationArray.put(r);
        }

        // 3. Build prompt
        String prompt = buildPrompt(userQuery, lotArray, reservationArray);

        log.info("ParkingLots JSON: {}", lotArray.toString());
        log.info("Reservations JSON: {}", reservationArray.toString());

        // 4. Call LLM
        return callHuggingFaceLLM(prompt);
    }

    private String buildPrompt(String userQuery, JSONArray parkingLots, JSONArray reservations) {
        String lower = userQuery.toLowerCase();
        String template;

        if (lower.contains("available") || lower.contains("free") || lower.contains("price")) {
            template = "You are a Parking Assistant.\n"
                    + "Parking lot data:\n{parkingLots}\n"
                    + "Reservation data:\n{reservations}\n"
                    + "Answer the user query in JSON format with only relevant fields.\n"
                    + "User question: \"{userQuery}\"";
        } else if (lower.contains("open") || lower.contains("closing") || lower.contains("opening")) {
            template = "You are a Parking Assistant.\n"
                    + "Parking lot data:\n{parkingLots}\n"
                    + "Reservation data:\n{reservations}\n"
                    + "Answer operational details (openingTime, closingTime, type) in JSON.\n"
                    + "User question: \"{userQuery}\"";
        } else if (lower.contains("book") || lower.contains("reserve") || lower.contains("cancel")) {
            template = "You are a Parking Assistant.\n"
                    + "Parking lot data:\n{parkingLots}\n"
                    + "Reservation data:\n{reservations}\n"
                    + "Answer suggested actions (reserve/cancel) in JSON format, without performing them.\n"
                    + "User question: \"{userQuery}\"";
        } else {
            template = "You are a Parking Assistant.\n"
                    + "Parking lot data:\n{parkingLots}\n"
                    + "Reservation data:\n{reservations}\n"
                    + "Answer the user query in JSON format.\n"
                    + "User question: \"{userQuery}\"";
        }

        template = template.replace("{parkingLots}", parkingLots.toString());
        template = template.replace("{reservations}", reservations.toString());
        template = template.replace("{userQuery}", userQuery);

        return template;
    }

    private String callHuggingFaceLLM(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            JSONArray messages = new JSONArray();
            messages.put(message);

            JSONObject body = new JSONObject();
            body.put("model", HF_MODEL);
            body.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(HF_API_KEY);

            HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(HF_API_URL, request, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

            // Remove code block markers
            content = content.replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling LLM: " + e.getMessage();
        }
    }
}
