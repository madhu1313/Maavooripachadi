package com.maavooripachadi.reviews;


import com.maavooripachadi.reviews.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;


@Service
public class ReviewsService {


    private final ReviewRepository reviews;
    private final ReviewImageRepository images;
    private final ReviewVoteRepository votes;
    private final ReviewFlagRepository flags;
    private final ReviewReplyRepository replies;
    private final ProductRatingAggRepository aggs;
    private final OrderPort orders;
    private final MediaPort media;


    public ReviewsService(ReviewRepository reviews,
                          ReviewImageRepository images,
                          ReviewVoteRepository votes,
                          ReviewFlagRepository flags,
                          ReviewReplyRepository replies,
                          ProductRatingAggRepository aggs,
                          OrderPort orders,
                          MediaPort media) {
        this.reviews = reviews; this.images = images; this.votes = votes; this.flags = flags; this.replies = replies; this.aggs = aggs; this.orders = orders; this.media = media;
    }


    @Transactional
    public Review submit(SubmitReviewRequest req){
        boolean verified = orders.hasPurchased(req.getSubjectId(), req.getProductId(), req.getVariantId());
        Review r = new Review();
        r.setProductId(req.getProductId()); r.setVariantId(req.getVariantId()); r.setRating(req.getRating()); r.setTitle(req.getTitle()); r.setBody(req.getBody()); r.setSubjectId(req.getSubjectId()); r.setVerifiedPurchase(verified); r.setStatus(ReviewStatus.PENDING);
        r = reviews.save(r);
        if (req.getImageUrls()!=null){
            for (String url : req.getImageUrls()){
                if (media.isAcceptable(url)){
                    ReviewImage ri = new ReviewImage(); ri.setReview(r); ri.setUrl(url); images.save(ri);
                }
            }
        }
// No agg yet; after approval
        return r;
    }

@Transactional(readOnly = true)
public Page<Review> listPublic(Long productId, int page, int size){
    return reviews.findByProductIdAndStatusOrderByCreatedAtDesc(productId, ReviewStatus.APPROVED, PageRequest.of(page, size));
}


@Transactional
public Review vote(VoteRequest req){
    Review r = reviews.findById(req.getReviewId()).orElseThrow();
    var existing = votes.findByReviewIdAndSubjectId(req.getReviewId(), req.getSubjectId());
    if (existing.isEmpty()){
        ReviewVote v = new ReviewVote(); v.setReview(r); v.setSubjectId(req.getSubjectId()); v.setType(req.isHelpful()?VoteType.HELPFUL:VoteType.NOT_HELPFUL); votes.save(v);
        if (req.isHelpful()) r.setHelpfulCount(r.getHelpfulCount()+1); else r.setNotHelpfulCount(r.getNotHelpfulCount()+1);
        reviews.save(r);
    }
    return r;
}


@Transactional
public ReviewFlag flag(FlagRequest req){
    Review r = reviews.findById(req.getReviewId()).orElseThrow();
    ReviewFlag f = new ReviewFlag(); f.setReview(r); f.setSubjectId(req.getSubjectId()); f.setReason(req.getReason()); f.setDetails(req.getDetails());
    return flags.save(f);
}


@Transactional
public ReviewReply reply(ReplyRequest req){
    Review r = reviews.findById(req.getReviewId()).orElseThrow();
    ReviewReply rr = new ReviewReply(); rr.setReview(r); rr.setAuthor(req.getAuthor()); rr.setBody(req.getBody()); rr.setPublicVisible(req.getPublicVisible()==null?Boolean.TRUE:req.getPublicVisible());
    return replies.save(rr);
}


@Transactional
public Review moderate(Long id, ReviewStatus status){
    Review r = reviews.findById(id).orElseThrow();
    r.setStatus(status);
    r = reviews.save(r);
    if (status == ReviewStatus.APPROVED){
        recalcAgg(r.getProductId(), r.getVariantId());
    }
    return r;
}


private void recalcAgg(Long productId, Long variantId){
    java.util.List<Review> rs = reviews.findAll().stream()
            .filter(x -> x.getProductId().equals(productId) && (variantId==null? x.getVariantId()==null : variantId.equals(x.getVariantId())))
            .filter(x -> x.getStatus()==ReviewStatus.APPROVED)
            .toList();
    int count = rs.size(); double avg = 0.0; if (count>0){ avg = rs.stream().mapToInt(Review::getRating).average().orElse(0.0); }
    ProductRatingAgg agg = aggs.findByProductIdAndVariantId(productId, variantId).orElseGet(ProductRatingAgg::new);
    agg.setProductId(productId); agg.setVariantId(variantId); agg.setCountReviews(count); agg.setAvgRating(avg);
    aggs.save(agg);
}
}