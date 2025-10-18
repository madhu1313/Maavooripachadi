package com.maavooripachadi.logistics;
import org.springframework.data.jpa.repository.*;
 import java.util.*; 
 public interface NdrTokenRepository extends JpaRepository<NdrToken,Long>{ 
    java.util.Optional<NdrToken> findByToken(String token); 
}
