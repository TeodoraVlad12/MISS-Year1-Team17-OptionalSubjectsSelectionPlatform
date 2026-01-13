package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    private final CourseValidationService validationService; // NEW: Extract validation
    private final RequirementDtoMapper dtoMapper; // NEW: Extract DTO mapping

    public List<OptionalCourseRequirementResponseDTO> getRequirements(Long optionalId) {
        return reqRepo.findByOptionalCourseId(optionalId)
                .stream()
                .map(dtoMapper::toResponseDto) // EXTRACT METHOD
                .toList();
    }

    @Transactional
    public OptionalCourseRequirementResponseDTO createRequirement(Long optionalId,
                                                                  OptionalCourseRequirementDTO dto) {
        validationService.validateRequirementDto(dto); // EXTRACT METHOD

        OptionalCourse optional = validationService.findOptionalCourseOrThrow(optionalId);
        MandatoryCourse mandatory = validationService.findMandatoryCourseOrThrow(dto.getMandatoryCourseId());

        validateNoExistingRequirement(optionalId, dto.getMandatoryCourseId());

        OptionalCourseRequirement req = buildRequirement(optional, mandatory, (int) dto.getPercentage());
        reqRepo.save(req);

        return dtoMapper.toResponseDto(req);
    }

    @Transactional
    public OptionalCourseRequirementResponseDTO updateRequirement(Long id,
                                                                  OptionalCourseRequirementDTO dto) {
        validationService.validateRequirementDto(dto);

        OptionalCourseRequirement req = findRequirementOrThrow(id);
        MandatoryCourse mandatory = validationService.findMandatoryCourseOrThrow(dto.getMandatoryCourseId());

        updateRequirementData(req, mandatory, (int) dto.getPercentage());
        reqRepo.save(req);

        return dtoMapper.toResponseDto(req);
    }

    private void validateNoExistingRequirement(Long optionalId, Long mandatoryId) {
        if (reqRepo.existsByOptionalCourseIdAndMandatoryCourseId(optionalId, mandatoryId)) {
            throw new BadRequestException("This mandatory course already has a percentage assigned.");
        }
    }

    private OptionalCourseRequirement buildRequirement(OptionalCourse optional,
                                                       MandatoryCourse mandatory,
                                                       Integer percentage) {
        return OptionalCourseRequirement.builder()
                .mandatoryCourse(mandatory)
                .optionalCourse(optional)
                .percentage(percentage)
                .build();
    }

    private void updateRequirementData(OptionalCourseRequirement req,
                                       MandatoryCourse mandatory,
                                       Integer percentage) {
        req.setMandatoryCourse(mandatory);
        req.setPercentage(percentage);
    }

    private OptionalCourseRequirement findRequirementOrThrow(Long id) {
        return reqRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Requirement not found"));
    }

    @Transactional
    public void deleteRequirement(Long id) {
        if (!reqRepo.existsById(id)) {
            throw new NotFoundException("Requirement not found");
        }
        reqRepo.deleteById(id);
    }
}

// NEW: Mapper class to handle DTO conversions
@Component
class RequirementDtoMapper {
    public OptionalCourseRequirementResponseDTO toResponseDto(OptionalCourseRequirement req) {
        return OptionalCourseRequirementResponseDTO.builder()
                .id(req.getId())
                .mandatoryId(req.getMandatoryCourse().getId())
                .mandatoryName(req.getMandatoryCourse().getName())
                .percentage(req.getPercentage())
                .build();
    }
}

// NEW: Validation service extracted
@Component
@RequiredArgsConstructor
class CourseValidationService {
    private final MandatoryCourseRepository mandatoryRepo;
    private final OptionalCourseRepository optionalRepo;

    public void validateRequirementDto(OptionalCourseRequirementDTO dto) {
        if (dto.getPercentage() < 0 || dto.getPercentage() > 100) {
            throw new BadRequestException("Percentage must be between 0 and 100");
        }
    }

    public OptionalCourse findOptionalCourseOrThrow(Long id) {
        return optionalRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Optional course not found"));
    }

    public MandatoryCourse findMandatoryCourseOrThrow(Long id) {
        return mandatoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Mandatory course not found"));
    }
}

