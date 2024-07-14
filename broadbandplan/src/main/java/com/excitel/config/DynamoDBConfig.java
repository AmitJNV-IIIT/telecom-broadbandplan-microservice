package com.excitel.config;

import com.amazonaws.auth.*;
import com.amazonaws.client.builder.*;
import com.amazonaws.services.dynamodbv2.*;
import org.socialsignin.spring.data.dynamodb.repository.config.*;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Configuration of AWS DynamoDB Connection
 */
@Configuration
@EnableDynamoDBRepositories(basePackages = "com.excitel.repository")
public class DynamoDBConfig {
    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
    @Value("${amazon.aws.access-key}")
    private String amazonAWSAccessKey;
    @Value("${amazon.aws.secret-key}")
    private String amazonAWSSecretKey;
    @Value("${amazon.aws.region}")
    private String region;
    /**
     * Configures and creates an instance of AmazonDynamoDB using the provided endpoint, access key, and secret key.
     *
     * @return An AmazonDynamoDB instance
     */
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey)))
                .build();
    }
    @Bean
    public DynamoDbClient dynamoDbClient(){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
 