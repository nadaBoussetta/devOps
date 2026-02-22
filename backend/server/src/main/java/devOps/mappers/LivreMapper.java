package devOps.mappers;

import devOps.models.LivreEntity;
import devOps.dtos.LivreResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivreMapper {

    LivreResponseDTO toResponse(LivreEntity entity);
}
