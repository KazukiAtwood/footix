package com.footix.backend.controller;

import com.footix.backend.dto.GroupDto;
import com.footix.backend.service.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupDto> getGroups() {
        return groupService.getAllGroups();
    }
}
