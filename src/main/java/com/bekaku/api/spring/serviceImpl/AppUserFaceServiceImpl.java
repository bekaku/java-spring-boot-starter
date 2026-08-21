package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.FaceRecognitionDtos;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AppUserFace;
import com.bekaku.api.spring.repository.AppUserFaceRepository;
import com.bekaku.api.spring.service.AppUserFaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AppUserFaceServiceImpl implements AppUserFaceService {
    private final AppUserFaceRepository appUserFaceRepository;

    @Override
    public ResponseListDto<AppUserFace> findAllWithPaging(Pageable pageable) {
        Page<AppUserFace> result = appUserFaceRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<AppUserFace> findAllWithSearch(SearchSpecification<AppUserFace> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<AppUserFace> findAllBy(Specification<AppUserFace> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<AppUserFace> findAllPageSpecificationBy(Specification<AppUserFace> specification, Pageable pageable) {
        return appUserFaceRepository.findAll(specification, pageable);
    }

    @Override
    public Page<AppUserFace> findAllPageSearchSpecificationBy(SearchSpecification<AppUserFace> specification, Pageable pageable) {
        return appUserFaceRepository.findAll(specification, pageable);
    }
    private ResponseListDto<AppUserFace> getListFromResult(Page<AppUserFace> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<AppUserFace> findAll() {
        return appUserFaceRepository.findAll();
    }


    @Transactional
    public AppUserFace save(AppUserFace appUserFace) {
        return appUserFaceRepository.save(appUserFace);
    }

    @Transactional
    @Override
    public AppUserFace update(AppUserFace appUserFace) {
        return appUserFaceRepository.save(appUserFace);
    }

    @Override
    public Optional<AppUserFace> findById(Long id) {
        return appUserFaceRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(AppUserFace appUserFace) {
        appUserFaceRepository.delete(appUserFace);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        appUserFaceRepository.deleteById(id);
    }

    @Override
    public AppUserFace convertEntityToDto(AppUserFace appUserFace) {
return appUserFace;
    }

    @Override
    public AppUserFace convertDtoToEntity(AppUserFace appUserFace) {
return appUserFace;
    }

    @Override
    public Optional<AppUserFace> findByAppUserId(Long appUserId) {
        return appUserFaceRepository.findByAppUserId(appUserId);
    }

    @Override
    public boolean existsByAppUserId(Long appUserId) {
        return appUserFaceRepository.existsByAppUserId(appUserId);
    }

    @Override
    public FaceRecognitionDtos.FaceMatchProjection findClosestFace(String targetVector, double threshold) {
        return appUserFaceRepository.findClosestFace(targetVector, threshold);
    }
}
