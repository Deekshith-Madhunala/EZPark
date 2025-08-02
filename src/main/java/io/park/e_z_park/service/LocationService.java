package io.park.e_z_park.service;

import io.park.e_z_park.entity.Location;
import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.model.LocationDTO;
import io.park.e_z_park.repository.LocationRepository;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.util.NotFoundException;
import io.park.e_z_park.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final ParkingLotRepository parkingLotRepository;

    public LocationService(final LocationRepository locationRepository,
            final ParkingLotRepository parkingLotRepository) {
        this.locationRepository = locationRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<LocationDTO> findAll() {
        final List<Location> locations = locationRepository.findAll(Sort.by("id"));
        return locations.stream()
                .map(location -> mapToDTO(location, new LocationDTO()))
                .toList();
    }

    public LocationDTO get(final String id) {
        return locationRepository.findById(id)
                .map(location -> mapToDTO(location, new LocationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final LocationDTO locationDTO) {
        final Location location = new Location();
        mapToEntity(locationDTO, location);
        location.setId(locationDTO.getId());
        return locationRepository.save(location).getId();
    }

    public void update(final String id, final LocationDTO locationDTO) {
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(locationDTO, location);
        locationRepository.save(location);
    }

    public void delete(final String id) {
        locationRepository.deleteById(id);
    }

    private LocationDTO mapToDTO(final Location location, final LocationDTO locationDTO) {
        locationDTO.setId(location.getId());
        locationDTO.setStreet(location.getStreet());
        locationDTO.setCity(location.getCity());
        locationDTO.setState(location.getState());
        locationDTO.setZipCode(location.getZipCode());
        locationDTO.setCountry(location.getCountry());
        locationDTO.setLatitude(location.getLatitude());
        locationDTO.setLongitude(location.getLongitude());
        return locationDTO;
    }

    private Location mapToEntity(final LocationDTO locationDTO, final Location location) {
        location.setStreet(locationDTO.getStreet());
        location.setCity(locationDTO.getCity());
        location.setState(locationDTO.getState());
        location.setZipCode(locationDTO.getZipCode());
        location.setCountry(locationDTO.getCountry());
        location.setLatitude(locationDTO.getLatitude());
        location.setLongitude(locationDTO.getLongitude());
        return location;
    }

    public boolean idExists(final String id) {
        return locationRepository.existsByIdIgnoreCase(id);
    }

    public ReferencedWarning getReferencedWarning(final String id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Location location = locationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ParkingLot locationParkingLot = parkingLotRepository.findFirstByLocation(location);
        if (locationParkingLot != null) {
            referencedWarning.setKey("location.parkingLot.location.referenced");
            referencedWarning.addParam(locationParkingLot.getId());
            return referencedWarning;
        }
        return null;
    }

    public List<LocationDTO> findByCity(String cityName) {
        return locationRepository.findByCity(cityName);
    }

}
