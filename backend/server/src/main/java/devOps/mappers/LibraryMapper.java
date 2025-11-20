package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    @Mapping(source = "tournee.id", target = "tourneeId", ignore = true)
    LibraryResponseDTO toResponse(LibraryEntity libraryEntity);

    @Mapping(target = "id", ignore = true) // Laisser Hibernate générer l'ID
    @Mapping(source = "tourneeId", target = "tournee.id", ignore = true)
    LibraryEntity toEntity(LibraryResponseDTO libraryResponseDTO);
}
