package com.alibou.finance.auth.infrastructure.adapter.in.controller;

import com.alibou.finance.auth.application.port.UserUseCase;
import com.alibou.finance.auth.domain.model.User;
import com.alibou.finance.auth.domain.vo.UserId;
import com.alibou.finance.auth.infrastructure.adapter.in.dto.*;
import com.alibou.finance.auth.infrastructure.adapter.out.mapper.UserMapper;
import com.alibou.finance.shared.application.PageResult;
import com.alibou.finance.shared.infrastructure.dto.GlobalResponse;
import com.alibou.finance.shared.infrastructure.dto.PageResponse;
import com.alibou.finance.shared.infrastructure.mapper.PageMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@Tag(name="users-endpoints", description = "Endpoint pour la gestion des employés de la micro-finance par l'admin et permette aux utilisateurs de modifier ses informations")
@RequiredArgsConstructor
public class UserRestResource {
    private final UserUseCase userService;

    @Operation(
            summary = "create",
            description = "Pour créer un nouveau employé"
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>create(@Valid @RequestBody UserRequest request){
        var user = UserRequest.toDomain(request);
        var created = userService.create(user);
        if(created != null){
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(GlobalResponse.builder()
                            .message(String.format("Le nouveau employé: %s est ajouté avec success", user.getUsername().value()))
                            .status(HttpStatus.CREATED.value())
                            .build()
                    );
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "findAllByUsername",
            description = "Pour récupérer la liste des employés par page selon la préfixe de son pseudo"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>>findAllByUsername(
          @RequestParam(name = "search", defaultValue = "")String search,
          @RequestParam(name = "page", defaultValue = "0")int page,
          @RequestParam(name = "size", defaultValue = "6")int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        PageResult<User> pages = userService.searchUserByUsername(search, pageable);
        return ResponseEntity.ok(
                PageMapper.toPageResponse(pages, UserMapper::domainToDto)
        );
    }

    @Operation(
            summary = "delete",
            description = "Permet à l'admin de supprimer un employé par son identifiant ID"
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>delete(@PathVariable("userId") UUID userId){
        var deletedId = userService.delete(UserId.from(userId));
        if(deletedId != null){
            return ResponseEntity.ok(
                    GlobalResponse.builder()
                            .message("Utilisateur a été bien supprimé avec success")
                            .status(HttpStatus.OK.value())
                            .data(Map.of("deletedId", deletedId))
                            .build()
            );
        }
        return ResponseEntity.internalServerError().build();
    }

    @Operation(
            summary = "getUserAuthenticatedById",
            description = "Pour récupérer les informations de l'utilisateur connecté par son identifiant Id"
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse>getUserAuthenticatedById(@PathVariable("userId") UUID userId){
        var user = userService.findByUserId(UserId.from(userId));
        return ResponseEntity.ok(UserMapper.domainToDto(user));
    }

    @Operation(
            summary = "changeUserStatus",
            description = "Permet de modifier le status de l'employé par l'administrateur"
    )
    @PatchMapping("/change-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GlobalResponse>changeUserStatus(
            @Valid @RequestBody ChangeUserStatusRequest request
    ){
        var userId = UserId.from(request.userId());
        userService.changeUserStatus(userId, request.status());
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Le status de l'utilisateur a été bien modifié avec success")
                        .build()
        );
    }

    @Operation(
            summary = "changePassword",
            description = "Pour changer le mots de passe en entrant le pseudo, l'ancien et le nouveau mots de passe de l'utilisateur"
    )
    @PostMapping("/change-password")
    public ResponseEntity<GlobalResponse>changePassword(@Valid @RequestBody ChangePasswordRequest request){
        userService.changePassword(request.username(), request.oldPasswordPlain(), request.newPasswordPlain());
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("le mots de passe a été modifié avec success")
                        .build()
        );
    }

    @Operation(
            summary = "changeProfileUser",
            description = "Permet à l'utilisateur de modifier son pseudo, son email ou son mots de passe"
    )
    @PatchMapping("/change-profile")
    public ResponseEntity<GlobalResponse>changeProfileUser(
       @Valid @RequestBody ChangeProfileRequest request
    ){
        var user = ChangeProfileRequest.toDomain(request);
        userService.update(user);
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Le profile de l'utilisateur a été bien modifié avec success")
                        .build()
        );
    }

}
