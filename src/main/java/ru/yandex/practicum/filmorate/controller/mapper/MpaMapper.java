package ru.yandex.practicum.filmorate.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.request.MpaRequest;
import ru.yandex.practicum.filmorate.model.response.MpaResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MpaMapper {

    Mpa toMpa(MpaRequest mpaRequest);

    MpaResponse toResponse(Mpa mpa);
}
