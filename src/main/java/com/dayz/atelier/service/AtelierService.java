package com.dayz.atelier.service;

import com.dayz.atelier.converter.AtelierConverter;
import com.dayz.atelier.domain.Atelier;
import com.dayz.atelier.domain.AtelierRepository;
import com.dayz.atelier.domain.WorkTime;
import com.dayz.atelier.dto.ReadAtelierDetailResponse;
import com.dayz.atelier.dto.ReadAteliersResult;
import com.dayz.atelier.dto.SaveAtelierRequest;
import com.dayz.atelier.dto.SaveAtelierResponse;
import com.dayz.atelier.dto.SearchAtelierResponse;
import com.dayz.common.dto.CustomPageResponse;
import com.dayz.common.enums.Auth;
import com.dayz.common.enums.ErrorInfo;
import com.dayz.common.exception.BusinessException;
import com.dayz.common.jwt.Jwt;
import com.dayz.common.jwt.JwtAuthentication;
import com.dayz.common.jwt.JwtAuthenticationToken;
import com.dayz.common.util.TimeUtil;
import com.dayz.member.domain.Address;
import com.dayz.member.domain.AddressRepository;
import com.dayz.member.domain.Member;
import com.dayz.member.domain.MemberRepository;
import com.dayz.member.domain.Permission;
import com.dayz.member.domain.PermissionRepository;
import com.dayz.onedayclass.dto.SearchOneDayClassResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AtelierService {

    private final AtelierRepository atelierRepository;

    private final AddressRepository addressRepository;

    private final AtelierConverter atelierConverter;

    private final PermissionRepository permissionRepository;

    private final MemberRepository memberRepository;

    private final Jwt jwt;

    private final TimeUtil timeUtil;

    public ReadAtelierDetailResponse getAtelierDetail(Long atelierId) {
        Atelier foundAtelier = atelierRepository.findById(atelierId)
                .orElseThrow(() -> new BusinessException(ErrorInfo.ATELIER_NOT_FOUND));

        return atelierConverter.convertToReadAtelierDetailResponse(foundAtelier);
    }

    @Transactional
    public SaveAtelierResponse savaAtelierInfo(Long memberId, SaveAtelierRequest request) {

        Address address = addressRepository.findByCityIdAndRegionId(request.getAddress().getCityId(), request.getAddress().getRegionId())
                .orElseThrow(() -> new BusinessException(ErrorInfo.ADDRESS_NOT_FOUND));

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(ErrorInfo.MEMBER_NOT_FOUND));

        Atelier newAtelier = Atelier.of(
                request.getName(),
                address,
                request.getAddress().getDetail(),
                request.getIntro(),
                WorkTime.of(timeUtil.timeStringToSecond(request.getWorkStartTime()),
                        timeUtil.timeStringToSecond(request.getWorkEndTime())),
                request.getBusinessNumber(),
                member
        );

        Atelier savedAtelier = atelierRepository.save(newAtelier);

        Permission permission = permissionRepository.findByName("ROLE_" + Auth.ATELIER.getValue()).get();
        member.changePermission(permission);

        String token = jwt
                .sign(Jwt.Claims.from(member.getId(), member.getProviderId(), member.getUsername(), new String[]{member.getPermission().getName()}));

        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(member.getPermission().getName()));
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                new JwtAuthentication(member.getId(), member.getProviderId(), token, member.getUsername()), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return atelierConverter.convertToSaveAtelierResponse(savedAtelier.getId(), token);
    }

    public CustomPageResponse<ReadAteliersResult> getAteliers(Long memberId, PageRequest pageRequest) {
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorInfo.MEMBER_NOT_FOUND));

        Address foundMemberAddress = foundMember.getAddress();

        Page<ReadAteliersResult> readAteliersResultPage = atelierRepository.findAteliersByAddress(
                foundMemberAddress.getCityId(),
                foundMemberAddress.getRegionId(), 
                pageRequest
        ).map(atelierConverter::convertToReadAteliersResult);

        return CustomPageResponse.<ReadAteliersResult>of(readAteliersResultPage);
    }

    public CustomPageResponse searchOneDayClass(Member member, String keyWord,
        Pageable pageRequest) {
        Page<SearchAtelierResponse> searchOneDayClassResponsePage = atelierRepository.searchAteliers(
                member.getAddress().getCityId(),member.getAddress().getRegionId(),keyWord, pageRequest);

        return CustomPageResponse.of(searchOneDayClassResponsePage);

    }

}
