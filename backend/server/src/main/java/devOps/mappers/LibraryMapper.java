package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    LibraryResponseDTO toResponse(LibraryEntity entity);

    LibraryEntity toEntity(LibraryResponseDTO dto);
}
