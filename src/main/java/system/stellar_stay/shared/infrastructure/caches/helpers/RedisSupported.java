package system.stellar_stay.shared.infrastructure.caches.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSupported {

    private final RedisTemplate<String, Object> redisTemplate;

    // ── String operations ──────────────────────────────────

    // Lưu string value với TTL
    public void set(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    // Lấy string value — trả null nếu không có
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    // ── Set operations (dùng cho permissions) ─────────────

    // Lưu Set<String> với TTL
    public void setSet(String key, Set<String> values, long ttlSeconds) {
        redisTemplate.delete(key); // clear set cũ nếu có
        redisTemplate.opsForSet().add(key, values.toArray());
        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    // Lấy Set — trả null nếu key không tồn tại
    public Set<Object> getSet(String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) return null;
        return members;
    }

    // ── Counter operations (dùng cho rate limit OTP) ───────

    // Tăng counter lên 1, set TTL nếu là lần đầu
    public long increment(String key, long ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            // Lần đầu tạo key → set TTL
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return count != null ? count : 0L;
    }

    // Lấy giá trị counter hiện tại
    public long getCounter(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return 0L;
        return Long.parseLong(value.toString());
    }

    // ── Common operations ──────────────────────────────────

    // Xóa key
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Check key có tồn tại không
    public boolean exists(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    // Lấy TTL còn lại (seconds) — trả -1 nếu không có TTL, -2 nếu key không tồn tại
    public long getTtl(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2L;
    }

    // Gia hạn TTL
    public void extendTtl(String key, long ttlSeconds) {
        redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }
}
