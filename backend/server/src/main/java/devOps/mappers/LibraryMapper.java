package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.dtos.LibraryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    // Convertit une entité vers un DTO
    LibraryResponseDTO toResponse(LibraryEntity entity);

    // Convertit un DTO vers une entité
    @Mapping(target = "id", ignore = true) // Hibernate gère l'ID
    LibraryEntity toEntity(LibraryResponseDTO dto);
}
