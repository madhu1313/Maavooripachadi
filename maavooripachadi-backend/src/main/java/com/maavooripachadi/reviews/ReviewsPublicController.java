package com.maavooripachadi.reviews;


import com.maavooripachadi.reviews.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsPublicController {


  private final ReviewsService service;


  public ReviewsPublicController(ReviewsService service){ this.service = service; }


  @PostMapping
  public Review submit(@Valid @RequestBody SubmitReviewRequest req){ return service.submit(req); }


  @GetMapping
  public Page<Review> list(@RequestParam(value = "productId") Long productId,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size){
    return service.listPublic(productId, page, size);
  }


  @PostMapping("/vote")
  public Review vote(@RequestBody @Valid VoteRequest req){ return service.vote(req); }


  @PostMapping("/flag")
  public ReviewFlag flag(@RequestBody @Valid FlagRequest req){ return service.flag(req); }
}
