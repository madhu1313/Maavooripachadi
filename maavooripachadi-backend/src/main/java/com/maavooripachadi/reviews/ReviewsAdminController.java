package com.maavooripachadi.reviews;


import com.maavooripachadi.reviews.dto.ReplyRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/reviews")
public class ReviewsAdminController {


    private final ReviewsService service;


    public ReviewsAdminController(ReviewsService service){ this.service = service; }


    @PostMapping("/{id}/status")
    @PreAuthorize("hasAuthority('REVIEWS_WRITE') or hasRole('ADMIN')")
    public Review setStatus(@PathVariable Long id, @RequestParam ReviewStatus status){ return service.moderate(id, status); }


    @PostMapping("/reply")
    @PreAuthorize("hasAuthority('REVIEWS_WRITE') or hasRole('ADMIN')")
    public ReviewReply reply(@RequestBody ReplyRequest req){ return service.reply(req); }
}