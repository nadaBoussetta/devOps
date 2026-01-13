package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LibraryMapper {

    public LibraryResponseDTO toResponse(LibraryEntity entity) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setHeuresOuverture(entity.getHeuresOuverture());
        return dto;
    }

    public LibraryEntity toEntity(LibraryResponseDTO dto) {
        LibraryEntity entity = new LibraryEntity();
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        entity.setHeuresOuverture(dto.getHeuresOuverture());
        return entity;
    }
}
