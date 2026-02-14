package com.utkarsh.journalApp.controller;

import com.utkarsh.journalApp.entity.User;
import com.utkarsh.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        List<User> all = userService.getAll();
        if (all != null & !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public void createAdmin(@RequestBody User user) {
        userService.saveAdmin(user);
    }

    @GetMapping("/user-journals")
    public ResponseEntity<?> getUserJournals(@RequestParam String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            List<com.utkarsh.journalApp.entity.JournalEntry> journalEntries = user.getJournalEntries();
            return new ResponseEntity<>(journalEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Autowired
    private com.utkarsh.journalApp.service.JournalEntryService journalEntryService;

    @GetMapping("/migrate-usernames")
    public ResponseEntity<?> migrateUsernames() {
        List<User> users = userService.getAll();
        int count = 0;
        for (User user : users) {
            for (com.utkarsh.journalApp.entity.JournalEntry entry : user.getJournalEntries()) {
                entry.setUsername(user.getUsername());
                journalEntryService.saveEntry(entry);
                count++;
            }
        }
        return new ResponseEntity<>("Migrated " + count + " entries.", HttpStatus.OK);
    }

}
