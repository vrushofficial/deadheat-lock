package com.vrush.microservices.financial.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import com.vrush.microservices.booking.dtos.BookingPayloadDTO;
import com.vrush.microservices.financial.annotations.TrackMethod;
import com.vrush.microservices.financial.dtos.PaymentAnalyzedDTO;
import com.vrush.microservices.financial.dtos.TransactionDTO;
import com.vrush.microservices.financial.entities.Transaction;
import com.vrush.microservices.financial.enums.BookingStateEnum;
import com.vrush.microservices.financial.mappers.TransactionMapper;
import com.vrush.microservices.financial.repositories.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper mapper;

    @TrackMethod
    @Transactional
    public Transaction save(@NonNull final TransactionDTO dto) {
        return repository.save(mapper.toEntity(dto));
    }

    @TrackMethod
    @Transactional(readOnly = true)
    public Page<TransactionDTO> search(@NonNull final Pageable pageable, Specification<Transaction> specification) {
        return mapper.toDTO(repository.findAll(specification, pageable));
    }

    @TrackMethod
    @Transactional(readOnly = true)
    public TransactionDTO findByIdFromResource(@NonNull UUID id) {
        return mapper.toDTO(repository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    @TrackMethod
    @Transactional(readOnly = true)
    public Transaction findById(@NonNull UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Transaction with id " + id + " not found."));
    }

    @TrackMethod
    public PaymentAnalyzedDTO analyze(@NonNull final BookingPayloadDTO dto) {
        return new PaymentAnalyzedDTO(dto.getIdBooking(), BookingStateEnum.getRandomAnalysis());
    }

}
