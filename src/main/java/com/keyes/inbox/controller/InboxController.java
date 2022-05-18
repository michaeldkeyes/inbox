package com.keyes.inbox.controller;

import com.keyes.inbox.folders.Folder;
import com.keyes.inbox.folders.FolderRepository;
import com.keyes.inbox.folders.FolderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InboxController {

  private final FolderRepository folderRepository;
  private final FolderService folderService;

  public InboxController(FolderRepository folderRepository, FolderService folderService) {
    this.folderRepository = folderRepository;
    this.folderService = folderService;
  }

  @GetMapping("/")
  public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model) {

    if (principal == null || !StringUtils.hasText(principal.getAttribute("name"))) {
      return "index";
    } else {
      String userId = principal.getAttribute("login");
      List<Folder> userFolders = folderRepository.findAllById(userId);
      model.addAttribute("userFolders", userFolders);

      List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
      model.addAttribute("defaultFolders", defaultFolders);

      return "inbox";
    }
  }
}
