package devOps.mappers;

import devOps.models.LibraryEntity;
import devOps.responses.LibraryResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class LibraryMapper {

    public LibraryResponseDTO toResponse(LibraryEntity entity) {
        LibraryResponseDTO dto = new LibraryResponseDTO();
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    public LibraryEntity toEntity(LibraryResponseDTO dto) {
        LibraryEntity entity = new LibraryEntity();
        entity.setName(dto.getName());
        entity.setAddress(dto.getAddress());
        return entity;
    }
}
