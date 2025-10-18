package com.maavooripachadi.payments;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * Legacy shim kept for backward compatibility. Prefer
 * {@link com.maavooripachadi.payments.gateway.PaymentAttemptRepository}.
 */
@NoRepositoryBean
@Deprecated(forRemoval = true)
public interface PaymentAttemptRepository extends com.maavooripachadi.payments.gateway.PaymentAttemptRepository {
}
