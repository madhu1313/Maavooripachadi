package com.maavooripachadi.reviews;


public interface MediaPort {
    /** Validate image url is owned/allowed and safe. No-op stub by default. */
    boolean isAcceptable(String imageUrl);
}