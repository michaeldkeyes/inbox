package com.keyes.inbox.controller;

import com.keyes.inbox.email.Email;
import com.keyes.inbox.email.EmailRepository;
import com.keyes.inbox.folders.Folder;
import com.keyes.inbox.folders.FolderRepository;
import com.keyes.inbox.folders.FolderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

  private final FolderRepository folderRepository;
  private final FolderService folderService;
  private final EmailRepository emailRepository;

  public EmailViewController(FolderRepository folderRepository, FolderService folderService, EmailRepository emailRepository) {
    this.folderRepository = folderRepository;
    this.folderService = folderService;
    this.emailRepository = emailRepository;
  }

  @GetMapping("/emails/{id}")
  public String emailView(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal, Model model) {

    if (principal == null || !StringUtils.hasText(principal.getAttribute("name"))) {
      return "index";
    }

    // Fetch folders
    String userId = principal.getAttribute("login");
    List<Folder> userFolders = folderRepository.findAllById(userId);
    model.addAttribute("userFolders", userFolders);

    List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
    model.addAttribute("defaultFolders", defaultFolders);

    Optional<Email> optionalEmail = emailRepository.findById(id);
    if (optionalEmail.isEmpty()) {
      return "inbox";
    }
    Email email = optionalEmail.get();
    String toIds = String.join(", ", email.getTo());
    model.addAttribute("email", email);
    model.addAttribute("toIds", toIds);

    return "email";
  }
}