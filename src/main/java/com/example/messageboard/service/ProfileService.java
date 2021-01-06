package com.example.messageboard.service;

import com.example.messageboard.model.Profile;
import com.example.messageboard.model.User;
import com.example.messageboard.repository.ProfileRepository;
import com.example.messageboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Profile getById(Long id) {
        return profileRepository.findById(id).get();
    }

    public void update(Long id, Profile profile) {
        profile.setId(id);
        profileRepository.save(profile);
    }

    public Profile saveProfile(User user) {
        Profile profile = new Profile();
        profile.setUser_id(user.getId());
        return profileRepository.save(profile);
    }
}
