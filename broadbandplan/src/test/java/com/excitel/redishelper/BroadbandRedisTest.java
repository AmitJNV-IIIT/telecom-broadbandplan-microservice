package com.excitel.redishelper;

import com.excitel.dto.RequestDTO;
import com.excitel.model.BroadbandConnection;
import com.excitel.model.BroadbandPlan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class BroadbandRedisTest {

    @InjectMocks
    private BroadbandRedis broadbandRedis;
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RedisCacheManager redisCacheManager;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public BroadbandRedisTest() {
        initMocks(this);
    }


    @Test
    void testCreateRedisKey1() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.

        // Arrange
        BroadbandRedis broadbandRedis = new BroadbandRedis();

        // Act and Assert
        assertEquals("BroadbandPlans_null_null_Broadband_null_null_null_0_10",
                broadbandRedis.createRedisKey(new RequestDTO()));
    }


    @Test
    void testCreateRedisKey() {
        // Arrange
        RequestDTO params = new RequestDTO();
        params.setActive("active");
        params.setPlanId("planId");
        params.setType("type");
        params.setCategory("category");
        params.setData("data");
        params.setSpeed("speed");
        params.setOffset(0);
        params.setLimit(10);

        BroadbandRedis broadbandRedis = new BroadbandRedis();

        // Act
        String key = broadbandRedis.createRedisKey(params);

        // Assert
        assertEquals("BroadbandPlans_active_planId_type_category_data_speed_0_10", key);
    }
    @Test
    void testCreateRedisKey_NullValues() {
        // Arrange
        RequestDTO params = new RequestDTO();
        params.setActive(null); // Set null value
        params.setPlanId(null); // Set null value
        params.setType(null); // Set default value
        params.setCategory(null); // Set null value
        params.setData(null); // Set null value
        params.setSpeed(null); // Set null value
        params.setOffset(null); // Set numeric offset
        params.setLimit(null); // Set numeric limit

        BroadbandRedis broadbandRedis = new BroadbandRedis();

        // Act
        String key = broadbandRedis.createRedisKey(params);

        // Assert
        assertEquals("BroadbandPlans_null_null_null_null_null_null_null_null", key);
    }

    @Test
    void testCreateRedisKey2() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.

        // Arrange
        BroadbandRedis broadbandRedis = new BroadbandRedis();
        RequestDTO params = mock(RequestDTO.class);
        when(params.getLimit()).thenReturn(1);
        when(params.getOffset()).thenReturn(2);
        when(params.getActive()).thenReturn("Active");
        when(params.getCategory()).thenReturn("Category");
        when(params.getData()).thenReturn("Data");
        when(params.getPlanId()).thenReturn("42");
        when(params.getSpeed()).thenReturn("Speed");
        when(params.getType()).thenReturn("Type");

        // Act
        String actualCreateRedisKeyResult = broadbandRedis.createRedisKey(params);

        // Assert
        verify(params).getActive();
        verify(params).getCategory();
        verify(params).getData();
        verify(params).getLimit();
        verify(params).getOffset();
        verify(params).getPlanId();
        verify(params).getSpeed();
        verify(params).getType();
        assertEquals("BroadbandPlans_Active_42_Type_Category_Data_Speed_2_1", actualCreateRedisKeyResult);
    }
    @Test
    void testCreateRedisKey_EmptyValues() {
        // Arrange
        RequestDTO params = new RequestDTO();
        params.setActive("");
        params.setPlanId("");
        params.setType("");
        params.setCategory("");
        params.setData("");
        params.setSpeed("");
        params.setOffset(0); // Set numeric offset
        params.setLimit(10); // Set numeric limit

        BroadbandRedis broadbandRedis = new BroadbandRedis();

        // Act
        String key = broadbandRedis.createRedisKey(params);

        // Assert
        assertEquals("BroadbandPlans_______0_10", key);
    }

    @Test
    void testAddBroadbandPlansCache_CacheIsNotNull() throws JsonProcessingException {
        // Arrange
        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class);
        Cache cache = mock(Cache.class);
        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(cache);

        List<BroadbandPlan> broadbandPlans = new ArrayList<>();
        broadbandPlans.add(new BroadbandPlan());

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(broadbandPlans)).thenReturn("[{\"planId\":null,\"planType\":null,\"price\":null,\"category\":null,\"validity\":null,\"ott\":null,\"voiceLimit\":null,\"sms\":null,\"data\":null,\"couponIds\":null,\"limit\":null,\"speed\":null,\"active\":null}]");

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);
        broadbandRedis.setObjectMapper(objectMapper);

        // ArgumentCaptor to capture the argument passed to cache.put
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        broadbandRedis.addBroadbandPlansCache("testKey", broadbandPlans);

        // Assert
        // Verify that cache.put was called with the expected arguments
        verify(cache, times(1)).put(keyCaptor.capture(), valueCaptor.capture());
        assertEquals("testKey", keyCaptor.getValue());
        assertEquals("[{\"planId\":null,\"planType\":null,\"price\":null,\"category\":null,\"validity\":null,\"ott\":null,\"voiceLimit\":null,\"sms\":null,\"data\":null,\"couponIds\":null,\"limit\":null,\"speed\":null,\"active\":null}]", valueCaptor.getValue());
    }

    @Test
    void testGetBroadbandPlansCache_CacheIsNull() {
        // Arrange
        String key = "testKey";
        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class);
        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);

        // Act
        List<BroadbandPlan> plans = broadbandRedis.getBroadbandPlansCache(key);

        // Assert
        assertNull(plans);
    }

    @Test
    void testGetBroadbandPlansCache_CachedObjectIsNull() {
        // Arrange
        String key = "testKey";
        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(null);

        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class);
        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(cache); // Fix this line

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager); // Set the RedisCacheManager

        // Act
        List<BroadbandPlan> plans = broadbandRedis.getBroadbandPlansCache(key);

        // Assert
        assertNull(plans);
    }
    @Test
    void testGetBroadbandPlansCache_CachedObjectIsNotString() {
        // Arrange
        String key = "testKey";
        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(valueWrapper.get()).thenReturn(new Object());

        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(valueWrapper);

        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class); // Use RedisCacheManager here
        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);

        // Act
        List<BroadbandPlan> plans = broadbandRedis.getBroadbandPlansCache(key);

        // Assert
        assertNull(plans);
    }

    @Test
    void testAddBroadbandPlansCache_Success() throws Exception {
        // Arrange
        String key = "testKey";
        List<BroadbandPlan> plansToAdd = new ArrayList<>();
        BroadbandPlan planToAdd = new BroadbandPlan();
        planToAdd.setPlanId("1");
        planToAdd.setPlanType("Plan1");
        plansToAdd.add(planToAdd);

        Cache cache = mock(Cache.class);
        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(valueWrapper.get()).thenReturn(null);
        when(cache.get(key)).thenReturn(valueWrapper);

        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class);
        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);

        // Act
        broadbandRedis.addBroadbandPlansCache(key, plansToAdd);

        // Assert
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(cache, times(1)).put(keyCaptor.capture(), valueCaptor.capture());

        assertEquals(key, keyCaptor.getValue());

        String storedSerializedPlans = (String) valueCaptor.getValue();
        ObjectMapper objectMapper = new ObjectMapper();
        List<BroadbandPlan> storedPlans = objectMapper.readValue(storedSerializedPlans, new TypeReference<List<BroadbandPlan>>() {});

        assertEquals(plansToAdd.size(), storedPlans.size());
        assertEquals(plansToAdd.get(0).getPlanId(), storedPlans.get(0).getPlanId());
        assertEquals(plansToAdd.get(0).getPlanType(), storedPlans.get(0).getPlanType());
    }
    @Test
    void testGetBroadbandPlansCache_Success() throws Exception {
        // Arrange
        String key = "testKey";
        List<BroadbandPlan> expectedPlans = new ArrayList<>();
        BroadbandPlan expectedPlan = new BroadbandPlan(); // Instantiate an object
        expectedPlan.setPlanId("1");
        expectedPlan.setPlanType("Plan1");
        expectedPlans.add(expectedPlan);

        String serializedPlans = "[{\"planId\":\"1\",\"planType\":\"Plan1\"}]";

        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(valueWrapper.get()).thenReturn(serializedPlans);

        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(valueWrapper);

        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class); // Use RedisCacheManager here
        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);

        // Act
        List<BroadbandPlan> plans = broadbandRedis.getBroadbandPlansCache(key);

        // Assert
        assertNotNull(plans);
        assertEquals(expectedPlans.size(), plans.size());
        assertEquals(expectedPlans.get(0).getPlanId(), plans.get(0).getPlanId());
        assertEquals(expectedPlans.get(0).getPlanType(), plans.get(0).getPlanType());
    }

    @Test
    void testAddConnectionDetailCache_CacheIsNotNull() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "active";
        BroadbandConnection connectionDetail = new BroadbandConnection();
        String key = mobileNumber + status + "_connection_details";

        Cache cache = mock(Cache.class);
        RedisCacheManager redisCacheManager = mock(RedisCacheManager.class);
        when(redisCacheManager.getCache("ConnectionDetail")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);

        // Act
        broadbandRedis.addConnectionDetailCache(mobileNumber, status, connectionDetail);

        // Assert
        verify(cache, times(1)).put(eq(key), eq(connectionDetail));
    }

    @Test
    void testGetConnectionDetailsCache_CacheAndCachedDetailsExist() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "active";
        String key = mobileNumber + status + "_connection_details";
        BroadbandConnection expectedConnectionDetail = new BroadbandConnection();

        Cache.ValueWrapper cachedConnectionDetailWrapper = mock(Cache.ValueWrapper.class);
        when(cachedConnectionDetailWrapper.get()).thenReturn(expectedConnectionDetail);

        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(cachedConnectionDetailWrapper);

        RedisCacheManager cacheManager = mock(RedisCacheManager.class); // Use RedisCacheManager
        when(cacheManager.getCache("ConnectionDetail")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(cacheManager);

        // Act
        BroadbandConnection connectionDetail = broadbandRedis.getConnectionDetailsCache(mobileNumber, status);

        // Assert
        assertNotNull(connectionDetail);
        assertEquals(expectedConnectionDetail, connectionDetail);
    }

    @Test
    void testGetConnectionDetailsCache_CacheExistsButCachedDetailsAreNull() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "active";
        String key = mobileNumber + status + "_connection_details";

        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(null); // Simulating null cached value

        RedisCacheManager cacheManager = mock(RedisCacheManager.class);
        when(cacheManager.getCache("ConnectionDetail")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(cacheManager);

        // Act
        BroadbandConnection connectionDetail = broadbandRedis.getConnectionDetailsCache(mobileNumber, status);

        // Assert
        assertNull(connectionDetail);
    }

    @Test
    void testGetConnectionDetailsCache_CacheExistsButCachedDetailsNotFound() {
        // Arrange
        String mobileNumber = "1234567890";
        String status = "active";
        String key = mobileNumber + status + "_connection_details";

        Cache cache = mock(Cache.class);
        when(cache.get(key)).thenReturn(null); // Simulating null cached value

        RedisCacheManager cacheManager = mock(RedisCacheManager.class);
        when(cacheManager.getCache("ConnectionDetail")).thenReturn(cache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(cacheManager);

        // Act
        BroadbandConnection connectionDetail = broadbandRedis.getConnectionDetailsCache(mobileNumber, status);

        // Assert
        assertNull(connectionDetail);
    }
    @Test
    void testClearBroadbandCache() {
        // Arrange
        Set<String> keys = new HashSet<>();
        keys.add("BroadbandPlans:1");
        keys.add("BroadbandPlans:2");
        when(redisTemplate.keys(anyString())).thenReturn(keys);
        // Act
        broadbandRedis.clearBroadbandCache();
        // Assert
        verify(redisTemplate, times(1)).keys("BroadbandPlans:*");
        verify(redisTemplate, times(2)).delete(anyString());
    }
    @Test
    void testClearBroadbandCache_noKeys() {
        // Arrange
        when(redisTemplate.keys(anyString())).thenReturn(null);
        // Act
        broadbandRedis.clearBroadbandCache();
        // Assert
        verify(redisTemplate, times(1)).keys("BroadbandPlans:*");
        verify(redisTemplate, times(0)).delete(anyString());
    }

    @Test
    public void testAddBroadbandPlansCache() {
        List<BroadbandPlan> broadbandPlans = Arrays.asList(new BroadbandPlan(), new BroadbandPlan());
        Cache mockCache = mock(Cache.class);

        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(mockCache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);
        broadbandRedis.addBroadbandPlansCache("testKey", broadbandPlans);

        verify(mockCache).put(anyString(), anyString());
    }

    @Test
    public void testGetBroadbandPlansCache() {
        Cache mockCache = mock(Cache.class);
        Cache.ValueWrapper mockValueWrapper = mock(Cache.ValueWrapper.class);

        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(mockCache);
        when(mockCache.get("testKey")).thenReturn(mockValueWrapper);
        when(mockValueWrapper.get()).thenReturn("[]"); // Return an empty JSON array

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);
        List<BroadbandPlan> retrievedPlans = broadbandRedis.getBroadbandPlansCache("testKey");

        assertTrue(retrievedPlans.isEmpty());
    }

    @Test
    public void testAddConnectionDetailCache() {
        BroadbandConnection connectionDetail = new BroadbandConnection();
        Cache mockCache = mock(Cache.class);

        when(redisCacheManager.getCache("ConnectionDetail")).thenReturn(mockCache);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);
        broadbandRedis.addConnectionDetailCache("1234567890", "active", connectionDetail);

        verify(mockCache).put(anyString(), eq(connectionDetail));
    }

    @Test
    public void testGetConnectionDetailsCache() {
        BroadbandConnection connectionDetail = new BroadbandConnection();
        Cache mockCache = mock(Cache.class);
        Cache.ValueWrapper mockValueWrapper = mock(Cache.ValueWrapper.class);

        when(redisCacheManager.getCache("ConnectionDetail")).thenReturn(mockCache);
        when(mockCache.get("1234567890active_connection_details")).thenReturn(mockValueWrapper);
        when(mockValueWrapper.get()).thenReturn(connectionDetail);

        BroadbandRedis broadbandRedis = new BroadbandRedis();
        broadbandRedis.setRedisCacheManager(redisCacheManager);
        BroadbandConnection retrievedConnectionDetail = broadbandRedis.getConnectionDetailsCache("1234567890", "active");

        assertEquals(connectionDetail, retrievedConnectionDetail);
    }

