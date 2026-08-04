package com.jonathan.picpay.Mappers;

import com.jonathan.picpay.Entity.Transfer;
import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TransferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userReceive", ignore = true)
    @Mapping(target = "userSend", ignore = true)
    Transfer toEntity (TransferRequest transferRequest);

    TransferResponse toResponse(Transfer transfer);
}
