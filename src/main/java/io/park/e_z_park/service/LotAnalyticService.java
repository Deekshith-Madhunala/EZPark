package io.park.e_z_park.service;

import io.park.e_z_park.entity.LotAnalytic;
import io.park.e_z_park.entity.ParkingLot;
import io.park.e_z_park.model.LotAnalyticDTO;
import io.park.e_z_park.repository.LotAnalyticRepository;
import io.park.e_z_park.repository.ParkingLotRepository;
import io.park.e_z_park.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LotAnalyticService {

    private final LotAnalyticRepository lotAnalyticRepository;
    private final ParkingLotRepository parkingLotRepository;

    public LotAnalyticService(final LotAnalyticRepository lotAnalyticRepository,
            final ParkingLotRepository parkingLotRepository) {
        this.lotAnalyticRepository = lotAnalyticRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<LotAnalyticDTO> findAll() {
        final List<LotAnalytic> lotAnalytics = lotAnalyticRepository.findAll(Sort.by("id"));
        return lotAnalytics.stream()
                .map(lotAnalytic -> mapToDTO(lotAnalytic, new LotAnalyticDTO()))
                .toList();
    }

    public LotAnalyticDTO get(final String id) {
        return lotAnalyticRepository.findById(id)
                .map(lotAnalytic -> mapToDTO(lotAnalytic, new LotAnalyticDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final LotAnalyticDTO lotAnalyticDTO) {
        final LotAnalytic lotAnalytic = new LotAnalytic();
        mapToEntity(lotAnalyticDTO, lotAnalytic);
        lotAnalytic.setId(lotAnalyticDTO.getId());
        return lotAnalyticRepository.save(lotAnalytic).getId();
    }

    public void update(final String id, final LotAnalyticDTO lotAnalyticDTO) {
        final LotAnalytic lotAnalytic = lotAnalyticRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lotAnalyticDTO, lotAnalytic);
        lotAnalyticRepository.save(lotAnalytic);
    }

    public void delete(final String id) {
        lotAnalyticRepository.deleteById(id);
    }

    private LotAnalyticDTO mapToDTO(final LotAnalytic lotAnalytic,
            final LotAnalyticDTO lotAnalyticDTO) {
        lotAnalyticDTO.setId(lotAnalytic.getId());
        lotAnalyticDTO.setReportDate(lotAnalytic.getReportDate());
        lotAnalyticDTO.setTotalBookings(lotAnalytic.getTotalBookings());
        lotAnalyticDTO.setTotalRevenue(lotAnalytic.getTotalRevenue());
        lotAnalyticDTO.setAvgOccupancy(lotAnalytic.getAvgOccupancy());
        lotAnalyticDTO.setParkingLot(lotAnalytic.getParkingLot() == null ? null : lotAnalytic.getParkingLot().getId());
        return lotAnalyticDTO;
    }

    private LotAnalytic mapToEntity(final LotAnalyticDTO lotAnalyticDTO,
            final LotAnalytic lotAnalytic) {
        lotAnalytic.setReportDate(lotAnalyticDTO.getReportDate());
        lotAnalytic.setTotalBookings(lotAnalyticDTO.getTotalBookings());
        lotAnalytic.setTotalRevenue(lotAnalyticDTO.getTotalRevenue());
        lotAnalytic.setAvgOccupancy(lotAnalyticDTO.getAvgOccupancy());
        final ParkingLot parkingLot = lotAnalyticDTO.getParkingLot() == null ? null : parkingLotRepository.findById(lotAnalyticDTO.getParkingLot())
                .orElseThrow(() -> new NotFoundException("parkingLot not found"));
        lotAnalytic.setParkingLot(parkingLot);
        return lotAnalytic;
    }

    public boolean idExists(final String id) {
        return lotAnalyticRepository.existsByIdIgnoreCase(id);
    }

}
