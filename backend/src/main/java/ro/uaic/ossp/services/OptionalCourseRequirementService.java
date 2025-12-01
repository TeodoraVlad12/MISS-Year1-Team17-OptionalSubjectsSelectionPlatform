package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.ossp.dtos.OptionalCourseRequirementDTO;
import ro.uaic.ossp.dtos.OptionalCourseRequirementResponseDTO;
import ro.uaic.ossp.models.*;
import ro.uaic.ossp.repositories.*;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionalCourseRequirementService {

    private final OptionalCourseRequirementRepository reqRepo;
    private final MandatoryCourseRepository mandatoryRepo;
    private final OptionalCourseRepository optionalRepo;

    public List<OptionalCourseRequirementResponseDTO> getRequirements(Long optionalId) {
        return reqRepo.findByOptionalCourseId(optionalId)
                .stream()
                .map(r -> OptionalCourseRequirementResponseDTO.builder()
                        .id(r.getId())
                        .mandatoryId(r.getMandatoryCourse().getId())
                        .mandatoryName(r.getMandatoryCourse().getName())
                        .percentage(r.getPercentage())
                        .build()
                )
                .toList();
    }

    @Transactional
    public OptionalCourseRequirementResponseDTO createRequirement(Long optionalId, OptionalCourseRequirementDTO dto) {

        OptionalCourse optional = optionalRepo.findById(optionalId)
                .orElseThrow(() -> new NotFoundException("Optional course not found"));

        MandatoryCourse mandatory = mandatoryRepo.findById(dto.getMandatoryCourseId())
                .orElseThrow(() -> new NotFoundException("Mandatory course not found"));

        if (reqRepo.existsByOptionalCourseIdAndMandatoryCourseId(optionalId, dto.getMandatoryCourseId())) {
            throw new BadRequestException("This mandatory course already has a percentage assigned.");
        }

        if (dto.getPercentage() < 0 || dto.getPercentage() > 100) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }

        OptionalCourseRequirement req = OptionalCourseRequirement.builder()
                .mandatoryCourse(mandatory)
                .optionalCourse(optional)
                .percentage(dto.getPercentage())
                .build();

        reqRepo.save(req);

        return OptionalCourseRequirementResponseDTO.builder()
                .id(req.getId())
                .mandatoryId(mandatory.getId())
                .mandatoryName(mandatory.getName())
                .percentage(req.getPercentage())
                .build();
    }

    @Transactional
    public OptionalCourseRequirementResponseDTO updateRequirement(Long id, OptionalCourseRequirementDTO dto) {
        OptionalCourseRequirement req = reqRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Requirement not found"));

        MandatoryCourse mandatory = mandatoryRepo.findById(dto.getMandatoryCourseId())
                .orElseThrow(() -> new NotFoundException("Mandatory course not found"));

        if (dto.getPercentage() < 0 || dto.getPercentage() > 100) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }

        req.setMandatoryCourse(mandatory);
        req.setPercentage(dto.getPercentage());

        reqRepo.save(req);

        return OptionalCourseRequirementResponseDTO.builder()
                .id(req.getId())
                .mandatoryId(mandatory.getId())
                .mandatoryName(mandatory.getName())
                .percentage(req.getPercentage())
                .build();
    }

    @Transactional
    public void deleteRequirement(Long id) {
        if (!reqRepo.existsById(id)) {
            throw new NotFoundException("Requirement not found");
        }
        reqRepo.deleteById(id);
    }
}
