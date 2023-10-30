package com.grandats.api.givedeefive.serviceImpl;

import com.grandats.api.givedeefive.dto.ResponseListDto;
import com.grandats.api.givedeefive.specification.SearchSpecification;
import com.grandats.api.givedeefive.model.FileMime;
import com.grandats.api.givedeefive.repository.FileMimeRepository;
import com.grandats.api.givedeefive.service.FileMimeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class FileMimeServiceImpl implements FileMimeService {
    private final FileMimeRepository fileMimeRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FileMime> findAllWithPaging(Pageable pageable) {
        Page<FileMime> result = fileMimeRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<FileMime> findAllWithSearch(SearchSpecification<FileMime> specification, Pageable pageable) {
        Page<FileMime> result = fileMimeRepository.findAll(specification, pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FileMime> findAllBy(Specification<FileMime> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FileMime> findAllPageSpecificationBy(Specification<FileMime> specification, Pageable pageable) {
        return null;
    }

    @Override
    public Page<FileMime> findAllPageSearchSpecificationBy(SearchSpecification<FileMime> specification, Pageable pageable) {
        return null;
    }

    private ResponseListDto<FileMime> getListFromResult(Page<FileMime> result) {
        return new ResponseListDto<>(result.getContent()
                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileMime> findAll() {
        return fileMimeRepository.findAll();
    }


    public FileMime save(FileMime fileMime) {
        return fileMimeRepository.save(fileMime);
    }

    @Override
    public FileMime update(FileMime fileMime) {
        return fileMimeRepository.save(fileMime);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FileMime> findById(Long id) {
        return fileMimeRepository.findById(id);
    }

    @Override
    public void delete(FileMime fileMime) {
        fileMimeRepository.delete(fileMime);
    }

    @Override
    public void deleteById(Long id) {
        fileMimeRepository.deleteById(id);
    }

    @Override
    public FileMime convertEntityToDto(FileMime fileMime) {
return fileMime;
    }

    @Override
    public FileMime convertDtoToEntity(FileMime fileMime) {
return fileMime;
    }

    @Override
    public Optional<FileMime> findByName(String name) {
        return fileMimeRepository.findByName(name);
    }
}
