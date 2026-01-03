package com.pet.businessdomain.petservice.controller;

import com.pet.businessdomain.petservice.common.BusinessTransactions;
import com.pet.businessdomain.petservice.dto.MatchDto;
import com.pet.businessdomain.petservice.dto.PetDto;
import com.pet.businessdomain.petservice.dto.UserDto;
import com.pet.businessdomain.petservice.entities.Pet;
import com.pet.businessdomain.petservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.petservice.mapper.PetMapper;
import com.pet.businessdomain.petservice.repository.PetRepository;
import com.pet.businessdomain.petservice.services.ICloudinaryService;
import com.pet.businessdomain.petservice.services.ILocationService;
import com.pet.businessdomain.petservice.services.IPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    @Autowired
    private IPetService petService;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private ICloudinaryService cloudinaryService;
    @Autowired
    private BusinessTransactions businessTransactions;
    @Autowired
    private ILocationService locationService;

    private static final int SIZE = 10;

    @GetMapping
    public ResponseEntity<?> getAllPets(
            @RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "uid") Long uid) {
        int size = 0;
        if(page == 0) {
            size=9;
        } else {
           size = SIZE;
        }
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Pagina: " + page);
        Page<Pet> petsPage = petRepository.findAll(pageable);
        UserDto userDto = businessTransactions.getUser(uid);
        Page<PetDto> petsDtoPage = petsPage.map(petMapper::toDto);

        if (petsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        String userAddress = userDto.getAddress();

        List<PetDto> petsList = petsDtoPage.getContent();

        for (int i = 0; i < petsList.size(); i++) {
            PetDto petDto = petsList.get(i);
            Pet pet = petsPage.getContent().get(i);

            double distanceKm = locationService
                    .distanceBetweenAddresses(userAddress, pet.getAddress());

            petDto.setDistance(distanceKm);
        }
        List<PetDto> listPetDto = petsDtoPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("pets", listPetDto);
        response.put("currentPage", petsPage.getNumber());
        response.put("totalItems", petsPage.getTotalElements());
        response.put("totalPages", petsPage.getTotalPages());
        response.put("pageSize", petsPage.getSize());
        response.put("hasNext", petsPage.hasNext());
        response.put("hasPrevious", petsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> getAllPetsById(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "status",defaultValue = "all") String status,
            @RequestParam(name = "search",defaultValue = "all") String search,
            @PathVariable(name = "uid") Long uid) {
        int size = 0;
        if(page == 0) {
            size=9;
        } else {
            size = SIZE;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Pet> petsPage = petRepository.findByUid(uid,pageable);
        if(!status.equals("all") && search.equals("all")){
            petsPage = petRepository.findByUidAndStatus(uid,status,pageable);
        } else if(status.equals("all") && !search.equals("all")){
            petsPage = petRepository.findByUidAndName(uid,search,pageable);
        } else if(!status.equals("all") && !search.equals("all")){
            petsPage = petRepository.findByUidAndNameAndStatus(uid,search, status,pageable);
        }
        UserDto userDto = businessTransactions.getUser(uid);
        Page<PetDto> petsDtoPage = petsPage.map(petMapper::toDto);

        if (petsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        String userAddress = userDto.getAddress();

        List<PetDto> petsList = petsDtoPage.getContent();

        for (int i = 0; i < petsList.size(); i++) {
            PetDto petDto = petsList.get(i);
            Pet pet = petsPage.getContent().get(i);

            double distanceKm = locationService
                    .distanceBetweenAddresses(userAddress, pet.getAddress());

            petDto.setDistance(distanceKm);
        }
        List<PetDto> listPetDto = petsDtoPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("pets", listPetDto);
        response.put("currentPage", petsPage.getNumber());
        response.put("totalItems", petsPage.getTotalElements());
        response.put("totalPages", petsPage.getTotalPages());
        response.put("pageSize", petsPage.getSize());
        response.put("hasNext", petsPage.hasNext());
        response.put("hasPrevious", petsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/total/{uid}")
    public ResponseEntity<?> getAllPetsByIdTotal(
            @PathVariable(name = "uid") Long uid) {
        int size = 0;
       List<Pet> listPets = petRepository.findByUid(uid);
        UserDto userDto = businessTransactions.getUser(uid);
        List<PetDto> petsDtoList = petMapper.toDtoList(listPets);

        if (petsDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        String userAddress = userDto.getAddress();


        for (int i = 0; i < petsDtoList.size(); i++) {
            PetDto petDto = petsDtoList.get(i);
            Pet pet = petMapper.toEntity(petDto);

            double distanceKm = locationService
                    .distanceBetweenAddresses(userAddress, pet.getAddress());

            petDto.setDistance(distanceKm);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("pets", petsDtoList);
        response.put("totalItems", petsDtoList.toArray().length);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/notMatch/{uid}")
    public ResponseEntity<?> getPetnotMatch(@PathVariable(name = "uid") Long uid,
                                            @RequestParam(name = "page", defaultValue = "0") int page) throws BusinessRuleException {
        int size = 0;
        if(page == 0) {
            size=9;
        } else {
            size = SIZE;
        }
        Pageable pageable = PageRequest.of(page, size);

        // Obtener todas las mascotas en adopción
        List<Pet> mascotasEnAdopcionList = petRepository.findByStatus("En adopción");

        List<?> matchList = petService.getMatchByUid(uid);
        List<Long> aids = matchList.stream()
                .map(m -> ((MatchDto) m).getAid())
                .collect(Collectors.toList());

        List<Pet> mascotasSinMatch = mascotasEnAdopcionList.stream()
                .filter(pet -> !aids.contains(pet.getId()))
                .collect(Collectors.toList());

        Collections.shuffle(mascotasSinMatch);

        if (mascotasSinMatch.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay mascotas sin match");
        }
        UserDto userDto = businessTransactions.getUser(uid);
        String userAddress = userDto.getAddress();

        List<PetDto> petsList = petMapper.toDtoList(mascotasSinMatch);

        for (int i = 0; i < petsList.size(); i++) {
            PetDto petDto = petsList.get(i);

            double distanceKm = locationService
                    .distanceBetweenAddresses(userAddress, petDto.getAddress());

            petDto.setDistance(distanceKm);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("pets", petsList);


        return ResponseEntity.ok(response);
    }
//
    @GetMapping("/{id}/{uid}")
    public ResponseEntity<?> getPetById(@PathVariable(name = "id") Long id,@PathVariable(name = "uid") Long uid) throws BusinessRuleException {
        Optional<Pet> opt = petRepository.findById(id);
        if (opt.isPresent()) {
            Pet pet = opt.get();
            PetDto petDto = petService.getUserByPet(pet);
            if(uid != 0) {
                UserDto userDto = businessTransactions.getUser(uid);
                String userAddress = userDto.getAddress();
                double distanceKm = locationService
                        .distanceBetweenAddresses(userAddress, pet.getAddress());
                petDto.setDistance(distanceKm);
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(petDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la mascota");
        }
    }
//
    @GetMapping("/urge/{uid}")
    public ResponseEntity<?> getPetUrgentes(@PathVariable(name = "uid") Long uid,
                                            @RequestParam(name = "page",defaultValue = "0") int page) throws BusinessRuleException {

        Pageable pageable = PageRequest.of(page, 5);
        String status = "En adopción";

        Page<Pet> petsPage = null;
        if(uid != 0) {
            petsPage = petRepository.findByUidNotAndStatusOrderByCreatedAtAsc(uid, status, pageable);

        } else {
            petsPage = petRepository.findByStatusOrderByCreatedAtAsc(status, pageable);
        }
        Page<PetDto> petsDtoPage = petsPage.map(petMapper::toDto);
        if (petsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas urgentes");
        }
        List<PetDto> listPetDto = petsDtoPage.getContent();

        if(uid != 0) {
            listPetDto = petService.setDistanceToPet(petsDtoPage,uid,petsPage);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("pets", listPetDto);
        response.put("currentPage", petsPage.getNumber());
        response.put("totalItems", petsPage.getTotalElements());
        response.put("totalPages", petsPage.getTotalPages());
        response.put("pageSize", petsPage.getSize());
        response.put("hasNext", petsPage.hasNext());
        response.put("hasPrevious", petsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
//
    @GetMapping("/adopt/{uid}")
    public ResponseEntity<?> getPetAdoptados(@PathVariable(name = "uid") Long uid,
                                             @RequestParam(name = "page",defaultValue = "0") int page) throws BusinessRuleException {

        Pageable pageable = PageRequest.of(page, 5);
        String status = "Adoptado";
        Page<Pet> petsPage = null;
        if(uid != 0) {
            petsPage = petRepository.findByUidNotAndStatusOrderByCreatedAtAsc(uid, status, pageable);

        } else {
            petsPage = petRepository.findByStatusOrderByCreatedAtAsc(status, pageable);
        }
        Page<PetDto> petsDtoPage = petsPage.map(petMapper::toDto);
        if (petsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas urgentes");
        }
        List<PetDto> listPetDto = petsDtoPage.getContent();

        if(uid != 0) {
            listPetDto = petService.setDistanceToPet(petsDtoPage,uid,petsPage);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("pets", listPetDto);
        response.put("currentPage", petsPage.getNumber());
        response.put("totalItems", petsPage.getTotalElements());
        response.put("totalPages", petsPage.getTotalPages());
        response.put("pageSize", petsPage.getSize());
        response.put("hasNext", petsPage.hasNext());
        response.put("hasPrevious", petsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
//
        @GetMapping("/full")
        public ResponseEntity<?> getPetsByUser(
                @RequestParam(name = "uid") Long uid,
                @RequestParam(name = "page", defaultValue = "0") int page) {

            int size = 0;
            if(page == 0) {
                size=9;
            } else {
                size = SIZE;
            }
            Pageable pageable = PageRequest.of(page, size);
            Page<Pet> petsPage = petRepository.findByUidOrderByCreatedAtDesc(uid, pageable);

            if (petsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            UserDto userDto = businessTransactions.getUser(uid);
            String userAddress = userDto.getAddress();

            List<PetDto> petsList = petMapper.toDtoList(petsPage.getContent());

            for (int i = 0; i < petsList.size(); i++) {
                PetDto petDto = petsList.get(i);
                Pet pet = petsPage.getContent().get(i);

                double distanceKm = locationService
                        .distanceBetweenAddresses(userAddress, pet.getAddress());

                petDto.setDistance(distanceKm);
            }

            return ResponseEntity.ok(petsList);
        }


    @GetMapping("/full/product/{id}")
    public ResponseEntity<?> getFull(@PathVariable(name = "id") Long id) throws BusinessRuleException {
        PetDto save = petService.getFull(id);

        if (save != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(save);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la mascota");
        }
    }

    @PostMapping
    public ResponseEntity<PetDto> createPet(@RequestBody PetDto petDto) {
        Pet pet = petMapper.toEntity(petDto);
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        Pet savedPet = petRepository.save(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(petMapper.toDto(savedPet));
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(Collections.singletonMap("url", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }

    @PostMapping("/delimage")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> file) {
        try {
            String fileDel = file.get("file");
            String resp = cloudinaryService.deleteFile(fileDel);
            return ResponseEntity.ok(Collections.singletonMap("url", resp));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting image: " + e.getMessage());
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deletePetAll() {
        petRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hecho");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable(name = "id") Long id) {
        Optional<Pet> find = petRepository.findById(id);
        if (find.isPresent()) {
            PetDto petDto = petMapper.toDto(find.get());
            petRepository.delete(find.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(petDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable(name="id") Long id, @RequestBody PetDto petDto) throws BusinessRuleException {
        if (petDto != null) {
            PetDto dto = petService.updatePet(id, petDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
}