//    @Test
//    public void testAddBroadbandPlansCache_JsonProcessingException() throws JsonProcessingException {
//        List<BroadbandPlan> broadbandPlans = Arrays.asList(new BroadbandPlan(), new BroadbandPlan());
//        Cache mockCache = mock(Cache.class);
//
//        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(mockCache);
//        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test exception") { });
//
//        BroadbandRedis broadbandRedis = new BroadbandRedis();
//        broadbandRedis.setRedisCacheManager(redisCacheManager);
//        broadbandRedis.setObjectMapper(objectMapper);
//
//        assertThrows(SerializationException.class, () -> broadbandRedis.addBroadbandPlansCache("testKey", broadbandPlans));
//    }

//    @Test
//    public void testGetBroadbandPlansCache_JsonProcessingException() throws JsonProcessingException {
//        Cache mockCache = mock(Cache.class);
//        Cache.ValueWrapper mockValueWrapper = mock(Cache.ValueWrapper.class);
//
//        when(redisCacheManager.getCache("BroadbandPlans")).thenReturn(mockCache);
//        when(mockCache.get("testKey")).thenReturn(mockValueWrapper);
//        when(mockValueWrapper.get()).thenReturn("[]");
//        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new JsonProcessingException("Test exception") { });
//
//        BroadbandRedis broadbandRedis = new BroadbandRedis();
//        broadbandRedis.setRedisCacheManager(redisCacheManager);
//        broadbandRedis.setObjectMapper(objectMapper);
//
//        assertThrows(SerializationException.class, () -> broadbandRedis.getBroadbandPlansCache("testKey"));
//    }

}
