package com.excitel.redishelper;

import com.excitel.dto.RequestDTO;
import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Set;

@Component
public class BroadbandRedis {
    @Autowired //NOSONAR
    private RedisCacheManager redisCacheManager;
    @Autowired //NOSONAR
    private RedisTemplate<String, String> redisTemplate;
    private ObjectMapper objectMapper; //NOSONAR

    private static final String BROADBANDPLANS = "BroadbandPlans";
    public void setRedisCacheManager(RedisCacheManager redisCacheManager) { //NOSONAR
        this.redisCacheManager = redisCacheManager; //NOSONAR
    } //NOSONAR

    public void setObjectMapper(ObjectMapper objectMapper) { //NOSONAR
        this.objectMapper = objectMapper; //NOSONAR
    } //NOSONAR

    public String createRedisKey(RequestDTO params) {
        StringBuilder keyBuilder = new StringBuilder("BroadbandPlans_");
        keyBuilder.append(params.getActive())
                .append("_")
                .append(params.getPlanId())
                .append("_")
                .append(params.getType())
                .append("_")
                .append(params.getCategory())
                .append("_")
                .append(params.getData())
                .append("_")
                .append(params.getSpeed())
                .append("_")
                .append(params.getOffset())
                .append("_")
                .append(params.getLimit());
        return keyBuilder.toString();
    }

    public void addBroadbandPlansCache(String key, List<BroadbandPlan> broadbandPlans) {
        Cache broadbandPlansCache = redisCacheManager.getCache(BROADBANDPLANS);
        assert broadbandPlansCache != null;
        ObjectMapper objectMapper1 = new ObjectMapper();
        try {
            String serializedBroadbandPlans = objectMapper1.writeValueAsString(broadbandPlans);
            broadbandPlansCache.put(key, serializedBroadbandPlans);
        } catch (JsonProcessingException e) {
            // Handle serialization error
            throw new SerializationException("Error occurred while serializing BroadbandPlans", e);
        }
    }

    public List<BroadbandPlan> getBroadbandPlansCache(String key) {
        Cache broadbandPlansCache = redisCacheManager.getCache(BROADBANDPLANS);
        if (broadbandPlansCache != null) {
            Cache.ValueWrapper cachedBroadbandPlansWrapper = broadbandPlansCache.get(key);
            if (cachedBroadbandPlansWrapper != null) {
                Object cachedObject = cachedBroadbandPlansWrapper.get();
                if (cachedObject instanceof String string) {
                    ObjectMapper objectMapper1 = new ObjectMapper();
                    try {
                        return objectMapper1.readValue(string, new TypeReference<List<BroadbandPlan>>() {});
                    } catch (JsonProcessingException e) {
                        // Handle deserialization error
                        throw new SerializationException("Error occurred while deserializing BroadbandPlans", e);
                    }
                }
            }
        }
        return null; //NOSONAR
    }

    public void addConnectionDetailCache(String mobileNumber, String status, BroadbandConnection connectionDetail)
    {
        String key = mobileNumber + status + "_connection_details";
        Cache connectionDetailCache = redisCacheManager.getCache("ConnectionDetail");
        assert connectionDetailCache != null;
        connectionDetailCache.put(key, connectionDetail);
    }

    public BroadbandConnection getConnectionDetailsCache(String mobileNumber, String status){
        String key = mobileNumber + status + "_connection_details";
        Cache connectionDetailCache = redisCacheManager.getCache("ConnectionDetail");
        if (connectionDetailCache != null) {
            Cache.ValueWrapper cachedConnectionDetailWrapper = connectionDetailCache.get(key);
            if (cachedConnectionDetailWrapper != null) {
                return (BroadbandConnection) cachedConnectionDetailWrapper.get();
            }
        }
        return null;
    }

    public void clearBroadbandCache() {
        Set<String> keys = redisTemplate.keys(BROADBANDPLANS + ":*");
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }
}
