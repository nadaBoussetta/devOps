package devOps.mappers;

import devOps.dtos.PublicationRequestDTO;
import devOps.dtos.PublicationResponseDTO;
import devOps.models.PublicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublicationMapper {

    // Entity → ResponseDTO
    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "repondeur.id", target = "repondeurId")
    PublicationResponseDTO toDto(PublicationEntity entity);

    // RequestDTO → Entity (partiel, complété dans le service)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    @Mapping(target = "repondeur", ignore = true)
    PublicationEntity toEntity(PublicationRequestDTO dto);
}
