package com.example.messageboard.controller;

import com.example.messageboard.model.Profile;
import com.example.messageboard.model.User;
import com.example.messageboard.service.ProfileService;
import com.example.messageboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Profile> getById(@PathVariable Long id, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Profile profile = profileService.getById(id);

        if(!hasAccess(principal, profile.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Profile> update(@PathVariable Long id, @RequestBody Profile profile, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Profile currentProfile = profileService.getById(id);

        if(!hasAccess(principal, currentProfile.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        profile.setUser_id(currentProfile.getUser_id());
        profileService.update(id, profile);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean hasAccess(Principal principal, Long profileId) {
        if (isAdmin(principal)) {
            return true;
        }
        try {
            User user = userService.findByEmail(principal.getName());
            return profileService.getById(profileId).getUser_id().equals(user.getId());
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    public boolean isAdmin(Principal principal) {
        if (userService.findByEmail(principal.getName()).getRole().getName().equals("ROLE_ADMIN")) {
            return true;
        }
        return false;
    }

}
