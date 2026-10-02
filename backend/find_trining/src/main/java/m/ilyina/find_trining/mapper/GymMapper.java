package m.ilyina.find_trining.mapper;

import m.ilyina.find_trining.dto.gym.GymCreateRequest;
import m.ilyina.find_trining.dto.gym.GymResponse;
import m.ilyina.find_trining.dto.gym.GymUpdateRequest;
import m.ilyina.find_trining.entity.Gym;
import org.springframework.stereotype.Component;

@Component
public class GymMapper {

    public Gym toEntity(GymCreateRequest request) {
        Gym gym = new Gym();
        gym.setName(request.name().trim());
        gym.setAddress(request.address().trim());
        gym.setCity(request.city().trim());
        gym.setPhone(request.phone());
        gym.setDescription(request.description());
        return gym;
    }

    public void updateEntity(Gym gym, GymUpdateRequest request) {
        gym.setName(request.name().trim());
        gym.setAddress(request.address().trim());
        gym.setCity(request.city().trim());
        gym.setPhone(request.phone());
        gym.setDescription(request.description());
    }

    public GymResponse toResponse(Gym gym) {
        return new GymResponse(
                gym.getId(),
                gym.getName(),
                gym.getAddress(),
                gym.getCity(),
                gym.getPhone(),
                gym.getDescription(),
                gym.getCreatedAt()
        );
    }
}
