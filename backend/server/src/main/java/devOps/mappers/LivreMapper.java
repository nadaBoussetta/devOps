package devOps.mappers;

import devOps.models.LivreEntity;
import devOps.responses.LivreResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LivreMapper {

    LivreResponseDTO toResponse(LivreEntity entity);
}
