package m.ilyina.find_trining.mapper;

import m.ilyina.find_trining.dto.sport.SportResponse;
import m.ilyina.find_trining.entity.Sport;
import org.springframework.stereotype.Component;

@Component
public class SportMapper {

    public SportResponse toResponse(Sport sport) {
        return new SportResponse(sport.getId(), sport.getName());
    }
}
