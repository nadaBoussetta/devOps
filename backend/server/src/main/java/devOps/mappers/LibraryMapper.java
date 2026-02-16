package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.dtos.LibraryResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    public LibraryResponseDTO toResponse(LibraryEntity entity);

    public LibraryEntity toEntity(LibraryResponseDTO dto);
}
