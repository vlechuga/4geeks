package com.fourgeeks.test.server.mappers;

import com.fourgeeks.test.server.domain.dtos.PersonDto;
import com.fourgeeks.test.server.domain.entities.Person;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    PersonDto personToPersonDto(Person person);

}
