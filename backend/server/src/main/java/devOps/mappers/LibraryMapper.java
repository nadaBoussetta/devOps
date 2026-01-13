package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    public LibraryResponseDTO toResponse(LibraryEntity entity);

    public LibraryEntity toEntity(LibraryResponseDTO dto);
}
