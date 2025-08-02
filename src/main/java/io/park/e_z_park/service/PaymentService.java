package io.park.e_z_park.service;

import io.park.e_z_park.entity.Payment;
import io.park.e_z_park.entity.Reservation;
import io.park.e_z_park.model.PaymentDTO;
import io.park.e_z_park.repository.PaymentRepository;
import io.park.e_z_park.repository.ReservationRepository;
import io.park.e_z_park.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final PaymentRepository paymentRepository,
            final ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<PaymentDTO> findAll() {
        final List<Payment> payments = paymentRepository.findAll(Sort.by("id"));
        return payments.stream()
                .map(payment -> mapToDTO(payment, new PaymentDTO()))
                .toList();
    }

    public PaymentDTO get(final String id) {
        return paymentRepository.findById(id)
                .map(payment -> mapToDTO(payment, new PaymentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public String create(final PaymentDTO paymentDTO) {
        final Payment payment = new Payment();
        mapToEntity(paymentDTO, payment);
        payment.setId(paymentDTO.getId());
        return paymentRepository.save(payment).getId();
    }

    public void update(final String id, final PaymentDTO paymentDTO) {
        final Payment payment = paymentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(paymentDTO, payment);
        paymentRepository.save(payment);
    }

    public void delete(final String id) {
        paymentRepository.deleteById(id);
    }

    private PaymentDTO mapToDTO(final Payment payment, final PaymentDTO paymentDTO) {
        paymentDTO.setId(payment.getId());
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setStatus(payment.getStatus());
        paymentDTO.setMethod(payment.getMethod());
        paymentDTO.setTransactionId(payment.getTransactionId());
        paymentDTO.setPaidAt(payment.getPaidAt());
        paymentDTO.setReservation(payment.getReservation() == null ? null : payment.getReservation().getId());
        return paymentDTO;
    }

    private Payment mapToEntity(final PaymentDTO paymentDTO, final Payment payment) {
        payment.setAmount(paymentDTO.getAmount());
        payment.setStatus(paymentDTO.getStatus());
        payment.setMethod(paymentDTO.getMethod());
        payment.setTransactionId(paymentDTO.getTransactionId());
        payment.setPaidAt(paymentDTO.getPaidAt());
        final Reservation reservation = paymentDTO.getReservation() == null ? null : reservationRepository.findById(paymentDTO.getReservation())
                .orElseThrow(() -> new NotFoundException("reservation not found"));
        payment.setReservation(reservation);
        return payment;
    }

    public boolean idExists(final String id) {
        return paymentRepository.existsByIdIgnoreCase(id);
    }

}
