package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.FavoriteMenuDto;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.mapper.FavoriteMenuMapper;
import com.bekaku.api.spring.model.AppUser;
import com.bekaku.api.spring.model.FavoriteMenu;
import com.bekaku.api.spring.repository.FavoriteMenuRepository;
import com.bekaku.api.spring.service.FavoriteMenuService;
import com.bekaku.api.spring.specification.SearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FavoriteMenuServiceImpl implements FavoriteMenuService {
    private final FavoriteMenuRepository favoriteMenuRepository;
    private final FavoriteMenuMapper modelMapper;

    @Override
    public ResponseListDto<FavoriteMenuDto> findAllWithPaging(Pageable pageable) {
        Page<FavoriteMenu> result = favoriteMenuRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<FavoriteMenuDto> findAllWithSearch(SearchSpecification<FavoriteMenu> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<FavoriteMenuDto> findAllBy(Specification<FavoriteMenu> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<FavoriteMenu> findAllPageSpecificationBy(Specification<FavoriteMenu> specification, Pageable pageable) {
        return favoriteMenuRepository.findAll(specification, pageable);
    }

    @Override
    public Page<FavoriteMenu> findAllPageSearchSpecificationBy(SearchSpecification<FavoriteMenu> specification, Pageable pageable) {
        return favoriteMenuRepository.findAll(specification, pageable);
    }

    private ResponseListDto<FavoriteMenuDto> getListFromResult(Page<FavoriteMenu> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<FavoriteMenu> findAll() {
        return favoriteMenuRepository.findAll();
    }


    @Transactional
    public FavoriteMenu save(FavoriteMenu favoriteMenu) {
        return favoriteMenuRepository.save(favoriteMenu);
    }

    @Transactional
    @Override
    public FavoriteMenu update(FavoriteMenu favoriteMenu) {
        return favoriteMenuRepository.save(favoriteMenu);
    }

    @Override
    public Optional<FavoriteMenu> findById(Long id) {
        return favoriteMenuRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(FavoriteMenu favoriteMenu) {
        favoriteMenuRepository.delete(favoriteMenu);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        favoriteMenuRepository.deleteById(id);
    }

    @Override
    public FavoriteMenuDto convertEntityToDto(FavoriteMenu favoriteMenu) {
        return modelMapper.toDto(favoriteMenu);
    }

    @Override
    public FavoriteMenu convertDtoToEntity(FavoriteMenuDto favoriteMenuDto) {
        return modelMapper.toEntity(favoriteMenuDto);
    }

    @Override
    public List<FavoriteMenuDto> findAllByAppUser(AppUser appUser) {
        return favoriteMenuRepository.findAllByAppUser(appUser).stream().map(this::convertEntityToDto).toList();
    }

    @Override
    public Optional<FavoriteMenu> findByAppUserAndUrl(AppUser appUser, String url) {
        return favoriteMenuRepository.findByAppUserAndUrl(appUser, url);
    }
}
