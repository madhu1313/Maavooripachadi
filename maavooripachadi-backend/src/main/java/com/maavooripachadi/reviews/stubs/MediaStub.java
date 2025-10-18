package com.maavooripachadi.reviews.stubs;


import com.maavooripachadi.reviews.MediaPort;
import org.springframework.stereotype.Component;


@Component
public class MediaStub implements MediaPort {
    @Override public boolean isAcceptable(String imageUrl){ return imageUrl != null && !imageUrl.isBlank(); }
}