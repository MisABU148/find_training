package m.ilyina.find_trining.mapper;

import m.ilyina.find_trining.dto.coach.CoachCreateRequest;
import m.ilyina.find_trining.dto.coach.CoachResponse;
import m.ilyina.find_trining.dto.coach.CoachUpdateRequest;
import m.ilyina.find_trining.entity.Coach;
import org.springframework.stereotype.Component;

@Component
public class CoachMapper {

    private final GymMapper gymMapper;
    private final SportMapper sportMapper;

    public CoachMapper(GymMapper gymMapper, SportMapper sportMapper) {
        this.gymMapper = gymMapper;
        this.sportMapper = sportMapper;
    }

    public Coach toEntity(CoachCreateRequest request) {
        Coach coach = new Coach();
        applyBasicFields(coach, request.fullName(), request.email(), request.phone(), request.bio(), request.experienceYears());
        return coach;
    }

    public void updateEntity(Coach coach, CoachUpdateRequest request) {
        applyBasicFields(coach, request.fullName(), request.email(), request.phone(), request.bio(), request.experienceYears());
    }

    private void applyBasicFields(Coach coach, String fullName, String email, String phone, String bio, Integer experienceYears) {
        coach.setFullName(fullName.trim());
        coach.setEmail(email.trim().toLowerCase());
        coach.setPhone(phone);
        coach.setBio(bio);
        coach.setExperienceYears(experienceYears == null ? 0 : experienceYears);
    }

    public CoachResponse toResponse(Coach coach) {
        return new CoachResponse(
                coach.getId(),
                coach.getFullName(),
                coach.getEmail(),
                coach.getPhone(),
                coach.getBio(),
                coach.getExperienceYears(),
                coach.getGym() == null ? null : gymMapper.toResponse(coach.getGym()),
                coach.getSports().stream().map(sportMapper::toResponse).collect(java.util.stream.Collectors.toSet()),
                coach.getCreatedAt()
        );
    }
}
