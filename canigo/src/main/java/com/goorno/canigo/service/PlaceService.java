package com.goorno.canigo.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.goorno.canigo.common.util.Base64Util;
import com.goorno.canigo.dto.place.PlaceRequestDTO;
import com.goorno.canigo.dto.place.PlaceResponseDTO;
import com.goorno.canigo.entity.Hashtag;
import com.goorno.canigo.entity.Place;
import com.goorno.canigo.entity.User;
import com.goorno.canigo.entity.enums.CategoryType;
import com.goorno.canigo.repository.HashtagRepository;
import com.goorno.canigo.repository.PlaceRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final로 선언된 필드를 자동으로 생성자 주입
public class PlaceService {
	
	private final PlaceRepository placeRepository;
	private final HashtagRepository hashtagRepository;
	
	// 장소 등록 메서드
	public PlaceResponseDTO createPlace(PlaceRequestDTO placeRequestDTO, User user) throws IOException {
		// 업로드된 파일을 Base64로 변환
		List<String> base64Files = Base64Util.encodeFilesToBase64(placeRequestDTO.getUploadFiles());
		
		// Place 엔티티 생성
		Place place = new Place();
		place.setName(placeRequestDTO.getName());
		place.setDescription(placeRequestDTO.getDescription());
		place.setLatitude(placeRequestDTO.getLatitude());
		place.setLongitude(placeRequestDTO.getLongitude());
		place.setCategory(CategoryType.fromDescription(placeRequestDTO.getCategoryName()));
		place.setUser(user); // 등록한 유저
		place.setUploadFiles(base64Files);
		
		// 해시태그 처리
		List<Hashtag> hashtagEntities = placeRequestDTO.getHashtags().stream()
				.map(name -> hashtagRepository.findByName(name)
						.orElseGet(() -> {
							Hashtag newTag = new Hashtag();
							newTag.setName(name);
							return hashtagRepository.save(newTag);
						}))
				.collect(Collectors.toList());
		
		place.setHashtags(hashtagEntities);
		
		// 장소를 DB에 저장
		Place savedPlace = placeRepository.save(place);
		
		// 응답 DTO로 변환하여 반환
		PlaceResponseDTO placeResponseDTO = mapToResponseDTO(savedPlace);
		
		placeResponseDTO.setCreatedBy(user.getNickname());
		
		return placeResponseDTO;
	}
	
	// 모든 장소 조회 메서드
	public List<Place> getAllPlaces() {
		return placeRepository.findAll();
	}
	
	// 장소 조회
	public PlaceResponseDTO getPlace(Long id) {
		Place place = placeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다. id: " + id));
		return mapToResponseDTO(place);
	}
	
	// DTO로 변환
	private PlaceResponseDTO mapToResponseDTO(Place place) {
		PlaceResponseDTO placeResponseDTO = new PlaceResponseDTO();
		placeResponseDTO.setId(place.getId());
		placeResponseDTO.setName(place.getName());
		placeResponseDTO.setDescription(place.getDescription());
		placeResponseDTO.setLatitude(place.getLatitude());
		placeResponseDTO.setLongitude(place.getLongitude());
		placeResponseDTO.setCategory(place.getCategory());
		
		// 첫 번째 파일을 응답
		placeResponseDTO.setUploadFiles(place.getUploadFiles().isEmpty() 
				? null : place.getUploadFiles().get(0));
		
		// Place 엔티티의 해시태그를 DTO에 포함
        placeResponseDTO.setHashtags(place.getHashtags().stream()
                                        .map(Hashtag::getName) // Hashtag 엔티티에서 이름만 추출
                                        .collect(Collectors.toList()));
		
		return placeResponseDTO;
	}
	
	// 장소 수정 메서드
	@Transactional
	public PlaceResponseDTO updatePlace(Long id, PlaceRequestDTO dto, User user) throws IOException {
		
		
		// 1. 기존 장소 조회
		Place place = placeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("장소를 찾을 수 없습니다. id: " + id));
		
		
		// 디버깅용 출력문
	    System.out.println("요청 사용자 ID: " + user.getId() + ", 장소 등록자 ID: " + (placeRepository.findById(id).map(p -> p.getUser().getId()).orElse(null)));
		
		// 2. 수정 권한 체크(등록자와 현재 사용자가 같은지)
		if (!place.getUser().getId().equals(user.getId())) {
			throw new IllegalStateException("수정 권한이 없습니다.");
		}
		
		// 3. 수정할 필드들 업데이트
	    place.setName(dto.getName());
	    place.setDescription(dto.getDescription());
	    place.setLatitude(dto.getLatitude());
	    place.setLongitude(dto.getLongitude());
	    
	    // category 변환
	    place.setCategory(CategoryType.fromName(dto.getCategoryName()));

	    // 4. 업로드 파일 있으면 Base64로 변환해서 세팅 (파일 업데이트가 필요한 경우)
	    if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
	        List<String> base64Files = Base64Util.encodeFilesToBase64(dto.getUploadFiles());
	        place.setUploadFiles(base64Files);
	    }

	    // 5. 해시태그 업데이트
        // 새로운 해시태그 리스트를 구성(기존에 있으면 찾아오고, 없으면 새로 저장)
        List<Hashtag> newHashtags = dto.getHashtags().stream()
                .map(name -> hashtagRepository.findByName(name)
                        .orElseGet(() -> {
                            Hashtag newTag = new Hashtag();
                            newTag.setName(name);
                            return hashtagRepository.save(newTag); // 새로운 해시태그는 DB에 저장
                        }))
                .collect(Collectors.toList());

        // Place 엔티티의 hashtags 컬렉션을 새로운 리스트로 설정
        // JPA가 이 변화를 감지하여 중간 테이블(place_hashtag)의 관계를 자동으로 동기화
        // 기존 관계는 끊고, 새로운 관계는 만듭니다. Hashtag 엔티티 자체는 삭제되지 않습니다.
        place.setHashtags(newHashtags);
		
		// 6. 저장 및 DTO 반환
	    Place updatedPlace = placeRepository.save(place);
	    PlaceResponseDTO responseDTO = mapToResponseDTO(updatedPlace);
	    responseDTO.setCreatedBy(user.getNickname());
	    
	    return responseDTO;
	}
}
