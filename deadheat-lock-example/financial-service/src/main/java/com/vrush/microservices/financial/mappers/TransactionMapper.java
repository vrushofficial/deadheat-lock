package com.vrush.microservices.financial.mappers;

import java.util.List;

import com.vrush.microservices.financial.dtos.TransactionDTO;
import com.vrush.microservices.financial.entities.Transaction;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(TransactionDTO dto);

    TransactionDTO toDTO(Transaction entity);

    List<TransactionDTO> toDTO(List<Transaction> entities);

    default Page<TransactionDTO> toDTO(Page<Transaction> page) {
        List<TransactionDTO> responses = toDTO(page.getContent());
        return new PageImpl<>(responses, page.getPageable(), page.getTotalElements());
    }
}
